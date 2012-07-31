/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveFactory;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.hostControl.Util;
/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveFromPacsLocationTest implements RetrieveListener {
	final static Logger logger = Logger.getLogger(RetrieveFromPacsLocationTest.class);	
	static PacsLocation calling;	//initiating request
	static PacsLocation called;		//serving request
	static AttributeList retrieveCriteria;
	static String dbFileName;
	static DicomManagerImpl dicomMgr1;
	static DicomManagerImpl dicomMgr2;
	static File workstation3_retrieve_dir;
	static File workstation4_retrieve_dir;
	static File hsqldbDir;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		called = new PacsLocation("127.0.0.1", 3004, "WORKSTATION4", "WashU WS4");		
		calling = new PacsLocation("127.0.0.1", 3003, "WORKSTATION3", "WashU WS3");
		retrieveCriteria = DicomUtil.constructEmptyAttributeList();
		try{
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			{ AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0022"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0022"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.9328.50.1.10607"); retrieveCriteria.put(t,a); }			
			{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.9328.50.1.10697"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("*"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); retrieveCriteria.put(t,a); }
		} catch (DicomException excep){
			
		}
		
		dicomMgr1 = new DicomManagerImpl();
		Properties workstation4Prop = new Properties();
		try {
			workstation4Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation4.properties"));
			workstation4Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION4").getCanonicalPath());
		} catch (FileNotFoundException e1) {
			logger.error(e1, e1);	
			System.exit(0);
		} catch (IOException e1) {
			logger.error(e1, e1);
			System.exit(0);
		}
		dicomMgr1.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest4", workstation4Prop);
		dbFileName = dicomMgr1.getDBFileName();
		
		dicomMgr2 = new DicomManagerImpl();
		Properties workstation3Prop = new Properties();
		try {
			workstation3Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation3.properties"));
			workstation3Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION3").getCanonicalPath());
		} catch (FileNotFoundException e1) {
			logger.error(e1, e1);	
			System.exit(0);
		} catch (IOException e1) {
			logger.error(e1, e1);
			System.exit(0);
		}
		dicomMgr2.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest3", workstation3Prop);
		
		workstation3_retrieve_dir = new File("./test-content/WORKSTATION3");
		if(workstation3_retrieve_dir.exists() == false){
			workstation3_retrieve_dir.mkdir();
		} else {
			File[] files = workstation3_retrieve_dir.listFiles(new DcmFileFilter());
			if(files.length > 0) {
				for(int i = 0 ; i < files.length; i++) {
					File file = files[i];
					Util.delete(file);
				}
			}
		}
		
		workstation4_retrieve_dir = new File("./test-content/WORKSTATION4");
		if(workstation4_retrieve_dir.exists() == false){
			workstation4_retrieve_dir.mkdir();
		} else {
			File[] files = workstation4_retrieve_dir.listFiles(new DcmFileFilter());
			if(files.length > 0) {
				for(int i = 0 ; i < files.length; i++) {
					File file = files[i];
					Util.delete(file);
				}
			}
		}
		
		hsqldbDir = new File("./src-tests/edu/wustl/xipHost/dicom/server/hsqldb/data");
		File[] files = hsqldbDir.listFiles();
		if(files.length > 0) {
			for(int i = 0 ; i < files.length; i++) {
				File file = files[i];
				if(!file.getName().endsWith(".svn")){
					Util.delete(file);
				}
			}
		}
		
		
		//Check if data prelaoded in WORKSTATION4. If query doesn't return expected results, clear WORKSTATION4 database directory and preload data.
		List<String> itemsSOPInstanceUIDs = new ArrayList<String>();
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10700");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10701");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10698");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10699");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10865");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10866");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10867");
		itemsSOPInstanceUIDs.add("1.3.6.1.4.1.9328.50.1.10868");
		
		SearchResult result = DicomManagerFactory.getInstance().query(retrieveCriteria, called);				
		if(result != null){
			List<Patient> patients = result.getPatients();
			if(patients.size() > 0){
				Patient patient = result.getPatients().get(0);
				if(patient != null){
					List<Study> studies = patient.getStudies();
					if(studies.size() > 0){
						Study study1 = patient.getStudies().get(0);
						Study study2 = patient.getStudies().get(1);
						if(study1 != null && study2 != null){
							int numSeriesStudy1 = study1.getSeries().size();
							int numSeriesStudy2 = study2.getSeries().size();
							if(numSeriesStudy1 > 0 && numSeriesStudy2 > 0){
								Series series1 = study1.getSeries().get(0);
								Series series2 = study2.getSeries().get(0);
								if(series1 != null && series2 != null){
									List<Item> itemsSeries1 = series1.getItems();
									for(Item item : itemsSeries1){
										if(!itemsSOPInstanceUIDs.contains(item.getItemID())){
											prelaodDataToWorkstation4();
											break;
										}
									}
									List<Item> itemsSeries2 = series2.getItems();
									for(Item item : itemsSeries2){
										if(!itemsSOPInstanceUIDs.contains(item.getItemID())){
											prelaodDataToWorkstation4();
											break;
										}
									}
								} else {
									prelaodDataToWorkstation4();
								}
							} else {
								prelaodDataToWorkstation4();
							}
						} else {
							prelaodDataToWorkstation4();
						}
					} else {
						prelaodDataToWorkstation4();
					}
				} else {
					prelaodDataToWorkstation4();
				}
			} else {
				prelaodDataToWorkstation4();
			}
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File[] files1 = workstation4_retrieve_dir.listFiles(new DcmFileFilter());
		for(int i = 0 ; i < files1.length; i++) {
			File file = files1[i];
			Util.delete(file);
		}
		File[] files2 = workstation3_retrieve_dir.listFiles(new DcmFileFilter());
		for(int i = 0 ; i < files2.length; i++) {
			File file = files2[i];
			Util.delete(file);
		}
		File[] files = hsqldbDir.listFiles();
		for(int i = 0 ; i < files.length; i++) {
			File file = files[i];
			if(!file.getName().endsWith(".svn")){
				Util.delete(file);
			}
		}
	}
	
	
	//DicomRetrieve 1A - basic flow. AttributeList, PacsLocation (calling and called) are valid and network is on.
	//TODO Retrieve JUnit test should be performed in the context of TargetIteratorRunner or refactor code to run it independently 
	@Test
	public void testRetrieveFromPacsLocation1A() {	
		Map<Integer, Object> dicomCriteria = DicomUtil.convertToADDicomCriteria(retrieveCriteria);
		Map<String, Object> aimCriteria = new HashMap<String, Object>();
		File importDir = new File("./test-content/WORKSTATION2");
		DataSource dataSource = called;
		DicomRetrieve retrieve = (DicomRetrieve) RetrieveFactory.getInstance("edu.wustl.xipHost.dicom.DicomRetrieve");				
		retrieve.addRetrieveListener(this);
		retrieve.setCriteria(dicomCriteria, aimCriteria);
		//setDefaultCallingPacsLocation(calling) has to be called after setCriteria() in JUnit testing. Otherwise the value of calling would be null.
		retrieve.setDefaultCallingPacsLocation(calling);
		retrieve.setImportDir(importDir);
		retrieve.setDataSource(dataSource);
		retrieve.setDatabaseFileName(dbFileName);
		Thread t = new Thread(retrieve);
		retrieve.setObjectDescriptors(getObjectDescriptors());
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		if(objectLocs != null){
			assertTrue("Number of retrieved objects should be 4, but actual is " + objectLocs.size(), objectLocs.size() == 4);
		} else {
			fail("Null value of ObjectLocators");
		}
		
	}

	Map<String, ObjectLocator> objectLocs;
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		objectLocs = (Map<String, ObjectLocator>) e.getSource();
	}
	
	List<ObjectDescriptor> getObjectDescriptors(){
		List<ObjectDescriptor> objectDescriptors = new ArrayList<ObjectDescriptor>();
		ObjectDescriptor objDesc1 = new ObjectDescriptor();					
		Uuid objDescUUID = new Uuid();
		objDescUUID.setUuid(UUID.randomUUID().toString());
		objDesc1.setUuid(objDescUUID);													
		objDesc1.setMimeType("application/dicom");	
		objectDescriptors.add(objDesc1);
		ObjectDescriptor objDesc2 = new ObjectDescriptor();					
		Uuid objDescUUID2 = new Uuid();
		objDescUUID2.setUuid(UUID.randomUUID().toString());
		objDesc2.setUuid(objDescUUID2);													
		objDesc2.setMimeType("application/dicom");	
		objectDescriptors.add(objDesc2);
		ObjectDescriptor objDesc3 = new ObjectDescriptor();					
		Uuid objDescUUID3 = new Uuid();
		objDescUUID3.setUuid(UUID.randomUUID().toString());
		objDesc3.setUuid(objDescUUID3);													
		objDesc3.setMimeType("application/dicom");			
		objectDescriptors.add(objDesc3);
		ObjectDescriptor objDesc4 = new ObjectDescriptor();					
		Uuid objDescUUID4 = new Uuid();
		objDescUUID4.setUuid(UUID.randomUUID().toString());
		objDesc4.setUuid(objDescUUID4);													
		objDesc4.setMimeType("application/dicom");			
		objectDescriptors.add(objDesc4);
		return objectDescriptors;
	}
	

	static void prelaodDataToWorkstation4(){
		logger.debug("Preloading WORKSTATION4 data source");
		DcmFileFilter dcmFilter = new DcmFileFilter(){
			@Override
			public boolean accept(File file) {
				try {
					if(DicomUtil.mimeType(file).equalsIgnoreCase("application/dicom")){
						return true;
					} else {
						return false;
					}
				} catch (IOException e) {
					return false;
				}
			}
		};
		File file = new File("./dicom-dataset-demo");
		File[] files = file.listFiles(dcmFilter);
		if(files == null){
			return;
		}		
		PacsLocation loc = new PacsLocation("127.0.0.1", 3004, "WORKSTATION4", "XIPHost test database");
		dicomMgr1.submit(files, loc);
	}
}
