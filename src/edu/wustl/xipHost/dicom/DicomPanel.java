/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.State;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.application.AppButton;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationEvent;
import edu.wustl.xipHost.application.ApplicationListener;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.application.ApplicationTerminationListener;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
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
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomPanel extends JPanel implements ActionListener, ApplicationListener, DataAccessListener, DataSelectionListener {
	final static Logger logger = Logger.getLogger(DicomPanel.class);
	JPanel calledLocationSelectionPanel = new JPanel();
	JLabel lblTitle = new JLabel("Select Called DICOM Service Location:");		
	ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");	
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);		
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JComboBox listCalledLocations;	
	DefaultComboBoxModel comboModel;
	JPanel callingLocationSelectionPanel = new JPanel();		
	JLabel lblTitle2 = new JLabel("Retrieve to / Calling DICOM Location:");
	JLabel lblGlobus2 = new JLabel(iconGlobus, JLabel.CENTER);	
	JComboBox listCallingLocations;
	DefaultComboBoxModel comboModelCalling;
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();
	SearchResultTree resultTree = new SearchResultTree();
	JScrollPane treeView = new JScrollPane(resultTree);   	
	JProgressBar progressBar = new JProgressBar();
	Font font_1 = new Font("Tahoma", 0, 13);
	Font font_2 = new Font("Tahoma", 0, 12);		
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Border border = BorderFactory.createLoweredBevelBorder();
	DicomManager dicomMgr;
	JPanel btnSelectionPanel = new JPanel();
	JButton btnSelectAll = new JButton("Select All");
	JButton btnDeselectAll = new JButton("Deselect All");
	
	public DicomPanel(){
		setBackground(xipColor);
		resultTree.addDataSelectionListener(this);
		comboModel = new DefaultComboBoxModel();
		listCalledLocations = new JComboBox(comboModel);
		dicomMgr = DicomManagerFactory.getInstance();
		List<PacsLocation> pacsLocs = dicomMgr.getPacsLocations();
		for(int i = 0; i < pacsLocs.size(); i++){
			comboModel.addElement(pacsLocs.get(i));
		}
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		listCalledLocations.setRenderer(renderer);
		listCalledLocations.setMaximumRowCount(10);
		listCalledLocations.setSelectedIndex(0);
		Object itemCalledLocation = listCalledLocations.getSelectedItem();
		calledPacsLocation = (PacsLocation)itemCalledLocation;
		listCalledLocations.setPreferredSize(new Dimension(465, 25));
		listCalledLocations.setFont(font_2);
		listCalledLocations.setEditable(false);		
		listCalledLocations.addActionListener(this);
		
		comboModelCalling = new DefaultComboBoxModel();
		listCallingLocations = new JComboBox(comboModelCalling);
		for(int i = 0; i < pacsLocs.size(); i++){
			comboModelCalling.addElement(pacsLocs.get(i));
		}
		listCallingLocations.setRenderer(renderer);
		listCallingLocations.setMaximumRowCount(10);
		listCallingLocations.setSelectedIndex(0);
		Object itemCallingLocation = listCallingLocations.getSelectedItem();
		callingPacsLocation = (PacsLocation)itemCallingLocation;
		listCallingLocations.setPreferredSize(new Dimension(465, 25));
		listCallingLocations.setFont(font_2);
		listCallingLocations.setEditable(false);		
		listCallingLocations.addActionListener(this);
		
		lblTitle.setForeground(Color.WHITE);
		lblTitle2.setForeground(Color.WHITE);
		calledLocationSelectionPanel.add(lblTitle);		
		calledLocationSelectionPanel.add(listCalledLocations);
		callingLocationSelectionPanel.add(lblTitle2);
		callingLocationSelectionPanel.add(listCallingLocations);
		lblGlobus.setToolTipText("DICOM service locations");
		lblGlobus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGlobus.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																
					//System.out.println("Display locations");
					//displayLocations();
					new UnderDevelopmentDialog(lblGlobus.getLocationOnScreen());
				}
			}
		);
		calledLocationSelectionPanel.add(lblGlobus);		
		calledLocationSelectionPanel.setBackground(xipColor);
		callingLocationSelectionPanel.add(lblGlobus2);
		callingLocationSelectionPanel.setBackground(xipColor);
		buildLayoutLocationSelectionPanel();
		lblGlobus2.setToolTipText("DICOM service locations");
		lblGlobus2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGlobus2.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																
					new UnderDevelopmentDialog(lblGlobus2.getLocationOnScreen());
				}
			}
		);			
		criteriaPanel.getQueryButton().addActionListener(this);			
		criteriaPanel.setQueryButtonText("Query");		
		leftPanel.add(calledLocationSelectionPanel);
		leftPanel.add(criteriaPanel);							    
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
	    treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);			
        rightPanel.add(treeView);       
        buildLayoutCallingLocationSelectionPanel();
        rightPanel.add(callingLocationSelectionPanel);        
        btnSelectAll.setBackground(xipColor);
		btnSelectAll.addActionListener(this);
		btnDeselectAll.setBackground(xipColor);
		btnDeselectAll.addActionListener(this);
		btnSelectionPanel.setBackground(xipColor);
		btnSelectionPanel.setLayout(new FlowLayout());
		btnSelectionPanel.add(btnSelectAll);
		btnSelectionPanel.add(btnDeselectAll);
		rightPanel.add(btnSelectionPanel);
        
        leftPanel.setBackground(xipColor);
        rightPanel.setBackground(xipColor);
        buildLeftPanelLayout();
        buildRightPanelLayout();
				
		add(leftPanel);
		add(rightPanel);
		
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
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 0;         
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
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
	
	void buildLeftPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        leftPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        //constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(calledLocationSelectionPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(criteriaPanel, constraints); 
	}
	
	
	void buildRightPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        rightPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        //constraints.insets.top = 30;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnSelectionPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;        
        constraints.insets.top = 5;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        //constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(callingLocationSelectionPanel, constraints);   
	}
	
	void buildLayoutLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        calledLocationSelectionPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        //constraints.insets.top = 10;
        //constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblTitle, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        //constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(listCalledLocations, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 10;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus, constraints);                
	}
	
	void buildLayoutCallingLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        callingLocationSelectionPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        //constraints.insets.top = 10;
        //constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblTitle2, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        //constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(listCallingLocations, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 10;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus2, constraints);                
	}	

	PacsLocation calledPacsLocation;
	PacsLocation callingPacsLocation;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == listCalledLocations){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			calledPacsLocation = (PacsLocation)item;			
		}else if(e.getSource() == listCallingLocations){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			callingPacsLocation = (PacsLocation)item;
		}else if(e.getSource() == criteriaPanel.getQueryButton()){
			resultTree.rootNode.removeAllChildren();
			selectedDataSearchResult = new SearchResult();
			resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			setCriteriaList(criteriaPanel.getFilterList());				
			Boolean bln = criteriaPanel.verifyCriteria(criteria);						
			if(bln && calledPacsLocation != null){
				DicomQuery dicomQuery = new DicomQuery(criteria, calledPacsLocation);
				dicomQuery.addDataAccessListener(this);
				Thread t = new Thread(dicomQuery);				
				t.start();				
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}
		} else if (e.getSource() == btnSelectAll){
			resultTree.selectAll(true);
		} else if (e.getSource() == btnDeselectAll){
			resultTree.selectAll(false);
		}	
	}

	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	SearchResult result;
	@Override
	public void queryResultsAvailable(QueryEvent e) {		
		DicomQuery dicomQuery = (DicomQuery)e.getSource();
		result = dicomQuery.getSearchResult();		        
		resultTree.updateNodes(result);
		progressBar.setString("DicomSearch finished");
		progressBar.setIndeterminate(false);		
	}
	

	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		   	
	}
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(440, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof PacsLocation) {
			    	renderer.setText(((PacsLocation)value).getShortName());
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
	
	Application targetApp = null;
	@Override
	public void launchApplication(ApplicationEvent event, ApplicationTerminationListener listener) {
		logger.debug("Current data source tab: " + this.getClass().getName());
		// If nothing is selected, there is nothing to launch with
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)resultTree.getRootNode();
		boolean isDataSelected = DataSelectionValidator.isDataSelected(rootNode);
		if(isDataSelected){
			// Determine which application button the user clicked, and retrieve info about the application
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
			Query query = new DicomQuery();
			File tmpDir = ApplicationManagerFactory.getInstance().getTmpDir();
			dicomMgr.setDefaultCallingPacsLOcation(callingPacsLocation);
			//DicomRetrieve retrieve = new DicomRetrieve(criteriaPanel.getFilterList(), calledPacsLocation, callingPacsLocation);
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setQueryDataSource(query);
				//instanceApp.setRetrieveDataSource(retrieve);
				instanceApp.setDataSourceDomainName("edu.wustl.xipHost.dicom.DicomRetrieve");
				instanceApp.setDataSource(callingPacsLocation);
				instanceApp.setDoSave(false);
				instanceApp.setApplicationTmpDir(tmpDir);
				appMgr.addApplication(instanceApp);
				instanceApp.addApplicationTerminationListener(listener);
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
				targetApp = instanceApp;
			}else{
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setQueryDataSource(query);
				//app.setRetrieveDataSource(retrieve);
				app.setDataSourceDomainName("edu.wustl.xipHost.dicom.DicomRetrieve");
				app.setDataSource(callingPacsLocation);
				app.setApplicationTmpDir(tmpDir);
				app.addApplicationTerminationListener(listener);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
				targetApp = app;
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
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}
}
