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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSPanel extends JPanel implements ActionListener, TreeSelectionListener, XDSSearchListener, ListSelectionListener {
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
	
	public XDSPanel(){
		setBackground(xipColor);		
		criteriaPanel.btnSearchPatientID.addActionListener(this);
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search XDS");	
		criteriaPanel.getPatientList().addListSelectionListener(this);
		leftPanel.add(criteriaPanel);				
	    resultTree.addTreeSelectionListener(this);
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
			XDSDocumentQuery xsdQuery = new XDSDocumentQuery(selectedID.getPatID());
			xsdQuery.addXDSSearchListener(this);
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
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 15;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.NORTH;
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
	
	public void searchResultAvailable(XDSSearchEvent e) {
		
			
	}

	public void patientIDs(List<XDSPatientIDResponse> patientIDs) {				
		if(patientIDs != null && patientIDs.size() != 0){
			progressBar.setString("Patient ID(s) found");
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
	
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
