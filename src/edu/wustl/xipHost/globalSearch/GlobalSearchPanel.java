/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.globalSearch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.caGrid.CQLTargetName;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.caGrid.GridManager;
import edu.wustl.xipHost.caGrid.GridManagerFactory;
import edu.wustl.xipHost.caGrid.GridQuery;
import edu.wustl.xipHost.caGrid.GridUtil;
import edu.wustl.xipHost.dicom.DicomManager;
import edu.wustl.xipHost.dicom.DicomManagerFactory;
import edu.wustl.xipHost.dicom.DicomQuery;
import edu.wustl.xipHost.dicom.PacsLocation;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

/**
 * @author Jaroslaw Krych
 *
 */
public class GlobalSearchPanel extends JPanel implements ActionListener, DataAccessListener {	
	JButton btnLocations = new JButton("Locations");	
	JPanel btnPanel = new JPanel();
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();
	JPanel leftPanel = new JPanel();
	
	JPanel rightPanel = new JPanel();
	SearchResultTree resultTree = new SearchResultTree();		
	JScrollPane treeView = new JScrollPane(resultTree);        
    JPanel btnPanelTree = new JPanel();
	JButton btnExpand = new JButton("Expand All");
	JButton btnColaps = new JButton("Collapse All");    		
	JButton btnRetrieve = new JButton("Retrieve");
	Border border1 = BorderFactory.createLoweredBevelBorder();	
	JProgressBar progressBar = new JProgressBar();		
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);	
	JLabel infoLabel = new JLabel("UNDER   DEVELOPMENT");
	
	public GlobalSearchPanel(){
		setBackground(xipColor);
		btnPanel.add(btnLocations);
		btnPanel.setBackground(xipColor);
		btnLocations.setPreferredSize(new Dimension(120, 25));
		btnLocations.setBackground(xipBtn);
		btnLocations.setForeground(Color.WHITE);
		btnLocations.addActionListener(this);
		leftPanel.setBackground(xipColor);
		leftPanel.add(btnPanel);
		leftPanel.add(criteriaPanel);
		
		btnPanelTree.add(btnExpand);
		btnPanelTree.add(btnColaps);
		btnExpand.setPreferredSize(new Dimension(120, 25));
		btnExpand.setBackground(xipBtn);
		btnExpand.setForeground(Color.WHITE);
		btnExpand.addActionListener(this);
		btnColaps.setPreferredSize(new Dimension(120, 25));
		btnColaps.setBackground(xipBtn);
		btnColaps.setForeground(Color.WHITE);
		btnColaps.addActionListener(this);
		btnPanelTree.setBackground(xipColor);		
		
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border1);	    
	    rightPanel.add(btnPanelTree);
		rightPanel.add(treeView);
		rightPanel.add(btnRetrieve);
		
	    btnRetrieve.setPreferredSize(new Dimension(120, 25));
	    btnRetrieve.setBackground(xipBtn);
	    btnRetrieve.setForeground(Color.WHITE);	
	    btnRetrieve.addActionListener(this);
	    rightPanel.setBackground(xipColor);	    	    
		criteriaPanel.getQueryButton().addActionListener(this);
		
	    progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));	    
	    progressBar.setForeground(xipColor);
	    infoLabel.setForeground(Color.ORANGE);
		add(infoLabel);
	    add(leftPanel);
	    add(rightPanel);
	    add(progressBar);
	    
	    buildLeftPanelLayout();		
		buildRightPanelLayout();		
		buildLayout();		
	}
	
	void buildLayout(){				
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(rightPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0; 
        constraints.gridwidth = 2;
        constraints.insets.top = 5;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(infoLabel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(progressBar, constraints);
	}
	
	void buildLeftPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        leftPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(btnPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(criteriaPanel, constraints); 
	}
	
	void buildRightPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
       rightPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 10;
        constraints.insets.left = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(btnPanelTree, constraints);                    
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        constraints.insets.left = 10;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 10;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnRetrieve, constraints);               
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		GlobalSearchPanel panel = new GlobalSearchPanel();
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		frame.pack();
	}
	
	AttributeList criteria;		
	public void actionPerformed(ActionEvent e) {		
		if(e.getSource() == criteriaPanel.getQueryButton()){
			setCriteriaList(criteriaPanel.getFilterList());
			searchAllLocationsInParallel();																		
		}else if(e.getSource() == btnLocations){
			new UnderDevelopmentDialog(btnLocations.getLocationOnScreen());
		}else if(e.getSource() == btnExpand){
			new UnderDevelopmentDialog(btnExpand.getLocationOnScreen());
		}else if(e.getSource() == btnColaps){
			new UnderDevelopmentDialog(btnColaps.getLocationOnScreen());
		}else if(e.getSource() == btnRetrieve){
			new UnderDevelopmentDialog(btnRetrieve.getLocationOnScreen());
		}
	}		
	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	DicomManager dicomMgr = DicomManagerFactory.getInstance();
	GridManager gridMgr = GridManagerFactory.getInstance();
	int numThreads = 1;
	ExecutorService exeService = Executors.newFixedThreadPool(numThreads);
	boolean searchAllLocationsInParallel(){		
		List<PacsLocation> pacsLocs = dicomMgr.getPacsLocations();
		List<GridLocation> gridLocs = gridMgr.getGridLocations();
		if(pacsLocs.size() == 0 && gridLocs.size() == 0){return true;}				
		if(criteria == null){return false;}
		Boolean bln = criteriaPanel.verifyCriteria(criteria);
		if(bln){
			//Logic when criteria are specified
			numOfCurrentLoc = 0;
			totalNumLocs = pacsLocs.size() + gridLocs.size();
			resultTree.rootNode.removeAllChildren();
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();			
			//set progress bar text
			//1.Create arguments for runnable query(AttributList, Location)						
			for(int i = 0; i < pacsLocs.size(); i++){							
				PacsLocation loc = pacsLocs.get(i);								
				DicomQuery dicomQuery = new DicomQuery(criteria, loc);
				dicomQuery.addDataAccessListener(this);
				exeService.execute(dicomQuery);	
				
				/*GlobalSearchQuery globalQuery = new GlobalSearchQuery(criteria, loc);
				globalQuery.addGlobalSearchListener(this);
				Thread t = new Thread(globalQuery);
				t.start();*/
				
			}												
			//2.Create arguments for runnable query(CQL, Location)			
			GridUtil gridUtil = gridMgr.getGridUtil();
			CQLQuery cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.SERIES);
			if(cql == null){return false;}
			for(int i = 0 ; i < gridLocs.size(); i++){								
				GridQuery gridQuery = new GridQuery(cql, gridLocs.get(i), null, null);				
				gridQuery.addDataAccessListener(this);
				Thread t = new Thread(gridQuery); 					
				t.start();	
			}														
		}else{
			return false;
		}
		return true;
	}
	
	int numOfCurrentLoc = 0;			//numOfCurrentLoc 
	int totalNumLocs = 0;	//totalNumLocs is used to stop progress bar after query for all locations are exhausted	
	public void searchResultAvailable(QueryEvent e) {
		DicomQuery dicomQuery = (DicomQuery)e.getSource();
		SearchResult result = dicomQuery.getSearchResult();		        
		if(result == null){			
			resultTree.updateNodes(result);
		}else{
			resultTree.updateNodes(result);			
		}
		numOfCurrentLoc++;
		if(numOfCurrentLoc == totalNumLocs){
			progressBar.setString("GlobalSearch finished");
			progressBar.setIndeterminate(false);
		}				
	}

	@Override
	public void queryResultsAvailable(QueryEvent e) {		
		GridQuery gridQuery = (GridQuery)e.getSource();
		SearchResult result = gridQuery.getSearchResult();							
		resultTree.updateNodes(result);			
		numOfCurrentLoc++;
		if(numOfCurrentLoc == totalNumLocs){
			progressBar.setString("GlobalSearch finished");
			progressBar.setIndeterminate(false);
		}			
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}	
}