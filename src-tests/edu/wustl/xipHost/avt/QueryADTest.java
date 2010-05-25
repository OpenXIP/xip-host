/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.pixelmed.dicom.AttributeList;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import edu.wustl.xipHost.avt2ext.ADQueryTarget;
import edu.wustl.xipHost.avt2ext.AVTFactory;
import edu.wustl.xipHost.avt2ext.AVTListener;
import edu.wustl.xipHost.avt2ext.AVTQuery;
import edu.wustl.xipHost.avt2ext.AVTRetrieveEvent;
import edu.wustl.xipHost.avt2ext.AVTSearchEvent;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dicom.DicomUtil;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryADTest extends TestCase implements AVTListener{
	ADFacade adService;
	CriteriaSetup setup;
	/**
	 * @param name
	 */
	public QueryADTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		adService = AVTFactory.getADServiceInstance();
		setup = new CriteriaSetup();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		result = null;
	}
	
	//AVTQuery - query AD database - basic flow. 
	//Parameters: adCriteria, adAimCriteria, ADQueryTarget, previousSearchResult, queryObject are all correct.
	public void testQueryAD_1A(){
		/*Map<String, Object> aimCriteria = new HashMap<String, Object>();
		dicomCriteria.put(Tag.PatientSex, "M");			
		String key = "Patient.sex";
		Object value = "M";
		aimCriteria.put(key, value);
		List<String> results = adService.findAnnotations(dicomCriteria, aimCriteria);*/
		AttributeList attList = setup.getCriteria();					
		Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(attList);
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();
		AVTQuery avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.PATIENT, null, null);
		avtQuery.addAVTListener(this);
		Thread t = new Thread(avtQuery);
		t.start();	
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		String patientID = "YAMAMOTO-00046";
		boolean isQueryOK = result.contains(patientID);
		boolean isNumPatients = (result.getPatients().size() == 1);
		assertTrue("The number of patients in AD > 0 but system unable to find data.", isQueryOK);
		assertTrue("Actual number of found patients is different than 1.", isNumPatients);
	}

	//AVTQuery - query AD database - basic flow. 
	//Parameters: adCriteria, adAimCriteria, ADQueryTarget, previousSearchResult, queryObject are all correct.
	public void testQueryAD_1B(){
		AttributeList attList = setup.getCriteria();					
		Map<Integer, Object> adCriteria1 = DicomUtil.convertToADDicomCriteria(attList);
		Map<String, Object> adAimCriteria1 = new HashMap<String, Object>();
		AVTQuery avtQuery1 = new AVTQuery(adCriteria1, adAimCriteria1, ADQueryTarget.PATIENT, null, null);
		avtQuery1.addAVTListener(this);
		Thread t1 = new Thread(avtQuery1);
		t1.start();	
		try {
			t1.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		Patient selectedNode = result.getPatients().get(0);
		
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();		
		String key = "ImagingObservationCharacteristic.codeMeaning";
		Object value = "irregularly shaped";
		adAimCriteria.put(key, value);
							
		Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(attList);
		
		AVTQuery avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.STUDY, result, selectedNode);
		avtQuery.addAVTListener(this);
		Thread t2 = new Thread(avtQuery);
		t2.start();	
		try {
			t2.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		String patientID = "YAMAMOTO-00046";
		boolean isQueryOK = result.contains(patientID);
		boolean isNumPatients = (result.getPatients().size() == 1);
		int numStudies = result.getPatients().get(0).getStudies().size();
		boolean isNumStudies = (numStudies == 1);	
		assertTrue("The number of patients in AD > 0 but system unable to find data.", isQueryOK);
		assertTrue("Actual number of found patients is different than 1.", isNumPatients);
		assertTrue("Actual number of found studies is " + numStudies + ". Expected 1.", isNumStudies);
	}
	
	public void testQueryAD_1C(){
		//AttributeList attList = setup.getCriteria();
		//Map<Integer, Object> adDicomCriteria = DicomUtil.convertToADDicomCriteria(attList);		
		Map<Integer, Object> adDicomCriteria = new HashMap<Integer, Object>();
		adDicomCriteria.put(new Integer(1048608), "YAMAMOTO-00046");
		//Map<Integer, Object> adDicomCriteria = new HashMap<Integer, Object>();
		Set<Integer> keySet = adDicomCriteria.keySet();
		Iterator<Integer> iter = keySet.iterator();
		System.out.println("-----------------------------------------------------------------------------");
		while(iter.hasNext()){
			Integer key = iter.next();
			String value = (String) adDicomCriteria.get(key);			
			System.out.println(key + " " + value);
		}
		System.out.println("-----------------------------------------------------------------------------");	
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();		
		String key = "ImagingObservationCharacteristic.codeMeaning";
		Object value = "irregularly shaped";
		adAimCriteria.put(key, value);
		
		List<com.siemens.scr.avt.ad.dicom.Patient> patients = adService.findPatientByCriteria(adDicomCriteria, adAimCriteria);
		assertTrue("Expected number of patients: 1. " + "Actual: " + patients.size(), patients.size() == 1);
		List<GeneralStudy> studies = adService.findStudiesByCriteria(adDicomCriteria, adAimCriteria);
		assertTrue("Expected number of studies: 1. " + "Actual: " + studies.size(), studies.size() == 1);					
			
	}
	
	
	//AVTQuery - query AD database - alternative flow. 
	//Parameters: adCriteria, ADQueryTarget, previousSearchResult, queryObject are all correct.
	//Multi-query requests sent all in parallel (two requests)
	/*
	public void testQueryAD_1B(){
		AttributeList attList = setup.getCriteria();					
		Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(attList);
		Map<String, Object> adAimCriteria = new HashMap<String, Object>();
		AVTQuery avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.PATIENT, null, null);
		avtQuery.addAVTListener(this);
		Thread t = new Thread(avtQuery);
		t.start();	
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
	}
	*/
	@Override
	public void retriveResultsAvailable(AVTRetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}

	SearchResult result;
	@Override
	public void searchResultsAvailable(AVTSearchEvent e) {
		result = (SearchResult) e.getSource();			
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}
}
