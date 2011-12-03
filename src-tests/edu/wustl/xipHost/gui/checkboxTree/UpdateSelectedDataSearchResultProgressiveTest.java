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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
	public UpdateSelectedDataSearchResultProgressiveTest(){
		resultTree = new SearchResultTreeProgressive();
		selectedDataSearchResult = new SearchResult();
		resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		result = new SearchResult("Test Progressive");
		dicomCriteria.put(Tag.PatientName, "*");
		Criteria criteria = new Criteria(dicomCriteria, aimCriteria);
		result.setOriginalCriteria(criteria);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	//Result: selectedDataSearchResult should contain one patient, and all studied found in selected Patient.
	@Ignore("Test suspends the system due to the synchronization problem")
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
				assertEquals("Invalid number of studies. Expected: 2, actual: " + studies.size(), 2, studies.size());
				for(Study study : studies){
					if(study.getStudyInstanceUID().equalsIgnoreCase("101.101")){
						List<Series> series = study.getSeries();
						assertEquals("Invalid number of series. Expected: 1, actual: " + series.size(), 1, series.size());
						for(Series oneSeries : series){
							if(oneSeries.getSeriesInstanceUID().equalsIgnoreCase("101.101.1")){
								List<Item> items = oneSeries.getItems();
								int numbOfItems = items.size();
								assertEquals("Invalid number of items in Series 101.101.1 . Expected: 1, actual: " + numbOfItems, 1, numbOfItems);
								assertEquals("Invalid item in Series 101.101.1 . Expected: 101.101.1.000.000.1, actual: " + items.get(0).getItemID(), "101.101.1.000.000.1", items.get(0).getItemID());
							}
						}
					} else if (study.getStudyInstanceUID().equalsIgnoreCase("202.202")) {
						List<Series> series = study.getSeries();
						assertEquals("Invalid number of series. Expected: 2, actual: " + series.size(), 2, series.size());
						for(Series oneSeries : series){
							if(oneSeries.getSeriesInstanceUID().equalsIgnoreCase("202.202.1")){
								List<Item> items = oneSeries.getItems();
								int numbOfItems = items.size();
								assertEquals("Invalid number of items in Series 202.202.1 . Expected: 2, actual: " + numbOfItems, 2, numbOfItems);
								for(Item item : items){
									if(item.getItemID().equalsIgnoreCase("202.202.1.000.000.1")){
										assertEquals("Invalid item in Series 202.202.1 . Expected: 202.202.1.000.000.1, actual: " + item.getItemID(), "202.202.1.000.000.1", item.getItemID());
									} else if (item.getItemID().equalsIgnoreCase("202.202.1.000.000.2")) {
										assertEquals("Invalid item in Series 202.202.1 . Expected: 202.202.1.000.000.2, actual: " + item.getItemID(), "202.202.1.000.000.2", item.getItemID());
									}
								}
							} else if(oneSeries.getSeriesInstanceUID().equalsIgnoreCase("303.303.1")){
								List<Item> items = oneSeries.getItems();
								int numbOfItems = items.size();
								assertEquals("Invalid number of items in Series 303.303.1 . Expected: 2, actual: " + numbOfItems, 2, numbOfItems);
								for(Item item : items){
									if(item.getItemID().equalsIgnoreCase("303.303.1.000.000.1")){
										assertEquals("Invalid item in Series 303.303.1 . Expected: 303.303.1.000.000.1, actual: " + item.getItemID(), "303.303.1.000.000.1", item.getItemID());
									} else if (item.getItemID().equalsIgnoreCase("303.303.1.000.000.2")) {
										assertEquals("Invalid item in Series 303.303.1 . Expected: 303.303.1.000.000.2, actual: " + item.getItemID(), "303.303.1.000.000.2", item.getItemID());
									}
								}
							}
						}
					}
				}
			} else if (patient.getPatientID().equalsIgnoreCase("222")){
				assertEquals("Invalid number of studies. Expected: 1, actual: " + studies.size(), 1, studies.size());
				for(Study study : studies){
					if(study.getStudyInstanceUID().equalsIgnoreCase("303.303")){
						List<Series> series = study.getSeries();
						assertEquals("Invalid number of series. Expected: 1, actual: " + series.size(), 1, series.size());
						for(Series oneSeries : series){
							if(oneSeries.getSeriesInstanceUID().equalsIgnoreCase("404.404.1")){
								List<Item> items = oneSeries.getItems();
								int numbOfItems = items.size();
								assertEquals("Invalid number of items in Series 404.404.1 . Expected: 1, actual: " + numbOfItems, 1, numbOfItems);
								assertEquals("Invalid item in Series 404.404.1 . Expected: 404.404.1.000.000.1, actual: " + items.get(0).getItemID(), "404.404.1.000.000.1", items.get(0).getItemID());
							}
						}
					}
				}
			}
		}
	}
	
}
