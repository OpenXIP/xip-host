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
import java.util.Map;
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

public class SearchResultTree extends JTree implements NodeSelectionListener2 {	
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
	
	
	public Map<Series, Study> getSelectedSeries(){
		return null;
	}
	 
	 public DefaultMutableTreeNode getRootNode(){
		 return rootNode;
	 }
	 public DefaultTreeModel getTreeModel(){
		 return treeModel;
	 }


	@Override
	public void nodeSelected(NodeSelectionEvent2 event) {
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
	

	MouseListener ml = new MouseAdapter(){  
		public void mouseClicked(final MouseEvent e) {
			x = e.getX();
	     	y = e.getY();
			if(e.getButton() == 1){
		    	if (e.getClickCount() == 1) {
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
	  			int row = getRowForLocation(x, y);
	  			TreePath path = getPathForRow(row);
	  			if (path != null) {
	  				DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
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
	  			}
	  			repaint();
	  };
	};
	
	void updateSelectedDataSearchResult(DefaultMutableTreeNode node, boolean selected){
		Object[] userObjectPath = node.getUserObjectPath();
		if(node.getUserObject() instanceof Patient){
			Patient patient = (Patient)node.getUserObject();
			if(selected){
				if(!selectedDataSearchResult.contains(patient.getPatientID())){
					selectedDataSearchResult.addPatient(patient);
				} else {
					
				}
			} else {
				selectedDataSearchResult.removePatient(patient);
			}
		} else if (node.getUserObject() instanceof Study){
			Study study = (Study)node.getUserObject();
			String patientID = ((Patient)userObjectPath[2]).getPatientID();
			if(selected){
				if(selectedDataSearchResult.contains(patientID)){
					Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
					if(!selectedPatient.contains(study.getStudyInstanceUID())){
						selectedPatient.addStudy(study);
					} 
				} else {
					Patient patientWithAllChildren = ((Patient)userObjectPath[2]);
					Patient patient = new Patient(patientWithAllChildren.getPatientName(), patientWithAllChildren.getPatientID(), patientWithAllChildren.getPatientBirthDate());
					patient.setLastUpdated(patientWithAllChildren.getLastUpdated());
					patient.addStudy(study);
					selectedDataSearchResult.addPatient(patient);
				}
			} else {
				Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
				selectedPatient.removeStudy(study);
				if(selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0){
					selectedDataSearchResult.removePatient(selectedPatient);
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
						Study study = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
						study.setLastUpdated(studyWithAllChildren.getLastUpdated());
						study.addSeries(series);
						selectedPatient.addStudy(study);
					} else {
						Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
						selectedStudy.addSeries(series);
					}
				} else {
					Patient patientWithAllChildren = ((Patient)userObjectPath[2]);
					Patient patient = new Patient(patientWithAllChildren.getPatientName(), patientWithAllChildren.getPatientID(), patientWithAllChildren.getPatientBirthDate());
					patient.setLastUpdated(patientWithAllChildren.getLastUpdated());
					Study studyWithAllChildren = (Study)userObjectPath[3];
					Study study = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
					study.setLastUpdated(studyWithAllChildren.getLastUpdated());
					study.addSeries(series);
					patient.addStudy(study);
					selectedDataSearchResult.addPatient(patient);
				}
			} else {
				Patient selectedPatient = selectedDataSearchResult.getPatient(patientID);
				Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
				selectedStudy.removeSeries(series);
				if(selectedStudy.getSeries().size() == 0 && selectedStudy.getItems().size() == 0){
					selectedPatient.removeStudy(selectedStudy);
					if(selectedPatient.getStudies().size() == 0 && selectedPatient.getItems().size() == 0){
						selectedDataSearchResult.removePatient(selectedPatient);
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
						Study study = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
						study.setLastUpdated(studyWithAllChildren.getLastUpdated());
						Series seriesWithAllChildren = (Series)userObjectPath[4];
						Series series = new Series(seriesWithAllChildren.getSeriesNumber(), seriesWithAllChildren.getModality(), seriesWithAllChildren.getSeriesDesc(), seriesWithAllChildren.getSeriesInstanceUID());
						series.setLastUpdated(seriesWithAllChildren.getLastUpdated());
						series.addItem(item);
						boolean containsSubsetOfItems = false;
						if(seriesWithAllChildren.getItems().size() != series.getItems().size()) {
							containsSubsetOfItems = true;
						}
						series.setContainsSubsetOfItems(containsSubsetOfItems);
						study.addSeries(series);
						selectedPatient.addStudy(study);
					} else {
						Study selectedStudy = selectedPatient.getStudy(studyInstanceUID);
						if(!selectedStudy.contains(seriesInstanceUID)){
							Series seriesWithAllChildren = (Series)userObjectPath[4];
							Series series = new Series(seriesWithAllChildren.getSeriesNumber(), seriesWithAllChildren.getModality(), seriesWithAllChildren.getSeriesDesc(), seriesWithAllChildren.getSeriesInstanceUID());
							series.setLastUpdated(seriesWithAllChildren.getLastUpdated());
							series.addItem(item);
							boolean containsSubsetOfItems = false;
							if(seriesWithAllChildren.getItems().size() != series.getItems().size()) {
								containsSubsetOfItems = true;
							}
							series.setContainsSubsetOfItems(containsSubsetOfItems);
							selectedStudy.addSeries(series);
						} else {
							Series selectedSeries = selectedStudy.getSeries(seriesInstanceUID);
							selectedSeries.addItem(item);
						}
					}
				} else {
					Patient patientWithAllChildren = ((Patient)userObjectPath[2]);
					Patient patient = new Patient(patientWithAllChildren.getPatientName(), patientWithAllChildren.getPatientID(), patientWithAllChildren.getPatientBirthDate());
					patient.setLastUpdated(patientWithAllChildren.getLastUpdated());
					Study studyWithAllChildren = (Study)userObjectPath[3];
					Study study = new Study(studyWithAllChildren.getStudyDate(), studyWithAllChildren.getStudyID(), studyWithAllChildren.getStudyDesc(), studyWithAllChildren.getStudyInstanceUID());
					study.setLastUpdated(studyWithAllChildren.getLastUpdated());
					Series seriesWithAllChildren = (Series)userObjectPath[4];
					Series series = new Series(seriesWithAllChildren.getSeriesNumber(), seriesWithAllChildren.getModality(), seriesWithAllChildren.getSeriesDesc(), seriesWithAllChildren.getSeriesInstanceUID());
					series.setLastUpdated(seriesWithAllChildren.getLastUpdated());
					series.addItem(item);
					boolean containsSubsetOfItems = false;
					if(seriesWithAllChildren.getItems().size() != series.getItems().size()) {
						containsSubsetOfItems = true;
					}
					series.setContainsSubsetOfItems(containsSubsetOfItems);
					study.addSeries(series);
					patient.addStudy(study);
					selectedDataSearchResult.addPatient(patient);
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


