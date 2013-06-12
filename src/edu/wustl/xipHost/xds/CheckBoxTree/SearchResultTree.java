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

package edu.wustl.xipHost.xds.CheckBoxTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.OtherItem;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dataModel.XDSDocumentItem;

public class SearchResultTree extends JTree {	

	public DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;			
	CheckBoxTreeRenderer renderer;
	
	Font font = new Font("Tahoma", 0, 12);
	Color xipLightBlue = new Color(156, 162, 189);	
	
	public SearchResultTree() {	  						    	    						
		rootNode = new DefaultMutableTreeNode("Search Results");		
	    treeModel = new DefaultTreeModel(rootNode);	
		setModel(treeModel);
		addMouseListener(ml);
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
	
	List<SearchResult> results;
	public void updateNodes(SearchResult result) {					    			
		results = new ArrayList<SearchResult>();
		firePropertyChange(JTree.ROOT_VISIBLE_PROPERTY, !isRootVisible(), isRootVisible());
		if(result == null){
			treeModel.reload(rootNode);
			return;
		}
		results.add(result);				    	    	    	      		   	    
	    //getting new nodes	    				
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
			locationNode.add(patientNode);
			for(int j = 0; j < patient.getItems().size(); j++){												
				final Item item = patient.getItems().get(j);
				ItemNode itemNode = new ItemNode(item){
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
				patientNode.add(itemNode);					
			}
			rootNode.add(locationNode);				
			treeModel.nodeChanged(rootNode);
			treeModel.reload(rootNode);				
		}
	}
	
	
	
	/**
	 * @param args
	 */
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
		SearchResultTree searchTree = new SearchResultTree();
		JFrame frame = new JFrame();
		frame.getContentPane().add(searchTree, BorderLayout.CENTER);		
		frame.setSize(650, 300);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	    
		SearchResult result = new SearchResult("WashU Test");
	    Patient patient1 = new Patient("Jaroslaw Krych", "1010101", "19730718");
	    Patient patient2 = new Patient("Jarek Krych", "2020202", "19730718");
	    XDSDocumentItem item1 = new XDSDocumentItem("1234", "Available", "US", "PDF", null, null, null);	    
	    XDSDocumentItem item2 = new XDSDocumentItem("2345", "Available", "US", "XML", null, null, null);	
	    XDSDocumentItem item3 = new XDSDocumentItem("3456", "Available", "US", "TXT", null, null, null);	
	    patient1.addItem(item1);
	    result.addPatient(patient1);
	    patient2.addItem(item2);
	    patient2.addItem(item3);
	    result.addPatient(patient2);	    	    
	    searchTree.updateNodes(result);
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
	
	
	List<XDSDocumentItem> selectedItems2 = new ArrayList<XDSDocumentItem>();	
	List<XDSDocumentItem> selectedItems = new ArrayList<XDSDocumentItem>();	
	public List<XDSDocumentItem> getSelectedItems(){
		return selectedItems2;
	}
	
	MouseListener ml = new MouseAdapter() {
	    @SuppressWarnings("unchecked")
		public void mousePressed(MouseEvent e) {
	    	int x = e.getX();
	     	int y = e.getY();
	     	int row = getRowForLocation(x, y);
	     	TreePath  path = getPathForRow(row);    	
	     	if (path != null) {    		
	     		DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();							
	     		//System.out.println(this.getRowForPath(new TreePath(node.getPath())));
	     		//System.out.println("Checking set changed, leading path: " + e.getPath().toString());			    
	     		if (node == null) return;		 
	     		if (!node.isRoot()) {																	
	     			Object selectedNode = node.getUserObject();
	     			if(selectedNode instanceof Patient){
	     				System.out.println(selectedNode.toString());
	     				//selectedNode.setSelected(true);
	     				repaint();
	     				//selectedSeriesInstanceUID = null;			
	     				//selectedStudyInstanceUID = ((Study)node.getUserObject()).getStudyInstanceUID();
	     			}else if(selectedNode instanceof XDSDocumentItem){				
	     				ItemNode itemNode = (ItemNode)node;
	     				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();					     					     				
	     				if(itemNode.isSelected){
	     					itemNode.setSelected(false);
	     					((ItemNode)node).getCheckBox().setSelected(false);
	     					selectedItems.remove((XDSDocumentItem)selectedNode);
	     					selectedItems2.remove((XDSDocumentItem)selectedNode);
	     				}else if(itemNode.isSelected == false){    					
	     					itemNode.setSelected(true);
	     					selectedItems.add((XDSDocumentItem)selectedNode);
	     					selectedItems2.add((XDSDocumentItem)selectedNode);
	     					((ItemNode)node).getCheckBox().setSelected(true);
	     				}    				
	     				repaint();	     				   					     				
	     				Iterator iter = selectedItems2.iterator();
	     				while(iter.hasNext()){
	     		    		XDSDocumentItem next = (XDSDocumentItem)iter.next();
							System.out.println("Item ID: " + next.getItemID());														
	     		    	}
	     			}
	     		} else {

	     		}
	     		System.out.println("-------------------------------");
	     	}    	
	     }
	 };		
}


