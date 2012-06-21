/*
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

public class ConfigPanel extends JDialog implements ActionListener, KeyListener{			
	JLabel lbl1 = new JLabel("TmpXIP directory: ");
	JLabel lbl2 = new JLabel("OutXIP directory: ");		
	JTextField txt1 = new JTextField("", 30);
	JLabel lbl3 = new JLabel("*TmpXIP - parent of all temp dirs used to store data retrived from caGrid");
	JLabel lbl4 = new JLabel("*OutXIP - parent of all output dirs used to store data produced by XIP applications");
	JTextField txt2 = new JTextField("", 30);	
	ImageIcon icon =  new ImageIcon("./gif/Open24.gif");
	JLabel lblImage1 = new JLabel();			
	JLabel lblImage2 = new JLabel();
	JButton btnOK = new JButton("OK");
	JPanel configPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane(configPanel);
	JCheckBox cbxStartup = new JCheckBox("display at startup");
	JPanel btnPanel = new JPanel();
	PathChooser pc = new PathChooser();	 
	String strParentTmpDir = new String();
	String strParentOutDir = new String();
	String strPixelmedSavedImagesFolder = new String();
	Boolean displayStartup = true;
	JTextField txt3 = new JTextField("", 30);	
	JLabel lbl5 = new JLabel("Pixelmed/HSQLDB SavedImagesFolderName: ");
	JLabel lbl6 = new JLabel("*Location of DICOM images stored with Pixelmed server");
	JLabel lblImage3 = new JLabel();
	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
		
	final JOptionPane optionPane = new JOptionPane(
		    "Host tmp, output and Pixelmed/HSQLDB SavedImages directory must exist.\n"
		    + "All three directories cannot be the same. \n"
		    + "To create directory select folder icon, right-click on the dialog and select 'New Folder'.",		    
		    JOptionPane.PLAIN_MESSAGE);								
	//dialog displays information about non existing directories.
	JDialog dialog;		
	
	public ConfigPanel(Frame owner){
		super(owner, "Host Configuration Parameters", true);
		dialog = optionPane.createDialog(this, "Host config dialog");				
		dialog.setModal(true);
		btnOK.addActionListener(this);		
		btnOK.setPreferredSize(new Dimension(100, 25));
		Border border1 = BorderFactory.createLoweredBevelBorder();
	    configPanel.setBorder(border1);	    
	    Font font_1 = new Font("SansSerif", 0, 12);	    
	    Font font_2 = new Font("SansSerif", 0, 9);
        lbl1.setFont(font_1);
        lbl2.setFont(font_1);
        lbl3.setFont(font_2);
        lbl4.setFont(font_2);
        lbl5.setFont(font_1);
        lbl6.setFont(font_2);
        txt1.setFont(font_1);
        txt2.setFont(font_1);
        txt3.setFont(font_1);
        cbxStartup.setFont(font_1);        
        lblImage1.setIcon(icon);	
        lblImage2.setIcon(icon);
        lblImage3.setIcon(icon);   
        txt1.addKeyListener(this);
        txt2.addKeyListener(this);
        txt3.addKeyListener(this);        
        lblImage1.addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){						
						pc.displayPathChooser(strParentTmpDir);			
						try {
							strParentTmpDir = pc.getSelectedPath().getCanonicalPath();							
							//strParentTmpDir.replace('\\', '/');
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						txt1.setText(strParentTmpDir);					
					}
				}
		);
        lblImage2.addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){						
						pc.displayPathChooser(strParentOutDir);			
						try {
							strParentOutDir = pc.getSelectedPath().getCanonicalPath();
							//strParentOutDir.replace('\\', '/');
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						txt2.setText(strParentOutDir);			
					}
				}
		);
        lblImage3.addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){						
						pc.displayPathChooser(strPixelmedSavedImagesFolder);			
						try {
							strPixelmedSavedImagesFolder = pc.getSelectedPath().getCanonicalPath();
							//strPixelmedSavedImagesFolder.replace('\\', '/');														
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						txt3.setText(strPixelmedSavedImagesFolder);			
					}
				}
		);
        configPanel.add(lbl1);
        configPanel.add(lbl2);
        configPanel.add(lbl3);
        configPanel.add(lbl4);
        configPanel.add(lbl5);
        configPanel.add(lbl6);
		configPanel.add(txt1);
		configPanel.add(txt2);		
		configPanel.add(txt3);
		configPanel.add(lblImage1);
		configPanel.add(lblImage2);
		configPanel.add(lblImage3);
		configPanel.setBackground(xipLightBlue);
		btnPanel.add(btnOK);
		btnOK.setBackground(xipBtn);
		btnOK.setForeground(Color.BLACK);
		btnPanel.setBackground(xipColor);
		add(scrollPane);
		add(cbxStartup);
		add(btnPanel);
		cbxStartup.setBackground(xipColor);
		cbxStartup.setForeground(Color.WHITE);
		getContentPane().setBackground(xipColor);
		buildLayout();
		
		setPreferredSize(new Dimension(770, 350));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();               								
		Dimension windowSize = getPreferredSize();		
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);		
        setResizable(false);
        pack();
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){	         
			public void windowClosing(WindowEvent e){    	        	 					
				if(checkDirectories()){
					setVisible(false);
		    	} else {		    					
					dialog.setVisible(true);					
		    	}    	
	        }	         
		}); 
	}
	
	public void display(){														       						
		txt1.setText(strParentTmpDir);        
        txt2.setText(strParentOutDir);
        txt3.setText(strPixelmedSavedImagesFolder);
        cbxStartup.setSelected(getDisplayStartup());
		setVisible(true);					
	}	
		
	public void closeConfigPanel(){
		this.setVisible(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOK){        	
        	try {
        		if(cbxStartup.isSelected()){
        			displayStartup = true;
        		}else{
        			displayStartup = false;
        		}
        		        		      		
        	} catch(NullPointerException e1){
        		//TODO
        		System.out.println("Config Dialog error");
        	}
			if(checkDirectories()){
				dispose();
			}else{				
				dialog.setVisible(true);
				
			}
        } 	    	    	
	}
	
	//checkDirectories makes sure tmp and output directories exist
	//if not user is prompted to create directories first
	public boolean checkDirectories(){
		File tmpDir = new File(txt1.getText());
    	File outDir = new File(txt2.getText());
    	File hsqldbDir = new File(txt3.getText());		
    	if(!tmpDir.exists() || !outDir.exists() || !hsqldbDir.exists()){    		
    		return false;
    	}else if (txt1.getText().equalsIgnoreCase(txt2.getText()) || txt1.getText().equalsIgnoreCase(txt3.getText()) ||
    			txt2.getText().equalsIgnoreCase(txt3.getText())){
    		return false;
    	}else{
    		strParentTmpDir = txt1.getText();
    		strParentOutDir = txt2.getText();
    		strPixelmedSavedImagesFolder = txt3.getText();
    		return true;
    	}
	}
	
	public void setParentOfTmpDir(String tmpDir){
		strParentTmpDir = tmpDir;
	}
	public String getParentOfTmpDir(){
		return strParentTmpDir;
	}
	
	public void setParentOfOutDir(String outDir){
		strParentOutDir = outDir;
	}
	public String getParentOfOutDir(){
		return strParentOutDir;
	}
	
	public void setPixelmedSavedImagesDir(String pixelmedDir){
		strPixelmedSavedImagesFolder = pixelmedDir;
	}
	
	public String getPixelmedSavedImagesDir(){
		return strPixelmedSavedImagesFolder;
	}
	
	public void setDisplayStartup(Boolean display){
		displayStartup = display;
	}
	public Boolean getDisplayStartup(){
		return displayStartup;
	}
	
	public void buildLayout(){	
		//GridBagLayout dimenssions are: rows = 2 x columns = 3)
		GridBagLayout layout1 = new GridBagLayout();
        GridBagConstraints constraints1 = new GridBagConstraints();
        configPanel.setLayout(layout1);
        
        constraints1.fill = GridBagConstraints.NONE;        
        constraints1.gridx = 0;
        constraints1.gridy = 0;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(20, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.EAST;
        layout1.setConstraints(lbl1, constraints1);
                        
        //constraints1.fill = GridBagConstraints.WEST;
        constraints1.gridx = 1;
        constraints1.gridy = 0;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(20, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(txt1, constraints1);
        
        //constraints1.fill = GridBagConstraints.NONE;
        constraints1.gridx = 2;
        constraints1.gridy = 0;
        constraints1.insets = new Insets(20, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(lblImage1, constraints1);
        
        constraints1.gridx = 1;
        constraints1.gridy = 1;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(5, 10, 10, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(lbl3, constraints1);
                
        constraints1.fill = GridBagConstraints.NONE;        
        constraints1.gridx = 0;
        constraints1.gridy = 2;        
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(10, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.EAST;
        layout1.setConstraints(lbl2, constraints1);
        
        //constraints1.fill = GridBagConstraints.WEST;
        constraints1.gridx = 1;
        constraints1.gridy = 2;
        constraints1.insets = new Insets(10, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(txt2, constraints1);
                
        //constraints1.fill = GridBagConstraints.NONE;
        constraints1.gridx = 2;
        constraints1.gridy = 2;
        constraints1.insets = new Insets(10, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(lblImage2, constraints1); 
        
        constraints1.gridx = 1;
        constraints1.gridy = 3;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(5, 10, 10, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(lbl4, constraints1);
        
        constraints1.gridx = 0;
        constraints1.gridy = 4;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(10, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.EAST;
        layout1.setConstraints(lbl5, constraints1);
        
        constraints1.gridx = 1;
        constraints1.gridy = 4;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(10, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(txt3, constraints1);
        
        constraints1.gridx = 2;
        constraints1.gridy = 4;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(10, 10, 0, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(lblImage3, constraints1);
        
        constraints1.gridx = 1;
        constraints1.gridy = 5;
        //Insets(top, left, bottom, right) 
        constraints1.insets = new Insets(5, 10, 10, 10);
        constraints1.anchor = GridBagConstraints.WEST;
        layout1.setConstraints(lbl6, constraints1);
        
                        
        //GridBagLayout dimenssions are: rows = 2 x columns = 1)
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout); 
								
		constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        //Insets(top, left, bottom, right) 
        constraints.insets = new Insets(10, 10, 10, 10);                      
        layout.setConstraints(scrollPane, constraints);                   
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(0, 10, 10, 10);               
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(cbxStartup, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(0, 10, 10, 10);               
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(btnPanel, constraints);
	}	
	
	public void keyPressed(KeyEvent arg0) {
		//System.out.println(arg0.getKeyCode());		
		if(arg0.getSource() == txt1 || arg0.getSource() == txt2 || arg0.getSource() == txt3){
			int nKeyCode = arg0.getKeyCode();
			if(nKeyCode == 10){
				btnOK.doClick();
			}
		}		
	}
	
	public void keyReleased(KeyEvent arg0) {		
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}	
}