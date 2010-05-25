/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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

public class SearchResultTree extends JTree {	

	public DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;			
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
		}else if(result.getPatients().size() == 0 && result.getItems().size() == 0){
			rootNode.removeAllChildren();
			treeModel.reload(rootNode);
			return;
		}
		results.add(result);				    	    	    	      		   	    
	    //getting new nodes	    				
		DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode(result.getDataSourceDescription());
		for(int i = 0; i < result.getPatients().size(); i++){
			Patient patient = result.getPatients().get(i);	
			final String patientDesc = patient.toString();						
			for(int j = 0; j < patient.getStudies().size(); j++){
				final Study study = patient.getStudies().get(j);
				DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(study){
					public String toString(){															
						String studyDesc = study.toString();
						if(studyDesc == null){
							studyDesc = "";
						}else{
							
						}	
						return patientDesc + " " + studyDesc;						
					}
					public Object getUserObject(){
						return study;
					}					
				};
				
				locationNode.add(studyNode);
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
					studyNode.add(seriesNode);
					for(int m = 0; m < series.getItems().size(); m++){
						final Item item = series.getItems().get(m);
						DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item){
							public String toString(){						
								String imageDesc = item.toString();
								if(imageDesc == null){
									imageDesc = "";
								}else{
									
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
	    Item otherItem1 = new OtherItem("1234", "XDS 1");	    
	    Item otherItem2 = new OtherItem("2345", "XDS 2");
	    Item otherItem3 = new OtherItem("3456", "XDS 3");
	    result.addPatient(patient1);
	    result.addPatient(patient2);
	    result.addItem(otherItem1);
	    Study study1 = new Study("01/13/2009", "12345678", "Test study 1", "123.123.123.123");
	    Study study2 = new Study("01/13/2009", "23456789", "Test study 2", "123.123.123.124");
	    study2.addItem(otherItem2);
		Series series1 = new Series("12345", "CT", "Test Series 1", "1.2.3.4.5");
		series1.addItem(otherItem3);
		Series series2 = new Series("23456", "CT", "Test Series 2", "1.2.3.4.6");
	    Item imageItem1 = new ImageItem("1");
	    Item imageItem2 = new ImageItem("2");
	    Item imageItem3 = new ImageItem("3");
	    Item imageItem4 = new ImageItem("4");
	    Item imageItem5 = new ImageItem("5");
	    Item aimItem1 = new AIMItem("Baseline", "01/13/2009", "Jarek Krych", "123456789");
		series1.addItem(imageItem1);
		series1.addItem(imageItem2);
		series1.addItem(imageItem3);
		series1.addItem(aimItem1);
		series2.addItem(imageItem4);
		series2.addItem(imageItem5);
	    study1.addSeries(series1);
	    study2.addSeries(series2);
		patient1.addStudy(study1);
		patient2.addStudy(study2);
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
	
	
	Map<Series, Study> selectedSeries = new LinkedHashMap<Series, Study>();			
	public Map<Series, Study> getSelectedSeries(){
		return selectedSeries;
	}
	
	public void clearSelectedSeries(){
		selectedSeries.clear();
	}
	
	MouseListener ml = new MouseAdapter() {
	    @SuppressWarnings("unchecked")
		public void mousePressed(MouseEvent e) {	    	
	    	if(e.getClickCount() == 1){
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
		     			if(selectedNode instanceof Study){
		     				//System.out.println(selectedNode.toString());	     				
		     				repaint();
		     			}else if(selectedNode instanceof Series){				
		     				SeriesNode seriesNode = (SeriesNode)node;
		     				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();				
		     				Study study = null;
		     				if(parentNode.getUserObject() instanceof Study){
		     					study = (Study)parentNode.getUserObject();
		     				}	     				
		     				if(seriesNode.isSelected){
		     					seriesNode.setSelected(false);
		     					((SeriesNode)node).getCheckBox().setSelected(false);	     					
		     					selectedSeries.remove((Series)selectedNode);
		     				}else if(seriesNode.isSelected == false){    					
		     					seriesNode.setSelected(true);	     					
		     					selectedSeries.put((Series)selectedNode, study);
		     					((SeriesNode)node).getCheckBox().setSelected(true);
		     				}    				
		     				repaint();	     				   				
		     				Set<Entry<Series, Study>> set = selectedSeries.entrySet();
		     				Iterator iter = set.iterator();
		     				while(iter.hasNext()){
		     		    		Entry<Series, Study> next = (Entry<Series, Study>)iter.next();
								//System.out.println("Series: " + next.getKey().getSeriesInstanceUID());
								//System.out.println("Study: " + next.getValue().getStudyInstanceUID());							
		     		    	}
		     			}
		     		} else {

		     		}
		     		//System.out.println("-------------------------------");
		     	}    	
	    	} else if(e.getClickCount() == 2){
	    		selectedSeries.clear();
	    	}
	     }
	 };		
}


