/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 *  */
package edu.wustl.xipHost.gui;

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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import edu.wustl.xipHost.hostControl.Login;
import edu.wustl.xipHost.hostControl.NewUserEvent;
import edu.wustl.xipHost.hostControl.NewUserListener;

public class LoginDialog extends JDialog implements ActionListener, KeyListener, NewUserListener, Runnable {				
	JPanel panel = new JPanel();	
	JLabel lblWashU = new JLabel("Washington University in Saint Louis");
	JLabel lblHost = new JLabel("Starting XIP Host ");
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
        panel.setPreferredSize(new Dimension(400, 450));
        panel.setBorder(new BevelBorder(BevelBorder.RAISED));
        panel.setBackground(new Color(51, 51, 102));
        loginPanel.btnOK.addActionListener(this);	
        loginPanel.txtUser.addKeyListener(this);
		loginPanel.txtPass.addKeyListener(this);
		this.addNewUserListener(this);
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
    
    public void run() {
		setVisible(true);
		while(isUserOK == false){
			
		}		
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
        //constraints.gridwidth = 2;
        constraints.insets.top = 0;
        constraints.insets.bottom = 15;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblWashU, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        //constraints.gridwidth = 2;
        constraints.insets.top = 0;
        constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblHost, constraints);
    }           

    boolean isUserOK = false;
    public void actionPerformed(ActionEvent e) {		
		if(e.getSource() == loginPanel.btnOK){
			loginPanel.txtUser.setEnabled(false);
			loginPanel.txtPass.setEnabled(false);
			String user = loginPanel.txtUser.getText();
			String password = new String(loginPanel.txtPass.getPassword());						
			isUserOK = fireValidateUser(user, password);
			updateUI(isUserOK);
		}	
	}

    public void keyPressed(KeyEvent arg0) {
		//System.out.println(arg0.getKeyCode());		
		if(arg0.getSource() == loginPanel.txtUser || arg0.getSource() == loginPanel.txtPass ){
			int nKeyCode = arg0.getKeyCode();
			if(nKeyCode == 10){
				loginPanel.txtUser.setEnabled(false);
				loginPanel.txtPass.setEnabled(false);
				String user = loginPanel.txtUser.getText();
				String password = new String(loginPanel.txtPass.getPassword());								
				isUserOK = fireValidateUser(user, password);				
				updateUI(isUserOK);				
			} else if(nKeyCode == 27){
				System.exit(0);
			}
		}		
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	NewUserListener listener;
    public void addNewUserListener(NewUserListener l) {        
        listener = l;          
    }
	
	boolean fireValidateUser(String user, String password){
		NewUserEvent event = new NewUserEvent(this);         		
        return listener.validateNewUser(event, user, password);
	}

	public void updateUI(boolean isUserOK){
		if(isUserOK){
			loginPanel.btnOK.setEnabled(false);
			setVisible(false);			
			return;
		}else{			
			loginPanel.txtUser.setText("");
			loginPanel.txtPass.setText("");
			loginPanel.txtUser.setEnabled(true);
			loginPanel.txtPass.setEnabled(true);
			loginPanel.txtUser.requestFocus();	
		}
	}
	
	//validateToHost
	//validateToGrid
	Login login;
	public void setLogin(Login login){
		this.login = login;
	}
	String userName;
	public boolean validateNewUser(NewUserEvent e, String user, String password) {				
		if(Login.validateUser(user, password)){
			if(Login.validateGridSecur){				
				userName = user;
				return true;				
			}else{				
				userName = user;
				return true;
			}
		}else{						
			return false;
		}		
	}
	
	boolean isUserOK(){
		return isUserOK;
	}
}
