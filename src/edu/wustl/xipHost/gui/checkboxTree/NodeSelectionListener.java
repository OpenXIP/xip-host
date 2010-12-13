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
	
	public void setSearchResultTree(SearchResultTree resultTree){
		this.resultTree = resultTree;
	}
	
	public void setSearchResult(SearchResult result){
		this.result = result;
	}
	
	public void setWasDoubleClick(boolean wasDoubleClick){
		this.wasDoubleClick = wasDoubleClick;
	}
	
	public void setSelectionCoordinates(int x, int y){
		this.x = x;
		this.y = y;
	}

	@Override
    public void actionPerformed(ActionEvent evt) {
		 if (wasDoubleClick) {
             wasDoubleClick = false; // reset flag
		 } else {
			//
    			selectedDataSearchResult.setOriginalCriteria(result.getOriginalCriteria());
    			selectedDataSearchResult.setDataSourceDescription("Selected data for " + result.getDataSourceDescription());
		     	int row = resultTree.getRowForLocation(x, y);
		     	TreePath  path = resultTree.getPathForRow(row);    	
		     	if (path != null) {    			
		     		DefaultMutableTreeNode node = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();							
		     		//System.out.println(this.getRowForPath(new TreePath(node.getPath())));
		     		//System.out.println("Checking set changed, leading path: " + e.getPath().toString());			    
		     		if (node == null) return;		 
		     		if (!node.isRoot()) {																	
		     			Object selectedNode = node.getUserObject();
		     			if(selectedNode instanceof Patient){
		     				PatientNode patientNode = (PatientNode)node;
		     				int studyCount = patientNode.getChildCount();
		     				if(patientNode.isSelected()){
		     					patientNode.setSelected(false);
		     					((PatientNode)node).getCheckBox().setSelected(false);
		     					Patient patient = null;
		     					if(patientNode.getUserObject() instanceof Patient){
		     						patient = (Patient)patientNode.getUserObject();
		     					}
		     					if(studyCount == 0){
		     						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		     						selectedDataSearchResult.removePatient(selectedPatient);
		     					}
		     					for(int i = 0; i < studyCount; i++){
		     						StudyNode studyNode = (StudyNode) patientNode.getChildAt(i);
		     						studyNode.setSelected(false);
		     						((StudyNode)studyNode).getCheckBox().setSelected(false);
		     						int seriesCount = studyNode.getChildCount();		     						
		     						Study study = null;
			     					if(studyNode.getUserObject() instanceof Study){
			     						study = (Study)studyNode.getUserObject();
			     					}
			     					if(seriesCount == 0){
		     							if(!selectedDataSearchResult.contains(patient.getPatientID())){
		     								
		     							} else if (selectedDataSearchResult.contains(patient.getPatientID())){
		     								Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
					     					Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		     								selectedPatient.removeStudy(selectedStudy);
			 								if(selectedPatient.getStudies().size() == 0){
			 									selectedDataSearchResult.removePatient(selectedPatient);
			 								}
		     							}
		     						}
		     						for(int j = 0; j < seriesCount; j++){
		     							SeriesNode seriesNode = (SeriesNode) studyNode.getChildAt(j);
		     							seriesNode.setSelected(false);	     					
				     					((SeriesNode)seriesNode).getCheckBox().setSelected(false);
				     					//Updating selectedDataSearchresult				     					
				     					Series series = null;
				     					if(seriesNode.getUserObject() instanceof Series){
				     						series = (Series)seriesNode.getUserObject();
				     					}
				     					Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
				     					Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		 								selectedStudy.removeSeries(series);
		 								if(selectedStudy.getSeries().size() == 0){
		 									selectedPatient.removeStudy(selectedStudy);
		 								}
		 								if(selectedPatient.getStudies().size() == 0){
		 									selectedDataSearchResult.removePatient(selectedPatient);
		 								}
		     						}
		     					}
		     				} else if(patientNode.isSelected() == false){    					
		     					patientNode.setSelected(true);	 
		     					((PatientNode)node).getCheckBox().setSelected(true);
		     					Patient patient = null;
		     					if(patientNode.getUserObject() instanceof Patient){
		     						patient = (Patient)patientNode.getUserObject();
		     					}
		     					if(studyCount == 0){
		     						Patient newPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
		     						newPatient.setLastUpdated(patient.getLastUpdated());
		     						selectedDataSearchResult.addPatient(newPatient);
		     					}
		     					for(int i = 0; i < studyCount; i++){
		     						StudyNode studyNode = (StudyNode) patientNode.getChildAt(i);
		     						studyNode.setSelected(true);
		     						((StudyNode)studyNode).getCheckBox().setSelected(true);
		     						int seriesCount = studyNode.getChildCount();
		     						Study study = null;
			     					if(studyNode.getUserObject() instanceof Study){
			     						study = (Study)studyNode.getUserObject();
			     					}
		     						if(seriesCount == 0){
		     							if(!selectedDataSearchResult.contains(patient.getPatientID())){
				     						Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
				     						newStudy.setLastUpdated(study.getLastUpdated());
				     						Patient newPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
				     						newPatient.setLastUpdated(patient.getLastUpdated());
				     						newPatient.addStudy(newStudy);
				     						selectedDataSearchResult.addPatient(newPatient);
				     					} else if (selectedDataSearchResult.contains(patient.getPatientID())){
				     						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
				     						if(!selectedPatient.contains(study.getStudyInstanceUID())){
				     							Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
					     						newStudy.setLastUpdated(study.getLastUpdated());					     						
					     						selectedPatient.addStudy(newStudy);
				     						} 
				     					}
		     						}
		     						for(int j = 0; j < seriesCount; j++){
		     							SeriesNode seriesNode = (SeriesNode) studyNode.getChildAt(j);
		     							seriesNode.setSelected(true);	     					
				     					((SeriesNode)seriesNode).getCheckBox().setSelected(true);
				     					//Update selectedDataSearchResult
				     					Series series = null;
				     					if(seriesNode.getUserObject() instanceof Series){
				     						series = (Series)seriesNode.getUserObject();
				     					}
				     					if(!selectedDataSearchResult.contains(patient.getPatientID())){
				     						Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
				     						newStudy.addSeries(series);
				     						newStudy.setLastUpdated(study.getLastUpdated());
				     						Patient newPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
				     						newPatient.setLastUpdated(patient.getLastUpdated());
				     						newPatient.addStudy(newStudy);
				     						selectedDataSearchResult.addPatient(newPatient);
				     					} else if (selectedDataSearchResult.contains(patient.getPatientID())){
				     						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
				     						if(!selectedPatient.contains(study.getStudyInstanceUID())){
				     							Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
					     						newStudy.setLastUpdated(study.getLastUpdated());
					     						newStudy.addSeries(series);
					     						selectedPatient.addStudy(newStudy);
				     						} else if (selectedPatient.contains(study.getStudyInstanceUID())){
				     							Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
				     							if(!selectedStudy.contains(series.getSeriesInstanceUID())){
				     								selectedStudy.addSeries(series);
				     							}
				     						}
				     					}
		     						}
		     					}
		     				}
		     				resultTree.repaint();
		     			} else if(selectedNode instanceof Study){
		     				StudyNode studyNode = (StudyNode)node;
		     				int seriesCount = studyNode.getChildCount();
		     				PatientNode patientNode = (PatientNode) studyNode.getParent();
		     				if(studyNode.isSelected()){
		     					studyNode.setSelected(false);
		     					((StudyNode)node).getCheckBox().setSelected(false);
		     					patientNode.setSelected(false);
		     					((PatientNode)patientNode).getCheckBox().setSelected(false);
		     					Patient patient = null;
		     					if(patientNode.getUserObject() instanceof Patient){
		     						patient = (Patient)patientNode.getUserObject();
		     					}
		     					Study study = null;
		     					if(studyNode.getUserObject() instanceof Study){
		     						study = (Study)studyNode.getUserObject();
		     					}
		     					if(seriesCount == 0){
	     							if(!selectedDataSearchResult.contains(patient.getPatientID())){
	     								
	     							} else if (selectedDataSearchResult.contains(patient.getPatientID())){
	     								Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
				     					Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
	     								selectedPatient.removeStudy(selectedStudy);
		 								if(selectedPatient.getStudies().size() == 0){
		 									selectedDataSearchResult.removePatient(selectedPatient);
		 								}
	     							}
	     						}
		     					for(int j = 0; j < seriesCount; j++){
	     							SeriesNode seriesNode = (SeriesNode) studyNode.getChildAt(j);
	     							seriesNode.setSelected(false);	     					
			     					((SeriesNode)seriesNode).getCheckBox().setSelected(false);
			     					//Updating selectedDataSearchresult			     								     					
			     					Series series = null;
			     					if(seriesNode.getUserObject() instanceof Series){
			     						series = (Series)seriesNode.getUserObject();
			     					}
			     					Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
			     					Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
	 								selectedStudy.removeSeries(series);
	 								if(selectedStudy.getSeries().size() == 0){
	 									selectedPatient.removeStudy(selectedStudy);
	 								}
	 								if(selectedPatient.getStudies().size() == 0){
	 									selectedDataSearchResult.removePatient(selectedPatient);
	 								}
	     						}
		     				} else if(studyNode.isSelected() == false){    					
		     					studyNode.setSelected(true);
		     					((StudyNode)node).getCheckBox().setSelected(true);
		     					Patient patient = null;
		     					if(patientNode.getUserObject() instanceof Patient){
		     						patient = (Patient)patientNode.getUserObject();
		     					}
		     					Study study = null;
		     					if(studyNode.getUserObject() instanceof Study){
		     						study = (Study)studyNode.getUserObject();
		     					}
		     					if(seriesCount == 0){
		     						if(!selectedDataSearchResult.contains(patient.getPatientID())){
			     						Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
			     						newStudy.setLastUpdated(study.getLastUpdated());
			     						Patient newPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
			     						newPatient.setLastUpdated(patient.getLastUpdated());
			     						newPatient.addStudy(newStudy);
			     						selectedDataSearchResult.addPatient(newPatient);
			     					} else if (selectedDataSearchResult.contains(patient.getPatientID())){
			     						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
			     						if(!selectedPatient.contains(study.getStudyInstanceUID())){
			     							Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
				     						newStudy.setLastUpdated(study.getLastUpdated());					     						
				     						selectedPatient.addStudy(newStudy);
			     						} 
			     					}
		     					}
		     					for(int j = 0; j < seriesCount; j++){
	     							SeriesNode seriesNode = (SeriesNode) studyNode.getChildAt(j);
	     							seriesNode.setSelected(true);	     					
			     					((SeriesNode)seriesNode).getCheckBox().setSelected(true);
			     					//Update selectedDataSearchResult
			     					Series series = null;
			     					if(seriesNode.getUserObject() instanceof Series){
			     						series = (Series)seriesNode.getUserObject();
			     					}
			     					if(!selectedDataSearchResult.contains(patient.getPatientID())){
			     						Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
			     						newStudy.addSeries(series);
			     						newStudy.setLastUpdated(study.getLastUpdated());
			     						Patient newPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
			     						newPatient.setLastUpdated(patient.getLastUpdated());
			     						newPatient.addStudy(newStudy);
			     						selectedDataSearchResult.addPatient(newPatient);
			     					} else if (selectedDataSearchResult.contains(patient.getPatientID())){
			     						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
			     						if(!selectedPatient.contains(study.getStudyInstanceUID())){
			     							Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
				     						newStudy.setLastUpdated(study.getLastUpdated());
				     						newStudy.addSeries(series);
				     						selectedPatient.addStudy(newStudy);
			     						} else if (selectedPatient.contains(study.getStudyInstanceUID())){
			     							Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
			     							if(!selectedStudy.contains(series.getSeriesInstanceUID())){
			     								selectedStudy.addSeries(series);
			     							}
			     						}
			     					}
	     						}
		     					//Check if all other studies selected. If yes, set patientNode check box to seleted.
		     					int studyCount = patientNode.getChildCount();
		     					boolean allStudiesSelected = true;
		     					for (int i = 0; i < studyCount; i++){
		     						StudyNode studyNodeOther = (StudyNode) patientNode.getChildAt(i);
		     						if(studyNodeOther.isSelected() == false){
		     							allStudiesSelected = false;
		     						}
		     					}
		     					if(allStudiesSelected){
		     						patientNode.setSelected(true);
			     					((PatientNode)patientNode).getCheckBox().setSelected(true);
		     					}
		     				}
		     				resultTree.repaint();
		     			} else if(selectedNode instanceof Series){				
		     				SeriesNode seriesNode = (SeriesNode)node;
		     				StudyNode studyNode = (StudyNode) node.getParent();
		     				Study study = null;
		     				if(studyNode.getUserObject() instanceof Study){
		     					study = (Study)studyNode.getUserObject();
		     				}	     				
		     				if(seriesNode.isSelected()){
		     					seriesNode.setSelected(false);
		     					((SeriesNode)node).getCheckBox().setSelected(false);
		     					studyNode.setSelected(false);
		     					((StudyNode)studyNode).getCheckBox().setSelected(false);
		     					PatientNode patientNode = (PatientNode) studyNode.getParent();
		     					patientNode.setSelected(false);
		     					((PatientNode)patientNode).getCheckBox().setSelected(false);
		     					//Updating selectedDataSearchresult
		     					Patient patient = null;
		     					if(patientNode.getUserObject() instanceof Patient){
		     						patient = (Patient)patientNode.getUserObject();
		     					}
		     					Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		     					Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		     					selectedStudy.removeSeries((Series)selectedNode);
 								if(selectedStudy.getSeries().size() == 0){
 									selectedPatient.removeStudy(selectedStudy);
 								}
 								if(selectedPatient.getStudies().size() == 0){
 									selectedDataSearchResult.removePatient(selectedPatient);
 								}
		     				}else if(seriesNode.isSelected() == false){    					
		     					seriesNode.setSelected(true);	     					
		     					((SeriesNode)node).getCheckBox().setSelected(true);
		     					//Check if all series selected for this study. If yes, set study selected
		     					int seriesCount = studyNode.getChildCount();
		     					boolean allSeriesSelected = true;
		     					for (int i = 0; i < seriesCount; i++){
		     						SeriesNode seriesNodeOther = (SeriesNode) studyNode.getChildAt(i);
		     						if(seriesNodeOther.isSelected() == false){
		     							allSeriesSelected = false;
		     						}
		     					}
		     					if(allSeriesSelected){
		     						studyNode.setSelected(true);
			     					((StudyNode)studyNode).getCheckBox().setSelected(true);
		     					}
		     					// check if all studies selected for this patient. If yes, select this patient
		     					PatientNode patientNode = (PatientNode) studyNode.getParent();
		     					int studyCount = patientNode.getChildCount();
		     					boolean allStudiesSelected = true;
		     					for (int i = 0; i < studyCount; i++){
		     						StudyNode studyNodeOther = (StudyNode) patientNode.getChildAt(i);
		     						if(studyNodeOther.isSelected() == false){
		     							allStudiesSelected = false;
		     						}
		     					}
		     					if(allStudiesSelected){
		     						patientNode.setSelected(true);
			     					((PatientNode)patientNode).getCheckBox().setSelected(true);
		     					}
		     					//Update selectedDataSearchresult
		     					Patient patient = null;
		     					if(patientNode.getUserObject() instanceof Patient){
		     						patient = (Patient)patientNode.getUserObject();
		     					}
		     					if(!selectedDataSearchResult.contains(patient.getPatientID())){
		     						Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
		     						newStudy.addSeries((Series)selectedNode);
		     						newStudy.setLastUpdated(study.getLastUpdated());
		     						Patient newPatient = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
		     						newPatient.setLastUpdated(patient.getLastUpdated());
		     						newPatient.addStudy(newStudy);
		     						selectedDataSearchResult.addPatient(newPatient);
		     					} else if (selectedDataSearchResult.contains(patient.getPatientID())){
		     						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
		     						if(!selectedPatient.contains(study.getStudyInstanceUID())){
		     							Study newStudy = new Study(study.getStudyDate(), study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
			     						newStudy.setLastUpdated(study.getLastUpdated());
			     						newStudy.addSeries((Series)selectedNode);
			     						selectedPatient.addStudy(newStudy);
		     						} else if (selectedPatient.contains(study.getStudyInstanceUID())){
		     							Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
		     							if(!selectedStudy.contains(((Series)selectedNode).getSeriesInstanceUID())){
		     								selectedStudy.addSeries((Series)selectedNode);
		     							}
		     						}
		     					}
		     				}
		     			}
		     			if(logger.isDebugEnabled()){
	     					List<Patient> patients = selectedDataSearchResult.getPatients();
	     					logger.debug("Value of selectedDataSearchresult: ");
	     					for(Patient logPatient : patients){
	     						logger.debug(logPatient.toString());
	     						List<Study> studies = logPatient.getStudies();
	     						for(Study logStudy : studies){
	     							logger.debug("   " + logStudy.toString());
	     							List<Series> series = logStudy.getSeries();
	     							for(Series logSeries : series){
	     								logger.debug("      " + logSeries.toString());
	     							}
	     						}
	     					}
	     				}
	     				resultTree.repaint();	     				   			
		     		} else {

		     		}
		     	} 
		     	
		     	//
		 	}
		 	notifyDataSelectionChanged(selectedDataSearchResult);
		}
	
	DataSelectionListener listener;
    public void addDataSelectionListener(DataSelectionListener l) {        
        listener = l;          
    }
	
	void notifyDataSelectionChanged(SearchResult selectedDataSearchResult){
		DataSelectionEvent event = new DataSelectionEvent(selectedDataSearchResult);
		listener.dataSelectionChanged(event);
	}
}



