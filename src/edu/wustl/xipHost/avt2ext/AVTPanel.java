/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
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
import edu.wustl.xipHost.application.ApplicationBar;
import edu.wustl.xipHost.application.ApplicationEvent;
import edu.wustl.xipHost.application.ApplicationListener;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionEvent;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.NodeSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.PatientNode;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTreeProgressive;
import edu.wustl.xipHost.gui.checkboxTree.SeriesNode;
import edu.wustl.xipHost.gui.checkboxTree.StudyNode;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;

public class AVTPanel extends JPanel implements ActionListener, ItemListener, AVTListener, ApplicationListener, DataSelectionListener {
	final static Logger logger = Logger.getLogger(AVTPanel.class);
	SearchCriteriaPanelAVT criteriaPanel = new SearchCriteriaPanelAVT();	
	SearchResultTree resultTree = new SearchResultTreeProgressive();
	JScrollPane treeView = new JScrollPane(resultTree);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	ApplicationBar appBar = new ApplicationBar();
	JProgressBar progressBar = new JProgressBar();	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Font font_1 = new Font("Tahoma", 0, 13);
	Border border = BorderFactory.createLoweredBevelBorder();		
	JCheckBox cbxDicom = new JCheckBox("DICOM", false);
	JCheckBox cbxAimSeg = new JCheckBox("AIM plus SEG", false);
	JPanel cbxPanel = new JPanel();
	JPanel btnPanel = new JPanel();
	AVTListener l;
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	NodeSelectionListener nodeSelectionListener = new NodeSelectionListener();
	
