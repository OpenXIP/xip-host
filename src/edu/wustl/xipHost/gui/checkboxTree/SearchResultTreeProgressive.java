/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

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
public class SearchResultTreeProgressive extends SearchResultTree{

	public SearchResultTreeProgressive(){
		super();
	}
	
	
	List<SearchResult> results;
	public void updateNodes(SearchResult result) {					    			
		results = new ArrayList<SearchResult>();
		firePropertyChange(JTree.ROOT_VISIBLE_PROPERTY, !isRootVisible(), isRootVisible());
		if(result == null){			
			rootNode.removeAllChildren();
			treeModel.reload(rootNode);
			return;
		}
		rootNode.removeAllChildren();
		treeModel.reload(rootNode);
		
		results.add(result);				    	    	    	      		   	    	    				
		DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode(result.getDataSourceDescription());
		for(int i = 0; i < result.getPatients().size(); i++){
			final Patient patient = result.getPatients().get(i);
			DefaultMutableTreeNode patientNode = new DefaultMutableTreeNode(patient){
				public String toString(){															
					String patientDesc = patient.toString();
					if(patientDesc == null){
						patientDesc = "";
					}else{
						
					}	
					return patientDesc;						
				}
				public Object getUserObject(){
					return patient;
				}					
			};			
			for(int j = 0; j < patient.getStudies().size(); j++){
				final Study study = patient.getStudies().get(j);
				DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(study){
					public String toString(){															
						String studyDesc = study.toString();
						if(studyDesc == null){
							studyDesc = "";
						}else{
							
						}	
						return studyDesc;						
					}
					public Object getUserObject(){
						return study;
					}					
				};
				
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
						public Object getUserObject(){
							return series;
						}
					};
					
					for(int m = 0; m < series.getItems().size(); m++){
						final Item item = series.getItems().get(m);
						DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item){
							public String toString(){															
								String itemDesc = item.toString();
								if(itemDesc == null){
									itemDesc = "";
								}else{
									
								}	
								return itemDesc;						
							}
							public Object getUserObject(){
								return item;
							}
						};
						seriesNode.add(itemNode);
					}										
					studyNode.add(seriesNode);
				}	
				patientNode.add(studyNode);			
			}
			locationNode.add(patientNode);
		}
		rootNode.add(locationNode);				
		treeModel.nodeChanged(rootNode);
		treeModel.reload(rootNode);
		expandAll();		
	}
	
	
	public void expandToLast(JTree tree) {
	    TreeModel data = tree.getModel();
	    Object node = data.getRoot();

	    if (node == null) return;

	    TreePath p = new TreePath(node);
	    while (true) {
	         int count = data.getChildCount(node);
	         if (count == 0) break;
	         node = data.getChild(node, count - 1);
	         p = p.pathByAddingChild(node);
	    }
	    tree.scrollPathToVisible(p);
	}

	
	 public static void main(String[] args) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
				//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		  
			SearchResultTreeProgressive searchTree = new SearchResultTreeProgressive();
			JFrame frame = new JFrame();
			frame.getContentPane().add(searchTree, BorderLayout.CENTER);		
			frame.setSize(650, 300);
		    frame.setVisible(true);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	    
			SearchResult result = new SearchResult("WashU Test");
		    Patient patient1 = new Patient("Jaroslaw Krych", "1010101", "19730718");
		    Patient patient2 = new Patient("Jarek Krych", "2020202", "19730718");
		    result.addPatient(patient1);
		    result.addPatient(patient2);
		    searchTree.updateNodes(result);
		}	
}
