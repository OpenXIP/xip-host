/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.checkboxTree.ItemNode;

public class SearchResultTree extends JTree implements NodeSelectionListener {	
	final static Logger logger = Logger.getLogger(SearchResultTree.class);
	public DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;			
	CheckBoxTreeRenderer renderer;
	Font font = new Font("Tahoma", 0, 12);
	Color xipLightBlue = new Color(156, 162, 189);
	SearchResult selectedDataSearchResult;
	
	public SearchResultTree() {	  						    	    						
		rootNode = new DefaultMutableTreeNode("Search Results");		
	    treeModel = new DefaultTreeModel(rootNode);	
		setModel(treeModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);		   
	    renderer = new CheckBoxTreeRenderer();
	    renderer.setNodeColor(new Color(156, 162, 189), new Color(255, 255, 255));
	    setCellRenderer(renderer);	    
	    setShowsRootHandles(true);
	    setRootVisible(true);	    
	    putClientProperty("JTree.lineStyle", "Horizontal");	   	    	    
	    setFont(font);
	    setBackground(xipLightBlue);
	    addMouseListener(ml);
	}			
	
	
	public void updateNodes(SearchResult result) {					    			
		firePropertyChange(JTree.ROOT_VISIBLE_PROPERTY, !isRootVisible(), isRootVisible());
		if(result == null){			
			treeModel.reload(rootNode);
			return;
		}else if(result.getPatients().size() == 0 && result.getItems().size() == 0){
			rootNode.removeAllChildren();
			treeModel.reload(rootNode);
			return;
		}
	    //Getting new nodes	    				
		DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode(result.getDataSourceDescription());
		for(int j = 0; j < result.getItems().size(); j++){												
			final Item item = result.getItems().get(j);
			ItemNode itemNode = new ItemNode(item){
				public String toString(){															
					String itemDesc = item.toString();
					if(itemDesc == null){
						itemDesc = "";
					}	
					return itemDesc;						
				}
				public Item getUserObject(){
					return item;
				}					
			};
			itemNode.addNodeSelectionListener(this);
			locationNode.add(itemNode);					
		}
		for(int i = 0; i < result.getPatients().size(); i++){
			final Patient patient = result.getPatients().get(i);						
			PatientNode patientNode = new PatientNode(patient){
				public String toString(){
					String patientDesc = patient.toString();	
					if(patientDesc == null){
						patientDesc = "";
					}
					return patientDesc;
				}
			};
			patientNode.addNodeSelectionListener(this);
			locationNode.add(patientNode);
			for(int j = 0; j < patient.getItems().size(); j++){												
				final Item item = patient.getItems().get(j);
				ItemNode itemNode = new ItemNode(item){
					public String toString(){															
						String itemDesc = item.toString();
						if(itemDesc == null){
							itemDesc = "";
						}	
						return itemDesc;						
					}
					public Item getUserObject(){
						return item;
					}					
				};
				itemNode.addNodeSelectionListener(this);
				patientNode.add(itemNode);					
			}
			for(int j = 0; j < patient.getStudies().size(); j++){
				final Study study = patient.getStudies().get(j);
				StudyNode studyNode = new StudyNode(study){
					public String toString(){															
						String studyDesc = study.toString();
						if(studyDesc == null){
							studyDesc = "";
						}	
						return studyDesc;						
					}
					public Study getUserObject(){
						return study;
					}					
				};
				studyNode.addNodeSelectionListener(this);
				patientNode.add(studyNode);
				for(int k = 0; k < study.getItems().size(); k++){												
					final Item item = study.getItems().get(k);
					ItemNode itemNode = new ItemNode(item){
						public String toString(){															
							String itemDesc = item.toString();
							if(itemDesc == null){
								itemDesc = "";
							}	
							return itemDesc;						
						}
						public Item getUserObject(){
							return item;
						}					
					};
					itemNode.addNodeSelectionListener(this);
					studyNode.add(itemNode);					
				}
				for(int k = 0; k < study.getSeries().size(); k++){
					final Series series = study.getSeries().get(k);
					SeriesNode seriesNode = new SeriesNode(series){
						public String toString(){						
							String seriesDesc = series.toString();
							if(seriesDesc == null){
								seriesDesc = "";
							}	
							return seriesDesc;
						}
						public Series getUserObject(){
							return series;
						}
					};
					seriesNode.addNodeSelectionListener(this);
					studyNode.add(seriesNode);
					for(int m = 0; m < series.getItems().size(); m++){
						final Item item = series.getItems().get(m);
						ItemNode itemNode = new ItemNode(item){
							public String toString(){						
								String imageDesc = item.toString();
								if(imageDesc == null){
									imageDesc = "";
								}
								return imageDesc;							
							}
							public Item getUserObject(){							
								return item;
							}
						};
						itemNode.addNodeSelectionListener(this);
						seriesNode.add(itemNode);					
					}
				}
			}
			rootNode.add(locationNode);				
			treeModel.nodeChanged(rootNode);
			treeModel.reload(rootNode);				
		}
	}		
	
