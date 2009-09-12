/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;

import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.XDSDocumentItem;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;
import edu.wustl.xipHost.xds.CheckBoxTree.SearchResultTree;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSPanel extends JPanel implements ActionListener, XDSSearchListener, XDSRetrieveListener, ListSelectionListener {
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
	Border border = BorderFactory.createLoweredBevelBorder();		
	JButton btnRetrieve;
	JLabel infoLabel = new JLabel("UNDER   DEVELOPMENT");
	
	public XDSPanel(){
		setBackground(xipColor);		
		criteriaPanel.btnSearchPatientID.addActionListener(this);
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search XDS");	
		criteriaPanel.getPatientList().addListSelectionListener(this);
		leftPanel.add(criteriaPanel);				
		resultTree.addMouseListener(ml);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);	
		btnRetrieve = new JButton("Retrieve");
        btnRetrieve.setFont(font_1); 
        btnRetrieve.setFocusable(true);
		btnRetrieve.setEnabled(false);				
		btnRetrieve.setBackground(xipBtn);
		btnRetrieve.setForeground(Color.WHITE);
		btnRetrieve.setPreferredSize(new Dimension(115, 25));
		btnRetrieve.addActionListener(this);
		rightPanel.add(treeView);
		rightPanel.add(btnRetrieve);
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
	    buildRightPanelLayout();
	    buildLayout();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getPatientIDQueryButton()){									
			criteriaPanel.getListModel().removeAllElements();
			resultTree.rootNode.removeAllChildren();
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			AttributeList criteria = criteriaPanel.getFilterList();										
			if(criteriaPanel.verifyCriteria(criteria)){																													
				XDSPatientIDQuery xsdQueryPatientID = new XDSPatientIDQuery(criteria);
				xsdQueryPatientID.addXDSSearchListener(this);
				Thread t = new Thread(xsdQueryPatientID);
				t.start();
			} else {
				//if no criteria specified do ...				
			}						
		}else if(e.getSource() == criteriaPanel.getQueryButton()){			
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			XDSDocumentQuery xsdQuery = new XDSDocumentQuery(selectedID.getPatID());
			xsdQuery.addXDSSearchListener(this);
			Thread t = new Thread(xsdQuery);
			t.start();
		}else if(e.getSource() == btnRetrieve){			
			allRetrivedFiles = new ArrayList<File>();
			numRetrieveThreadsStarted = 0;
			numRetrieveThreadsReturned = 0;
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();
			List<XDSDocumentItem> selectedItems = resultTree.getSelectedItems();
			for(int i = 0; i < selectedItems.size(); i++){
				XDSDocumentItem xdsDocItem = selectedItems.get(i);
				DocumentEntryType docType = xdsDocItem.getDocumentType();
				CX patientId = xdsDocItem.getPatientId();
				String homeCommunityId = xdsDocItem.getHomeCommunityId();
				XDSDocumentRetrieve xdsRetrieve = new XDSDocumentRetrieve(docType, patientId, homeCommunityId);
				xdsRetrieve.addXDSRetrieveListener(this);
				Thread t = new Thread(xdsRetrieve);
				t.start();
				numRetrieveThreadsStarted++;
			}						
			
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
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnRetrieve, constraints);
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
	public void documentsAvailable(XDSSearchEvent e) {
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
	
	MouseListener ml = new MouseAdapter() {
	     public void mousePressed(MouseEvent e) {
	    	 if(resultTree.getSelectedItems().size() > 0){
	    		 btnRetrieve.setEnabled(true);
	    	 }else{
	    		 btnRetrieve.setEnabled(false);
	    	 }
	     }
	};

	List<File> allRetrivedFiles;
	int numRetrieveThreadsStarted;
	int numRetrieveThreadsReturned;
	@Override
	public boolean documentsAvailable(File xdsRetrievedFile) {
		allRetrivedFiles.add(xdsRetrievedFile);
		numRetrieveThreadsReturned++;
		if(numRetrieveThreadsStarted == numRetrieveThreadsReturned){
			progressBar.setString("XDS Retrieve finished");
			progressBar.setIndeterminate(false);
			criteriaPanel.getQueryButton().setBackground(xipBtn);
			criteriaPanel.getQueryButton().setEnabled(true);		
			btnRetrieve.setEnabled(true);
			btnRetrieve.setBackground(xipBtn);
			File[] files = new File[allRetrivedFiles.size()];
			allRetrivedFiles.toArray(files);		
			FileManager fileMgr = FileManagerFactory.getInstance();						
	        fileMgr.run(files);
		}
		return true;
	}
	
}
