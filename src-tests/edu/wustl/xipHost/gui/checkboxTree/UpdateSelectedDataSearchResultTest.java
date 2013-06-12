/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.gui.checkboxTree;

import static org.junit.Assert.*;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

/**
 * @author Jaroslaw Krych
 *
 */
public class UpdateSelectedDataSearchResultTest implements DataSelectionListener{
	final static Logger logger = Logger.getLogger(UpdateSelectedDataSearchResultTest.class);
	static SearchResult searchResult;
	static SearchResultTree resultTree;
	static SearchResult selectedDataSearchResult;
	
	/**
	 * @throws java.lang.Exception
	 */
	public UpdateSelectedDataSearchResultTest(){
		resultTree = new SearchResultTree();
		selectedDataSearchResult = new SearchResult();
		resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
		resultTree.updateNodes(searchResult);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SearchResultSetup result = new SearchResultSetup();
		searchResult = result.getSearchResult();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	//SearchResultTree 1A - basic flow. One ItemNode selected.
	//Result: selectedDataSearchResult should contain one patient, one study, one series and one selected itemNode.
	@Test
	public void testUpdatedSelectedDataSearchResult1A() {
		resultTree.addDataSelectionListener(this);
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		StudyNode studyNode1 = (StudyNode)patientNode1.getChildAt(0);
		SeriesNode seriesNode1 = (SeriesNode)studyNode1.getChildAt(0);
		ItemNode itemNode1 = (ItemNode)seriesNode1.getChildAt(0);
		resultTree.updateSelection(itemNode1, true);
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
		assertEquals("Invalid number of studies. Expected: 1, actual: " + numbOfStudies, 1, numbOfStudies);
		if(numbOfStudies == 1) {
			Study study = studies.get(0);
			assertEquals("Invalid study. Expected studyInstanceUID: 101.101, actual: " + study.getStudyInstanceUID(), "101.101", study.getStudyInstanceUID());
		}
		List<Series> series = studies.get(0).getSeries();
		int numbOfSeries = series.size();
		assertEquals("Invalid number of series. Expected: 1, actual: " + numbOfSeries, 1, numbOfSeries);
		if(numbOfSeries == 1) {
			Series oneSeries = series.get(0);
			assertEquals("Invalid series. Expected seriesInstanceUID: 101.101.1, actual: " + oneSeries.getSeriesInstanceUID(), "101.101.1", oneSeries.getSeriesInstanceUID());
		}
		List<Item> items = series.get(0).getItems();
		int numbOfItems = items.size();
		assertEquals("Invalid number of items. Expected: 1, actual: " + numbOfItems, 1, numbOfItems);
		if(numbOfItems == 1) {
			Item item = items.get(0);
			assertEquals("Invalid item. Expected item id: 101.101.1.000.000.1, actual: " + item.getItemID(), "101.101.1.000.000.1", item.getItemID());
		}
	}

	//SearchResultTree 2A - basic flow. One SeriesNode selected.
	//Result: selectedDataSearchResult should contain one patient, one study, one selected series and all items found in selected Series.
	@Test
	public void testUpdatedSelectedDataSearchResult2A() {
		resultTree.addDataSelectionListener(this);
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		StudyNode studyNode2 = (StudyNode)patientNode1.getChildAt(1);
		SeriesNode seriesNode2 = (SeriesNode)studyNode2.getChildAt(0);
		resultTree.updateSelection(seriesNode2, true);
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
		assertEquals("Invalid number of studies. Expected: 1, actual: " + numbOfStudies, 1, numbOfStudies);
		if(numbOfStudies == 1) {
			Study study = studies.get(0);
			assertEquals("Invalid study. Expected studyInstanceUID: 202.202, actual: " + study.getStudyInstanceUID(), "202.202", study.getStudyInstanceUID());
		}
		List<Series> series = studies.get(0).getSeries();
		int numbOfSeries = series.size();
		assertEquals("Invalid number of series. Expected: 1, actual: " + numbOfSeries, 1, numbOfSeries);
		if(numbOfSeries == 1) {
			Series oneSeries = series.get(0);
			assertEquals("Invalid series. Expected seriesInstanceUID: 202.202.1, actual: " + oneSeries.getSeriesInstanceUID(), "202.202.1", oneSeries.getSeriesInstanceUID());
		}
		List<Item> items = series.get(0).getItems();
		int numbOfItems = items.size();
		assertEquals("Invalid number of items. Expected: 2, actual: " + numbOfItems, 2, numbOfItems);
		if(numbOfItems == 2) {
			Item item2 = items.get(0);
			assertEquals("Invalid item. Expected item id: 202.202.1.000.000.1, actual: " + item2.getItemID(), "202.202.1.000.000.1", item2.getItemID());
			Item item3 = items.get(1);
			assertEquals("Invalid item. Expected item id: 202.202.1.000.000.2, actual: " + item3.getItemID(), "202.202.1.000.000.2", item3.getItemID());
		}
	}
	
	//SearchResultTree 3A - basic flow. One StudyNode selected.
	//Result: selectedDataSearchResult should contain one patient, one selected study, and Series and items found in selected Study.
	@Test
	public void testUpdatedSelectedDataSearchResult3A() {
		resultTree.addDataSelectionListener(this);
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		StudyNode studyNode2 = (StudyNode)patientNode1.getChildAt(1);
		resultTree.updateSelection(studyNode2, true);
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
		assertEquals("Invalid number of studies. Expected: 1, actual: " + numbOfStudies, 1, numbOfStudies);
		if(numbOfStudies == 1) {
			Study study = studies.get(0);
			assertEquals("Invalid study. Expected studyInstanceUID: 202.202, actual: " + study.getStudyInstanceUID(), "202.202", study.getStudyInstanceUID());
		}
		List<Series> series = studies.get(0).getSeries();
		int numbOfSeries = series.size();
		assertEquals("Invalid number of series. Expected: 2, actual: " + numbOfSeries, 2, numbOfSeries);
		if(numbOfSeries == 2) {
			Series series2 = series.get(0);
			assertEquals("Invalid series. Expected seriesInstanceUID: 202.202.1, actual: " + series2.getSeriesInstanceUID(), "202.202.1", series2.getSeriesInstanceUID());
			Series series3 = series.get(1);
			assertEquals("Invalid series. Expected seriesInstanceUID: 303.303.1, actual: " + series3.getSeriesInstanceUID(), "303.303.1", series3.getSeriesInstanceUID());
		}
		List<Item> itemsSeries2 = series.get(0).getItems();
		int numbOfItemsSeries2 = itemsSeries2.size();
		assertEquals("Invalid number of items. Expected: 2, actual: " + numbOfItemsSeries2, 2, numbOfItemsSeries2);
		if(numbOfItemsSeries2 == 2) {
			Item item2 = itemsSeries2.get(0);
			assertEquals("Invalid item. Expected item id: 202.202.1.000.000.1, actual: " + item2.getItemID(), "202.202.1.000.000.1", item2.getItemID());
			Item item3 = itemsSeries2.get(1);
			assertEquals("Invalid item. Expected item id: 202.202.1.000.000.2, actual: " + item3.getItemID(), "202.202.1.000.000.2", item3.getItemID());
		}
		List<Item> itemsSeries3 = series.get(1).getItems();
		int numbOfItemsSeries3 = itemsSeries3.size();
		assertEquals("Invalid number of items. Expected: 2, actual: " + numbOfItemsSeries3, 2, numbOfItemsSeries3);
		if(numbOfItemsSeries3 == 2) {
			Item item4 = itemsSeries3.get(0);
			assertEquals("Invalid item. Expected item id: 303.303.1.000.000.1, actual: " + item4.getItemID(), "303.303.1.000.000.1", item4.getItemID());
			Item item5 = itemsSeries3.get(1);
			assertEquals("Invalid item. Expected item id: 303.303.1.000.000.2, actual: " + item5.getItemID(), "303.303.1.000.000.2", item5.getItemID());
		}
	}
	
	//SearchResultTree 4A - basic flow. One PatientNode selected.
	//Result: selectedDataSearchResult should contain one selected patient, and all studies, series and all items found in selected Patient.
	@Test
	public void testUpdatedSelectedDataSearchResult4A() {
		resultTree.addDataSelectionListener(this);
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
		assertEquals("Invalid number of studies. Expected: 2, actual: " + numbOfStudies, 2, numbOfStudies);
		if(numbOfStudies == 2) {
			Study study1 = studies.get(0);
			assertEquals("Invalid study. Expected studyInstanceUID: 101.101, actual: " + study1.getStudyInstanceUID(), "101.101", study1.getStudyInstanceUID());
			
			Study study2 = studies.get(1);
			assertEquals("Invalid study. Expected studyInstanceUID: 202.202, actual: " + study2.getStudyInstanceUID(), "202.202", study2.getStudyInstanceUID());
		}
		//Fully assert study1, assert number of series and items of study2. study2 is fully asserted in the previous test. 
		List<Series> series = studies.get(0).getSeries();
		int numbOfSeries = series.size();
		assertEquals("Invalid number of series. Expected: 1, actual: " + numbOfSeries, 1, numbOfSeries);
		if(numbOfSeries == 1) {
			Series series1 = series.get(0);
			assertEquals("Invalid series. Expected seriesInstanceUID: 101.101.1, actual: " + series1.getSeriesInstanceUID(), "101.101.1", series1.getSeriesInstanceUID());
		}
		List<Item> itemsSeries1 = series.get(0).getItems();
		int numbOfItemsSeries1 = itemsSeries1.size();
		assertEquals("Invalid number of items. Expected: 1, actual: " + numbOfItemsSeries1, 1, numbOfItemsSeries1);
		if(numbOfItemsSeries1 == 1) {
			Item item1 = itemsSeries1.get(0);
			assertEquals("Invalid item. Expected item id: 101.101.1.000.000.1, actual: " + item1.getItemID(), "101.101.1.000.000.1", item1.getItemID());
		}
		
		List<Item> itemsSeries2 = studies.get(1).getSeries().get(0).getItems();
		List<Item> itemsSeries3 = studies.get(1).getSeries().get(1).getItems();
		int numbOfItemsSeries2 = itemsSeries2.size();
		int numbOfItemsSeries3 = itemsSeries3.size();
		assertEquals("Invalid number of items in Series 2. Expected: 2, actual: " + numbOfItemsSeries2, 2, numbOfItemsSeries2);
		assertEquals("Invalid number of items in Series 3. Expected: 2, actual: " + numbOfItemsSeries3, 2, numbOfItemsSeries3);
	}
	
	//SearchResultTree 5A - basic flow. SelectAll set to TRUE.
	//Result: selectedDataSearchResult should contain all Patients and all other JTree items.
	@Test
	public void testUpdatedSelectedDataSearchResult5A() {
		resultTree.addDataSelectionListener(this);
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
		assertSelectAll(selectedDataSearchResult);
	}
	
	//Test selectedAll, deselect item, selectAll
	//SearchResultTree 6A - basic flow. SelectAll then deselect one itemNode and then selectAll again.
	//Result: selectedDataSearchResult should contain all Patients and all other JTree items.
	@Test
	public void testUpdatedSelectedDataSearchResult6A() {
		resultTree.addDataSelectionListener(this);
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
		DefaultMutableTreeNode rootNode = resultTree.getRootNode();
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)rootNode.getChildAt(0);
		PatientNode patientNode1 = (PatientNode)locationNode.getChildAt(0);
		StudyNode studyNode1 = (StudyNode)patientNode1.getChildAt(0);
		SeriesNode seriesNode1 = (SeriesNode)studyNode1.getChildAt(0);
		ItemNode itemNode1 = (ItemNode)seriesNode1.getChildAt(0);
		resultTree.updateSelection(itemNode1, false);
		synchronized(this){
			while(selectedDataSearchResult.getPatients().size() == 0){
				try {
					this.wait();
				} catch (InterruptedException e1) {
					logger.error(e1, e1);
				}
			}
		}
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
		assertSelectAll(selectedDataSearchResult);
	}

	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
		selectedDataSearchResult.setOriginalCriteria(searchResult.getOriginalCriteria());
		selectedDataSearchResult.setDataSourceDescription("Selected data for " + searchResult.getDataSourceDescription());
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