	public void expandAll() {
		expandSubTree(getPathForRow(0));
    }
	
	private void expandSubTree(TreePath path) {
		expandPath(path);
		Object node = path.getLastPathComponent();
		int childrenNumber = getModel().getChildCount(node);
		TreePath[] childrenPath = new TreePath[childrenNumber];
		for (int childIndex = 0; childIndex < childrenNumber; childIndex++) {
		    childrenPath[childIndex] = path.pathByAddingChild(getModel().getChild(node, childIndex));
		    expandSubTree(childrenPath[childIndex]);
		}
	}
	 
	 public DefaultMutableTreeNode getRootNode(){
		 return rootNode;
	 }
	 public DefaultTreeModel getTreeModel(){
		 return treeModel;
	 }

	
	@Override
	public void nodeSelected(NodeSelectionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getSource();
		boolean selected = false;
		if(node instanceof PatientNode){
			selected = !((PatientNode)node).isSelected();
		} else if (node instanceof StudyNode){
			selected = !((StudyNode)node).isSelected();
		} else if (node instanceof SeriesNode){
			selected = !((SeriesNode)node).isSelected();
		} else if (node instanceof ItemNode){
			selected = !((ItemNode)node).isSelected();
		}
		if(doubleClicked){
			selected = !selected;
		}
		updateSelection(node, selected);
	}
	
	
	synchronized void updateSelection(DefaultMutableTreeNode node, boolean selected){
		 if(node instanceof PatientNode){
			PatientNode patientNode = (PatientNode)node;
			patientNode.getCheckBox().setSelected(selected);
			patientNode.setSelected(selected);
			int numbOfStudies = patientNode.getChildCount();
			for(int i = 0; i < numbOfStudies; i++){
				StudyNode studyNode = (StudyNode)patientNode.getChildAt(i);
				studyNode.getCheckBox().setSelected(selected);
				studyNode.setSelected(selected);
				int numbOfSeries = studyNode.getChildCount();
				for(int j = 0; j < numbOfSeries; j++){
					SeriesNode seriesNode = (SeriesNode)studyNode.getChildAt(j);
					seriesNode.getCheckBox().setSelected(selected);
					seriesNode.setSelected(selected);
					int numbOfItems = seriesNode.getChildCount();
					for(int k = 0; k < numbOfItems; k++){
						ItemNode itemNode = (ItemNode)seriesNode.getChildAt(k);
						itemNode.getCheckBox().setSelected(selected);
						itemNode.setSelected(selected);
					}
				}
			}
		} else if(node instanceof StudyNode) {
			StudyNode studyNode = (StudyNode)node;
			studyNode.getCheckBox().setSelected(selected);
			studyNode.setSelected(selected);
			int numbOfSeries = studyNode.getChildCount();
			for(int i = 0; i < numbOfSeries; i++){
				SeriesNode seriesNode = (SeriesNode)studyNode.getChildAt(i);
				seriesNode.getCheckBox().setSelected(selected);
				seriesNode.setSelected(selected);
				int numbOfItems = seriesNode.getChildCount();
				for(int j = 0; j < numbOfItems; j++){
					ItemNode itemNode = (ItemNode)seriesNode.getChildAt(j);
					itemNode.getCheckBox().setSelected(selected);
					itemNode.setSelected(selected);
				}
			}
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
			int numbOfSiblings = parentNode.getChildCount();
			boolean allSiblingsSelected = true;
			for(int i = 0; i < numbOfSiblings; i++){
				StudyNode siblingNode = (StudyNode)parentNode.getChildAt(i);
				if(siblingNode.isSelected() == false){
					allSiblingsSelected = false;
					break;
				}
			}
			PatientNode patientNode = (PatientNode)parentNode;
			if(patientNode.isSelected() != allSiblingsSelected)	{
				updateParent(parentNode, allSiblingsSelected);
			}
		} else if(node instanceof SeriesNode) {
			SeriesNode seriesNode = (SeriesNode)node;
			seriesNode.getCheckBox().setSelected(selected);
			seriesNode.setSelected(selected);
			int numbOfItems = seriesNode.getChildCount();
			for(int i = 0; i < numbOfItems; i++){
				ItemNode itemNode = (ItemNode)seriesNode.getChildAt(i);
				itemNode.getCheckBox().setSelected(selected);
				itemNode.setSelected(selected);
			}
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
			int numbOfSiblings = parentNode.getChildCount();
			boolean allSiblingsSelected = true;
			for(int i = 0; i < numbOfSiblings; i++){
				SeriesNode siblingNode = (SeriesNode)parentNode.getChildAt(i);
				if(siblingNode.isSelected() == false){
					allSiblingsSelected = false;
					break;
				}
			}
			StudyNode studyNode = (StudyNode)parentNode;
			if(studyNode.isSelected() != allSiblingsSelected){
				updateParent(parentNode, allSiblingsSelected);
			}
		} else if(node instanceof ItemNode) {
			ItemNode itemNode = (ItemNode)node;
			itemNode.getCheckBox().setSelected(selected);
			itemNode.setSelected(selected);
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
			boolean allSiblingsSelected = true;
			int numbOfSiblings = parentNode.getChildCount();
			for(int i = 0; i < numbOfSiblings; i++){
				ItemNode siblingNode = (ItemNode)parentNode.getChildAt(i);
				if(siblingNode.isSelected() == false){
					allSiblingsSelected = false;
					break;
				}
			}
			SeriesNode seriesNode = (SeriesNode)parentNode;
			if(seriesNode.isSelected() != allSiblingsSelected){
				updateParent(parentNode, allSiblingsSelected);
			}
		}
		updateSelectedDataSearchResult(node, selected);
		repaint();
	}
	
