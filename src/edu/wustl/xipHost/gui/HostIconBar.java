/*
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Customized IconBar
 */
public class HostIconBar extends JToolBar {			
	Font font = new Font("Tahoma", 0, 12);
	Font fontBold = new Font("Tahoma", 1, 12);		
	JButton btnHost = new JButton(new ImageIcon("./gif/XIP-Host-Corner-Logo-Red.PNG"));
	ImageIcon iconHelp = new ImageIcon("./gif/Help24.gif");
	ImageIcon iconAbout = new ImageIcon("./gif/About24.gif");
	JLabel lblHelp = new JLabel(iconHelp, JLabel.CENTER);
	JLabel lblAbout = new JLabel(iconAbout, JLabel.CENTER);
	
	JLabel lblName = new JLabel();
	JPanel hostPanel = new JPanel();	
	JButton btnLocal = new JButton("C:\\", new ImageIcon("./gif/folder-open.png"));
	JButton btnOptions = new JButton("Options", new ImageIcon("./gif/Options24.gif"));
	JButton btnExit = new JButton("Exit", new ImageIcon("./gif/cup.gif"));
	JPanel hostBtnPanel = new JPanel();			
	
	JButton btnSuspend = new JButton("Suspend", new ImageIcon("./gif/suspend-24x24.GIF"));
	JButton btnCancel = new JButton("Cancel", new ImageIcon("./gif/Delete-24x24.GIF"));	
	JButton btnExitApp = new JButton("Exit", new ImageIcon("./gif/cup.gif"));
	JPanel appBtnPanel = new JPanel();	
		
    public HostIconBar() {                                        	    	    	
    	setLayout(new BorderLayout());    	
    	lblName.setFont(fontBold);
    	lblName.setForeground(Color.WHITE);
    	lblHelp.setToolTipText("Help Contents");
    	lblAbout.setToolTipText("About");
    	JButton[] btns = new JButton[7];
    	btns[0] = btnHost;    	    	
    	btns[1] = btnLocal;
    	btns[2] = btnOptions;
    	btns[3] = btnExit;
    	btns[4] = btnSuspend;
    	btns[5] = btnCancel;
    	btns[6] = btnExitApp;
    	    	
    	for(int i = 0; i < btns.length; i++){    		
        	btns[i].setFont(font);
        	btns[i].setBackground(new Color(51, 51, 102));
        	btns[i].setForeground(Color.WHITE);
        	btns[i].setOpaque(true);        	
        	btns[i].setRolloverEnabled(true);
        	btns[i].setPreferredSize(new Dimension((int)btns[i].getPreferredSize().getWidth(), 30));
    	}    	
    	btnHost.setForeground(Color.BLACK);
    	btnLocal.setForeground(Color.BLACK);
    	btnOptions.setForeground(Color.BLACK);
    	btnExit.setForeground(Color.BLACK);
    	btnSuspend.setForeground(Color.BLACK);
    	btnCancel.setForeground(Color.BLACK);
    	btnExitApp.setForeground(Color.BLACK);
    	hostPanel.add(btnHost);
    	hostPanel.add(lblHelp);
    	hostPanel.add(lblAbout);
    	hostPanel.add(lblName);
    	hostPanel.setBackground(Color.BLACK);
    	add(hostPanel, BorderLayout.WEST);    	    	
    	hostBtnPanel.add(btnLocal);    	    	
    	hostBtnPanel.add(btnOptions);
    	hostBtnPanel.add(btnExit);
    	add(hostBtnPanel, BorderLayout.EAST);
    	hostBtnPanel.setBackground(Color.BLACK);
    	appBtnPanel.add(btnSuspend);    	
    	appBtnPanel.add(btnCancel);
    	appBtnPanel.add(btnExitApp);
    	appBtnPanel.setBackground(Color.BLACK);    	
    	setBackground(Color.BLACK);    	
    }                   
    
    /*
     * SwitchButtons is used to switch beteewn Host and Application icons (buttons)
     * int 0 - switch to host icons
     * int 1 - witch to application icons
     */    
    public void switchButtons(int i){
    	if(i == 0){
    		remove(appBtnPanel);
    		add(hostBtnPanel, BorderLayout.EAST);    		
    	}else if(i == 1){
    		remove(hostBtnPanel);
    		add(appBtnPanel, BorderLayout.EAST);
    	}    	    	
    	updateUI(); 
    	
    }
    
    public static void main (String args[]){   
	    	try {
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
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
        	HostIconBar bar = new HostIconBar();
        	JFrame frame = new JFrame();
        	frame.getContentPane().add(bar, BorderLayout.NORTH);
        	frame.pack();
            frame.setExtendedState (JFrame.MAXIMIZED_BOTH);        	
        	frame.setVisible(true);
        	frame.addWindowListener(new WindowAdapter(){	         
    			public void windowClosing(WindowEvent e){    	        	     	        	
    	        	System.exit(0);
    	         }	         
    		});
    }
    
	public void setUserName(String userName){
		lblName.setText("User: " + userName);
	}
}