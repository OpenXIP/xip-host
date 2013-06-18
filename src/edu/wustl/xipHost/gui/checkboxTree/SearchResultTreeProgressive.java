/*
Copyright (c) 2013, Washington University in St.Louis.
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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.gui.checkboxTree.SeriesNode;

/**
 * @author Jaroslaw Krych
 *
 */
public class SearchResultTreeProgressive extends SearchResultTree {

	public SearchResultTreeProgressive(){
		super();
	}
	
	public void updateNodes(SearchResult result) {
		firePropertyChange(JTree.ROOT_VISIBLE_PROPERTY, !isRootVisible(), isRootVisible());
		if(result == null){			
			rootNode.removeAllChildren();
			treeModel.reload(rootNode);
			return;
		}
		DefaultMutableTreeNode locationNode;
		if(rootNode.getChildCount() == 0){
			locationNode = new DefaultMutableTreeNode(result.getDataSourceDescription());
			for(int i = 0; i < result.getPatients().size(); i++){
				final Patient patient = result.getPatients().get(i);
				PatientNode patientNode = new PatientNode(patient){
					public String toString(){															
						String patientDesc = patient.toString();
						if(patientDesc == null){
							patientDesc = "";
						}else{
							
						}	
						return patientDesc;						
					}
					public Patient getUserObject(){
						return patient;
					}					
				};
				patientNode.addNodeSelectionListener(this);
				locationNode.add(patientNode);
			}
		} else {
			locationNode = (DefaultMutableTreeNode) rootNode.getFirstChild();
			int numOfPatientNodes = locationNode.getChildCount();
			PatientNode patientNode = null;
			for(int i = 0; i < result.getPatients().size(); i++){
				final Patient patient = result.getPatients().get(i);
				for(int a = 0; a < numOfPatientNodes; a++){
					PatientNode existingPatientNode = (PatientNode) locationNode.getChildAt(a);					
					Patient existingPatient = (Patient) existingPatientNode.getUserObject();
					if(patient.equals(existingPatient)){
						patientNode = existingPatientNode;
						int numOfStudyNodes = patientNode.getChildCount();
						if(numOfStudyNodes == 0){
							for(int j = 0; j < patient.getStudies().size(); j++){
								final Study study = patient.getStudies().get(j);
								StudyNode studyNode = new StudyNode(study){
									public String toString(){															
										String studyDesc = study.toString();
										if(studyDesc == null){
											studyDesc = "";
										}else{
											
										}	
										return studyDesc;						
									}
									public Study getUserObject(){
										return study;
									}					
								};
								if(patientNode.isSelected()){
									studyNode.setSelected(true);
      		     					studyNode.getCheckBox().setSelected(true);
								}
								studyNode.addNodeSelectionListener(this);
								patientNode.add(studyNode);								
							}
						} else {
							StudyNode studyNode = null;
							for(int j = 0; j < patient.getStudies().size(); j++){
								final Study study = patient.getStudies().get(j);
								for(int b = 0; b < numOfStudyNodes; b++){
									StudyNode existingStudyNode = (StudyNode)patientNode.getChildAt(b);
									Study existingStudy = (Study) existingStudyNode.getUserObject();
									if(study.equals(existingStudy)){
										studyNode = existingStudyNode;
										int numOfSeriesNodes = studyNode.getChildCount();
										if(numOfSeriesNodes == 0){
											for(int k = 0; k < study.getSeries().size(); k++){
												final Series series = study.getSeries().get(k);
												SeriesNode seriesNode = new SeriesNode(series){
													public String toString(){						
														String seriesDesc = series.toString();
														if(seriesDesc == null){
															seriesDesc = "";
														}else{
															
														}	
														return seriesDesc;
													}
													public Series getUserObject(){
														return series;
													}
												};
												if(studyNode.isSelected()){
													seriesNode.setSelected(true);
				      		     					seriesNode.getCheckBox().setSelected(true);
												}
												seriesNode.addNodeSelectionListener(this);
												studyNode.add(seriesNode);
											}
										} else {
											SeriesNode seriesNode = null;
											for(int k = 0; k < study.getSeries().size(); k++){
												final Series series = study.getSeries().get(k);
												SeriesNode existingSeriesNode = (SeriesNode)studyNode.getChildAt(k);
												Series existingSeries = (Series)existingSeriesNode.getUserObject();
												if(series.equals(existingSeries)){
													seriesNode = existingSeriesNode;
													int numOfItemNodes = seriesNode.getChildCount();
													if(numOfItemNodes == 0){
														for(int m = 0; m < series.getItems().size(); m++){
															final Item item = series.getItems().get(m);
															ItemNode itemNode = new ItemNode(item);
															if(seriesNode.isSelected()){
																itemNode.setSelected(true);
							      		     					itemNode.getCheckBox().setSelected(true);
															}
															itemNode.addNodeSelectionListener(this);
															seriesNode.add(itemNode);
														}	
													} 
												} 
											}	
										}
									}
								}
							}
						}
					}
				}
			}
		}
		rootNode.add(locationNode);				
		treeModel.nodeChanged(rootNode);
		treeModel.reload(rootNode);
		expandAll();
	}
	