	 synchronized void updateParent(DefaultMutableTreeNode node, boolean selected){
		if(node instanceof PatientNode){
			PatientNode patientNode = (PatientNode)node;
			patientNode.getCheckBox().setSelected(selected);
			patientNode.setSelected(selected);
		} else if(node instanceof StudyNode){
			StudyNode studyNode = (StudyNode)node;
			studyNode.getCheckBox().setSelected(selected);
			studyNode.setSelected(selected);
			PatientNode patientNode = (PatientNode)studyNode.getParent();
			boolean allStudiesSelected = true;
			int numbOfStudies = patientNode.getChildCount();
			for(int i = 0; i < numbOfStudies; i++){
				StudyNode siblingNode = (StudyNode)patientNode.getChildAt(i);
				if(siblingNode.isSelected() == false){
					allStudiesSelected = false;
					break;
				}
			}
			if(studyNode.isSelected() != patientNode.isSelected()) {
				patientNode.getCheckBox().setSelected(allStudiesSelected);
				patientNode.setSelected(allStudiesSelected);
			}
		} else if(node instanceof SeriesNode){
			SeriesNode seriesNode = (SeriesNode)node;
			seriesNode.getCheckBox().setSelected(selected);
			seriesNode.setSelected(selected);
			StudyNode studyNode = (StudyNode)seriesNode.getParent();
			boolean allSeriesSelected = true;
			int numbOfSeries = studyNode.getChildCount();
			for(int i = 0; i < numbOfSeries; i++){
				SeriesNode siblingNode = (SeriesNode)studyNode.getChildAt(i);
				if(siblingNode.isSelected() == false){
					allSeriesSelected = false;
					break;
				}
			}
			if (studyNode.isSelected() != seriesNode.isSelected()) {
				studyNode.getCheckBox().setSelected(allSeriesSelected);
				studyNode.setSelected(allSeriesSelected);
			}
			PatientNode patientNode = (PatientNode)studyNode.getParent();
			boolean allStudiesSelected = true;
			int numbOfStudies = patientNode.getChildCount();
			for(int i = 0; i < numbOfStudies; i++){
				StudyNode siblingNode = (StudyNode)patientNode.getChildAt(i);
				if(siblingNode.isSelected() == false){
					allStudiesSelected = false;
					break;
				}
			}
			if(studyNode.isSelected() != patientNode.isSelected()) {
				patientNode.getCheckBox().setSelected(allStudiesSelected);
				patientNode.setSelected(allStudiesSelected);
			}
		}
	}
	
