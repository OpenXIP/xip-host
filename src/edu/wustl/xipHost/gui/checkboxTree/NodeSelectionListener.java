/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;

import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

/**
 * @author Jaroslaw Krych
 * 
 */
public class NodeSelectionListener implements ActionListener {
	final static Logger logger = Logger.getLogger(NodeSelectionListener.class);
	SearchResult selectedDataSearchResult;
	SearchResult result;
	SearchResultTree resultTree;
	boolean wasDoubleClick = false;
	int x;
	int y;

	/**
	 * 
	 */
	public NodeSelectionListener() {
		selectedDataSearchResult = new SearchResult();
	}
	
	public void resetSelectedDataSearchResult(){
		selectedDataSearchResult = new SearchResult();
	}

	public void setSearchResultTree(SearchResultTree resultTree) {
		this.resultTree = resultTree;
	}

	public void setSearchResult(SearchResult result) {
		this.result = result;
	}

	public void setWasDoubleClick(boolean wasDoubleClick) {
		this.wasDoubleClick = wasDoubleClick;
	}

	public void setSelectionCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (wasDoubleClick) {
			wasDoubleClick = false; 	//Reset flag
		} else {
			selectedDataSearchResult.setOriginalCriteria(result.getOriginalCriteria());
			selectedDataSearchResult.setDataSourceDescription("Selected data for " + result.getDataSourceDescription());
			int row = resultTree.getRowForLocation(x, y);
			TreePath path = resultTree.getPathForRow(row);
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) resultTree.getLastSelectedPathComponent();
				if (node == null)
					return;
				if (!node.isRoot()) {
					Object selectedNode = node.getUserObject();
					if (selectedNode instanceof Patient) {
						PatientNode patientNode = (PatientNode) node;						
						boolean selected = !patientNode.isSelected();
						Patient patient = null;
						if (patientNode.getUserObject() instanceof Patient) {
							patient = (Patient) patientNode.getUserObject();
						}
						updateSelection(patientNode, selected);
						updateSelectedDataSearchResult(patient, selected);
						//Reset Series flag containsSubsetOfData 
						int patientChildCount = patientNode.getChildCount();
						if(patientChildCount > 0){
							for(int i = 0; i < patientChildCount; i++){
								DefaultMutableTreeNode patientChildNode = (DefaultMutableTreeNode)patientNode.getChildAt(i);
								StudyNode studyNode = (StudyNode)patientChildNode;
								Study study =(Study)studyNode.getUserObject();
								int studyChildCount = studyNode.getChildCount();
								if(studyChildCount > 0){
									for(int j = 0; j < studyChildCount; j++){
										DefaultMutableTreeNode studyChildNode = (DefaultMutableTreeNode)studyNode.getChildAt(j);										
										SeriesNode seriesNode = (SeriesNode)studyChildNode;
										Series series = (Series)seriesNode.getUserObject();
										int seriesChildCount = seriesNode.getChildCount();
										boolean allSeriesChildrenSelected = true;
										for(int m = 0; m < seriesChildCount; m++){
											DefaultMutableTreeNode childSeriesNode = (DefaultMutableTreeNode) seriesNode.getChildAt(m);
											if (childSeriesNode.getUserObject() instanceof Item) {
												ItemNode otherItemNode = (ItemNode) childSeriesNode;
												if(otherItemNode.isSelected() == false){
													allSeriesChildrenSelected = false;
													break;
												}
											}
										}						
										boolean subsetOfItems = !allSeriesChildrenSelected;
										setSeriesDatasetFlag(series, study, patient, subsetOfItems);
									}									
								}								
							}
						}						
					} else if (selectedNode instanceof Study) {
						StudyNode studyNode = (StudyNode) node;
						boolean selected = !studyNode.isSelected();
						PatientNode patientNode = (PatientNode) studyNode.getParent();
						Patient patient = null;
						if (patientNode.getUserObject() instanceof Patient) {
							patient = (Patient) patientNode.getUserObject();
						}
						updateSelection(studyNode, patientNode, selected);
						Study study = null;
						if (studyNode.getUserObject() instanceof Study) {
							study = (Study) studyNode.getUserObject();
						}
						int studyChildCount = studyNode.getChildCount();
						if(studyChildCount > 0){
							for(int i = 0; i < studyChildCount; i++){
								DefaultMutableTreeNode studyChildNode = (DefaultMutableTreeNode)studyNode.getChildAt(i);
								if(studyChildNode.getUserObject() instanceof Series){
									Series series = (Series)studyChildNode.getUserObject();
									updateSelectedDataSearchResult(series, study, patient, selected);
									//Reset Series flag containsSubsetOfData 
									SeriesNode seriesNode = (SeriesNode)studyChildNode;
									int seriesChildCount = seriesNode.getChildCount();
									boolean allSeriesChildrenSelected = true;
									for(int j = 0; j < seriesChildCount; j++){
										DefaultMutableTreeNode childSeriesNode = (DefaultMutableTreeNode) seriesNode.getChildAt(j);
										if (childSeriesNode.getUserObject() instanceof Item) {
											ItemNode otherItemNode = (ItemNode) childSeriesNode;
											if(otherItemNode.isSelected() == false){
												allSeriesChildrenSelected = false;
												break;
											}
										}
									}						
									boolean subsetOfItems = !allSeriesChildrenSelected;
									setSeriesDatasetFlag(series, study, patient, subsetOfItems);
								} else {
									
								}
							}
						} else if (studyChildCount == 0){
							updateSelectedDataSearchResult(study, patient, selected);
						}						
					} else if (selectedNode instanceof Series) {
						SeriesNode seriesNode = (SeriesNode) node;
						boolean selected = !seriesNode.isSelected();
						Series series = null;
						if(seriesNode.getUserObject() instanceof Series){
							series = (Series)seriesNode.getUserObject();							
						}
						StudyNode studyNode = (StudyNode) node.getParent();
						Study study = null;
						if (studyNode.getUserObject() instanceof Study) {
							study = (Study) studyNode.getUserObject();
						}
						PatientNode patientNode = (PatientNode)studyNode.getParent();
						Patient patient = null;
						if(patientNode.getUserObject() instanceof Patient){
							patient = (Patient)patientNode.getUserObject();
						}
						updateSelection(seriesNode, studyNode, selected);
						updateSelectedDataSearchResult(series, study, patient, selected);
						//Reset Series flag containsSubsetOfData 
						int seriesChildCount = seriesNode.getChildCount();
						boolean allSeriesChildrenSelected = true;
						for(int i = 0; i < seriesChildCount; i++){
							DefaultMutableTreeNode childSeriesNode = (DefaultMutableTreeNode) seriesNode.getChildAt(i);
							if (childSeriesNode.getUserObject() instanceof Item) {
								ItemNode otherItemNode = (ItemNode) childSeriesNode;
								if(otherItemNode.isSelected() == false){
									allSeriesChildrenSelected = false;
									break;
								}
							}
						}						
						boolean subsetOfItems = !allSeriesChildrenSelected;
						setSeriesDatasetFlag(series, study, patient, subsetOfItems);
					} else if (selectedNode instanceof Item) {
						ItemNode itemNode = (ItemNode) node;
						boolean selected = !itemNode.isSelected();
						SeriesNode seriesNode = (SeriesNode)itemNode.getParent();
						updateSelection(itemNode, seriesNode, selected);
						Item item = (Item) node.getUserObject();
						Series series = (Series)seriesNode.getUserObject();
						StudyNode studyNode = (StudyNode)seriesNode.getParent();
						Study study = (Study)studyNode.getUserObject();
						PatientNode patientNode = (PatientNode)studyNode.getParent();
						Patient patient = (Patient)patientNode.getUserObject();
						updateSelectedDataSearchResult(item, series, study, patient, selected);
						int seriesChildCount = seriesNode.getChildCount();
						boolean allSeriesChildrenSelected = true;
						for(int i = 0; i < seriesChildCount; i++){
							DefaultMutableTreeNode childSeriesNode = (DefaultMutableTreeNode) seriesNode.getChildAt(i);
							if (childSeriesNode.getUserObject() instanceof Item) {
								ItemNode otherItemNode = (ItemNode) childSeriesNode;
								if(otherItemNode.isSelected() == false){
									allSeriesChildrenSelected = false;
									break;
								}
							}
						}						
						boolean subsetOfItems = !allSeriesChildrenSelected;
						setSeriesDatasetFlag(series, study, patient, subsetOfItems);						
					}
				}
			}
			resultTree.repaint();
			if (logger.isDebugEnabled()) {
				List<Patient> patients = selectedDataSearchResult.getPatients();
				logger.debug("Value of selectedDataSearchresult: ");
				for (Patient logPatient : patients) {
					logger.debug(logPatient.toString());
					List<Study> studies = logPatient.getStudies();
					for (Study logStudy : studies) {
						logger.debug("   " + logStudy.toString());
						List<Series> series = logStudy.getSeries();
						for (Series logSeries : series) {
							logger.debug("      " + logSeries.toString() + " Subset of items: " + logSeries.containsSubsetOfItems());
							List<Item> items = logSeries.getItems();
							for(Item logItem : items){
								logger.debug("         " + logItem.toString());
							}
						}
					}
				}
			}
			notifyDataSelectionChanged(selectedDataSearchResult);
		}
	}
	
	
	void updateSelection(PatientNode patientNode, boolean selected){
		int patientChildCount = patientNode.getChildCount();
		patientNode.setSelected(selected);
		patientNode.getCheckBox().setSelected(selected);						
		for(int i = 0; i < patientChildCount; i++){
			DefaultMutableTreeNode childPatientNode = (DefaultMutableTreeNode) patientNode.getChildAt(i);
			if (childPatientNode.getUserObject() instanceof Item) {
				ItemNode itemNode = (ItemNode) childPatientNode;
				updateSelection(itemNode, patientNode, selected);
			} else if (childPatientNode.getUserObject() instanceof Study) {
				StudyNode studyNode = (StudyNode) patientNode.getChildAt(i);
				updateSelection(studyNode, patientNode, selected);
			}								
		}
		resultTree.repaint();
	}
	
	void updateSelection(StudyNode studyNode, PatientNode parentOfStudyNode, boolean selected){	
		studyNode.setSelected(selected);
		studyNode.getCheckBox().setSelected(selected);
		int studyChildCount = studyNode.getChildCount();
		for (int j = 0; j < studyChildCount; j++) {
			DefaultMutableTreeNode childStudyNode = (DefaultMutableTreeNode) studyNode.getChildAt(j);
			if(childStudyNode.getUserObject() instanceof Series){
				SeriesNode seriesNode = (SeriesNode) studyNode.getChildAt(j);
				updateSelection(seriesNode, studyNode, selected);
			} else if (childStudyNode.getUserObject() instanceof Item){
				ItemNode itemNode = (ItemNode) childStudyNode;
				updateSelection(itemNode, studyNode, selected);
			}
		}
		//Select PatientNode if all StudyNodes and ItemNodes for this PatientNode are selected
		PatientNode patientNode = parentOfStudyNode;
		int patientChildCount = patientNode.getChildCount();
		boolean allPatientChildrenSelected = true;
		for(int i = 0; i < patientChildCount; i++){
			DefaultMutableTreeNode childPatientNode = (DefaultMutableTreeNode) patientNode.getChildAt(i);
			if (childPatientNode.getUserObject() instanceof Item) {
				ItemNode itemNode = (ItemNode)childPatientNode;
				if(itemNode.isSelected() == false){
					allPatientChildrenSelected = false;
					break;
				}
			} else if (childPatientNode.getUserObject() instanceof Study){
				StudyNode otherStudyNode = (StudyNode)childPatientNode;
				if(otherStudyNode.isSelected() == false){
					allPatientChildrenSelected = false;
					break;
				}
			}
		}
		if(allPatientChildrenSelected){
			patientNode.setSelected(true);
			patientNode.getCheckBox().setSelected(true);	
		} else {
			patientNode.setSelected(false);
			patientNode.getCheckBox().setSelected(false);
		}
		resultTree.repaint();
	}
	
	void updateSelection(SeriesNode seriesNode, StudyNode parentOfSeriesNode, boolean selected){
		seriesNode.setSelected(selected);
		seriesNode.getCheckBox().setSelected(selected);
		int seriesChildCount = seriesNode.getChildCount();
		for(int k = 0; k < seriesChildCount; k++){
			DefaultMutableTreeNode childSeriesNode = (DefaultMutableTreeNode) seriesNode.getChildAt(k);
			ItemNode itemNode = (ItemNode) childSeriesNode;
			updateSelection(itemNode, seriesNode, selected);
		}
		//Update StudyNode if all SeriesNodes and ItemNodes for this StudyNode are selected
		StudyNode studyNode = parentOfSeriesNode;
		int studyChildCount = studyNode.getChildCount();
		boolean allStudyChildrenSelected = true;
		for(int i = 0; i < studyChildCount; i++){
			DefaultMutableTreeNode childStudyNode = (DefaultMutableTreeNode) studyNode.getChildAt(i);
			if(childStudyNode.getUserObject() instanceof Item){
				ItemNode itemNode = (ItemNode)childStudyNode;
				if(itemNode.isSelected() == false){
					allStudyChildrenSelected = false;
					break;
				}
			} else if (childStudyNode.getUserObject() instanceof Series){
				SeriesNode otherSeriesNode = (SeriesNode)childStudyNode;
				if(otherSeriesNode.isSelected() == false){
					allStudyChildrenSelected = false;
					break;
				}
			}
		}
		if(allStudyChildrenSelected){
			studyNode.setSelected(true);
			studyNode.getCheckBox().setSelected(true);
		} else {
			studyNode.setSelected(false);
			studyNode.getCheckBox().setSelected(false);
		}
		//Update PatientNode if all StudyNodes and ItemNodes for this PatientNode are selected
		PatientNode patientNode = (PatientNode)studyNode.getParent();
		int patientChildCount = patientNode.getChildCount();
		boolean allPatientChildrenSelected = true;
		for(int i = 0; i < patientChildCount; i++){
			DefaultMutableTreeNode childPatientNode = (DefaultMutableTreeNode) patientNode.getChildAt(i);
			if (childPatientNode.getUserObject() instanceof Item) {
				ItemNode itemNode = (ItemNode)childPatientNode;
				if(itemNode.isSelected() == false){
					allPatientChildrenSelected = false;
					break;
				}
			} else if (childPatientNode.getUserObject() instanceof Study){
				StudyNode otherStudyNode = (StudyNode)childPatientNode;
				if(otherStudyNode.isSelected() == false){
					allPatientChildrenSelected = false;
					break;
				}
			}
		}
		if(allPatientChildrenSelected){
			patientNode.setSelected(true);
			patientNode.getCheckBox().setSelected(true);	
		} else {
			patientNode.setSelected(false);
			patientNode.getCheckBox().setSelected(false);
		}
		resultTree.repaint();
	}
	
	void updateSelection(ItemNode itemNode, SeriesNode seriesNode, boolean selected){
		itemNode.setSelected(selected);
		itemNode.getCheckBox().setSelected(selected);
		int seriesChildCount = seriesNode.getChildCount();
		boolean allSeriesChildrenSelected = true;
		for(int i = 0; i < seriesChildCount; i++){
			DefaultMutableTreeNode childSeriesNode = (DefaultMutableTreeNode) seriesNode.getChildAt(i);
			if(childSeriesNode.getUserObject() instanceof Item){
				ItemNode otherItemNode = (ItemNode)childSeriesNode;
				if(otherItemNode.isSelected() == false){
					allSeriesChildrenSelected = false;
					break;
				}
			} 
		}
		StudyNode studyNode = (StudyNode)seriesNode.getParent();
		ItemNode emptyItemNode = null;
		if(allSeriesChildrenSelected){
			seriesNode.setSelected(true);
			seriesNode.getCheckBox().setSelected(true);
			updateSelection(emptyItemNode, studyNode, true);
		} else {
			seriesNode.setSelected(false);
			seriesNode.getCheckBox().setSelected(false);
			updateSelection(emptyItemNode, studyNode, false);
		}
		resultTree.repaint();
	}
	
	void updateSelection(ItemNode itemNode, StudyNode studyNode, boolean selected){
		if(itemNode != null){
			itemNode.setSelected(selected);
			itemNode.getCheckBox().setSelected(selected);
		}		
		int studyChildCount = studyNode.getChildCount();
		boolean allStudyChildrenSelected = true;
		for(int i = 0; i < studyChildCount; i++){
			DefaultMutableTreeNode childStudyNode = (DefaultMutableTreeNode) studyNode.getChildAt(i);
			if(childStudyNode.getUserObject() instanceof Item){
				ItemNode otherItemNode = (ItemNode)childStudyNode;
				if(otherItemNode.isSelected() == false){
					allStudyChildrenSelected = false;
					break;
				}
			} else if (childStudyNode.getUserObject() instanceof Series){
				SeriesNode seriesNode = (SeriesNode)childStudyNode;
				if(seriesNode.isSelected() == false){
					allStudyChildrenSelected = false;
					break;
				}
			}
		}
		PatientNode patientNode = (PatientNode)studyNode.getParent();
		ItemNode emptyItemNode = null;
		if(allStudyChildrenSelected){
			studyNode.setSelected(true);
			studyNode.getCheckBox().setSelected(true);
			updateSelection(emptyItemNode, patientNode, true);
		} else {
			studyNode.setSelected(false);
			studyNode.getCheckBox().setSelected(false);
			updateSelection(emptyItemNode, patientNode, true);
		}
		resultTree.repaint();
	}
	
	void updateSelection(ItemNode itemNode, PatientNode patientNode, boolean selected){
		if(itemNode != null){
			itemNode.setSelected(selected);
			itemNode.getCheckBox().setSelected(selected);
		}		
		int patientChildCount = patientNode.getChildCount();
		boolean allPatientChildrenSelected = true;
		for(int i = 0; i < patientChildCount; i++){
			DefaultMutableTreeNode childPatientNode = (DefaultMutableTreeNode) patientNode.getChildAt(i);
			if(childPatientNode.getUserObject() instanceof Item){
				ItemNode otherItemNode = (ItemNode)childPatientNode;
				if(otherItemNode.isSelected() == false){
					allPatientChildrenSelected = false;
					break;
				}
			} else if (childPatientNode.getUserObject() instanceof Study){
				StudyNode studyNode = (StudyNode)childPatientNode;
				if(studyNode.isSelected() == false){
					allPatientChildrenSelected = false;
					break;
				}
			}
		}
		if(allPatientChildrenSelected){
			patientNode.setSelected(true);
			patientNode.getCheckBox().setSelected(true);
		} else {
			patientNode.setSelected(false);
			patientNode.getCheckBox().setSelected(false);
		}
		resultTree.repaint();
	}
	
	
	void updateSelectedDataSearchResult(Patient patient, boolean selected){
		if (selected == false && selectedDataSearchResult.contains(patient.getPatientID())){
			Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
			selectedDataSearchResult.removePatient(selectedPatient);
			return;	
		} else if (selected == true){
			List<Item> items = patient.getItems();
			List<Study> studies = patient.getStudies();
			Patient selectedPatient = null;
			if(!selectedDataSearchResult.contains(patient.getPatientID())){
				selectedPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
				selectedPatient.setLastUpdated(patient.getLastUpdated());
				selectedDataSearchResult.addPatient(selectedPatient);		
			} else {
				selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
			}
			for (int i = 0; i < items.size(); i++) {	
				Item item = items.get(i);									
				/*
				if (item instanceof XDSDocumentItem) {
					XDSDocumentItem xdsItem = (XDSDocumentItem) item;	
					XDSDocumentItem newItem = new XDSDocumentItem(
							xdsItem.getItemID(),
							xdsItem.getAvailability(),
							xdsItem.getLanguage(),
							xdsItem.getMimeType(),
							xdsItem.getDocumentType(),
							xdsItem.getPatientId(),
							xdsItem.getHomeCommunityId());
					newItem.setObjectDescriptor(xdsItem.getObjectDescriptor());
					newItem.setObjectLocator(xdsItem.getObjectLocator());
				}*/
				if (item != null) {
					selectedPatient.addItem(item);
				}				
			}
			for (int i = 0; i < studies.size(); i++) {	
				Study study = studies.get(i);					
				if (study != null) {
					if(!selectedPatient.contains(study.getStudyInstanceUID())){
						selectedPatient.addStudy(study);						
					} else {
						updateSelectedDataSearchResult(study, selectedPatient, selected);
					}
				}
			}
		}
	}
	
	void updateSelectedDataSearchResult(Study study, Patient parentOfStudy, boolean selected){
		Patient patient = parentOfStudy;
		Patient selectedPatient = null;
		if(selectedDataSearchResult.contains(patient.getPatientID())){
			selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		} else {
			selectedPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
			selectedPatient.setLastUpdated(patient.getLastUpdated());
			selectedDataSearchResult.addPatient(selectedPatient);
		}
		Study selectedStudy = null;
		if (selected == false){						
			if(selectedPatient.contains(study.getStudyInstanceUID())){
				selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
				selectedPatient.removeStudy(selectedStudy);
			}
			if (selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0) {
				selectedDataSearchResult.removePatient(selectedPatient);
			}
			return;		
		} else if (selected == true){
			if(!selectedPatient.contains(study.getStudyInstanceUID())){
				selectedStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
				selectedStudy.setLastUpdated(study.getLastUpdated());				
				selectedPatient.addStudy(selectedStudy);
			} else {
				selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
			}
			List<Item> items = study.getItems();
			for (int i = 0; i < items.size(); i++) {	
				Item item = items.get(i);									
				if (item != null) {
					if(!selectedStudy.containsItem(item.getItemID())){
						selectedStudy.addItem(item);
					}					
				}				
			}
			//Update Series found for selectedStudy
			List<Series> series = study.getSeries();
			for (int i = 0; i < series.size(); i++) {	
				Series oneSeries = series.get(i);
				if(oneSeries != null){
					updateSelectedDataSearchResult(oneSeries, selectedStudy, selectedPatient, selected);
				}
			}
		}
	}
	
	void updateSelectedDataSearchResult(Series series, Study study, Patient patient, boolean selected){
		Patient selectedPatient = null;
		if(selectedDataSearchResult.contains(patient.getPatientID())){
			selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		} else {
			selectedPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
			selectedPatient.setLastUpdated(patient.getLastUpdated());
			selectedDataSearchResult.addPatient(selectedPatient);
		}
		Study selectedStudy = null;
		if(selectedPatient.contains(study.getStudyInstanceUID())){
			selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		} else {
			selectedStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
			selectedStudy.setLastUpdated(study.getLastUpdated());
			selectedPatient.addStudy(selectedStudy);
		}
		Series selectedSeries = null;
		if (selected == false){	
			if(selectedStudy.contains(series.getSeriesInstanceUID())){
				selectedSeries = selectedStudy.getSeries(series.getSeriesInstanceUID());
				selectedStudy.removeSeries(selectedSeries);
			}
			if (selectedStudy.getSeries().size() == 0 && selectedStudy.getItems().size() == 0) {
				selectedPatient.removeStudy(selectedStudy);
			}
			if (selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0) {
				selectedDataSearchResult.removePatient(selectedPatient);
			}
			return;		
		} else if (selected == true){
			if(!selectedStudy.contains(series.getSeriesInstanceUID())){
				selectedSeries = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
				selectedSeries.setLastUpdated(series.getLastUpdated());
				selectedStudy.addSeries(selectedSeries);
			} else {
				selectedSeries = selectedStudy.getSeries(series.getSeriesInstanceUID());
			}
			List<Item> items = series.getItems();
			for (int i = 0; i < items.size(); i++) {	
				Item item = items.get(i);									
				if (item != null) {
					if(!selectedSeries.contains(item.getItemID())){
						selectedSeries.addItem(item);
					}					
				}				
			}
		}
	}
	
	void updateSelectedDataSearchResult(Item item, Series series, Study study, Patient patient, boolean selected){
		Patient selectedPatient = null;
		if(selectedDataSearchResult.contains(patient.getPatientID())){
			selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		} else {
			selectedPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
			selectedPatient.setLastUpdated(patient.getLastUpdated());
			selectedDataSearchResult.addPatient(selectedPatient);
		}
		Study selectedStudy = null;
		if(selectedPatient.contains(study.getStudyInstanceUID())){
			selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		} else {
			selectedStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
			selectedStudy.setLastUpdated(study.getLastUpdated());
			selectedPatient.addStudy(selectedStudy);
		}
		Series selectedSeries = null;
		if(selectedStudy.contains(series.getSeriesInstanceUID())){
			selectedSeries = selectedStudy.getSeries(series.getSeriesInstanceUID());
		} else {
			selectedSeries = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
			selectedSeries.setLastUpdated(series.getLastUpdated());
			selectedStudy.addSeries(selectedSeries);
		}
		if (selected == false){	
			Item selectedItem = null;
			if(selectedSeries.contains(item.getItemID())){
				selectedItem = selectedSeries.getItem(item.getItemID());
				selectedSeries.removeItem(selectedItem);
			}
			if(selectedSeries.getItems().size() == 0){
				selectedStudy.removeSeries(selectedSeries);
			}
			if (selectedStudy.getSeries().size() == 0 && selectedStudy.getItems().size() == 0) {
				selectedPatient.removeStudy(selectedStudy);
			}
			if (selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0) {
				selectedDataSearchResult.removePatient(selectedPatient);
			}
			return;		
		} else if (selected == true){
			if(!selectedSeries.contains(item.getItemID())){
				Item newItem = new ImageItem(item.getItemID());			
				newItem.setObjectDescriptor(item.getObjectDescriptor());
				newItem.setObjectLocator(item.getObjectLocator());
				selectedSeries.addItem(newItem);
			}
		}
	}
	
	synchronized  void setSeriesDatasetFlag(Series series, Study study, Patient patient, boolean subsetOfItems){
		Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		if(selectedPatient == null){
			return;
		}
		Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		if(selectedStudy == null){
			return;
		}		
		Series selectedSeries = selectedStudy.getSeries(series.getSeriesInstanceUID());
		if(selectedSeries == null){
			return;
		} else {
			selectedSeries.setContainsSubsetOfItems(subsetOfItems);
		}		
	}
	
	DataSelectionListener listener;
	public void addDataSelectionListener(DataSelectionListener l) {
		listener = l;
	}

	void notifyDataSelectionChanged(SearchResult selectedDataSearchResult) {
		DataSelectionEvent event = new DataSelectionEvent(selectedDataSearchResult);
		listener.dataSelectionChanged(event);
	}
}
 
