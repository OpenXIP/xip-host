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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nema.dicom.wg23.ObjectLocator;
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

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveFromPacsLocationTest implements RetrieveListener {
	final static Logger logger = Logger.getLogger(RetrieveFromPacsLocationTest.class);	
	static PacsLocation calling;
	static PacsLocation called;
	static AttributeList retrieveCriteria;
	static String dbFileName;
	static DicomManagerImpl dicomMgr1;
	static DicomManagerImpl dicomMgr2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		called = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "WashU WS2");		
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
		Properties workstation2Prop = new Properties();
		try {
			workstation2Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"));
			workstation2Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION2").getCanonicalPath());
		} catch (FileNotFoundException e1) {
			logger.error(e1, e1);	
			System.exit(0);
		} catch (IOException e1) {
			logger.error(e1, e1);
			System.exit(0);
		}
		dicomMgr1.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest", workstation2Prop);
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
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dicomMgr1.runDicomShutDownSequence("jdbc:hsqldb:./src-tests/edu/wustl/xipHost/dicom/server/hsqldb/data/ws2db", "sa", "");
		dicomMgr2.runDicomShutDownSequence("jdbc:hsqldb:./src-tests/edu/wustl/xipHost/dicom/server/hsqldb/data/ws3db", "sa", "");
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
		//setDefaultCallingPacsLocation(calling) has to be called after setCriteria() in JUnit testing. Otherwise calling would be null.
		retrieve.setDefaultCallingPacsLocation(calling);
		retrieve.setImportDir(importDir);
		retrieve.setDataSource(dataSource);
		retrieve.setDatabaseFileName(dbFileName);
		Thread t = new Thread(retrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		if(objectLocs != null){
			assertTrue("Number of retrieved objects should be 2, but actual is " + objectLocs.size(), objectLocs.size() == 2);
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

}
