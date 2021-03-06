/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.hostLogin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import org.apache.log4j.Logger;


/**
 * <font  face="Tahoma" size="2" color="Black">
 * XIP Host UI component, used to capture user name and password <b></b>
 * @version	June 2012
 * @author Jarek Krych
 * </font>
 */
public class LoginPanel extends JPanel {
	final static Logger logger = Logger.getLogger(LoginPanel.class);
	JLabel lblWelcome = new JLabel("<html><font color=yellow>Welcome!</font></html>");
	JLabel lblTitle = new JLabel("Login to XIP Host");	
	JLabel lblUser = new JLabel("Username");
	JLabel lblPass = new JLabel("Password");
	JTextField txtUser = new JTextField("", 20);
	JPasswordField txtPass = new JPasswordField("", 20);
	JButton btnOK = new JButton("OK");
	JLabel lblGuestUser = new JLabel("or login as guest   ");
	Icon iconGuestUser = new ImageIcon("./gif/Right.png");
	JLabel lblGuestUserIcon = new JLabel(iconGuestUser, JLabel.CENTER);
	JPanel guestUserPanel = new JPanel();
	JLabel lblImage = new JLabel();
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
				logger.error(e, e);
			} catch (IOException e) {
				logger.error(e, e);
			}
			String messageString = new String(message);
			lblWelcome.setText(messageString);
		} 			
		lblImage.setIcon(icon);
		btnOK.setPreferredSize(new Dimension(100, 25));	
		btnOK.setMnemonic('O');			
		lblTitle.setFont(font1);
		lblTitle.setForeground(Color.WHITE);
		lblUser.setForeground(Color.WHITE);
		lblPass.setForeground(Color.WHITE);
		lblGuestUser.setForeground(Color.WHITE);
		txtPass.setEchoChar('*');		
		loginPanel.add(lblWelcome);		
		loginPanel.add(lblTitle);		
		loginPanel.add(lblUser);
		loginPanel.add(lblPass);
		loginPanel.add(txtUser);
		loginPanel.add(txtPass);
		loginPanel.add(btnOK);
		guestUserPanel.add(lblGuestUser);
		guestUserPanel.add(lblGuestUserIcon);
		guestUserPanel.setBackground(xipColor);
		lblGuestUserIcon.setToolTipText("Guest Login");
		lblGuestUserIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		loginPanel.add(guestUserPanel);
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

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblWelcome, constraints);              

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 20;
        constraints.insets.left = 40;
        constraints.insets.right = 40;
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
        constraints.insets.top = 10;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(guestUserPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 8;        
        constraints.insets.top = 20;        
        constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblImage, constraints);
	}	
}
