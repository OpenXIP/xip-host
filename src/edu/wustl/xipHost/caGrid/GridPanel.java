/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ObjectLocator;
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
import edu.wustl.xipHost.caGrid.GridUtil;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionEvent;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionValidator;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.iterator.IterationTarget;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;


/**
 * @author Jaroslaw Krych
 *
 */
public class GridPanel extends JPanel implements ActionListener, ApplicationListener, QueryListener, DataSelectionListener, RetrieveListener {	
	final static Logger logger = Logger.getLogger(GridPanel.class);
	JPanel locationSelectionPanel = new JPanel();
	JLabel lblTitle = new JLabel("Select caGRID DICOM Service Location:");		
	ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");	
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);		
    DefaultComboBoxModel comboModel;	
	JComboBox list;
	//JCheckBox enableSecuredConn = new JCheckBox("Enable Secured Connection");
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();			
	JPanel leftPanel = new JPanel();
	RightPanel rightPanel = new RightPanel();
	SearchResultTree resultTree;
	JProgressBar progressBar = new JProgressBar();	
	Font font_1 = new Font("Tahoma", 0, 13);
	Font font_2 = new Font("Tahoma", 0, 12);		
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	
	GridManager gridMgr;
	QueryListener l;
	
	public GridPanel(){
		l = this;
		setBackground(xipColor);							
		comboModel = new DefaultComboBoxModel();
		list = new JComboBox(comboModel);
		gridMgr = GridManagerFactory.getInstance();
		List<GridLocation> gridTypeDicomLocations = gridMgr.getGridTypeDicomLocations();
		for(int i = 0; i < gridTypeDicomLocations.size(); i++){
			comboModel.addElement(gridTypeDicomLocations.get(i));
		}
		List<GridLocation> gridTypeAimLocations = gridMgr.getGridTypeAimLocations();
		for(int i = 0; i < gridTypeAimLocations.size(); i++){
			rightPanel.comboModel.addElement(gridTypeAimLocations.get(i));
		}		
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		list.setRenderer(renderer);
		list.setMaximumRowCount(10);
		list.setSelectedIndex(0);
		GridLocation itemDICOM = (GridLocation)list.getSelectedItem();
		selectedGridTypeDicomService = (GridLocation)itemDICOM;
		gridMgr.setSelectedGridLocation(selectedGridTypeDicomService);
		list.setPreferredSize(new Dimension(465, 25));
		list.setFont(font_2);
		list.setEditable(false);		
		list.addActionListener(this);
		rightPanel.list.setSelectedIndex(0);
		GridLocation itemAIM = (GridLocation)rightPanel.list.getSelectedItem();
		selectedGridTypeAimService = (GridLocation)itemAIM;			
		rightPanel.list.addActionListener(this);
		resultTree = rightPanel.getGridJTreePanel(); 
		resultTree.addDataSelectionListener(this);
		resultTree.addMouseListener(ml);
		rightPanel.lblGlobus.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																
					new UnderDevelopmentDialog(rightPanel.lblGlobus.getLocationOnScreen());
				}
			}
		);
		lblTitle.setForeground(Color.WHITE);
		locationSelectionPanel.add(lblTitle);		
		locationSelectionPanel.add(list);
		//enableSecuredConn.setSelected(false);
		//enableSecuredConn.setForeground(Color.WHITE);
		//enableSecuredConn.addItemListener(this);
		//locationSelectionPanel.add(enableSecuredConn);
		lblGlobus.setToolTipText("DICOM service locations");
		lblGlobus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGlobus.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																					
					new UnderDevelopmentDialog(lblGlobus.getLocationOnScreen());
				}
			}
		);
		locationSelectionPanel.add(lblGlobus);		
		locationSelectionPanel.setBackground(xipColor);		
		buildLayoutLocationSelectionPanel();
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search");
		leftPanel.add(criteriaPanel);
		leftPanel.add(locationSelectionPanel);
		leftPanel.setBackground(xipColor);
		buildLeftPanelLayout();
		add(leftPanel);
		add(rightPanel);
		
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
		rightPanel.btnSelectAll.addActionListener(this);
		rightPanel.btnDeselectAll.addActionListener(this);
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
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(rightPanel, constraints);
        
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
	
	void buildLayoutLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        locationSelectionPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;     
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblTitle, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(list, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus, constraints);  
        
        /*constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 3;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(enableSecuredConn, constraints);*/  
	}	
	
	void buildLeftPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        leftPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.left = 20;
        constraints.insets.right = 10;    
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(locationSelectionPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 20;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(criteriaPanel, constraints); 
	}
	
	
	GridLocation selectedGridTypeDicomService;
	GridLocation selectedGridTypeAimService;
	Query gridQuery;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == list){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			selectedGridTypeDicomService = (GridLocation)item;
			gridMgr.setSelectedGridLocation(selectedGridTypeDicomService);
		}else if(e.getSource() == rightPanel.list){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			selectedGridTypeAimService = (GridLocation)item;			
		}else if(e.getSource() == criteriaPanel.getQueryButton()){
			logger.info("Executing GRID query ...");
			//Remove existing children from the result JTree and reload JTree
			resultTree.getRootNode().removeAllChildren();
			selectedDataSearchResult = new SearchResult();
			resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
			queryNode = null;
			rightPanel.cbxAnnot.setEnabled(true);
			progressBar.setBackground(new Color(156, 162, 189));
		    progressBar.setForeground(xipColor);
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			AttributeList criteria = criteriaPanel.getFilterList();				
			Boolean bln = criteriaPanel.verifyCriteria(criteria);
			if(bln && selectedGridTypeDicomService != null){											
				GridUtil gridUtil = gridMgr.getGridUtil();
				CQLQuery cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.PATIENT);							
				activeSubqueryMonitor = false;
				gridQuery = new GridQuery(cql, criteria, selectedGridTypeDicomService, null, null);				
				gridQuery.addQueryListener(this);
				Thread t = new Thread(gridQuery); 					
				t.start();									
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}
		} else if (e.getSource() == rightPanel.btnSelectAll){
			resultTree.selectAll(true);
		} else if (e.getSource() == rightPanel.btnDeselectAll){
			resultTree.selectAll(false);
		}	
	} 
	
	
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(440, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof GridLocation) {
			    	renderer.setText(((GridLocation)value).getShortName());
			    	renderer.setBackground(Color.WHITE);
			    }
			    if(cellHasFocus || isSelected){
			    	renderer.setBackground(xipLightBlue);
			    	renderer.setForeground(Color.WHITE);
			    	renderer.setBorder(new LineBorder(Color.DARK_GRAY));
			    }else{
			    	renderer.setBorder(null);
			    }			    
			    renderer.setPreferredSize(preferredSize);
			    return renderer;
			  }
		
	}
	
	SearchResult result;
	boolean activeSubqueryMonitor;
	boolean subqueryCompleted;
	@Override
	public void queryResultsAvailable(QueryEvent e) {
		Query query = (Query) e.getSource();
		result = query.getSearchResult();
		if(activeSubqueryMonitor == true){
			subqueryCompleted = true;
		}
		synchronized(result){
			resultTree.updateNodes(result);
			resultTree.updateNodeProgressive(queryNode);
			activeSubqueryMonitor = true;
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
		rightPanel.getGridJTreePanel().updateNodes(result);								
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				resultTree.scrollRowToVisible(queryNodeIndex);
				int visibleRows = resultTree.getVisibleRowCount();
				resultTree.scrollRowToVisible(queryNodeIndex + visibleRows);
			}			 
		});
		
	}
	
	List<CQLQuery> getAimRetrieveCriteria() {
		/*List<CQLQuery> retrieveCriterias = new ArrayList<CQLQuery>();
		Map<Series, Study> map = getSelectedSeries();
		Set<Series> seriesSet = map.keySet();
		Iterator<Series> iter = seriesSet.iterator();
		while (iter.hasNext()){
			Series series = iter.next();
			String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
			String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();			
			//CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(selectedStudyInstanceUID, selectedSeriesInstanceUID, null);;
			CQLQuery aimCQL = AIMTCGADataServiceHelper.generateImageAnnotationQuery(selectedStudyInstanceUID, selectedSeriesInstanceUID, null);
			retrieveCriterias.add(aimCQL);
		}
		return retrieveCriterias;*/
		return null;
	}
	
	Object selectedNode;
	int queryNodeIndex = 0;
	boolean wasDoubleClick = false;
	DefaultMutableTreeNode queryNode;
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
			     		queryNode = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();		    
			     		if (queryNode == null) return;		 
			     		if (!queryNode.isRoot()) {
			     			queryNodeIndex = resultTree.getRowForPath(new TreePath(queryNode.getPath()));
			     			selectedNode = queryNode.getUserObject();			     			
			     			AttributeList initialCriteria = criteriaPanel.getFilterList();
			     			if(selectedNode instanceof Patient){			     				
			     				Patient selectedPatient = Patient.class.cast(selectedNode);
			     				logger.info("Starting node query: " + selectedPatient.toString());
			     				//Retrieve studies for selected patient
			     				rightPanel.cbxAnnot.setEnabled(true);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);		     				
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			     				String patientID = initialCriteria.get(TagFromName.PatientID).getDelimitedStringValuesOrEmptyString();
			     				try {			     					
			     					{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
			     						a.addValue(selectedPatient.getPatientID());
			     						initialCriteria.put(t,a); }
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());								
								} 											     						
		     					Boolean bln = criteriaPanel.verifyCriteria(initialCriteria);
		     					if(bln && selectedGridTypeDicomService != null){											
			     					GridUtil gridUtil = gridMgr.getGridUtil();
			     					CQLQuery cql = gridUtil.convertToCQLStatement(initialCriteria, CQLTargetName.STUDY);				
			     					//After PatientID is added to the initial criteria and criteria are verified, cql is created.
			     					//In the next step initialCriteria is rolled back to its original value. 	
			     					{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
		     							try {
		     								a.addValue(patientID);
										} catch (DicomException e1) {
											logger.error(e1, e1);
											notifyException(e1.getMessage());
										}
		     							initialCriteria.put(t,a);
		     						}
			     					gridQuery = new GridQuery(cql, initialCriteria, selectedGridTypeDicomService, result, selectedNode);
			     					gridQuery.addQueryListener(l);
			     					Thread t = new Thread(gridQuery); 					
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
			     				rightPanel.cbxAnnot.setEnabled(true);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);			     				
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			     				String studyInstanceUID = initialCriteria.get(TagFromName.StudyInstanceUID).getDelimitedStringValuesOrEmptyString();
			     				try {			     								     					 			     									     						
			     					{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
				     					a.addValue(selectedStudy.getStudyInstanceUID());									 
				     					initialCriteria.put(t,a); }
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());
								} 			
			     				Boolean bln = criteriaPanel.verifyCriteria(initialCriteria);
			     				if(bln && selectedGridTypeDicomService != null){											
			     					GridUtil gridUtil = gridMgr.getGridUtil();
			     					CQLQuery cql = gridUtil.convertToCQLStatement(initialCriteria, CQLTargetName.SERIES);	
			     					try {
			     						{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 		     							
			     							a.addValue(studyInstanceUID);									
											initialCriteria.put(t,a);}
										} catch (DicomException e1) {
											logger.error(e1, e1);
											notifyException(e1.getMessage());
										}		     						
			     					gridQuery = new GridQuery(cql, initialCriteria, selectedGridTypeDicomService, result, selectedNode);
			     					gridQuery.addQueryListener(l);
			     					Thread t = new Thread(gridQuery); 					
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
	
	
	public void importedFilesAvailable(GridRetrieveEvent e) {
		if(e.getSource() instanceof AimRetrieve){
			AimRetrieve aimRetrieve = (AimRetrieve)e.getSource();
			List<File> result = aimRetrieve.getRetrievedFiles();
			if(result != null){
				//allRetrivedFiles.addAll(result);
			}
		}		
	}
	
	SearchResult selectedDataSearchResult;
	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
		selectedDataSearchResult.setOriginalCriteria(result.getOriginalCriteria());
		selectedDataSearchResult.setDataSourceDescription("Selected data for " + result.getDataSourceDescription());
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
							logger.debug("      " + logSeries.toString());
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
		//Check if selectedDataSearchresult is not null and at least one PatientNode is selected
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
			String instanceExePath = app.getExePath();
			logger.debug("Exe path: " + instanceExePath);
			String instanceVendor = app.getVendor();
			logger.debug("Vendor: " + instanceVendor);
			String instanceVersion = app.getVersion();
			logger.debug("Version: " + instanceVersion);
			String instanceIconFile = app.getIconPath();
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
			Query query = (Query)gridQuery;
			File tmpDir = ApplicationManagerFactory.getInstance().getTmpDir();
			//RetrieveListener retrieveListener;
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				//retrieveListener = instanceApp.getRetrieveListener();
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setQueryDataSource(query);
				instanceApp.setRetrieveTarget(RetrieveTarget.DICOM);
				instanceApp.setDataSourceDomainName("edu.wustl.xipHost.caGrid.GridRetrieveNBIA");
				instanceApp.setDoSave(false);
				instanceApp.setApplicationTmpDir(tmpDir);
				appMgr.addApplication(instanceApp);	
				instanceApp.addApplicationTerminationListener(listener);
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			}else{
				//retrieveListener = app.getRetrieveListener();
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setQueryDataSource(query);
				app.setRetrieveTarget(RetrieveTarget.DICOM);
				app.setDataSourceDomainName("edu.wustl.xipHost.caGrid.GridRetrieveNBIA");
				app.setApplicationTmpDir(tmpDir);
				app.addApplicationTerminationListener(listener);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			}
			//Retrieve caGrid DICOM and NBIA data first, then launch application
			//List<CQLQuery> criteriaDicom = getDicomRetrieveCriteria();
			/*
			List<CQLQuery> criteriaDicom = null;
			List<CQLQuery> criteriaAim = getAimRetrieveCriteria();
			progressBar.setString("Retrieving data ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			criteriaPanel.getQueryButton().setBackground(Color.GRAY);
			criteriaPanel.getQueryButton().setEnabled(false);
			rightPanel.cbxAnnot.setEnabled(true);																
			if(selectedGridTypeDicomService.getProtocolVersion().equalsIgnoreCase("DICOM")){					
				for(int i = 0; i < criteriaDicom.size(); i++){
					CQLQuery cqlQuery = criteriaDicom.get(i);
					try {				
						Retrieve gridRetrieve = new GridRetrieve(cqlQuery, selectedGridTypeDicomService, gridMgr.getImportDirectory());
						gridRetrieve.addRetrieveListener(retrieveListener);
						Thread t = new Thread(gridRetrieve);
						t.start();
						t.join();
					} catch (IOException e1) {
						logger.error(e1, e1);
					} catch (InterruptedException e) {
						logger.error(e, e);
					}																				
				}
			} else if (selectedGridTypeDicomService.getProtocolVersion().equalsIgnoreCase("NBIA-5.0")){
				String selectedSeriesInstanceUID = getSelectedSeries();			
				Retrieve nbiaRetrieve = new GridRetrieveNCIA(selectedSeriesInstanceUID, selectedGridTypeDicomService, gridMgr.getImportDirectory());
				nbiaRetrieve.addRetrieveListener(retrieveListener);
				Thread t = new Thread(nbiaRetrieve);
				t.start();
				try {
					t.join();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}	*/											
			
			//Retrieve AIM				
			/*if(rightPanel.cbxAnnot.isSelected() && selectedGridTypeAimService != null){
				for(int i = 0; i < criteriaAim.size(); i++){
					CQLQuery aimCQL = criteriaAim.get(i);						
					try {
						Retrieve aimRetrieve = new AimRetrieve(aimCQL, selectedGridTypeAimService, gridMgr.getImportDirectory());
						Thread t = new Thread(aimRetrieve);
						t.start();
						t.join();
					} catch (IOException e1) {
						logger.error(e1, e1);
					} catch (InterruptedException e) {
						logger.error(e, e);
					}					
				}					
			}*/
		}
	}

	
	String  getSelectedSeries(){
		String seriesInstanceUID = null;
		if(selectedDataSearchResult != null) {
			List<Patient> patients = selectedDataSearchResult.getPatients();
			for(Patient logPatient : patients){
				List<Study> studies = logPatient.getStudies();
				for(Study logStudy : studies){
					List<Series> series = logStudy.getSeries();
					for(Series oneSeries : series){
						seriesInstanceUID = oneSeries.getSeriesInstanceUID();
					}
				}
			}
		}
		return seriesInstanceUID;
	}
	
	Map<String, ObjectLocator> objectLocators;
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		objectLocators = (Map<String, ObjectLocator>) e.getSource();
	}

	/*@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource().equals(enableSecuredConn)){
			if(enableSecuredConn.isSelected()) {
				logger.debug("Enabling NBIA/grid DICOM secured connection...");
			} else {
				logger.debug("Disenabling NBIA/grid DICOM secured connection...");
			}
		}
	}*/
	
}

