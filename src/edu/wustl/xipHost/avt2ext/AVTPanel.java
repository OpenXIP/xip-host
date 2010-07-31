/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTreeProgressive;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;

public class AVTPanel extends JPanel implements ActionListener, ItemListener, AVTListener {
	final static Logger logger = Logger.getLogger(AVTPanel.class);
	SearchCriteriaPanelAVT criteriaPanel = new SearchCriteriaPanelAVT();	
	SearchResultTree resultTree = new SearchResultTreeProgressive();
	JScrollPane treeView = new JScrollPane(resultTree);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Font font_1 = new Font("Tahoma", 0, 13);
	Border border = BorderFactory.createLoweredBevelBorder();		
	JCheckBox cbxSeries = new JCheckBox("Series", false);
	JCheckBox cbxAimSeg = new JCheckBox("AIM plus SEG", false);
	JPanel cbxPanel = new JPanel();
	JButton btnRetrieve;
	JButton btnRetrieveAll;
	JPanel btnPanel = new JPanel();
	AVTListener l;
	
	
	public AVTPanel(){
		l = this;
		setBackground(xipColor);				
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search AD");
		leftPanel.add(criteriaPanel);			    
	    //resultTree.addTreeSelectionListener(this);
		resultTree.addMouseListener(ml);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution() - 25));
		treeView.setBorder(border);			
		btnRetrieve = new JButton("Retrieve");
        btnRetrieve.setFont(font_1); 
        btnRetrieve.setFocusable(true);
		btnRetrieve.setEnabled(false);				
		btnRetrieve.setBackground(xipBtn);
		btnRetrieve.setForeground(Color.WHITE);
		btnRetrieve.setPreferredSize(new Dimension(180, 25));
		btnRetrieve.addActionListener(this);
		btnRetrieveAll = new JButton("Retrieve All");
		btnRetrieveAll.setFont(font_1); 
		btnRetrieveAll.setFocusable(true);
		btnRetrieveAll.setEnabled(false);				
		btnRetrieveAll.setBackground(xipBtn);
		btnRetrieveAll.setForeground(Color.WHITE);
		btnRetrieveAll.setPreferredSize(new Dimension(180, 25));
		btnRetrieveAll.addActionListener(this);
		rightPanel.add(treeView);
		cbxSeries.setBackground(xipColor);
		cbxSeries.setForeground(Color.WHITE);
		cbxSeries.addItemListener(this);
		cbxAimSeg.setBackground(xipColor);
		cbxAimSeg.setForeground(Color.WHITE);
		cbxAimSeg.addItemListener(this);
		cbxPanel.setLayout(new GridLayout(1, 2));		
		cbxPanel.add(cbxSeries);
		cbxPanel.add(cbxAimSeg);
		cbxPanel.setBackground(xipColor);
		rightPanel.add(cbxPanel);
		btnPanel.setBackground(xipColor);
		btnPanel.add(btnRetrieve);
		btnPanel.add(btnRetrieveAll);		
		rightPanel.add(btnPanel);
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
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getQueryButton()){												
			logger.info("Starting AVT query.");
			resultTree.rootNode.removeAllChildren();
			resultTree.clearSelectedSeries();
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
				//pass adCriteria to AVTQuery
				AVTQuery avtQuery = new AVTQuery(adDicomCriteria, adAimCriteria, ADQueryTarget.PATIENT, null, null);
				avtQuery.addAVTListener(this);
				Thread t = new Thread(avtQuery);
				t.start();			
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}																	
		} else if (e.getSource() == btnRetrieve){			
			allRetrivedFiles = new ArrayList<File>();
			numRetrieveThreadsStarted = 0;
			numRetrieveThreadsReturned = 0;
			File importDir = HostConfigurator.getHostConfigurator().getHostTmpDir();						
			progressBar.setString("Processing retrieve request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			criteriaPanel.getQueryButton().setBackground(Color.GRAY);
			criteriaPanel.getQueryButton().setEnabled(false);
			btnRetrieve.setBackground(Color.GRAY);
			btnRetrieve.setEnabled(false);												
			Map<Series, Study> map = resultTree.getSelectedSeries();
			Set<Series> seriesSet = map.keySet();
			Iterator<Series> iter = seriesSet.iterator();
			while (iter.hasNext()){
				Series series = iter.next();
				String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
				String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();
				try {
					ADRetrieveTarget retrieveTarget = null;
					if(cbxSeries.isSelected() && cbxAimSeg.isSelected()){
						retrieveTarget = ADRetrieveTarget.DICOM_AND_AIM;
					}else if(cbxSeries.isSelected() && !cbxAimSeg.isSelected()){
						retrieveTarget = ADRetrieveTarget.SERIES;
					}else if(!cbxSeries.isSelected() && cbxAimSeg.isSelected()){
						retrieveTarget = ADRetrieveTarget.AIM_SEG;
					}
					Map<String, Object> adAimCriteria = criteriaPanel.panelAVT.getSearchCriteria();
					AVTRetrieve avtRetrieve = new AVTRetrieve(selectedStudyInstanceUID, selectedSeriesInstanceUID, adAimCriteria, importDir, retrieveTarget);
					avtRetrieve.addAVTListener(this);					
					Thread t = new Thread(avtRetrieve);
					t.start();
					numRetrieveThreadsStarted++;
				} catch (IOException e1) {
					logger.error(e1, e1);
					notifyException(e1.getMessage());
				}	
			}											
		} else if(e.getSource() == btnRetrieveAll){
			new UnderDevelopmentDialog(btnRetrieveAll.getLocationOnScreen());
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
        constraints.gridy = 1; 
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
        constraints.insets.bottom = 5;        
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
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;        
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnPanel, constraints);
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
	public void searchResultsAvailable(AVTSearchEvent e) {
		result = (SearchResult) e.getSource();				
		if(result == null){			
			resultTree.updateNodes(result);
		}else{
			resultTree.updateNodes(result);			
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
		//
		
		File[] files = new File[allRetrivedFiles.size()];		 
		allRetrivedFiles.toArray(files);		
		FileManager fileMgr = FileManagerFactory.getInstance();						
        fileMgr.run(files);	
        criteriaPanel.getQueryButton().setBackground(xipBtn);
		criteriaPanel.getQueryButton().setEnabled(true);		
		btnRetrieve.setEnabled(true);
		btnRetrieve.setBackground(xipBtn);					
	}
	

	@Override
	public void notifyException(String message) {
		progressBar.setIndeterminate(false);		
		progressBar.setForeground(Color.RED);
		progressBar.setBackground(Color.GREEN);
		progressBar.setString("Exception: " + message);
		result = null;							
		resultTree.updateNodes(result);
		resultTree.clearSelectedSeries();
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
		btnRetrieve.setBackground(Color.GRAY);
		btnRetrieve.setEnabled(false);
		btnRetrieveAll.setEnabled(false);	    		 	    		 
		btnRetrieveAll.setBackground(Color.GRAY);
		cbxSeries.setSelected(false);
		cbxAimSeg.setSelected(false);
	}
	
	
	Object selectedNode;
	int queryNodeIndex = 0;
	MouseListener ml = new MouseAdapter(){
	     public void mousePressed(MouseEvent e) {
	    	 if(resultTree.getSelectedSeries().size() > 0 && (cbxSeries.isSelected() || cbxAimSeg.isSelected())){
	    		btnRetrieve.setEnabled(true);	    		 	    			
	 			btnRetrieve.setBackground(xipBtn);
	 			btnRetrieve.setForeground(Color.WHITE);
	 			btnRetrieveAll.setEnabled(true);
	 			btnRetrieveAll.setBackground(xipBtn);
	 			btnRetrieveAll.setForeground(Color.WHITE);
	    	 }else{
	    		btnRetrieve.setEnabled(false);	    		 	    		 
	 			btnRetrieve.setBackground(Color.GRAY);
	 			btnRetrieveAll.setEnabled(false);	    		 	    		 
	 			btnRetrieveAll.setBackground(Color.GRAY);
	    	 }
	     }
	     	    
	     public void mouseClicked(MouseEvent e) {	        
	        if (e.getClickCount() == 1) {
	        	int x = e.getX();
		     	int y = e.getY();
		     	int row = resultTree.getRowForLocation(x, y);
		     	TreePath  path = resultTree.getPathForRow(row);    	
		     	if (path != null) {    		
		     		DefaultMutableTreeNode queryNode = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();										     					     					     		
		     		//System.out.println(resultTree.getRowForPath(new TreePath(queryNode.getPath())));
		     		//System.out.println("Checking set changed, leading path: " + e.getPath().toString());			    
		     		if (queryNode == null) return;		 
		     		if (!queryNode.isRoot()) {
		     			queryNodeIndex = resultTree.getRowForPath(new TreePath(queryNode.getPath()));
		     			selectedNode = queryNode.getUserObject();			     			
		     			AttributeList initialCriteria = criteriaPanel.getFilterList();
		     			if(selectedNode instanceof Patient){			     				
		     				Patient selectedPatient = Patient.class.cast(selectedNode);
		     				logger.info("Staring node query: " + selectedPatient.toString());
		     				//Retrieve studies for selected patient
		     				btnRetrieve.setEnabled(false);			     				
		     				btnRetrieve.setBackground(Color.GRAY);
		     				btnRetrieveAll.setEnabled(false);			     				
		     				btnRetrieveAll.setBackground(Color.GRAY);
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
		     					AVTQuery avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.STUDY, result, selectedNode);
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
		     				logger.info("Staring node query: " + selectedStudy.toString());
		     				//Retrieve studies for selected patient
		     				btnRetrieve.setEnabled(false);
		     				btnRetrieve.setBackground(Color.GRAY);		     				
		     				btnRetrieveAll.setEnabled(false);
		     				btnRetrieveAll.setBackground(Color.GRAY);
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
		     					AVTQuery avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.SERIES, result, selectedNode);
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
		     	}
	        } else if (e.getClickCount() == 2){
	        	int x = e.getX();
		     	int y = e.getY();
		     	int row = resultTree.getRowForLocation(x, y);
		     	TreePath  path = resultTree.getPathForRow(row);    	
		     	if (path != null) {    		
		     		DefaultMutableTreeNode queryNode = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();										     					     					     		
		     		//System.out.println(resultTree.getRowForPath(new TreePath(queryNode.getPath())));
		     		//System.out.println("Checking set changed, leading path: " + e.getPath().toString());			    
		     		if (queryNode == null) return;		 
		     		if (!queryNode.isRoot()) {
		     			queryNodeIndex = resultTree.getRowForPath(new TreePath(queryNode.getPath()));
		     			selectedNode = queryNode.getUserObject();			     			
		     			AttributeList initialCriteria = criteriaPanel.getFilterList();
		     			if(selectedNode instanceof Series){
		     				Series selectedSeries = Series.class.cast(selectedNode);
		     				logger.info("Staring node query: " + selectedSeries.toString());
		     				//Retrieve annotations for selected series
		     				btnRetrieve.setEnabled(false);
		     				btnRetrieve.setBackground(Color.GRAY);		     				
		     				btnRetrieveAll.setEnabled(false);
		     				btnRetrieveAll.setBackground(Color.GRAY);
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
		     					AVTQuery avtQuery = new AVTQuery(adCriteria, adAimCriteria, ADQueryTarget.ITEM, result, selectedNode);
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
		     	}
	        }	        	        
	    }
	};

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox) e.getItemSelectable();
	    if (source == cbxSeries) {
	    	    	
	    } else if (source == cbxAimSeg) {
	    	
	    }
	    if(resultTree.getSelectedSeries().size() > 0 && (cbxSeries.isSelected() || cbxAimSeg.isSelected())){
    		btnRetrieve.setEnabled(true);	    		 	    			
 			btnRetrieve.setBackground(xipBtn);
 			btnRetrieve.setForeground(Color.WHITE);
 			btnRetrieveAll.setEnabled(true);
 			btnRetrieveAll.setBackground(xipBtn);
 			btnRetrieveAll.setForeground(Color.WHITE);
    	 }else{
    		btnRetrieve.setEnabled(false);	    		 	    		 
 			btnRetrieve.setBackground(Color.GRAY);
 			btnRetrieveAll.setEnabled(false);	    		 	    		 
 			btnRetrieveAll.setBackground(Color.GRAY);
    	 }
	}
}
