/*
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostLogin;

import javax.swing.*;
import org.jdom.JDOMException;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.hostControl.HostConfigurator;

import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SwitchUserDialog extends JDialog implements ActionListener {
    JLabel icon = new JLabel();
    JLabel lblInfo = new JLabel("Running hosted applications:");
    JLabel lblInfo_3 = new JLabel("Do you want to terminate the above applications and switch user?");
    JPanel panelActiveApps;
    JPanel btnPanel = new JPanel(new GridLayout(1, 2));
    JButton btnCancel = new JButton("Cancel");
    Icon iconSwitchUser =  new ImageIcon("./gif/Users.PNG");
    JButton btnSwitch = new JButton("SwitchUser");
    Color xipColor = new Color(51, 51, 102);		
	
    public SwitchUserDialog(Frame owner, List<Application> activeApps){
        super(owner, "Switch User", true);        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        icon.setIcon(iconSwitchUser);
        //icon.setBorder(border1);
        Font font = new Font("SansSerif", 1, 14);   
        Font font_1 = new Font("SansSerif", 1, 12);
        
        lblInfo.setFont(font);
        lblInfo.setForeground(Color.WHITE);
        
        panelActiveApps = new JPanel(new GridLayout(activeApps.size(), 1));
        panelActiveApps.setBackground(xipColor);    
        for(Application activeApp : activeApps){
        	 JLabel lblActiveApp = new JLabel(activeApp.getName() + " ID: " + activeApp.getID().toString());
        	 lblActiveApp.setFont(font_1);        
        	 lblActiveApp.setForeground(Color.WHITE);
        	 panelActiveApps.add(lblActiveApp);
        }
        
        
        lblInfo_3.setFont(font_1);        
        lblInfo_3.setForeground(Color.WHITE);
        
        btnCancel.addActionListener(this);
        btnSwitch.addActionListener(this);
        btnCancel.setPreferredSize(new Dimension(80, 30));
        btnSwitch.setPreferredSize(new Dimension(80, 30));
        btnPanel.add(btnCancel);
        btnPanel.add(btnSwitch);
        btnPanel.setBackground(xipColor);
        
        add(icon);
        add(lblInfo);        
        add(panelActiveApps);
        add(lblInfo_3); 
        add(btnPanel);
        
        //versionDataPanel.setBackground(xipColor);        
        //add(versionDataPanel);
        this.getContentPane().setBackground(xipColor);
        buildLayout();
                               
        //setSize(new Dimension(150, 25));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();                        
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /4,  windowSize.width, windowSize.height);
        setVisible(true);
        pack();
    }        
            
    public void buildLayout(){
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
        layout.setConstraints(lblInfo, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.top = 10;                
        constraints.insets.left = 30;        
        constraints.insets.bottom = 20;        
        layout.setConstraints(panelActiveApps, constraints);         
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.top = 10;                        
        constraints.insets.right = 50;        
        constraints.insets.bottom = 20;        
        layout.setConstraints(lblInfo_3, constraints);  
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.top = 10;                        
        constraints.insets.right = 50;        
        constraints.insets.bottom = 20;        
        layout.setConstraints(btnPanel, constraints);  
    }
    
    public static void main (String [] args) throws JDOMException, IOException{
    	ApplicationManager appMgr = ApplicationManagerFactory.getInstance();
    	File xipApplicationsConfig = new File("./config/applications.xml");	
    	appMgr.loadApplications(xipApplicationsConfig);
    	new SwitchUserDialog(new Frame(), appMgr.getApplications());
    }
    
    public JButton getBtnSwitch(){
    	return btnSwitch;
    }

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == btnCancel) {
			dispose();
		} else if (event.getSource() == btnSwitch) {
			dispose();
			HostConfigurator hostConfig = HostConfigurator.getHostConfigurator();
			hostConfig.terminateActiveApplications(hostConfig.getActiveApplications());
			Login login = HostConfigurator.getLogin();
    		login.invalidateSecuredConnection();
    		HostConfigurator.getHostConfigurator().logNewUser();
		}
		
	}
}