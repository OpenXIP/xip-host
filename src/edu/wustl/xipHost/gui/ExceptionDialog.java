/*
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ExceptionDialog {

	JPanel messagePanel = new JPanel();
	Font font = new Font("Tahoma", 0, 12); 
	JLabel lblLine_1;
    JLabel lblLine_2;
    JOptionPane optPane = new JOptionPane(null, JOptionPane.YES_OPTION);
    ImageIcon iconLogo =  new ImageIcon("./gif/xip-logo.GIF");      
    
	public ExceptionDialog(String strLine1, String strLine2, String strTitle){
		messagePanel.setPreferredSize(new Dimension(350, 80));	
	    messagePanel.setLayout(new GridLayout(2, 1));
	    lblLine_1 = new JLabel(strLine1, JLabel.LEFT);
	    lblLine_2 = new JLabel(strLine2, JLabel.LEFT);
	    lblLine_1.setFont(font);
	    lblLine_2.setFont(font);
	    messagePanel.add(lblLine_1);
	    messagePanel.add(lblLine_2);
	    optPane.setMessageType(JOptionPane.PLAIN_MESSAGE);            
	    optPane.setMessage(messagePanel);
	    optPane.setIcon(iconLogo);
	    JPanel buttonPanel = (JPanel)optPane.getComponent(1);
	    JButton buttonOk = (JButton)buttonPanel.getComponent(0);
	    buttonOk.setPreferredSize(new Dimension(100, 25));            
	    JDialog d = optPane.createDialog(null,strTitle);
		d.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ExceptionDialog("Jarek", "Krych", "XIP");

	}

}
