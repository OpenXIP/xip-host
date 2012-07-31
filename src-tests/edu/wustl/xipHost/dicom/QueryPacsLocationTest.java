/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.hostControl.Util;
/**
 * @author Jaroslaw Krych
 *
 */
public class QueryPacsLocationTest {
	final static Logger logger = Logger.getLogger(QueryPacsLocationTest.class);	
	static AttributeList criteria;	
	static PacsLocation pacsLoc;
	static TestServerSetup setup;
	static File hsqldbDir;
	static DicomManagerImpl dicomMgr;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		setup = new TestServerSetup();

		dicomMgr = new DicomManagerImpl();
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
		dicomMgr.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest2", workstation2Prop);
		
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
		prelaodDataToWorkstation2();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File[] files = hsqldbDir.listFiles();
		for(int i = 0 ; i < files.length; i++) {
			File file = files[i];
			if(!file.getName().endsWith(".svn")){
				Util.delete(file);
			}
		}
	}
	
	//DicomManagerImpl 1A - basic flow. AttributeList, PacsLocation are valid and network is on.
	@Test
	public void testQueryPacsLocation1A() {										              		
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);					
		Boolean blnType = result instanceof SearchResult; //instanceof would return false if result would be null
		assertTrue("Result returned is DicomSearchResult but system did not define it this way.", blnType);
	}

	//DicomManagerImpl 1B - alternative flow. AttributeList is valid. PacsLocation is valid. 
	//Check if return study attributes are not null and patient ID not empty.
	@Test
	public void testQueryPacsLocation1B() {					
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);						
		Patient patient = result.getPatients().get(0);
		Study study = patient.getStudies().get(0);
		Boolean isNameOK = new Boolean(patient.getPatientName() != null);
		Boolean isPatientIDOK = new Boolean(patient.getPatientID() != null && patient.getPatientID().isEmpty() == false);
		Boolean isPatientBirthDateOK = new Boolean(patient.getPatientBirthDate() != null);
		Boolean isDateOK = new Boolean(study.getStudyDate() != null);
		Boolean isDescOK = new Boolean(study.getStudyDesc() != null);		
		Boolean isStudyIDOK = new Boolean(study.getStudyID() != null);		
        Boolean areAllAttributesCorrect = new Boolean(isNameOK && isPatientIDOK && isPatientBirthDateOK && isDateOK && 
        		isDescOK && isStudyIDOK);
        assertTrue("Query error. Found study attributes do not pass validation test.", areAllAttributesCorrect);
	}

	
	//DicomManagerImpl 1C - alternative flow. AttributeList is valid and PacsLocation is valid. 
	//Check if return series attributes are not null and series number is not empty.	
	@Test
	public void testQueryPacsLocation1C() {	
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);				
		Patient patient = result.getPatients().get(0);
		Study study = patient.getStudies().get(0);
		Series series = study.getSeries().get(0);
		Boolean isNumberOK = new Boolean(series.getSeriesNumber() != null && !series.getSeriesNumber().isEmpty());
		Boolean isDescOK = new Boolean(series.getSeriesDesc() != null);
		Boolean isModalityOK = new Boolean(series.getModality() != null);		
		Boolean areAllSeriesAttOK = new Boolean(isNumberOK && isDescOK && isModalityOK);
		assertTrue("Query error. Found series attributes do not pass validation test.", areAllSeriesAttOK);		
	}

	//DicomManagerImpl 1D - alternative flow. AttributeList is valid. PacsLocation  is valid. 
	//Check if return image attributes are not null and image number is not empty.	
	@Test
	public void testQueryPacsLocation1D() {	
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);				
		Patient patient = result.getPatients().get(0);
		Study study = patient.getStudies().get(0);
		Series series = study.getSeries().get(0);
		ImageItem image = (ImageItem) series.getItems().get(0);
		Boolean isNumberOK = new Boolean(image.getItemID() != null && !image.getItemID().isEmpty());		
		Boolean areAllImageAttOK = new Boolean(isNumberOK);
		assertTrue("Query error. Found image attributes do not pass validation test", areAllImageAttOK);
	}
			
	//DicomManagerImpl 1E - alternative flow. AttributeList is null. PacsLocation is valid.	
	@Test
	public void testQueryPacsLocation1E() throws DicomException {	
		pacsLoc = setup.getLocation();		
		AttributeList criteria = null;		
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);	
		assertNull("Criteria AttributeList is null but GlobalSearch reasult is valid.", result);
	}
	
	//DicomManagerImpl 1F - alternative flow. AttributeList valid, PacsLocation null. 	
	@Test
	public void testQueryPacsLocation1F() {							
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, null);			
		assertNull("PacsLocation is null but system did not return null", result);
	}
		
	//DicomManagerImpl 1G - alternative flow. AttributeList valid, checks number of patients, series and images
	//Tests if returned result contains number of patients, studies, series and images as expected
	//Also locationDesc should be different than null and not empty
	@Test
	public void testQueryPacsLocation1G() {										              
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);
		Patient patient = result.getPatients().get(0);
		int numStudies = patient.getStudies().size();	//should be 2
		/*List<Study> studies = result.getStudies();
		for(int i = 0; i < studies.size(); i++){
			System.out.println(studies.get(i).getStudyID() + " " + studies.get(i).getStudyDesc());
		}*/
		int numSeriesStudy1 = patient.getStudies().get(0).getSeries().size();	//should be 1
		int numSeriesStudy2 = patient.getStudies().get(1).getSeries().size();	//should be 1
		int numImagesSeries1 = patient.getStudies().get(0).getSeries().get(0).getItems().size(); //should be 4
		int numImagesSeries2 = patient.getStudies().get(1).getSeries().get(0).getItems().size(); //should be 4
		Boolean isLocValid = new Boolean(result.getDataSourceDescription() != null && !result.getDataSourceDescription().isEmpty());
		Boolean bln = (numStudies == 2 && numSeriesStudy1 == 1 && numSeriesStudy2 == 1 && numImagesSeries1 == 4 && numImagesSeries2 == 4 && isLocValid);
		assertTrue("System did not return correct number of patients, studies or series.", bln);		
	}
	
	//DicomManagerImpl 1H - alternative flow. AttributeList valid, PacsLocation invalid (invalid port).	
	@Test
	public void testQueryPacsLocation1H(){								
		criteria = setup.getCriteria();
		PacsLocation loc = new PacsLocation("127.0.0.1", 30002, "WORKSTATION2", "WashU WS2");				
		assertNull(" ", DicomManagerFactory.getInstance().query(criteria, loc));			
	}	
	
	static void prelaodDataToWorkstation2(){
		logger.debug("Preloading WORKSTATION2 data source");
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
		PacsLocation loc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "WashU WS2");		;
		dicomMgr.submit(files, loc);
	}
	
}
