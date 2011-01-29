/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.OtherItem;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.checkboxTree.ItemNode;

public class SearchResultTree extends JTree {	
	final static Logger logger = Logger.getLogger(SearchResultTree.class);
	public DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;			
	CheckBoxTreeRenderer renderer;
	
	Font font = new Font("Tahoma", 0, 12);
	Color xipLightBlue = new Color(156, 162, 189);	
	
	public SearchResultTree() {	  						    	    						
		rootNode = new DefaultMutableTreeNode("Search Results");		
	    treeModel = new DefaultTreeModel(rootNode);	
		setModel(treeModel);
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);		   
	    renderer = new CheckBoxTreeRenderer();
	    renderer.setNodeColor(new Color(156, 162, 189), new Color(255, 255, 255));
	    setCellRenderer(renderer);	   	    	   
	    //setEditable(true);		    		    
	    setShowsRootHandles(true);
	    setRootVisible(true);	    
	    putClientProperty("JTree.lineStyle", "Horizontal");	   	    	    
	    setFont(font);
	    setBackground(xipLightBlue);
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
	    //getting new nodes	    				
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
				public Object getUserObject(){
					return item;
				}					
			};
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
					public Object getUserObject(){
						return item;
					}					
				};
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
					public Object getUserObject(){
						return study;
					}					
				};
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
						public Object getUserObject(){
							return item;
						}					
					};
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
						public Object getUserObject(){
							return series;
						}
					};
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
							public Object getUserObject(){							
								return item;
							}
						};
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
	 
	 public void updateNodes2(SearchResult result) {
		 
	 }
	 
	 public DefaultMutableTreeNode getRootNode(){
		 return rootNode;
	 }
	 public DefaultTreeModel getTreeModel(){
		 return treeModel;
	 }
}


