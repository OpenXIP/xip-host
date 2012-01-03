/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
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
import edu.wustl.xipHost.application.ApplicationTerminationListener;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionEvent;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionValidator;
import edu.wustl.xipHost.gui.checkboxTree.PatientNode;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTreeProgressive;
import edu.wustl.xipHost.gui.checkboxTree.SeriesNode;
import edu.wustl.xipHost.gui.checkboxTree.StudyNode;
import edu.wustl.xipHost.hostControl.HostConfigurator;

public class AVTPanel extends JPanel implements ActionListener, ItemListener, DataAccessListener, ApplicationListener, DataSelectionListener {
	final static Logger logger = Logger.getLogger(AVTPanel.class);
	SearchCriteriaPanelAVT criteriaPanel = new SearchCriteriaPanelAVT();	
	SearchResultTreeProgressive resultTree = new SearchResultTreeProgressive();
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
	JPanel optionsPanel = new JPanel();
	JPanel cbxPanel = new JPanel();
	JPanel btnSelectionPanel = new JPanel();
	JButton btnSelectAll = new JButton("Select All");
	JButton btnDeselectAll = new JButton("Deselect All");
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	DataAccessListener l;
	
	public AVTPanel(){
		l = this;
		setBackground(xipColor);				
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search AD");
		leftPanel.add(criteriaPanel);			    
		resultTree.addMouseListener(ml);
		resultTree.addDataSelectionListener(this);
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);			
		rightPanel.add(treeView);
		cbxDicom.setBackground(xipColor);
		cbxDicom.setForeground(Color.WHITE);
		cbxDicom.addItemListener(this);
		cbxAimSeg.setBackground(xipColor);
		cbxAimSeg.setForeground(Color.WHITE);
		cbxAimSeg.addItemListener(this);
		cbxPanel.setLayout(new GridLayout(1, 2));		
		btnSelectAll.setBackground(xipColor);
		btnSelectAll.addActionListener(this);
		btnDeselectAll.setBackground(xipColor);
		btnDeselectAll.addActionListener(this);
		btnSelectionPanel.setBackground(xipColor);
		btnSelectionPanel.setLayout(new FlowLayout());
		btnSelectionPanel.add(btnSelectAll);
		btnSelectionPanel.add(btnDeselectAll);
		cbxPanel.add(cbxDicom);
		cbxPanel.add(cbxAimSeg);
		cbxPanel.setBackground(xipColor);
		optionsPanel.add(btnSelectionPanel);
		optionsPanel.add(cbxPanel);
		optionsPanel.setBackground(xipColor);
		buildOptionPanelLayout();
		rightPanel.add(optionsPanel);	
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
	
