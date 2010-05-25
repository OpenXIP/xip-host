/*
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.event.*;
import java.awt.*;
//import java.util.*;

public class AboutDialog extends JDialog {
    JLabel icon = new JLabel();
    JLabel lblInfo_1 = new JLabel("eXtensible Imaging Platform");
    JLabel lblInfo_2 = new JLabel("Product version:");
    JLabel lblInfo_3 = new JLabel("Developed by:");
    JLabel lblInfo_4 = new JLabel("Contact:");
    JPanel labelsPanel = new JPanel(new GridLayout(3, 1));
    JLabel lblInfo_5 = new JLabel("XIPHost Reference Implementation - Release, April 17th, 2010");
    JLabel lblInfo_6 = new JLabel("Copyright (c) 2007-2010 Washington University in St. Louis. All Rights Reserved.");
    JLabel lblInfo_7 = new JLabel("314-747-1728");
    JPanel versionDataPanel = new JPanel(new GridLayout(3, 1));    
    //ImageIcon iconLogo =  new ImageIcon("./gif/xip-logo.GIF");
    ImageIcon iconLogo =  new ImageIcon("./gif/XIP-Host-Corner-Logo-Red.PNG");
    Border border1 = BorderFactory.createLineBorder(Color.black);
    Color xipColor = new Color(51, 51, 102);		
	
    public AboutDialog(Frame owner){
        super(owner, "About", true);        
        icon.setIcon(iconLogo);
        //icon.setBorder(border1);
        Font font = new Font("SansSerif", 1, 18);   
        Font font_1 = new Font("SansSerif", 1, 12);
        Font font_2 = new Font("SansSerif", 0, 12);
        
        lblInfo_1.setFont(font);
        lblInfo_1.setForeground(Color.WHITE);
        lblInfo_2.setFont(font_1);        
        lblInfo_2.setForeground(Color.WHITE);
        lblInfo_3.setFont(font_1);        
        lblInfo_3.setForeground(Color.WHITE);
        lblInfo_4.setFont(font_1);        
        lblInfo_4.setForeground(Color.WHITE);
        lblInfo_5.setFont(font_2);        
        lblInfo_5.setForeground(Color.WHITE);
        lblInfo_6.setFont(font_2);        
        lblInfo_6.setForeground(Color.WHITE);
        lblInfo_7.setFont(font_2);        
        lblInfo_7.setForeground(Color.WHITE);
        
        add(icon);
        add(lblInfo_1);        
        labelsPanel.add(lblInfo_2);        
        labelsPanel.add(lblInfo_3);        
        labelsPanel.add(lblInfo_4);
        labelsPanel.setBackground(xipColor);       
        add(labelsPanel);
        
        versionDataPanel.add(lblInfo_5);
        versionDataPanel.add(lblInfo_6);
        versionDataPanel.add(lblInfo_7);
        versionDataPanel.setBackground(xipColor);        
        add(versionDataPanel);
        this.getContentPane().setBackground(xipColor);
        buildLayout();
                               
        //setSize(new Dimension(150, 25));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();                        
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /4,  windowSize.width, windowSize.height);
        pack();        
        setVisible(true);
        addWindowListener(
            new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    dispose();
                }
            }
        );
    }        
            
    public void buildLayout(){
//    	Set GridBagLayout 3 x 2
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout); 
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.top = 20;
        constraints.insets.left = 30;        
        layout.setConstraints(icon, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.insets.top = 20;        
        constraints.insets.right = 30;
        layout.setConstraints(lblInfo_1, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.top = 10;                
        constraints.insets.left = 30;        
        constraints.insets.bottom = 20;        
        layout.setConstraints(labelsPanel, constraints);         
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.top = 10;                        
        constraints.insets.right = 50;        
        constraints.insets.bottom = 20;        
        layout.setConstraints(versionDataPanel, constraints);  
    }
    
    public static void main (String [] args){
        new AboutDialog(new Frame());
    }
}