/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.nbia;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.State;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import edu.wustl.xipHost.application.AppButton;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationEvent;
import edu.wustl.xipHost.application.ApplicationListener;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.application.ApplicationTerminationListener;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionEvent;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionValidator;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.iterator.IterationTarget;


/**
 * @author Jaroslaw Krych
 *
 */
public class DataSourcePanel extends JPanel implements ActionListener, ApplicationListener, DataAccessListener, DataSelectionListener {	
	final static Logger logger = Logger.getLogger(DataSourcePanel.class);
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();			
	SearchResultPanel searchResultPanel = new SearchResultPanel();
	SearchResultTree resultTree;
	JProgressBar progressBar = new JProgressBar();	
	Color xipColor = new Color(51, 51, 102);
	//Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	DataSource ds;
	DataAccessListener l;
	
	public DataSourcePanel(){
		l = this;
		setBackground(xipColor);							
		ds = DataSourceFactory.getDataSource();
		searchResultPanel.list.addActionListener(this);
		resultTree = searchResultPanel.getGridJTreePanel(); 
		resultTree.addDataSelectionListener(this);
		resultTree.addMouseListener(ml);
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search");		
		add(criteriaPanel);
		add(searchResultPanel);
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    add(progressBar);	    
		buildLayout();
	}
	