	public AVTPanel(){
		l = this;
		nodeSelectionListener.addDataSelectionListener(this);
		setBackground(xipColor);				
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search AD");
		leftPanel.add(criteriaPanel);			    
	    //resultTree.addTreeSelectionListener(this);
		resultTree.addMouseListener(ml);
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);			
		rightPanel.add(treeView);
		cbxDicom.setBackground(xipColor);
		cbxDicom.setForeground(Color.WHITE);
		cbxDicom.addItemListener(this);
		cbxDicom.setSelected(false);
		cbxAimSeg.setBackground(xipColor);
		cbxAimSeg.setForeground(Color.WHITE);
		cbxAimSeg.addItemListener(this);
		cbxAimSeg.setSelected(false);
		cbxPanel.setLayout(new GridLayout(1, 2));		
		cbxPanel.add(cbxDicom);
		cbxPanel.add(cbxAimSeg);
		cbxPanel.setBackground(xipColor);
		rightPanel.add(cbxPanel);	
		leftPanel.setBackground(xipColor);
		rightPanel.setBackground(xipColor);
		add(leftPanel);
		add(rightPanel);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    add(progressBar);
	    buildRightPanelLayout();
	    buildLayout();
	}
		
	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	AVTQuery avtQuery;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getQueryButton()){												
			logger.info("Starting AVT query.");
			resultTree.rootNode.removeAllChildren();
			progressBar.setBackground(new Color(156, 162, 189));
		    progressBar.setForeground(xipColor);
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);			
			progressBar.updateUI();
			setCriteriaList(criteriaPanel.getFilterList());	
			Map<String, Object> adAimCriteria = criteriaPanel.panelAVT.getSearchCriteria();
			Map<Integer, Object> dicomPrivateTagCriterion = criteriaPanel.panelPrivateTag.getSearchCriteria();			
			Boolean bln = criteriaPanel.verifyCriteria(criteria, adAimCriteria);			
			boolean isPrivateAttribute = dicomPrivateTagCriterion.keySet().size() > 0;
			if(bln || isPrivateAttribute){				
				//create AVT AD criteria map HashMap<Integer, Object>
				Map<Integer, Object> adDicomCriteria = DicomUtil.convertToADDicomCriteria(criteriaPanel.getFilterList());				
				//add private tag criterion
				Iterator<Integer> iter = dicomPrivateTagCriterion.keySet().iterator();
				while(iter.hasNext()){
					Integer key = iter.next();
					Object value = dicomPrivateTagCriterion.get(key);
					adDicomCriteria.put(key, value);
				}
				activeSubqueryMonitor = false;
				//pass adCriteria to AVTQuery
				avtQuery = new AVTQuery(adDicomCriteria, adAimCriteria, ADQueryTarget.PATIENT, null, null);
				avtQuery.addAVTListener(this);
				Thread t = new Thread(avtQuery);
				t.start();
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}																	
		}
		
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
        constraints.insets.right = 15;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 15;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(rightPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 2; 
        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(appBar, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 3; 
        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(progressBar, constraints);
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
        constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);               
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(cbxPanel, constraints);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		AVTPanel panel = new AVTPanel();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);		
	}

	SearchResult result;
	boolean activeSubqueryMonitor;
	public void searchResultsAvailable(AVTSearchEvent e) {				
		synchronized(this){
			result = (SearchResult) e.getSource();
				synchronized(result){
					resultTree.updateNodes2(result);
					if(activeSubqueryMonitor){
						result.notify();
					}
					activeSubqueryMonitor = true;
				}
		}
		progressBar.setString("AVT AD Search finished");
		progressBar.setIndeterminate(false);				
	}
	
	List<File> retrivedFiles;
	List<File> allRetrivedFiles;
	int numRetrieveThreadsStarted;
	int numRetrieveThreadsReturned;
	@SuppressWarnings("unchecked")
	public void retriveResultsAvailable(AVTRetrieveEvent e) {		
		retrivedFiles = (List<File>) e.getSource();	
		synchronized(retrivedFiles){
			allRetrivedFiles.addAll(retrivedFiles);
		}		
		numRetrieveThreadsReturned++;
		if(numRetrieveThreadsStarted == numRetrieveThreadsReturned){
			finalizeRetrieve();
		}
	}	
		
	synchronized void finalizeRetrieve(){		
		progressBar.setString("AD Retrieve finished");
		progressBar.setIndeterminate(false);						
		//allretrivedFiles are checked for duplicate items, both DICOM and AIM
		//File names are compared. Duplicate items are removed.
		int size = allRetrivedFiles.size();
		Map<String, String> filePaths = new LinkedHashMap<String, String>();
		for(int i = 0; i < size; i++){
			try {
				String fileName = allRetrivedFiles.get(i).getName();
				String filePath = allRetrivedFiles.get(i).getCanonicalPath();
				filePaths.put(fileName, filePath);
			} catch (IOException e) {
				logger.error(e, e);
				notifyException(e.getMessage());
			}
		}
		allRetrivedFiles.clear();
		Iterator<String> iter = filePaths.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String filePath = filePaths.get(key);
			allRetrivedFiles.add(new File(filePath));
		}
		File[] files = new File[allRetrivedFiles.size()];		 
		allRetrivedFiles.toArray(files);		
		FileManager fileMgr = FileManagerFactory.getInstance();						
        fileMgr.run(files);
        criteriaPanel.getQueryButton().setBackground(xipBtn);
		criteriaPanel.getQueryButton().setEnabled(true);						
	}
	
	@Override
	public void notifyException(String message) {
		progressBar.setIndeterminate(false);		
		progressBar.setForeground(Color.RED);
		progressBar.setBackground(Color.GREEN);
		progressBar.setString("Exception: " + message);
		result = null;							
		//resultTree.updateNodes(result);
		resultTree.updateNodes2(result);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				resultTree.scrollRowToVisible(queryNodeIndex);
				int visibleRows = resultTree.getVisibleRowCount();
				resultTree.scrollRowToVisible(queryNodeIndex + visibleRows);
			}			 
		});
		criteriaPanel.getQueryButton().setBackground(xipBtn);
		criteriaPanel.getQueryButton().setEnabled(true);		
		cbxDicom.setSelected(false);
		cbxAimSeg.setSelected(false);
	}
	
	Object selectedNode;
	int queryNodeIndex = 0;
	boolean wasDoubleClick = false;
	
	MouseListener ml = new MouseAdapter(){  
		public void mouseClicked(final MouseEvent e) {
			int x = e.getX();
	     	int y = e.getY();
	     	nodeSelectionListener.setSearchResultTree(resultTree);
	     	nodeSelectionListener.setSelectionCoordinates(x, y);
	     	nodeSelectionListener.setSearchResult(result);
		    int row = resultTree.getRowForLocation(x, y);
		    final TreePath  path = resultTree.getPathForRow(row);
	    	if (e.getClickCount() == 2){
	    		wasDoubleClick = true;
	    		nodeSelectionListener.setWasDoubleClick(wasDoubleClick);
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
		     				progressBar.setString("Processing search request ...");
		     				progressBar.setIndeterminate(true);
		     				progressBar.updateUI();				     				
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
							Map<String, Object> adAimCriteria = criteriaPanel.panelAVT.getSearchCriteria();
							Boolean bln = criteriaPanel.verifyCriteria(initialCriteria, adAimCriteria);
		     				if(bln){		     					
		     					Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(criteriaPanel.getFilterList());
		     					//After PatientID is added to the initial criteria and criteria are verified, adCriteria Map is created.
		     					//In the next step initialCriteria is cleared of inserted PatientID and rolled back to the original value. 	
		     					{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
		 							try {
		 								a.addValue(patientID);
									} catch (DicomException e1) {
										logger.error(e1, e1);
										notifyException(e1.getMessage());
									}
	 							initialCriteria.put(t,a);}		     						     							     					
		     					avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.STUDY, result, selectedNode);
		     					avtQuery.addAVTListener(l);
		     					Thread t = new Thread(avtQuery); 					
		     					t.start();
		     				} else {
		     					progressBar.setString("");
		     					progressBar.setIndeterminate(false);
		     				}	     										     															     						     				
		     				repaint();
		     			}else if(selectedNode instanceof Study){
		     				Study selectedStudy = Study.class.cast(selectedNode);
		     				logger.info("Starting node query: " + selectedStudy.toString());
		     				//Retrieve studies for selected patient
		     				progressBar.setString("Processing search request ...");
		     				progressBar.setIndeterminate(true);
		     				progressBar.updateUI();	
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
							Map<String, Object> adAimCriteria = criteriaPanel.panelAVT.getSearchCriteria();
							Boolean bln = criteriaPanel.verifyCriteria(initialCriteria, adAimCriteria);
		     				if(bln){											     							     					
		     					Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(criteriaPanel.getFilterList());
		     					//After studyInstanceUID is added to the initial criteria and criteria are verified, adCriteria Map is created.
		     					//In the next step initialCriteria is cleared of inserted studyInstanceUID and rolled back to the original value.
		     					try {	
		     						{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 		     								     							
	     							a.addValue(studyInstanceUID);										
									initialCriteria.put(t,a);}
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());
								}		     															     					
		     					avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.SERIES, result, selectedNode);
		     					avtQuery.addAVTListener(l);
		     					Thread t = new Thread(avtQuery); 					
		     					t.start();							
		     				}else{
		     					progressBar.setString("");
		     					progressBar.setIndeterminate(false);
		     				}			     				
		     				repaint();
		     			} else if(selectedNode instanceof Series){
		     				Series selectedSeries = Series.class.cast(selectedNode);
		     				logger.info("Starting node query: " + selectedSeries.toString());
		     				//Retrieve annotations for selected series		     				
		     				progressBar.setString("Processing search request ...");
		     				progressBar.setIndeterminate(true);
		     				progressBar.updateUI();
		     				String[] characterSets = { "ISO_IR 100" };
		     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
		     				String seriesInstanceUID = initialCriteria.get(TagFromName.SeriesInstanceUID).getDelimitedStringValuesOrEmptyString();
	     					try {			     								     					 			     									     						
		     					{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
			     					a.addValue(selectedSeries.getSeriesInstanceUID());									 
			     					initialCriteria.put(t,a); }
							} catch (DicomException e1) {
								logger.error(e1, e1);
								notifyException(e1.getMessage());
							} 
							Map<String, Object> adAimCriteria = criteriaPanel.panelAVT.getSearchCriteria();
							Boolean bln = criteriaPanel.verifyCriteria(initialCriteria, adAimCriteria);
		     				if(bln){											     							     					
		     					Map<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(criteriaPanel.getFilterList());
		     					//After seriesInstanceUID is added to the initial criteria and criteria are verified, adCriteria Map is created.
		     					//In the next step initialCriteria is cleared of inserted seriesInstanceUID and rolled back to the original value.
		     					try {	
		     						{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 		     								     							
	     							a.addValue(seriesInstanceUID);										
									initialCriteria.put(t,a);}
								} catch (DicomException e1) {
									logger.error(e1, e1);
									notifyException(e1.getMessage());
								}		     															     					
		     					avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.ITEM, result, selectedNode);
		     					avtQuery.addAVTListener(l);
		     					Thread t = new Thread(avtQuery); 					
		     					t.start();							
		     				}else{
		     					progressBar.setString("");
		     					progressBar.setIndeterminate(false);
		     				}			     				
		     				repaint();
		     			}
	     			}
		     		updateSelectedDataSearchResult(queryNode);
		     	}
	        } else {
	        	Timer timer = new Timer(300, nodeSelectionListener);
	        	timer.setRepeats(false);
	        	timer.start();
	        }
	    }
	};
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox)e.getItemSelectable();
		if (source == cbxDicom){
			if (e.getStateChange() == ItemEvent.SELECTED){
				cbxDicom.setSelected(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED){
				cbxDicom.setSelected(false);
			}
		} else if (source == cbxAimSeg){
			if (e.getStateChange() == ItemEvent.SELECTED){
				cbxAimSeg.setSelected(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED){
				cbxAimSeg.setSelected(false);
			}
		}
	}

	@Override
	public void launchApplication(ApplicationEvent event ) {
		logger.debug("Current data source tab: " + AVTPanel.class.getName());
		//check if DICOM or AIM and SEG check boxes are selected.
		if(cbxDicom.isSelected() == false && cbxAimSeg.isSelected() == false){
			logger.debug("Is dataset type spesified: " + false);
			logger.warn("DICOM or AIM and SEG boxes not selected");
			new ExceptionDialog("Cannot launch selected application.", 
					"Ensure DICOM or AIM plus SEG check boxes are selected.",
					"Launch Application Dialog");
			return;
    	}
		//check if selectedDataSearchresult is not null and at least one PatientNode is selected
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
						PatientNode existingPatientNode = (PatientNode) locationNode.getChildAt(i);
						if(existingPatientNode.isSelected() == true){
							isDataSelected = true;
							break;
						} else {
							int numOfStudies = existingPatientNode.getChildCount();
							for(int j = 0; j < numOfStudies; j++){
								StudyNode existingStudyNode = (StudyNode)existingPatientNode.getChildAt(j);
								if(existingStudyNode.isSelected() == true){
									isDataSelected = true;
									break;
								} else {
									int numOfSeries = existingStudyNode.getChildCount();
									for(int k = 0; k < numOfSeries; k++){
										SeriesNode existingSeriesNode = (SeriesNode)existingStudyNode.getChildAt(k);
										if(existingSeriesNode.isSelected() == true){
											isDataSelected = true;
											break;
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
			Query query = avtQuery;
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setDataSource(query);
				instanceApp.setDoSave(false);
				appMgr.addApplication(instanceApp);		
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			}else{
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setDataSource(query);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());			
			}	
		}				
	}

	SearchResult selectedDataSearchResult;
	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
	}
	
	/**
	 * Method is used to update selectedDataSearchResult after sub-queries
	 * If PatientNode selected before sub-query, all of the studies belonging to this patient should not 
	 * only be selected but also added to the selectedDataSearchResult object
	 * @throws InterruptedException 
	 */
	void updateSelectedDataSearchResult(DefaultMutableTreeNode node) {
		synchronized(result){
			try {
				result.wait();
				//PatientNode is not included, because Patient node are found always in first query and not not sub-queries
				if (node instanceof PatientNode){
					Patient patient = (Patient)node.getUserObject();
					if(selectedDataSearchResult != null){
						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
						if(selectedPatient != null){
							if(((PatientNode) node).isSelected()){
								Patient updatedPatient = result.getPatient(patient.getPatientID());
								List<Study> studies = updatedPatient.getStudies();
								for(Study study : studies){
									if(!selectedPatient.contains(study.getStudyInstanceUID())){
										selectedPatient.addStudy(study);
									}							
								}
							}
						}
					}
				} else if (node instanceof StudyNode){
					Study study = (Study)node.getUserObject();
					DefaultMutableTreeNode patientNode = (DefaultMutableTreeNode) node.getParent();
					Patient patient = (Patient)patientNode.getUserObject();
					if(selectedDataSearchResult != null){
						Patient selectedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
						if(selectedPatient != null){
							Study selectedStudy = selectedPatient.getStudy(study.getStudyInstanceUID());
							if(selectedStudy != null){
								if(((StudyNode) node).isSelected()){
									Study updatedStudy = result.getPatient(patient.getPatientID()).getStudy(study.getStudyInstanceUID());
									List<Series> series = updatedStudy.getSeries();
									for(Series oneSeries : series){
										if(!selectedStudy.contains(oneSeries.getSeriesInstanceUID())){
											selectedStudy.addSeries(oneSeries);
										}
									}
								} 
							}						
						}		
					}						 
				}
			} catch (InterruptedException e) {
				logger.error(e, e);
			}
		}
	}
}