	public void update(DefaultMutableTreeNode queryNode, RetrieveTarget retrieveTarget) {
		if (queryNode instanceof PatientNode) {
			PatientNode patientNode = (PatientNode)queryNode;
			updatePatientNode(patientNode, retrieveTarget);
		} else if (queryNode instanceof StudyNode) {
			StudyNode studyNode = (StudyNode)queryNode;
			updateStudyNode(studyNode, retrieveTarget);	
		} else if (queryNode instanceof SeriesNode) {
			SeriesNode seriesNode = (SeriesNode)queryNode;
			updateSeriesNode(seriesNode, retrieveTarget);
		} else if (queryNode instanceof ItemNode) {
			ItemNode itemNode = (ItemNode)queryNode;
			updateItemNode(itemNode, retrieveTarget);
		} else {
			
		}
	}
	
	void updatePatientNode(PatientNode patientNode, RetrieveTarget retrieveTarget) {
		int numbOfStudyNodes = patientNode.getChildCount();
		for(int j = 0; j < numbOfStudyNodes; j++) {
			StudyNode studyNode = (StudyNode)patientNode.getChildAt(j);
			updateStudyNode(studyNode, retrieveTarget);
		}
	}
	
	void updateStudyNode(StudyNode studyNode, RetrieveTarget retrieveTarget) {
		int numbOfSeriesNodes = studyNode.getChildCount();
		for(int k = 0; k < numbOfSeriesNodes; k++) {
			SeriesNode seriesNode = (SeriesNode)studyNode.getChildAt(k);
			updateSeriesNode(seriesNode, retrieveTarget);
		}
	}
	
	void updateSeriesNode(SeriesNode seriesNode, RetrieveTarget retrieveTarget) {
		int numbOfItemNodes = seriesNode.getChildCount();
		for(int m = 0; m < numbOfItemNodes; m++) {
			ItemNode itemNode = (ItemNode)seriesNode.getChildAt(m);
			updateItemNode(itemNode, retrieveTarget);
		}
	}
	
	void updateItemNode(ItemNode itemNode, RetrieveTarget retrieveTarget){
		if(retrieveTarget.equals(RetrieveTarget.AIM_SEG)) {
			if(itemNode.getUserObject() instanceof AIMItem) {
				if(!itemNode.isSelected()) {
					itemNode.updateNode();
				}
			} else if (itemNode.getUserObject() instanceof ImageItem) {
				if(itemNode.isSelected()) {
					itemNode.updateNode();
				}
			}
		} else if (retrieveTarget.equals(RetrieveTarget.DICOM)) {
			if(itemNode.getUserObject() instanceof ImageItem) {
				if(!itemNode.isSelected()) {
					itemNode.updateNode();
				}
			} else if (itemNode.getUserObject() instanceof AIMItem) {
				if(itemNode.isSelected()) {
					itemNode.updateNode();
				}
			}
		} else if (retrieveTarget.equals(RetrieveTarget.DICOM_AIM_SEG)) {
			if(!itemNode.isSelected()) {
				itemNode.updateNode();
			}
		} else if (retrieveTarget.equals(RetrieveTarget.NONE)) {
			if(itemNode.isSelected()) {
				itemNode.updateNode();
			}
		}
	}
}
