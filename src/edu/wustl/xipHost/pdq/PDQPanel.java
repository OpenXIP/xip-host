/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.pdq;

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
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
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
 * @author Lawrence Tarbox, from code originally created by Jaroslaw Krych
 *
 */
public class PDQPanel extends JPanel implements ActionListener, PDQSearchListener, ListSelectionListener {
	final static Logger logger = Logger.getLogger(PDQPanel.class);
	JPanel pdqLocationSelectionPanel = new JPanel();
	JLabel lblPdqTitle = new JLabel("Select Patient Demographic Supplier:");		
	ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");	
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);		
    DefaultComboBoxModel pdqComboModel;	
	JComboBox pdqList;	
	PDQManager pdqManager;

	PDQSearchCriteriaPanel criteriaPanel = new PDQSearchCriteriaPanel();	
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
	
	public PDQPanel(){
		setBackground(xipColor);		
		pdqComboModel = new DefaultComboBoxModel();
		pdqList = new JComboBox(pdqComboModel);
		pdqManager = PDQManagerFactory.getInstance();
		List<PDQLocation> pdqLocations = pdqManager.getPDQLocations();
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
		criteriaPanel.getPatientList().addListSelectionListener(this);
		leftPanel.add(pdqLocationSelectionPanel);
		leftPanel.add(criteriaPanel);				
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
				PDQPatientIDQuery pdqQueryPatientID = new PDQPatientIDQuery(criteria, selectedPDQLocation);
				pdqQueryPatientID.addPDQSearchListener(this);
				Thread t = new Thread(pdqQueryPatientID);
				t.start();
			} else {
				//if no criteria specified do ...				
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
		PDQPanel panel = new PDQPanel();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);		
	}
	
	
	public void patientIDsAvailable(List<PDQPatientIDResponse> patientIDs) {				
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

	PDQPatientIDResponse selectedID;
	public void valueChanged(ListSelectionEvent e) {
		JList list = ((JList)e.getSource());		
		selectedID = (PDQPatientIDResponse)list.getSelectedValue();
	}

}