	Query avtQuery;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getQueryButton()){												
			logger.info("Starting AVT query.");
			resultTree.rootNode.removeAllChildren();
			cbxDicom.setEnabled(false);
			cbxAimSeg.setEnabled(false);
			selectedDataSearchResult = new SearchResult();
			resultTree.setSelectedDataSearchResult(selectedDataSearchResult);
			queryNode = null;
			skipSearchResultTreeUpdate = true;
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
				avtQuery = new AVTQuery(adDicomCriteria, adAimCriteria, QueryTarget.PATIENT, null, null);
				avtQuery.addDataAccessListener(this);
				Thread t = new Thread(avtQuery);
				t.start();
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}																	
		} else if (e.getSource() == btnSelectAll){
			resultTree.selectAll(true);
			//FIX
			//Update happens three times, first when selectAll is called and second and third when cbxDicom and cbxAimSeg are set.
			skipSearchResultTreeUpdate = true;
			cbxDicom.setSelected(true);
			skipSearchResultTreeUpdate = true;
			cbxAimSeg.setSelected(true);
			cbxDicom.setEnabled(true);
			cbxAimSeg.setEnabled(true);
		} else if (e.getSource() == btnDeselectAll){
			resultTree.selectAll(false);
			skipSearchResultTreeUpdate = true;
			cbxDicom.setSelected(false);
			skipSearchResultTreeUpdate = true;
			cbxAimSeg.setSelected(false);
			cbxDicom.setEnabled(false);
			cbxAimSeg.setEnabled(false);
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
        constraints.anchor = GridBagConstraints.CENTER;
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
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);               
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        //constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        //constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(optionsPanel, constraints);
	}
	
	void buildOptionPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        optionsPanel.setLayout(layout); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        //constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(btnSelectionPanel, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        //constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.EAST;
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
		progressBar.setString("AVT AD Search finished");
		progressBar.setIndeterminate(false);				
	}
	
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {		
		
	}	
	
	@Override
	public void notifyException(String message) {
		progressBar.setIndeterminate(false);		
		progressBar.setForeground(Color.RED);
		progressBar.setBackground(Color.GREEN);
		progressBar.setString("Exception: " + message);
		result = null;							
		resultTree.updateNodes(result);
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
	DefaultMutableTreeNode queryNode;
	MouseListener ml = new MouseAdapter(){  
		public void mouseClicked(final MouseEvent e) {
			int x = e.getX();
	     	int y = e.getY();
		    int row = resultTree.getRowForLocation(x, y);
		    final TreePath  path = resultTree.getPathForRow(row);
		    queryNode = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();
	    	if (e.getClickCount() == 2) {
	    		wasDoubleClick = true;
	    		subqueryCompleted = false;
		     	if (path != null) {		    
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
		     					avtQuery = new AVTQuery(adCriteria, adAimCriteria, QueryTarget.STUDY, result, selectedNode);
		     					avtQuery.addDataAccessListener(l);
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
		     					avtQuery = new AVTQuery(adCriteria, adAimCriteria, QueryTarget.SERIES, result, selectedNode);
		     					avtQuery.addDataAccessListener(l);
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
		     					avtQuery = new AVTQuery(adCriteria, adAimCriteria, QueryTarget.ITEM, result, selectedNode);
		     					avtQuery.addDataAccessListener(l);
		     					Thread t = new Thread(avtQuery); 					
		     					t.start();							
		     				}else{
		     					progressBar.setString("");
		     					progressBar.setIndeterminate(false);
		     				}			     				
		     				repaint();
		     			} else if(selectedNode instanceof Item) {
		     				return;
		     			}
	     			}
		     	}
	        } else if (e.getClickCount() == 1) {
	        	if(queryNode instanceof PatientNode || queryNode instanceof StudyNode || queryNode instanceof SeriesNode) {
			    	cbxDicom.setSelected(false);
				    cbxAimSeg.setSelected(false);
			    }
	        }
     	}
	};
	
	
	boolean skipSearchResultTreeUpdate = false;
	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox)e.getItemSelectable();
		resultTree.setDoubleClicked(false);
		if (source == cbxDicom){
			if (e.getStateChange() == ItemEvent.SELECTED){
				cbxDicom.setSelected(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED){
				cbxDicom.setSelected(false);
			}
			if(skipSearchResultTreeUpdate == false) {
				if (cbxDicom.isSelected() && cbxAimSeg.isSelected()) {
					synchronized(result){
						resultTree.update(queryNode, RetrieveTarget.DICOM_AIM_SEG);
					}
				} else if(cbxDicom.isSelected() && !cbxAimSeg.isSelected()) {
					resultTree.update(queryNode, RetrieveTarget.DICOM);
				} else if (!cbxDicom.isSelected() && cbxAimSeg.isSelected()) {
					resultTree.update(queryNode, RetrieveTarget.AIM_SEG);
				}  else if (!cbxDicom.isSelected() && !cbxAimSeg.isSelected()) {
					resultTree.update(queryNode, RetrieveTarget.NONE);
				}
			}
		} else if (source == cbxAimSeg){
			if (e.getStateChange() == ItemEvent.SELECTED){
				cbxAimSeg.setSelected(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED){
				cbxAimSeg.setSelected(false);
			}
			if(skipSearchResultTreeUpdate == false) {
				if (cbxAimSeg.isSelected() && cbxDicom.isSelected()) {
					synchronized(result){
						resultTree.update(queryNode, RetrieveTarget.DICOM_AIM_SEG);
					}
				} else if(cbxAimSeg.isSelected() && !cbxDicom.isSelected()) {
					resultTree.update(queryNode, RetrieveTarget.AIM_SEG);
				} else if(!cbxAimSeg.isSelected() && cbxDicom.isSelected()) {
					resultTree.update(queryNode, RetrieveTarget.DICOM);
				} else if (!cbxAimSeg.isSelected() && !cbxDicom.isSelected()) {
					resultTree.update(queryNode, RetrieveTarget.NONE);
				}
			}
		}
		skipSearchResultTreeUpdate = false;
	}

	
	@Override
	public void launchApplication(ApplicationEvent event, ApplicationTerminationListener listener) {
		logger.debug("Current data source tab: " + this.getClass().getName());
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
			Query query = (Query) avtQuery;
			RetrieveTarget retrieveTarget = null;
			if(cbxDicom.isSelected() == true && cbxAimSeg.isSelected() == false){
				retrieveTarget = RetrieveTarget.DICOM;
			} else if (cbxDicom.isSelected() == true && cbxAimSeg.isSelected() == true) {
				retrieveTarget = RetrieveTarget.DICOM_AIM_SEG;
			} else if (cbxDicom.isSelected() == false && cbxAimSeg.isSelected() == true) {
				retrieveTarget = RetrieveTarget.AIM_SEG;
			}
			File tmpDir = ApplicationManagerFactory.getInstance().getTmpDir();
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setQueryDataSource(query);
				instanceApp.setRetrieveTarget(retrieveTarget);
				instanceApp.setDataSourceDomainName("edu.wustl.xipHost.avt2ext.AVTRetrieve");
				instanceApp.setDoSave(false);
				instanceApp.setApplicationTmpDir(tmpDir);
				appMgr.addApplication(instanceApp);	
				instanceApp.addApplicationTerminationListener(listener);
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			}else{
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setQueryDataSource(query);
				app.setRetrieveTarget(retrieveTarget);
				app.setDataSourceDomainName("edu.wustl.xipHost.avt2ext.AVTRetrieve");
				app.setApplicationTmpDir(tmpDir);
				app.addApplicationTerminationListener(listener);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());			
			}	
		}				
	}

	SearchResult selectedDataSearchResult;
	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
		selectedDataSearchResult.setOriginalCriteria(result.getOriginalCriteria());
		selectedDataSearchResult.setDataSourceDescription("Selected data for " + result.getDataSourceDescription());
		if(selectedDataSearchResult.getPatients().size() > 0) {
			cbxDicom.setEnabled(true);
			cbxAimSeg.setEnabled(true);
		}
		//if(logger.isDebugEnabled()){
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
							boolean enableCheckBoxes = false;
							for(Item logItem : items){
								logger.debug("         " + logItem.toString());
								if(enableCheckBoxes == false) {
									cbxDicom.setEnabled(true);
									cbxAimSeg.setEnabled(true);
								}
							}
						}
					}
				}
			//}
		}
	}
}
