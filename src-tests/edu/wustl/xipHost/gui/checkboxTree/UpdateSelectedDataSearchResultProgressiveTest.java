/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.iterator.Criteria;

/**
 * @author Jaroslaw Krych
 *
 */
public class UpdateSelectedDataSearchResultProgressiveTest implements DataSelectionListener{
	final static Logger logger = Logger.getLogger(UpdateSelectedDataSearchResultProgressiveTest.class);
	static SearchResultTreeProgressive resultTree;
	static SearchResult selectedDataSearchResult;
	static SearchResult result;
	static Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
	static Map<String, Object> aimCriteria = new HashMap<String, Object>();
	
	/**
	 * @throws java.lang.Exception
	 */
	/*public UpdateSelectedDataSearchResultProgressiveTest(){
		resultTree = new SearchResultTreeProgressive();
		selectedDataSearchResult = new SearchResult();
		resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
	}*/
	
	@Before
	public void setUpBeforeTest() throws Exception {
		result = new SearchResult("Test Progressive");
		dicomCriteria.put(Tag.PatientName, "*");
		Criteria criteria = new Criteria(dicomCriteria, aimCriteria);
		result.setOriginalCriteria(criteria);
		resultTree = new SearchResultTreeProgressive();
		selectedDataSearchResult = new SearchResult();
		resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDownAfterTest() throws Exception {
		result = null;
		resultTree = null;
		selectedDataSearchResult = null;
	}
	
	//SearchResultTree 1A - basic flow. One PatientNode selected. Patient not sub-queried.
	//Result: selectedDataSearchResult should contain one selected patient.
	@Test
	public void testUpdatedSelectedDataSearchResult1A() {
		resultTree.addDataSelectionListener(this);
		Patient patient1 = new Patient("Jarek1", "111", "07/18/1973");
		result.addPatient(patient1);
		resultTree.updateNodes(result);
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		resultTree.updateSelection(patientNode1, true);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		List<Patient> patients = selectedDataSearchResult.getPatients();
		int numbOfPatients = patients.size();
		assertEquals("Invalid number of patients. Expected: 1, actual: " + numbOfPatients, 1, numbOfPatients);
		if(numbOfPatients == 1) {
			Patient patient = patients.get(0);
			assertEquals("Invalid patient. Expected id: 111, actual: " + patient.getPatientID(), "111", patient.getPatientID());
		}
		List<Study> studies = patients.get(0).getStudies();
		int numbOfStudies = studies.size();
		assertEquals("Invalid number of studies. Expected: 0, actual: " + numbOfStudies, 0, numbOfStudies);
	}

	//SearchResultTree 2A - basic flow. One PatientNode selected. No Studies found for Patient. Then selected Patient sub-queried.
	//Result: selectedDataSearchResult should contain one patient, and all studies found in selected Patient.
	@Test
	public void testUpdatedSelectedDataSearchResult2A() {
		resultTree.addDataSelectionListener(this);
		Patient patient1 = new Patient("Jarek1", "111", "07/18/1973");
		result.addPatient(patient1);
		resultTree.updateNodes(result);
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		resultTree.updateSelection(patientNode1, true);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		Patient selectedPatient = result.getPatients().get(0);
		Timestamp patient1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		selectedPatient.setLastUpdated(patient1LastUpdated);
		Study study1 = new Study("06/12/2010", "101010", "Test Study", "101.101");
		Study study2 = new Study("06/12/2010", "202020", "Test Study", "202.202");
		selectedPatient.addStudy(study1);
		selectedPatient.addStudy(study2);
		resultTree.updateNodes(result);
		resultTree.setDoubleClicked(true);
		resultTree.updateNodeProgressive(patientNode1);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().get(0).getStudies().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		List<Patient> patients = selectedDataSearchResult.getPatients();
		int numbOfPatients = patients.size();
		assertEquals("Invalid number of patients. Expected: 1, actual: " + numbOfPatients, 1, numbOfPatients);
		if(numbOfPatients == 1) {
			Patient patient = patients.get(0);
			assertEquals("Invalid patient. Expected id: 111, actual: " + patient.getPatientID(), "111", patient.getPatientID());
		}
		List<Study> studies = patients.get(0).getStudies();
		int numbOfStudies = studies.size();
		assertEquals("Invalid number of studies. Expected: 2, actual: " + numbOfStudies, 2, numbOfStudies);
	}
	
	//SearchResultTree 3A - basic flow. One PatientNode and two Studies found for Patient no initially selected. Then Study is selected and sub-queried.
	//Result: selectedDataSearchResult should contain one Patient, one Study and all Series found in selected Study.
	@Test
	public void testUpdatedSelectedDataSearchResult3A() {
		resultTree.addDataSelectionListener(this);
		Patient patient1 = new Patient("Jarek1", "111", "07/18/1973");
		result.addPatient(patient1);
		resultTree.updateNodes(result);
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		resultTree.updateSelection(patientNode1, true);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		Patient selectedPatient = null;
		selectedPatient = result.getPatients().get(0);
		Timestamp patient1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		selectedPatient.setLastUpdated(patient1LastUpdated);
		Study study1 = new Study("06/12/2010", "101010", "Test Study", "101.101");
		Study study2 = new Study("06/12/2010", "202020", "Test Study", "202.202");
		selectedPatient.addStudy(study1);
		selectedPatient.addStudy(study2);
		resultTree.updateNodes(result);
		resultTree.setDoubleClicked(true);
		resultTree.updateNodeProgressive(patientNode1);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().get(0).getStudies().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		selectedPatient = result.getPatients().get(0);
		Study selectedStudy = selectedPatient.getStudy("202.202");
		Timestamp study2LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		selectedStudy.setLastUpdated(study2LastUpdated);
		Series series2 = new Series("2", "MIR", "Series Test", "202.202.1");
		Series series3 = new Series("3", "MR", "Series Test", "303.303.1");
		selectedStudy.addSeries(series2);
		selectedStudy.addSeries(series3);
		resultTree.updateNodes(result);
		resultTree.setDoubleClicked(true);
		StudyNode study2Node = (StudyNode)patientNode1.getChildAt(1);
		resultTree.updateNodeProgressive(study2Node);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().get(0).getStudies().get(1).getSeries().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		List<Patient> patients = selectedDataSearchResult.getPatients();
		int numbOfPatients = patients.size();
		assertEquals("Invalid number of patients. Expected: 1, actual: " + numbOfPatients, 1, numbOfPatients);
		if(numbOfPatients == 1) {
			Patient patient = patients.get(0);
			assertEquals("Invalid patient. Expected id: 111, actual: " + patient.getPatientID(), "111", patient.getPatientID());
		}
		List<Study> studies = patients.get(0).getStudies();
		int numbOfStudies = studies.size();
		assertEquals("Invalid number of studies. Expected: 2, actual: " + numbOfStudies, 2, numbOfStudies);
		for(Study study : studies){
			if(study.getStudyInstanceUID().equalsIgnoreCase("202.202")){
				int numbOfSeriesInStudy2 = study.getSeries().size();
				assertEquals("Invalid number of series in Study2. Expected: 2, actual: " + numbOfSeriesInStudy2, 2, numbOfSeriesInStudy2);
			}
		}
	}
	
	//SearchResultTree 4A - basic flow. Two Patients found. Non selected. Then SelectAll executed.
	//Result: selectedDataSearchResult should contain two patient.
	@Test
	public void testUpdatedSelectedDataSearchResult4A() {
		resultTree.addDataSelectionListener(this);
		Patient patient1 = new Patient("Jarek1", "111", "07/18/1973");
		result.addPatient(patient1);
		Patient patient2 = new Patient("Jarek2", "222", "07/18/1973");
		result.addPatient(patient2);
		resultTree.updateNodes(result);
		resultTree.selectAll(true);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
		//List<Patient> patients = selectedDataSearchResult.getPatients();
		//int numbOfPatients = patients.size();
		//assertEquals("Invalid number of patients. Expected: 2, actual: " + numbOfPatients, 2, numbOfPatients);
		assertSelectAll(selectedDataSearchResult);
	}


	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
		selectedDataSearchResult.setOriginalCriteria(result.getOriginalCriteria());
		selectedDataSearchResult.setDataSourceDescription("Selected data for " + result.getDataSourceDescription());
		synchronized(this){
			this.notify();
		}
		if(logger.isDebugEnabled()){
			logger.debug("Value of selectedDataSearchresult: ");
			if(selectedDataSearchResult != null) {
				List<Patient> patients = selectedDataSearchResult.getPatients();
				for(Patient logPatient : patients){
					logger.debug(logPatient.toString());
					List<Study> studies = logPatient.getStudies();
					for(Study logStudy : studies){
						logger.debug("   " + logStudy.toString());
						List<Series> series = logStudy.getSeries();
						for(Series logSeries : series){
							logger.debug("      " + logSeries.toString());
							List<Item> items = logSeries.getItems();
							for(Item logItem : items){
								logger.debug("         " + logItem.toString());
							}
						}
					}
				}
			}
		}
		
	}
	
	void assertSelectAll(SearchResult selectedDataSearchResult){
		List<Patient> patients = selectedDataSearchResult.getPatients();
		int numbOfPatients = patients.size();
		assertEquals("Invalid number of patients. Expected: 2, actual: " + numbOfPatients, 2, numbOfPatients);
		for(Patient patient : patients){
			List<Study> studies = patient.getStudies();
			if(patient.getPatientID().equalsIgnoreCase("111")){
				assertEquals("Invalid number of studies. Expected: 0, actual: " + studies.size(), 0, studies.size());
			} else if (patient.getPatientID().equalsIgnoreCase("222")){
				assertEquals("Invalid number of studies. Expected: 0, actual: " + studies.size(), 0, studies.size());
			}
		}
	}
	
}
