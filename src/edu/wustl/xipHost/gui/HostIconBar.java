/*
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import javax.swing.*;

import org.apache.log4j.Logger;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationBar;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Customized IconBar
 */
public class HostIconBar extends JPanel {			
	final static Logger logger = Logger.getLogger(HostIconBar.class);
	Font font = new Font("Tahoma", 0, 12);
	Font fontBold = new Font("Tahoma", 1, 12);		
	JButton btnHost = new JButton(new ImageIcon("./gif/XIP-Host-Corner-Logo-Red.PNG"));
	ImageIcon iconHelp = new ImageIcon("./gif/Help24.gif");
	ImageIcon iconAbout = new ImageIcon("./gif/About24.gif");
	JLabel lblHelp = new JLabel(iconHelp, JLabel.CENTER);
	JLabel lblAbout = new JLabel(iconAbout, JLabel.CENTER);
	
	JLabel lblName = new JLabel();
	JPanel hostTopPanel = new JPanel();
	JPanel hostPanel = new JPanel();	
	JButton btnLocal = new JButton("C:\\", new ImageIcon("./gif/folder-open.png"));
	JButton btnOptions = new JButton("Options", new ImageIcon("./gif/Options24.gif"));
	JButton btnExit = new JButton("Exit", new ImageIcon("./gif/cup.gif"));
	JPanel hostBtnPanel = new JPanel();			
	
	JButton btnSuspend = new JButton("Suspend", new ImageIcon("./gif/suspend-24x24.GIF"));
	JButton btnCancel = new JButton("Cancel", new ImageIcon("./gif/Delete-24x24.GIF"));	
	JButton btnExitApp = new JButton("Exit", new ImageIcon("./gif/cup.gif"));
	JPanel appBtnPanel = new JPanel();	
	
	ApplicationBar appBar = new ApplicationBar();
	Color xipColor = new Color(51, 51, 102);
	
    public HostIconBar() {                                        	    	    	
        setLayout(new BorderLayout());
        hostTopPanel.setLayout(new BorderLayout());
        hostTopPanel.add(hostPanel, BorderLayout.WEST);
        hostTopPanel.add(hostBtnPanel, BorderLayout.EAST);
        hostTopPanel.setBackground(xipColor);
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
    	btnHost.setBackground(xipColor);
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
    	hostPanel.setBackground(xipColor);
    	hostBtnPanel.add(btnLocal);    	    	
    	hostBtnPanel.add(btnOptions);
    	hostBtnPanel.add(btnExit);
    	hostBtnPanel.setBackground(xipColor);
    	appBtnPanel.add(btnSuspend);    	
    	appBtnPanel.add(btnCancel);
    	appBtnPanel.add(btnExitApp);
    	appBtnPanel.setBackground(xipColor);    	
    	setBackground(xipColor); 
    	add(hostTopPanel, BorderLayout.NORTH);
    	List<Application> allApps = ApplicationManagerFactory.getInstance().getApplications();
		Map<String, Application> appMap = new LinkedHashMap<String, Application>();
		for(int i = 0; i < allApps.size(); i++){
			Application app = allApps.get(i);
			String name = app.getName();
			appMap.put(name, app);
		}
		Collection<Application> values = appMap.values();					
		appBar.setApplications(new ArrayList<Application>(values));
		appBar.setBackground(xipColor);
		add(appBar, BorderLayout.SOUTH);
    	
    }                   
    
    /*
     * SwitchButtons is used to switch beteewn Host and Application icons (buttons)
     * int 0 - switch to host icons
     * int 1 - witch to application icons
     */    
    public void switchButtons(int i){
    	if(i == 0){
    		hostTopPanel.remove(appBtnPanel);
    		hostTopPanel.add(hostBtnPanel, BorderLayout.EAST);
    	}else if(i == 1){
    		hostTopPanel.remove(hostBtnPanel);
    		hostTopPanel.add(appBtnPanel, BorderLayout.EAST);
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
	
	public ApplicationBar getApplicationBar(){
		return appBar;
	}
}