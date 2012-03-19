/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

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
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionEvent;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.ItemNode;
import edu.wustl.xipHost.gui.checkboxTree.PatientNode;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.gui.checkboxTree.SeriesNode;
import edu.wustl.xipHost.gui.checkboxTree.StudyNode;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSPanel extends JPanel implements ActionListener, XDSSearchListener, ApplicationListener, 
									ListSelectionListener, QueryListener, DataSelectionListener {
	final static Logger logger = Logger.getLogger(XDSPanel.class);
	JPanel pdqLocationSelectionPanel = new JPanel();
	JLabel lblPdqTitle = new JLabel("Select Patient Demographic Supplier:");		
	ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");	
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);		
    DefaultComboBoxModel pdqComboModel;	
	JComboBox pdqList;	
	XDSManager xdsManager;

	XDSSearchCriteriaPanel criteriaPanel = new XDSSearchCriteriaPanel();	
	SearchResultTree resultTree = new SearchResultTree();
	JScrollPane treeView = new JScrollPane(resultTree);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Font font_1 = new Font("Tahoma", 0, 13);
	Font font_2 = new Font("Tahoma", 0, 12);		
	Border border = BorderFactory.createLoweredBevelBorder();		
	JLabel infoLabel = new JLabel("UNDER   DEVELOPMENT");
	
	public XDSPanel(){
		setBackground(xipColor);		
		pdqComboModel = new DefaultComboBoxModel();
		pdqList = new JComboBox(pdqComboModel);
		xdsManager = XDSManagerFactory.getInstance();
		List<PDQLocation> pdqLocations = xdsManager.getPDQLocations();
		for(int i = 0; i < pdqLocations.size(); i++){
			pdqComboModel.addElement(pdqLocations.get(i));
		}
		ComboBoxRenderer pdqListRenderer = new ComboBoxRenderer();		
		pdqList.setRenderer(pdqListRenderer);
		pdqList.setMaximumRowCount(10);
		pdqList.setSelectedIndex(0);
		PDQLocation itemPDQ = (PDQLocation)pdqList.getSelectedItem();
		selectedPDQLocation = (PDQLocation)itemPDQ;
		pdqList.setPreferredSize(new Dimension(465, 25));
		pdqList.setFont(font_2);
		pdqList.setEditable(false);		
		pdqList.addActionListener(this);
		lblPdqTitle.setForeground(Color.WHITE);
		pdqLocationSelectionPanel.add(lblPdqTitle);		
		pdqLocationSelectionPanel.add(pdqList);
		lblGlobus.setToolTipText("PDQ supplier locations");
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
		pdqLocationSelectionPanel.add(lblGlobus);		
		pdqLocationSelectionPanel.setBackground(xipColor);
		buildLayoutPDQLocationSelectionPanel();

		criteriaPanel.btnSearchPatientID.addActionListener(this);
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search XDS");	
		criteriaPanel.getPatientList().addListSelectionListener(this);
		leftPanel.add(pdqLocationSelectionPanel);
		leftPanel.add(criteriaPanel);				
		resultTree.addDataSelectionListener(this);
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);	
		rightPanel.add(treeView);
		leftPanel.setBackground(xipColor);
		rightPanel.setBackground(xipColor);
		infoLabel.setForeground(Color.ORANGE);
		add(infoLabel);
		add(leftPanel);
		add(rightPanel);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    add(progressBar);
        buildLeftPanelLayout();
	    buildRightPanelLayout();
	    buildLayout();
	}

	PDQLocation selectedPDQLocation;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == pdqList){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			selectedPDQLocation = (PDQLocation)item;
		}
		else if(e.getSource() == criteriaPanel.getPatientIDQueryButton()){									
			criteriaPanel.getListModel().removeAllElements();
			resultTree.rootNode.removeAllChildren();
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			AttributeList criteria = criteriaPanel.getFilterList();										
			if(criteriaPanel.verifyCriteria(criteria)){																													
				XDSPatientIDQuery xdsQueryPatientID = new XDSPatientIDQuery(criteria, selectedPDQLocation);
				xdsQueryPatientID.addXDSSearchListener(this);
				Thread t = new Thread(xdsQueryPatientID);
				t.start();
			} else {
				//if no criteria specified do ...				
			}						
		}else if(e.getSource() == criteriaPanel.getQueryButton()){			
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			XDSDocumentQuery xsdQuery = new XDSDocumentQuery(selectedID.getPatID());
			xsdQuery.addQueryListener(this);
			Thread t = new Thread(xsdQuery);
			t.start();
		}
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
        constraints.insets.right = 15;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(leftPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 15;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
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
        constraints.anchor = GridBagConstraints.CENTER;
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
        constraints.insets.left = 5;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(pdqLocationSelectionPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
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
        constraints.insets.top = 0;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);               
	}
	
	void buildLayoutPDQLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        pdqLocationSelectionPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblPdqTitle, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(pdqList, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus, constraints);                
	}

	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(440, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof PDQLocation) {
			    	renderer.setText(((PDQLocation)value).getShortName());
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

	public static void main(String[] args){
		JFrame frame = new JFrame();
		XDSPanel panel = new XDSPanel();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);		
	}
	
	
	SearchResult result;
	@Override
	public void queryResultsAvailable(QueryEvent e) {
		XDSDocumentQuery source = (XDSDocumentQuery) e.getSource();
		result = source.getxsdQueryResponse();				        
		if(result == null){			
			resultTree.updateNodes(result);
		}else{
			resultTree.updateNodes(result);				
		}							
		progressBar.setString("XDS Search finished");
		progressBar.setIndeterminate(false);	
	}
	

	public void patientIDsAvailable(List<XDSPatientIDResponse> patientIDs) {				
		if(patientIDs != null && patientIDs.size() != 0){
			progressBar.setString("Patient ID(s) found");
			progressBar.setIndeterminate(false);
			for(int i = 0; i < patientIDs.size(); i++){
				criteriaPanel.getListModel().addElement(patientIDs.get(i));				
			}			
		}else{
			progressBar.setString("Patient ID(s) not found");					
		}		
	}

	XDSPatientIDResponse selectedID;
	public void valueChanged(ListSelectionEvent e) {
		JList list = ((JList)e.getSource());		
		selectedID = (XDSPatientIDResponse)list.getSelectedValue();
		criteriaPanel.getQueryButton().setEnabled(true);
	}

	Application targetApp = null;
	@Override
	public void launchApplication(ApplicationEvent event, ApplicationTerminationListener listener) {
		logger.debug("Current data source tab: " + this.getClass().getName());
		// If nothing is selected, there is nothing to launch with
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)resultTree.getRootNode();
		boolean isDataSelected = false;
		if(rootNode != null){
			if(rootNode.getChildCount() != 0){
				DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode) rootNode.getFirstChild();
				int numOfPatients = locationNode.getChildCount();
				if (numOfPatients == 0){
					logger.warn("No data is selected");
					new ExceptionDialog("Cannot launch selected application.", 
							"No dataset selected. Please query and select data nodes.",
							"Launch Application Dialog");
					return;
				} else {
					for(int i = 0; i < numOfPatients; i++){
						DefaultMutableTreeNode childLocationNode = (DefaultMutableTreeNode) locationNode.getChildAt(i);
						if (childLocationNode.getUserObject() instanceof Item) {
							ItemNode existingItemNode = (ItemNode) childLocationNode;
							if (existingItemNode.isSelected() == true) {
								isDataSelected = true;
								break;
							} else {
								continue;
							}
						}
						PatientNode existingPatientNode = (PatientNode) childLocationNode;
						if(existingPatientNode.isSelected() == true){
							isDataSelected = true;
							break;
						} else {
							int numOfStudies = existingPatientNode.getChildCount();
							for(int j = 0; j < numOfStudies; j++){
								DefaultMutableTreeNode childPatientNode = (DefaultMutableTreeNode) existingPatientNode.getChildAt(j);
								if (childPatientNode.getUserObject() instanceof Item) {
									ItemNode existingItemNode = (ItemNode) childPatientNode;
									if (existingItemNode.isSelected() == true) {
										isDataSelected = true;
										break;
									} else {
										continue;
									}
								}
								StudyNode existingStudyNode = (StudyNode)childPatientNode;
								if(existingStudyNode.isSelected() == true){
									isDataSelected = true;
									break;
								} else {
									int numOfSeries = existingStudyNode.getChildCount();
									for(int k = 0; k < numOfSeries; k++){
										DefaultMutableTreeNode childStudyNode = (DefaultMutableTreeNode) existingStudyNode.getChildAt(k);
										if (childStudyNode.getUserObject() instanceof Item) {
											ItemNode existingItemNode = (ItemNode) childStudyNode;
											if (existingItemNode.isSelected() == true) {
												isDataSelected = true;
												break;
											} else {
												continue;
											}
										}
										SeriesNode existingSeriesNode = (SeriesNode)childStudyNode;
										if(existingSeriesNode.isSelected() == true){
											isDataSelected = true;
											break;
										} else {
											int numOfItems = existingSeriesNode.getChildCount();
											for(int m = 0; m < numOfItems; m++) {
												ItemNode existingItemNode = (ItemNode) existingSeriesNode.getChildAt(m);
												if (existingItemNode.isSelected() == true) {
													isDataSelected = true;
													break;
												}
												
											}
										}
									}
								}
							}
						}
					}
					if(isDataSelected == false){
						logger.warn("No data is selected");
						new ExceptionDialog("Cannot launch selected application.", 
								"No dataset selected. Please select data nodes.",
								"Launch Application Dialog");
						return;
					}
				}
			} else {
				logger.warn("No data is selected");
				new ExceptionDialog("Cannot launch selected application.", 
						"No dataset selected. Please query and select data nodes.",
						"Launch Application Dialog");
				return;
			}
		} else {
			logger.warn("No data is selected");
			new ExceptionDialog("Cannot launch selected application.", 
					"No dataset selected. Please query and select data nodes.",
					"Launch Application Dialog");
			return;
		}

		if(isDataSelected){
			// Determine which application button the user clicked, and retrieve info about the application
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
			Query query = new XDSDocumentQuery(selectedID.getPatID());
			File tmpDir = ApplicationManagerFactory.getInstance().getTmpDir();
			/*
			List<XDSDocumentItem> selectedItems = resultTree.getSelectedItems();
			for(int i = 0; i < selectedItems.size(); i++){
				XDSDocumentItem xdsDocItem = selectedItems.get(i);
				DocumentEntryType docType = xdsDocItem.getDocumentType();
				CX patientId = xdsDocItem.getPatientId();
				String homeCommunityId = xdsDocItem.getHomeCommunityId();
				XDSDocumentRetrieve xdsRetrieve = new XDSDocumentRetrieve(docType, patientId, homeCommunityId);
			}						
			*/
		
			Retrieve retrieve = new XDSDocumentRetrieve();
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setQueryDataSource(query);
				instanceApp.setRetrieveDataSource(retrieve);
				instanceApp.setDoSave(false);
				instanceApp.setApplicationTmpDir(tmpDir);
				appMgr.addApplication(instanceApp);	
				instanceApp.addApplicationTerminationListener(listener);
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
				targetApp = instanceApp;
			}else{
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setQueryDataSource(query);
				app.setRetrieveDataSource(retrieve);
				app.setApplicationTmpDir(tmpDir);
				app.addApplicationTerminationListener(listener);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
				targetApp = app;
			}	
		}
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}

	SearchResult selectedDataSearchResult;
	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
	}
}
