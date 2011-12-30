/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import com.pixelmed.dicom.AttributeList;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import edu.wustl.xipHost.avt2ext.AVTFactory;
import edu.wustl.xipHost.avt2ext.AVTQuery;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryADTest implements DataAccessListener{
	final static Logger logger = Logger.getLogger(QueryADTest.class);
	static ADFacade adService;
	static CriteriaSetup setup;
	static AttributeList attList;
	

	@BeforeClass
	public static void setUp() throws Exception {
		adService = AVTFactory.getADServiceInstance();
		setup = new CriteriaSetup();
		attList = setup.getCriteria();
		DOMConfigurator.configure("log4j.xml");
	}

	@After
	public void tearDown() throws Exception {
		result = null;
	}
	
	//AVTQuery - query AD database - basic flow (Patient level query test).
	//Parameters: adCriteria i valid, adAimCriteria is empty, ADQueryTarget, previousSearchResult, queryObject are all correct.
	@Test
	public void testQueryAD_1A(){				
		Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(attList);
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();
		Query avtQuery = new AVTQuery(adCriteria, adAimCriteria, QueryTarget.PATIENT, null, null);
		avtQuery.addDataAccessListener(this);
		Thread t = new Thread(avtQuery);
		t.start();	
		try {
			t.join();
		} catch (InterruptedException e) {			
			logger.error(e, e);
		}
		String patientID = "1.3.6.1.4.1.9328.50.1.0022";
		boolean isQueryOK = result.contains(patientID);
		boolean isNumPatients = (result.getPatients().size() == 1);
		assertTrue("Unable to find specified patientID.", isQueryOK);
		assertTrue("Actual number of found patients is different than 1.", isNumPatients);
	}

	//AVTQuery - query AD database - basic flow (Study level query test). 
	//Parameters: adCriteria is valid, adAimCriteria is empty, ADQueryTarget, previousSearchResult, queryObject are all correct.
	@Test
	public void testQueryAD_1B(){					
		Map<Integer, Object> adDicomCriteria = DicomUtil.convertToADDicomCriteria(attList);
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();
		Query avtQuery = new AVTQuery(adDicomCriteria, adAimCriteria, QueryTarget.PATIENT, null, null);
		avtQuery.addDataAccessListener(this);
		Thread t1 = new Thread(avtQuery);
		t1.start();	
		try {
			t1.join();
		} catch (InterruptedException e) {			
			logger.error(e, e);
		}
		Patient selectedNode = result.getPatients().get(0);
		Query avtQuery2 = new AVTQuery(adDicomCriteria, adAimCriteria, QueryTarget.STUDY, result, selectedNode);
		avtQuery2.addDataAccessListener(this);
		Thread t2 = new Thread(avtQuery2);
		t2.start();	
		try {
			t2.join();
		} catch (InterruptedException e) {			
			logger.error(e, e);
		}
		String patientID = "1.3.6.1.4.1.9328.50.1.0022";
		boolean isQueryOK = result.contains(patientID);
		boolean isNumPatients = (result.getPatients().size() == 1);
		int numStudies = result.getPatients().get(0).getStudies().size();
		boolean isNumStudies = (numStudies == 1);	
		assertTrue("Unable to find specified patientID..", isQueryOK);
		assertTrue("Actual number of found patients is different than 1.", isNumPatients);
		assertTrue("Actual number of found studies is " + numStudies + ". Expected 1.", isNumStudies);
	}
	
	//AVT AD query - test AD database directly with adServive
	//DICOM query criteria are valid, AIM criteria are empty
	@Test
	public void testQueryAD_1C(){
		Map<Integer, Object> adDicomCriteria = new HashMap<Integer, Object>();
		adDicomCriteria.put(new Integer(1048608), "1.3.6.1.4.1.9328.50.1.0022");
		Set<Integer> keySet = adDicomCriteria.keySet();
		Iterator<Integer> iter = keySet.iterator();
		logger.debug("AD DICOM criteria:");
		while(iter.hasNext()){
			Integer key = iter.next();
			String value = (String) adDicomCriteria.get(key);			
			logger.debug(key + " " + value);
		}	
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();		
		List<com.siemens.scr.avt.ad.dicom.Patient> patients = adService.findPatientByCriteria(adDicomCriteria, adAimCriteria);
		assertTrue("Expected number of patients: 1. " + "Actual: " + patients.size(), patients.size() == 1);
		List<GeneralStudy> studies = adService.findStudiesByCriteria(adDicomCriteria, adAimCriteria);
		assertTrue("Expected number of studies: 1. " + "Actual: " + studies.size(), studies.size() == 1);						
	}
	
	//AVTQuery - query AD database with query AIM criteria and empty DICOM criteria - basic flow (Patient level query test). 
	//Parameters: adCriteria is empty, adAimCriteria is valid, ADQueryTarget, previousSearchResult, queryObject are all correct.
	@Test
	public void testQueryAD_1D(){	
		Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(attList);
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();
		String key = "ImagingObservationCharacteristic.codeMeaning";
		Object value = "Extremely Obvious";
		adAimCriteria.put(key, value);
		Query avtQuery = new AVTQuery(adCriteria, adAimCriteria, QueryTarget.PATIENT, null, null);
		avtQuery.addDataAccessListener(this);
		Thread t = new Thread(avtQuery);
		t.start();	
		try {
			t.join();
		} catch (InterruptedException e) {			
			logger.error(e, e);
		}
		String patientID = "1.3.6.1.4.1.9328.50.1.0022";
		boolean isQueryOK = result.contains(patientID);
		boolean isNumPatients = (result.getPatients().size() == 1);
		assertTrue("Unable to find specified patientID.", isQueryOK);
		assertTrue("Actual number of found patients is different than 1.", isNumPatients);
	}
	
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}

	SearchResult result;
	@Override
	public void queryResultsAvailable(QueryEvent e) {
		Query query = (Query) e.getSource();
		result = query.getSearchResult();		
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}
}
