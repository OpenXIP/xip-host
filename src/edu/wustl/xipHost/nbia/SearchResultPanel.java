/*
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.nbia;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTreeProgressive;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * Creates RightPanel fo caGridDiscoveryPanel
 */
public class SearchResultPanel extends JPanel {	
	//SearchResultTree gridJTree;	
	SearchResultTree gridJTree;
	JScrollPane treeView;
    JCheckBox cbxAnnot;
    DefaultComboBoxModel comboModel;	
	JComboBox list;	
    ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);
	JPanel annotPanel = new JPanel();
	
	Font font_1 = new Font("Tahoma", 0, 13); 
	Font font_2 = new Font("Tahoma", 0, 12);
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	
	public SearchResultPanel(){		
		setBackground(xipColor);
		//gridJTree = new SearchResultTree();			    
		gridJTree = new SearchResultTreeProgressive();
		treeView = new JScrollPane(gridJTree);
		cbxAnnot = new JCheckBox("with annotations", true);
		comboModel = new DefaultComboBoxModel();
		list = new JComboBox(comboModel);
		
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		list.setRenderer(renderer);
		list.setMaximumRowCount(4);
		
		list.setPreferredSize(new Dimension(340, 25));
		list.setFont(font_2);
		list.setEditable(false);		
		
		cbxAnnot.setFont(font_1);
		cbxAnnot.setEnabled(false);		
		lblGlobus.setToolTipText("AIM service locations");
		lblGlobus.setEnabled(true);
		lblGlobus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));		
		cbxAnnot.setBackground(xipColor);
		cbxAnnot.setForeground(Color.WHITE);
									
		annotPanel.setBackground(xipColor);
		annotPanel.add(cbxAnnot);
		annotPanel.add(list);
		annotPanel.add(lblGlobus);
		buildLayoutAnnotPanel();						
	    treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));		    
	    Border border1 = BorderFactory.createLoweredBevelBorder();
	    treeView.setBorder(border1);		
		add(treeView);
		add(annotPanel);
		buildLayout();		
	}
	
	public SearchResultTree getGridJTreePanel(){
		return gridJTree;
	}		
	
	void buildLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout); 		
		
		constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 30;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        layout.setConstraints(treeView, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(annotPanel, constraints);
	}
	
	void buildLayoutAnnotPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        annotPanel.setLayout(layout); 		
		
		constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.left = 10;
        constraints.insets.bottom = 10;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(cbxAnnot, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.bottom = 10;
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(list, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets.left = 20;
        constraints.insets.right = 10;
        constraints.insets.bottom = 10;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblGlobus, constraints);        
	}
	
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(400, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof DataSource) {
			    	renderer.setText(((DataSource)value).getShortName());
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
		
	public static void main (String [] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SearchResultPanel lp = new SearchResultPanel();						
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();               				
		JFrame frame = new JFrame("Application GlobalSearchUtil");
		//frame.setSize(screenSize.width - 600, screenSize.height - 600);		
		frame.getContentPane().add(lp);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}		
}