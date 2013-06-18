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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import edu.wustl.xipHost.hostLogin.Login;

public class LoginDialog extends JDialog implements ActionListener, KeyListener { 
			
	JPanel panel = new JPanel();	
	JLabel lblWashU = new JLabel("Washington University in St. Louis");
	JLabel lblHost = new JLabel("Launching XIP Host ");
	LoginPanel loginPanel = new LoginPanel();
	Font font = new Font("Tahoma", 1, 12);		
	
    public LoginDialog(){                  	            
        lblHost.setFont(font);
        lblHost.setForeground(new Color(51, 51, 102));
        lblWashU.setFont(font);
        lblWashU.setForeground(Color.WHITE);
        panel.add(lblHost);                
        panel.add(loginPanel);
        panel.add(lblWashU);
        panel.setPreferredSize(new Dimension(400, 500));
        panel.setBorder(new BevelBorder(BevelBorder.RAISED));
        panel.setBackground(new Color(51, 51, 102));
        loginPanel.btnOK.addActionListener(this);	
        loginPanel.txtUser.addKeyListener(this);
		loginPanel.txtPass.addKeyListener(this);
		loginPanel.lblGuestUserIcon.addMouseListener(customMouseListener);
        add(panel);
        buildLayout();
        setUndecorated(true);
        pack();
        setAlwaysOnTop(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();                        
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
        setVisible(false);
    }
    
    public void setStatusHost(String strStatus){
    	lblHost.setText(strStatus);    	
    }    

    void buildLayout(){
    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);                
                        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 50;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(loginPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 0;
        constraints.insets.bottom = 15;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblWashU, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets.top = 0;
        constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblHost, constraints);
    }           

    boolean isUserOK = false;
    public void actionPerformed(ActionEvent e) {		
		if(e.getSource() == loginPanel.btnOK){
			String user = loginPanel.txtUser.getText();
			String password = new String(loginPanel.txtPass.getPassword());	
			resetLoginDialog(user, password);
		}	
	}

    boolean enableExitOnEsc = true;
    public void setEnableExitOnEsc(boolean enableExitOnEsc){
    	this.enableExitOnEsc = enableExitOnEsc;
    }
    
    public void keyPressed(KeyEvent arg0) {
		if(arg0.getSource() == loginPanel.txtUser || arg0.getSource() == loginPanel.txtPass ){
			int nKeyCode = arg0.getKeyCode();
			if(nKeyCode == 10){
				String user = loginPanel.txtUser.getText();
				String password = new String(loginPanel.txtPass.getPassword());	
				resetLoginDialog(user, password);			
			} else if(nKeyCode == 27){
				if(enableExitOnEsc){
					System.exit(0);
				}
			}
		}		
	}
    
    void resetLoginDialog(String user, String password){
    	loginPanel.txtUser.setEnabled(false);
		loginPanel.txtPass.setEnabled(false);					
		isUserOK = login.login(user, password); 
		if(isUserOK){
			loginPanel.btnOK.setEnabled(false);
			setVisible(false);			
		}else{			
			loginPanel.txtUser.setText("");
			loginPanel.txtPass.setText("");
			loginPanel.txtUser.setEnabled(true);
			loginPanel.txtPass.setEnabled(true);
			loginPanel.txtUser.requestFocus();	
		}
    }

	public void keyReleased(KeyEvent arg0) {
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}
	
	Login login;
	public void setLogin(Login login){
		this.login = login;
	}
	
	
	MouseListener customMouseListener = new MouseAdapter(){
		 public void mouseClicked(final MouseEvent e) {	        
			String user = "Guest";
			String password = "";								
			resetLoginDialog(user, password);					
		 }
	};
}
