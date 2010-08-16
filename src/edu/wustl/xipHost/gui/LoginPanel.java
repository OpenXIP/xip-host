/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;


/**
 * <font  face="Tahoma" size="2" color="Black">
 * GUI component, used to capture user name and password <b></b>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class LoginPanel extends JPanel {
	JLabel lblWelcome = new JLabel("<html><font color=yellow>Welcome!</font></html>");
	JLabel lblTitle = new JLabel("Login to XIP Host");	
	JLabel lblUser = new JLabel("Username");
	JLabel lblPass = new JLabel("Password");
	JTextField txtUser = new JTextField("wustl", 20);
	JPasswordField txtPass = new JPasswordField("123", 20);
	JButton btnOK = new JButton("OK");
	JLabel lblImage = new JLabel();
	//ImageIcon icon =  new ImageIcon("./gif/xip-logo_164_x_48.gif");
	ImageIcon icon = new ImageIcon("./gif/XIP-Host-Spash-Screen-Logo.PNG");
	JPanel loginPanel = new JPanel();
	Font font1 = new Font("Tahoma", 1, 13); 
	Border border1 = BorderFactory.createLoweredBevelBorder();
	Border border2 = BorderFactory.createLineBorder(Color.BLACK);	
	Color xipColor = new Color(51, 51, 102);	
	String user;
	String password;	
		
	public LoginPanel(){
		File welcome = new File("./config/welcome.html");
		if ( welcome.exists() ){
			int size = (int) welcome.length();
			byte[] message = new byte[size];
			try {
				FileInputStream in = new FileInputStream(welcome);
				in.read(message);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			String messageString = new String(message);
			lblWelcome.setText(messageString);
		} 			
		lblImage.setIcon(icon);
		//lblImage.setBorder(border2);
		btnOK.setPreferredSize(new Dimension(100, 25));		
		//btnOK.setMnemonic(KeyEvent.VK_ENTER);
		btnOK.setMnemonic('O');			
		lblTitle.setFont(font1);
		lblTitle.setForeground(Color.WHITE);
		lblUser.setForeground(Color.WHITE);
		lblPass.setForeground(Color.WHITE);
		txtPass.setEchoChar('*');		
		loginPanel.add(lblWelcome);		
		loginPanel.add(lblTitle);		
		loginPanel.add(lblUser);
		loginPanel.add(lblPass);
		loginPanel.add(txtUser);
		loginPanel.add(txtPass);
		loginPanel.add(btnOK);
		loginPanel.add(lblImage);
		loginPanel.setBackground(xipColor);
		setBackground(xipColor);
		add(loginPanel);
		buildLayout();								
	}
	 
	void buildLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        loginPanel.setLayout(layout);        

        constraints.fill = GridBagConstraints.BOTH;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblWelcome, constraints);              

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 20;
        constraints.insets.left = 20;
        constraints.insets.right = 20;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblTitle, constraints);              

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;        
        constraints.insets.top = 5;        
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblUser, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 3;        
        constraints.insets.top = 0;        
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(txtUser, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 4;        
        constraints.insets.top = 5;        
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblPass, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 5;        
        constraints.insets.top = 0;        
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(txtPass, constraints);

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 6;        
        constraints.insets.top = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnOK, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 7;        
        constraints.insets.top = 40;        
        constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblImage, constraints);
	}	
}