	void buildLayout(){				
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);         
                
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(criteriaPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(searchResultPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1; 
        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(progressBar, constraints);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getQueryButton()){
			logger.info("Executing query process ...");
			//Remove existing children from the result JTree and reload JTree
			resultTree.getRootNode().removeAllChildren();
			selectedDataSearchResult = new SearchResult();
			resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
			progressBar.setBackground(new Color(156, 162, 189));
		    progressBar.setForeground(xipColor);
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);			
			progressBar.updateUI();										
			AttributeList criteria = null;	
			Boolean bln = criteriaPanel.verifyCriteria(criteria);
			if(bln){
				activeSubqueryMonitor = false;
				Query query = ds.getQuery();
				Map<Integer, Object> dicomCriteria = null;
				Map<String, Object> aimCriteria = null;
				QueryTarget target = null;
				SearchResult previousSearchResult = null;
				Object queriedObject = null;
				query.setQuery(dicomCriteria, aimCriteria, target, previousSearchResult, queriedObject);				
				query.addDataAccessListener(this);
				Thread t = new Thread(query); 					
				t.start();		
			} else {
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}
		}
	}	
	
	
	
	
	SearchResult result;
	boolean activeSubqueryMonitor;
	boolean subqueryCompleted;
	@Override
	public void queryResultsAvailable(QueryEvent e) {
		synchronized(this){
			Query query = (Query)e.getSource();
			result = query.getSearchResult();
			if(activeSubqueryMonitor == true){
				subqueryCompleted = true;
			}
			synchronized(result){
				searchResultPanel.getGridJTreePanel().updateNodes(result);
				if(activeSubqueryMonitor){
					result.notify();
				}
				activeSubqueryMonitor = true;
			}
		}	
		progressBar.setString("Search finished");
		progressBar.setIndeterminate(false);
	}
	
	@Override
	public void notifyException(String message) {		
		progressBar.setIndeterminate(false);
		progressBar.invalidate();
		progressBar.setForeground(Color.RED);
		progressBar.validate();
		progressBar.setString("Exception: " + message);
		result = null;							
		searchResultPanel.getGridJTreePanel().updateNodes(result);								
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				resultTree.scrollRowToVisible(queryNodeIndex);
				int visibleRows = resultTree.getVisibleRowCount();
				resultTree.scrollRowToVisible(queryNodeIndex + visibleRows);
			}			 
		});
		
	}
	
	Object selectedNode;
	int queryNodeIndex = 0;
	boolean wasDoubleClick = false;
	MouseListener ml = new MouseAdapter(){
	     public void mouseClicked(final MouseEvent e) {	        
	    	 	int x = e.getX();
		     	int y = e.getY();
			    int row = resultTree.getRowForLocation(x, y);
			    final TreePath  path = resultTree.getPathForRow(row);
	    	 	if (e.getClickCount() == 2) {
		        	wasDoubleClick = true;
		        	subqueryCompleted = false;
			     	if (path != null) {    		
			     		DefaultMutableTreeNode queryNode = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();		    
			     		if (queryNode == null) return;		 
			     		if (!queryNode.isRoot()) {
			     			queryNodeIndex = resultTree.getRowForPath(new TreePath(queryNode.getPath()));
			     			selectedNode = queryNode.getUserObject();			     			
			     			AttributeList initialCriteria = criteriaPanel.getFilterList();
			     			if(selectedNode instanceof Patient){			     				
			     				Patient selectedPatient = Patient.class.cast(selectedNode);
			     				logger.info("Starting node query: " + selectedPatient.toString());
			     				//Retrieve studies for selected patient
			     				searchResultPanel.cbxAnnot.setEnabled(true);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);
			     				progressBar.updateUI();				     				
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);			  
			     				try {			     					
			     					{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
			     						a.addValue(selectedPatient.getPatientID());
			     						initialCriteria.put(t,a); }
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());								} 											     						
			     				Boolean bln = criteriaPanel.verifyCriteria(initialCriteria);
			     				if(bln){	
			     					{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
		     							try {
											a.addValue("");
										} catch (DicomException e1) {
											logger.error(e1, e1);
											notifyException(e1.getMessage());
										}
		     							initialCriteria.put(t,a);}
			     					Query query = ds.getQuery();
			     					Map<Integer, Object> dicomCriteria = null;
			     					Map<String, Object> aimCriteria = null;
			     					QueryTarget target = null;
			     					SearchResult previousSearchResult = null;
			     					Object queriedObject = null;
			     					query.setQuery(dicomCriteria, aimCriteria, target, previousSearchResult, queriedObject);				
			     					query.addDataAccessListener(l);
			     					Thread t = new Thread(query); 					
			     					t.start();								
			     				}else{
			     					progressBar.setString("");
			     					progressBar.setIndeterminate(false);
			     				}			     				
			     				repaint();
			     			}else if(selectedNode instanceof Study){
			     				Study selectedStudy = Study.class.cast(selectedNode);
			     				logger.info("Starting node query: " + selectedStudy.toString());
			     				//Retrieve series for selected study
			     				searchResultPanel.cbxAnnot.setEnabled(true);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);
			     				progressBar.updateUI();	
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			     				try {			     								     					 			     									     						
			     					{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
				     					a.addValue(selectedStudy.getStudyInstanceUID());									 
				     					initialCriteria.put(t,a); }
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());
								} 
			     				//setCriteriaList(updatedCriteria);				
			     				Boolean bln = criteriaPanel.verifyCriteria(initialCriteria);
			     				if(bln){											
			     					try {
			     						{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 		     							
											a.addValue("");										
											initialCriteria.put(t,a);}
										} catch (DicomException e1) {
											logger.error(e1, e1);
											notifyException(e1.getMessage());
										}		     						
			     					Query query = ds.getQuery();
			     					Map<Integer, Object> dicomCriteria = null;
			     					Map<String, Object> aimCriteria = null;
			     					QueryTarget target = null;
			     					SearchResult previousSearchResult = null;
			     					Object queriedObject = null;
			     					query.setQuery(dicomCriteria, aimCriteria, target, previousSearchResult, queriedObject);				
			     					query.addDataAccessListener(l);
			     					Thread t = new Thread(query); 					
			     					t.start();								
			     				}else{
			     					progressBar.setString("");
			     					progressBar.setIndeterminate(false);
			     				}			     				
			     				repaint();
			     			} else if(selectedNode instanceof Series){
			     				Series selectedSeries = Series.class.cast(selectedNode);
			     				logger.info("Starting node query: " + selectedSeries.toString());
			     				//Retrieve items for selected series
			     				searchResultPanel.cbxAnnot.setEnabled(true);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);
			     				progressBar.updateUI();
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			     				try {			     								     					 			     									     						
			     					{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
				     					a.addValue(selectedSeries.getSeriesInstanceUID());									 
				     					initialCriteria.put(t,a); }
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());
								}
								Boolean bln = criteriaPanel.verifyCriteria(initialCriteria);
			     				if(bln){											
			     					try {
			     						{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 		     							
											a.addValue("");										
											initialCriteria.put(t,a);}
									} catch (DicomException e1) {
											logger.error(e1, e1);
											notifyException(e1.getMessage());
									}		     						
			     					Query query = ds.getQuery();
			     					Map<Integer, Object> dicomCriteria = null;
			     					Map<String, Object> aimCriteria = null;
			     					QueryTarget target = null;
			     					SearchResult previousSearchResult = null;
			     					Object queriedObject = null;
			     					query.setQuery(dicomCriteria, aimCriteria, target, previousSearchResult, queriedObject);				
			     					query.addDataAccessListener(l);
			     					Thread t = new Thread(query); 					
			     					t.start();								
			     				}else{
			     					progressBar.setString("");
			     					progressBar.setIndeterminate(false);
			     				}			     				
			     				repaint();
			     				
			     				
			     			}
			     		}
			     	}
		        } 
		    }
	};	
	
	
	SearchResult selectedDataSearchResult;
	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
		if(logger.isDebugEnabled()){
			logger.debug("Value of selectedDataSearchresult: ");
			if(selectedDataSearchResult != null) {
				List<Patient> patients = selectedDataSearchResult.getPatients();
				for(Patient logPatient : patients){
					logger.debug(logPatient.toString());
					List<Study> studies = logPatient.getStudies();
					for(Study logStudy : studies){
						logger.debug("   " + logStudy.toString());
						List<Series> series = logStudy.getSeries();
						for(Series logSeries : series){
							logger.debug("      " + logSeries.toString() + " / Contains subset of items: " + logSeries.containsSubsetOfItems());
							List<Item> items = logSeries.getItems();
							for(Item logItem : items){
								logger.debug("         " + logItem.toString());
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void launchApplication(ApplicationEvent event, ApplicationTerminationListener listener) {
		logger.debug("Current data source tab: " + this.getClass().getName());
		// If nothing is selected, there is nothing to launch with
		//check if selectedDataSearchresult is not null and at least one PatientNode is selected
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)resultTree.getRootNode();
		boolean isDataSelected = DataSelectionValidator.isDataSelected(rootNode);
		if(isDataSelected){
			AppButton btn = (AppButton)event.getSource();
			ApplicationManager appMgr = ApplicationManagerFactory.getInstance(); 
			Application app = appMgr.getApplication(btn.getApplicationUUID());
			String appID = app.getID().toString();
			logger.debug("Application internal id: " + appID);
			String instanceName = app.getName();
			logger.debug("Application name: " + instanceName);
			File instanceExePath = app.getExePath();
			logger.debug("Exe path: " + instanceExePath);
			String instanceVendor = app.getVendor();
			logger.debug("Vendor: " + instanceVendor);
			String instanceVersion = app.getVersion();
			logger.debug("Version: " + instanceVersion);
			File instanceIconFile = app.getIconFile();
			String type = app.getType();
			logger.debug("Type: " + type);
			boolean requiresGUI = app.requiresGUI();
			logger.debug("Requires GUI: " + requiresGUI);
			String wg23DataModelType = app.getWG23DataModelType();
			logger.debug("WG23 data model type: " + wg23DataModelType);
			int concurrentInstances = app.getConcurrentInstances();
			logger.debug("Number of allowable concurrent instances: " + concurrentInstances);
			IterationTarget iterationTarget = app.getIterationTarget();
			logger.debug("IterationTarget: " + iterationTarget.toString());
			
			//Check if application to be launched is not running.
			//If yes, create new application instance
			State state = app.getState();
			// TODO Fill in with whatever is needed to make this work with Grid
			// TODO replace AVTQuery and AVTRetrieve2 with GridQuery and GridRetrieve
			Query query = ds.getQuery();
			File tmpDir = ApplicationManagerFactory.getInstance().getTmpDir();
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setQueryDataSource(query);
				instanceApp.setDoSave(false);
				appMgr.addApplication(instanceApp);	
				instanceApp.addApplicationTerminationListener(listener);
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			}else{
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setQueryDataSource(query);
				app.setApplicationTmpDir(tmpDir);
				app.addApplicationTerminationListener(listener);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			}
		}
	}

	
	Map<Series, Study>  getSelectedSeries(){
		return null;
	}
	
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

