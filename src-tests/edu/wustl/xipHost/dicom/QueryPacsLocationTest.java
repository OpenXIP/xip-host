/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryPacsLocationTest extends TestCase {
	static AttributeList criteria;	
	static PacsLocation pacsLoc;
	static TestServerSetup setup;
		
	public static Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTestSuite(QueryPacsLocationTest.class);
		setup = new TestServerSetup(suite); 		
		return setup;		
	}
	
	//DicomManagerImpl 1A - basic flow. AttributeList, PacsLocation are valid and network is on.
	public void testQueryPacsLocation1A() {										              		
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);
		//Check if return type is an instance of DicomSearchResult and not null						
		Boolean blnType = result instanceof SearchResult; //instanceof would return false if result would be null
		//assertNotNull("AttributeList is correct", result);
		assertTrue("Result returned is DicomSearchResult but system did not define it this way.", blnType);
	}

	//DicomManagerImpl 1B - alternative flow. AttributeList is valid. PacsLocation is valid. 
	//Check if return study attributes are not null and patient ID not empty.
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
        /*List<Study> studies = result.getStudies();
		for(int i = 0; i < studies.size(); i++){
			System.out.println(studies.get(i).getStudyID() + " " + studies.get(i).getStudyDesc());
		}*/
        assertTrue("Query error. Found study attributes do not pass validation test.", areAllAttributesCorrect);
	}

	
	//DicomManagerImpl 1C - alternative flow. AttributeList is valid and PacsLocation is valid. 
	//Check if return series attributes are not null and series number is not emmpty.	
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
	public void testQueryPacsLocation1E() throws DicomException {	
		pacsLoc = setup.getLocation();		
		AttributeList criteria = null;		
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);	
		assertNull("Criteria AttributeList is null but GlobalSearch reasult is valid.", result);
	}
	
	//DicomManagerImpl 1F - alternative flow. AttributeList valid, PacsLocation null. 	
	public void testQueryPacsLocation1F() {							
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, null);			
		assertNull("PacsLocation is null but system did not return null", result);
	}
		
	//DicomManagerImpl 1G - alternative flow. AttributeList valid, checks number of patients, series and images
	//Test if returned result contains num of patients, studies, series and images as expected
	//Also locationDesc should be different than null and not empty
	public void testQueryPacsLocation1G() {										              
		pacsLoc = setup.getLocation();
		criteria = setup.getCriteria();
		SearchResult result = DicomManagerFactory.getInstance().query(criteria, pacsLoc);
		Patient patient = result.getPatients().get(0);
		int numStudies = patient.getStudies().size();	//should be 1
		/*List<Study> studies = result.getStudies();
		for(int i = 0; i < studies.size(); i++){
			System.out.println(studies.get(i).getStudyID() + " " + studies.get(i).getStudyDesc());
		}*/
		int numSeries = patient.getStudies().get(0).getSeries().size();	//should be 1
		int numImages = patient.getStudies().get(0).getSeries().get(0).getItems().size(); //should be 1
		Boolean isLocValid = new Boolean(result.getDataSourceDescription() != null && !result.getDataSourceDescription().isEmpty());
		Boolean bln = numStudies == 1 && numSeries == 1 && numImages == 1 && isLocValid;
		assertTrue("System did not return correct number of patients, studies or series.", bln);		
	}
	
	//DicomManagerImpl 1H - alternative flow. AttributeList valid, PacsLocation invalid. 	
	public void testQueryPacsLocation1H(){								
		criteria = setup.getCriteria();
		PacsLocation loc = new PacsLocation("127.0.0.1", 30002, "WORKSTATION2", "WashU WS2");				
		assertNull(" ", DicomManagerFactory.getInstance().query(criteria, loc));			
	}	
}