	public void selectAll(boolean selectAll){
		DefaultMutableTreeNode rootNode = getRootNode();
		if(rootNode.getChildCount() == 0){
			return;
		}
		DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode) rootNode.getFirstChild();
		int numbOfPatientNodes = locationNode.getChildCount();
		for(int i = 0; i < numbOfPatientNodes; i++){
			DefaultMutableTreeNode patientNode = (DefaultMutableTreeNode)locationNode.getChildAt(i);
			updateSelection(patientNode, selectAll);
		}
	}
	
	boolean doubleClicked;
	public void setDoubleClicked(boolean doubleClicked){
		this.doubleClicked = doubleClicked;
	}
	
	MouseListener ml = new MouseAdapter(){  
		public void mouseClicked(final MouseEvent e) {
			x = e.getX();
	     	y = e.getY();
	     	setDoubleClicked(false);
			if(e.getButton() == 1){
				if (e.getClickCount() == 2){
		        	setDoubleClicked(true);
		        } else if (e.getClickCount() == 1) {
		        	Timer timer = new Timer(300, taskPerformer);
		        	timer.setRepeats(false);
		        	timer.start();
		        }
			} else if (e.getButton() == 3) {
				int row = getRowForLocation(x, y);
	  			TreePath path = getPathForRow(row);
	  			expandSubTree(path);
			}
	    }
	};
	
	
	int x;
	int y;
	ActionListener taskPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if(doubleClicked == false){
				int row = getRowForLocation(x, y);
				TreePath path = getPathForRow(row);
				if (path != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
					updateNodeProgressive(node);
				}
			}
		};
	};
	
	public void updateNodeProgressive(DefaultMutableTreeNode node){
			if (node == null)
				return;
			if (!node.isRoot()) {
				Object selectedNode = node.getUserObject();
				if (selectedNode instanceof Patient) {
					PatientNode patientNode = (PatientNode) node;
					patientNode.updateNode();			
				} else if (selectedNode instanceof Study) {
					StudyNode studyNode = (StudyNode) node;
					studyNode.updateNode();		
				} else if (selectedNode instanceof Series) {
					SeriesNode seriesNode = (SeriesNode) node;
					seriesNode.updateNode();
				} else if (selectedNode instanceof Item) {
					ItemNode itemNode = (ItemNode) node;
					itemNode.updateNode();
				}
			}
			repaint();
	}
	
	void updateSelectedDataSearchResult(DefaultMutableTreeNode node, boolean selected){
		Object[] userObjectPath = node.getUserObjectPath();
		if(node.getUserObject() instanceof Patient){
			Patient patient = (Patient)node.getUserObject();
			if(selected){
				if(!selectedDataSearchResult.contains(patient.getPatientID())){
					Patient patientToAdd = new Patient(patient.getPatientName(), patient.getPatientID(), patient.getPatientBirthDate());
					patientToAdd.setLastUpdated(patient.getLastUpdated());
					//Add all the Study, Series and Items
					List<Study> studies = patient.getStudies();
					for(int i = 0; i < studies.size(); i++){
						Study study = studies.get(i);
						Study studyToAdd = new Study(study.getStudyDate(),study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
						studyToAdd.setLastUpdated(study.getLastUpdated());
						List<Series> series = study.getSeries();
						for(int j = 0; j < series.size(); j++){
							Series oneSeries = series.get(j);
							Series seriesToAdd = new Series(oneSeries.getSeriesNumber(), oneSeries.getModality(), oneSeries.getSeriesDesc(), oneSeries.getSeriesInstanceUID());
							seriesToAdd.setLastUpdated(oneSeries.getLastUpdated());
							List<Item> items = oneSeries.getItems();
							for(int m = 0; m < items.size(); m++){
								Item item = items.get(m);
								seriesToAdd.addItem(item);
							}
							studyToAdd.addSeries(seriesToAdd);
						}
						patientToAdd.addStudy(studyToAdd);
					}
					selectedDataSearchResult.addPatient(patientToAdd);
				} else {
					Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
					//Check if any Studies, Series or Items need to be added
					List<Study> allStudies = patient.getStudies();
					for(int i = 0; i < allStudies.size(); i++){
						Study study = allStudies.get(i);
						List<Series> allSeries = study.getSeries();
						if(!selectedPatient.contains(study.getStudyInstanceUID())){
							Study studyToAdd = new Study(study.getStudyDate(),study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
							studyToAdd.setLastUpdated(study.getLastUpdated());
							for(int j = 0; j < allSeries.size(); j++){
								Series series = allSeries.get(j);
								Series seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
								seriesToAdd.setLastUpdated(series.getLastUpdated());								
								List<Item> allItems = series.getItems();
								for(int k = 0; k < allItems.size(); k++){
									Item item = allItems.get(k);
									if(!seriesToAdd.contains(item.getItemID())){
										seriesToAdd.addItem(item);
									}
								}
								studyToAdd.addSeries(seriesToAdd);
							}
							selectedPatient.addStudy(studyToAdd);
						} else {
							Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
							for(int j = 0; j < allSeries.size(); j++){
								Series series = allSeries.get(j);
								Series seriesToAdd;
								if(!selectedStudy.contains(series.getSeriesInstanceUID())) {
									seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
									seriesToAdd.setLastUpdated(series.getLastUpdated());
									selectedStudy.addSeries(seriesToAdd);
								} else {
									seriesToAdd = selectedStudy.getSeries(series.getSeriesInstanceUID());
								}
								List<Item> allItems = series.getItems();
								for(int k = 0; k < allItems.size(); k++){
									Item item = allItems.get(k);
									if(!seriesToAdd.contains(item.getItemID())){
										seriesToAdd.addItem(item);
									}
								}
							}
						}
					}
				}
			} else {
				Patient patientToRemove = selectedDataSearchResult.getPatient(patient.getPatientID());
				if(patientToRemove != null){
					selectedDataSearchResult.removePatient(patientToRemove);
				}
			}
		} else if (node.getUserObject() instanceof Study){
			Study study = (Study)node.getUserObject();
			String patientID = ((Patient)userObjectPath[2]).getPatientID();
			if(selected){
				if(selectedDataSearchResult.contains(patientID)){
					Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
					if(!selectedPatient.contains(study.getStudyInstanceUID())){
						Study studyToAdd = new Study(study.getStudyDate(),study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
						studyToAdd.setLastUpdated(study.getLastUpdated());
						List<Series> series = study.getSeries();
						for(int i = 0; i < series.size(); i++){
							Series oneSeries = series.get(i);
							Series seriesToAdd = new Series(oneSeries.getSeriesNumber(), oneSeries.getModality(), oneSeries.getSeriesDesc(), oneSeries.getSeriesInstanceUID());
							seriesToAdd.setLastUpdated(oneSeries.getLastUpdated());
							List<Item> items = oneSeries.getItems();
							for(int j = 0; j < items.size(); j++){
								Item item = items.get(j);
								seriesToAdd.addItem(item);
							}
							studyToAdd.addSeries(seriesToAdd);
						}
						selectedPatient.addStudy(studyToAdd);
					} else {
						Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
						List<Series> selectedSeries = selectedStudy.getSeries();
						List<Series> allSeries = study.getSeries();
						if(selectedSeries.size() != allSeries.size()){
							for(int i = 0; i < allSeries.size(); i++){
								Series series = allSeries.get(i);
								if(!selectedStudy.contains(series.getSeriesInstanceUID())){
									Series seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
									seriesToAdd.setLastUpdated(series.getLastUpdated());
									selectedStudy.addSeries(seriesToAdd);
									//Add all items to seriesToAdd
									List<Item> items = series.getItems();
									for(int j = 0; j < items.size(); j++){
										Item item = items.get(j);
										seriesToAdd.addItem(item);
									}
								}
							}
						} else {
							for(int i = 0; i < selectedSeries.size(); i++){
								Series series = selectedSeries.get(i);
								Series seriesWithAllItems = study.getSeries(series.getSeriesInstanceUID());
								if(series.getItems().size() != seriesWithAllItems.getItems().size()){
									for(int j = 0; j < seriesWithAllItems.getItems().size(); j++){
										Item item = seriesWithAllItems.getItems().get(j);
										if(!series.contains(item.getItemID())){
											series.addItem(item);
										}
									}
								}
							}
						}
					}
				} else {
					Patient patientWithAllChildren = ((Patient)userObjectPath[2]);
					Patient patientToAdd = new Patient(patientWithAllChildren.getPatientName(), patientWithAllChildren.getPatientID(), patientWithAllChildren.getPatientBirthDate());
					patientToAdd.setLastUpdated(patientWithAllChildren.getLastUpdated());
					Study studyToAdd = new Study(study.getStudyDate(),study.getStudyID(), study.getStudyDesc(), study.getStudyInstanceUID());
					studyToAdd.setLastUpdated(study.getLastUpdated());
					List<Series> seriesWithAllChildren = study.getSeries(); 
					for(int i = 0; i < seriesWithAllChildren.size(); i++){
						Series series = seriesWithAllChildren.get(i);
						Series seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
						seriesToAdd.setLastUpdated(series.getLastUpdated());
						List<Item> items = series.getItems();
						for(int j = 0; j < items.size(); j++){
							Item item = items.get(j);
							seriesToAdd.addItem(item);
						}
						studyToAdd.addSeries(seriesToAdd);
					}
					patientToAdd.addStudy(studyToAdd);
					selectedDataSearchResult.addPatient(patientToAdd);
				}
			} else {
				Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
				if(selectedPatient != null){
					Study studyToRemove = selectedPatient.getStudy(study.getStudyInstanceUID());
					selectedPatient.removeStudy(studyToRemove);
					if(selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0){
						selectedDataSearchResult.removePatient(selectedPatient);
					}
				}
			}
		} else if (node.getUserObject() instanceof Series){
			Series series = (Series)node.getUserObject();
			String studyInstanceUID = ((Study)userObjectPath[3]).getStudyInstanceUID();
			String patientID = ((Patient)userObjectPath[2]).getPatientID();
			if(selected) {
				if(selectedDataSearchResult.contains(patientID)){
					Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
					if(!selectedPatient.contains(studyInstanceUID)){
						Study studyWithAllChildren = (Study)userObjectPath[3];
						Study studyToAdd = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
						studyToAdd.setLastUpdated(studyWithAllChildren.getLastUpdated());
						Series seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
						seriesToAdd.setLastUpdated(series.getLastUpdated());
						List<Item> items = series.getItems();
						for(int i = 0; i < items.size(); i++){
							Item item = items.get(i);
							seriesToAdd.addItem(item);
						}
						studyToAdd.addSeries(seriesToAdd);
						selectedPatient.addStudy(studyToAdd);
					} else {
						Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
						if(!selectedStudy.contains(series.getSeriesInstanceUID())){
							Series seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
							seriesToAdd.setLastUpdated(series.getLastUpdated());
							selectedStudy.addSeries(seriesToAdd);
							//Add all items to seriesToAdd
							List<Item> items = series.getItems();
							for(int i = 0; i < items.size(); i++){
								Item item = items.get(i);
								seriesToAdd.addItem(item);
							}
						} else {
							Series selectedSeries = selectedStudy.getSeries(series.getSeriesInstanceUID());
							if(selectedSeries.getItems().size() != series.getItems().size()){
								List<Item> items = series.getItems();
								for(int i = 0; i < items.size(); i++){
									Item item = items.get(i);
									if(!selectedSeries.contains(item.getItemID())){
										selectedSeries.addItem(item);
									}
								}
							}
						}
					}
				} else {
					Patient patientWithAllChildren = ((Patient)userObjectPath[2]);
					Patient patientToAdd = new Patient(patientWithAllChildren.getPatientName(), patientWithAllChildren.getPatientID(), patientWithAllChildren.getPatientBirthDate());
					patientToAdd.setLastUpdated(patientWithAllChildren.getLastUpdated());
					Study studyWithAllChildren = (Study)userObjectPath[3];
					Study studyToAdd = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
					studyToAdd.setLastUpdated(studyWithAllChildren.getLastUpdated());
					Series seriesToAdd = new Series(series.getSeriesNumber(), series.getModality(), series.getSeriesDesc(), series.getSeriesInstanceUID());
					seriesToAdd.setLastUpdated(series.getLastUpdated());
					List<Item> items = series.getItems();
					for(int i = 0; i < items.size(); i++){
						Item item = items.get(i);
						seriesToAdd.addItem(item);
					}
					studyToAdd.addSeries(seriesToAdd);
					patientToAdd.addStudy(studyToAdd);
					selectedDataSearchResult.addPatient(patientToAdd);
				}
			} else {
				Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
				if(selectedPatient != null){
					Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
					Series seriesToRemove = selectedStudy.getSeries(series.getSeriesInstanceUID());
					if(selectedStudy != null) {
						selectedStudy.removeSeries(seriesToRemove);
						if(selectedStudy.getSeries().size() == 0 && selectedStudy.getItems().size() == 0){
							selectedPatient.removeStudy(selectedStudy);
							if(selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0){
								selectedDataSearchResult.removePatient(selectedPatient);
							}
						}
					}
				}
			}
		} else if (node.getUserObject() instanceof Item ){
			Item item = (Item)node.getUserObject();
			String seriesInstanceUID = ((Series)userObjectPath[4]).getSeriesInstanceUID();
			String studyInstanceUID = ((Study)userObjectPath[3]).getStudyInstanceUID();
			String patientID = ((Patient)userObjectPath[2]).getPatientID();
			if(selected) {
				if(selectedDataSearchResult.contains(patientID)){
					Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
					if(!selectedPatient.contains(studyInstanceUID)){
						Study studyWithAllChildren = (Study)userObjectPath[3];
						Study studyToAdd = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
						studyToAdd.setLastUpdated(studyWithAllChildren.getLastUpdated());
						Series seriesWithAllChildren = (Series)userObjectPath[4];
						Series seriesToAdd = new Series(seriesWithAllChildren.getSeriesNumber(), seriesWithAllChildren.getModality(), seriesWithAllChildren.getSeriesDesc(), seriesWithAllChildren.getSeriesInstanceUID());
						seriesToAdd.setLastUpdated(seriesWithAllChildren.getLastUpdated());
						seriesToAdd.addItem(item);
						boolean containsSubsetOfItems = false;
						if(seriesWithAllChildren.getItems().size() != seriesToAdd.getItems().size()) {
							containsSubsetOfItems = true;
						}
						seriesToAdd.setContainsSubsetOfItems(containsSubsetOfItems);
						studyToAdd.addSeries(seriesToAdd);
						selectedPatient.addStudy(studyToAdd);
					} else {
						Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
						if(!selectedStudy.contains(seriesInstanceUID)){
							Series seriesWithAllChildren = (Series)userObjectPath[4];
							Series seriesToAdd = new Series(seriesWithAllChildren.getSeriesNumber(), seriesWithAllChildren.getModality(), seriesWithAllChildren.getSeriesDesc(), seriesWithAllChildren.getSeriesInstanceUID());
							seriesToAdd.setLastUpdated(seriesWithAllChildren.getLastUpdated());
							seriesToAdd.addItem(item);
							boolean containsSubsetOfItems = false;
							if(seriesWithAllChildren.getItems().size() != seriesToAdd.getItems().size()) {
								containsSubsetOfItems = true;
							}
							seriesToAdd.setContainsSubsetOfItems(containsSubsetOfItems);
							selectedStudy.addSeries(seriesToAdd);
						} else {
							Series selectedSeries = selectedStudy.getSeries(seriesInstanceUID);
							selectedSeries.addItem(item);
						}
					}
				} else {
					Patient patientWithAllChildren = ((Patient)userObjectPath[2]);
					Patient patientToAdd = new Patient(patientWithAllChildren.getPatientName(), patientWithAllChildren.getPatientID(), patientWithAllChildren.getPatientBirthDate());
					patientToAdd.setLastUpdated(patientWithAllChildren.getLastUpdated());
					Study studyWithAllChildren = (Study)userObjectPath[3];
					Study studyToAdd = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
					studyToAdd.setLastUpdated(studyWithAllChildren.getLastUpdated());
					Series seriesWithAllChildren = (Series)userObjectPath[4];
					Series seriesToAdd = new Series(seriesWithAllChildren.getSeriesNumber(), seriesWithAllChildren.getModality(), seriesWithAllChildren.getSeriesDesc(), seriesWithAllChildren.getSeriesInstanceUID());
					seriesToAdd.setLastUpdated(seriesWithAllChildren.getLastUpdated());
					seriesToAdd.addItem(item);
					boolean containsSubsetOfItems = false;
					if(seriesWithAllChildren.getItems().size() != seriesToAdd.getItems().size()) {
						containsSubsetOfItems = true;
					}
					seriesToAdd.setContainsSubsetOfItems(containsSubsetOfItems);
					studyToAdd.addSeries(seriesToAdd);
					patientToAdd.addStudy(studyToAdd);
					selectedDataSearchResult.addPatient(patientToAdd);
				}
			} else {
				Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
				Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
				Series selectedSeries = selectedStudy.getSeries(seriesInstanceUID);
				selectedSeries.removeItem(item);
				Series seriesWithAllChildren = (Series)userObjectPath[4];
				boolean containsSubsetOfItems = false;
				if(seriesWithAllChildren.getItems().size() != selectedSeries.getItems().size()) {
					containsSubsetOfItems = true;
				}
				selectedSeries.setContainsSubsetOfItems(containsSubsetOfItems);
				if(selectedSeries.getItems().size() == 0){
					selectedStudy.removeSeries(selectedSeries);
					if(selectedStudy.getSeries().size() == 0 && selectedStudy.getItems().size() == 0){
						selectedPatient.removeStudy(selectedStudy);
						if(selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0){
							selectedDataSearchResult.removePatient(selectedPatient);
						}
					}
				}
			}
		} 
		notifyDataSelectionChanged(selectedDataSearchResult);
	}
	
	DataSelectionListener listener;
	public void addDataSelectionListener(DataSelectionListener l) {
		listener = l;
	}

	void notifyDataSelectionChanged(SearchResult selectedDataSearchResult) {
		DataSelectionEvent event = new DataSelectionEvent(selectedDataSearchResult);
		listener.dataSelectionChanged(event);
	}
	
	public void setSelectedDataSearchResult(SearchResult selectedDataSearchResult){
		this.selectedDataSearchResult = selectedDataSearchResult;
	}
}


