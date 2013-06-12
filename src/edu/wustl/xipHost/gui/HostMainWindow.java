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

package edu.wustl.xipHost.gui;

import javax.swing.*;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.avt2ext.AVTPanel;
import edu.wustl.xipHost.caGrid.GridPanel;
import edu.wustl.xipHost.dicom.DicomPanel;
import edu.wustl.xipHost.globalSearch.GlobalSearchPanel;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.hostLogin.SwitchUserDialog;
import edu.wustl.xipHost.localFileSystem.LocalFileSystemPanel;
import edu.wustl.xipHost.pdq.PDQPanel;
import edu.wustl.xipHost.worklist.WorklistPanel;
import edu.wustl.xipHost.xds.XDSPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.UUID;

/**
 * 
 */
public class HostMainWindow extends JFrame implements ActionListener {	             		    			
    static HostIconBar toolBar = new HostIconBar();
    static JTabbedPane sideTabbedPane;
    SideTabMouseAdapter mouseAdapter = new SideTabMouseAdapter();
    static PanelUUID hostPanel = new PanelUUID();   
    JTabbedPane tabPaneCenter = new JTabbedPane();            
    static Rectangle appScreenSize = new Rectangle();    
    OptionsDialog optionsDialog = new OptionsDialog(new JFrame());        
    static Color xipColor = new Color(51, 51, 102);
    Color xipLightBlue = new Color(156, 162, 189);
	Font font = new Font("Tahoma", 0, 12);
    
	String userName;			
	WorklistPanel worklistPanel;
	DicomPanel dicomPanel;
	GridPanel gridPanel;
	GlobalSearchPanel globalSearchPanel;
	XDSPanel xdsPanel;
	PDQPanel pdqPanel;
	AVTPanel avt2extPanel;
	LocalFileSystemPanel localFileSystemPanel;
	
	static Dimension screenSize;	
	
	public HostMainWindow(){
		super("XIP Host");
		worklistPanel = new WorklistPanel();
		dicomPanel = new DicomPanel();
		gridPanel = new GridPanel();
		globalSearchPanel = new GlobalSearchPanel();
		avt2extPanel = new AVTPanel();
		pdqPanel = new PDQPanel();
		xdsPanel = new XDSPanel();
		localFileSystemPanel = new LocalFileSystemPanel();
		if(HostConfigurator.OS.contains("Windows")){
			setUndecorated(true);
		}else{
			setUndecorated(false);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
	}	
		
    public void display(){                
    	UIManager.put("TabbedPane.selected", xipLightBlue);    	
    	setLayout(new BorderLayout());	
        toolBar.setUserName(userName);		
        add(toolBar, BorderLayout.NORTH);
        sideTabbedPane = VerticalTextIcon.createTabbedPane(JTabbedPane.RIGHT);
        sideTabbedPane.setBackground(xipColor);
        add(sideTabbedPane, BorderLayout.CENTER);
        sideTabbedPane.addMouseListener(mouseAdapter);       
        hostPanel.add(tabPaneCenter);
        hostPanel.setBackground(xipColor);
        buildHostPanelLayout();        
        hostPanel.setUUID(UUID.randomUUID());
        VerticalTextIcon.addTab(sideTabbedPane, "Host", hostPanel);   
                
        //Add tabs        
        ImageIcon icon = null;
        tabPaneCenter.addTab("Local File System", icon, localFileSystemPanel, null);
        tabPaneCenter.addTab("Local Database (AVT AD)", icon, avt2extPanel, null);
        tabPaneCenter.addTab("NBIA/caGrid", icon, gridPanel, null);                      	   
        tabPaneCenter.addTab("PACS", icon, dicomPanel, null);	   
        tabPaneCenter.addTab("GlobalSearch", icon, globalSearchPanel, null);        
        tabPaneCenter.addTab("Pat. ID Lookup (PDQ)", icon, pdqPanel, null);
        tabPaneCenter.addTab("Document Search (XDS)", icon, xdsPanel, null);
        tabPaneCenter.addTab("Worklist", icon, worklistPanel, null);
        tabPaneCenter.setFont(font);
        tabPaneCenter.setSelectedComponent(localFileSystemPanel);
        
        toolBar.btnHost.addActionListener(this);
        toolBar.btnOptions.addActionListener(this);
        toolBar.btnExit.addActionListener(this);
        toolBar.btnSuspend.addActionListener(this);
        toolBar.btnCancel.addActionListener(this);
        toolBar.btnSwitchUser.addActionListener(this);
        toolBar.btnExitApp.addActionListener(this);
        toolBar.lblAbout.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){						
				new AboutDialog(new JFrame());
			}
        });
        toolBar.lblHelp.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){						
				new HelpManager(new JFrame());
			}
        });
        
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, (int)screenSize.getWidth(), (int)screenSize.getHeight());
        getContentPane().setBackground(xipColor);        
        setVisible(true);                        
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);              
    }        	    	   
    
    void buildHostPanelLayout(){
    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        hostPanel.setLayout(layout);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;	        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(tabPaneCenter, constraints);  	               
    }
        
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == toolBar.btnHost){
    		sideTabbedPane.setSelectedIndex(0);
    		toolBar.switchButtons(0);
    		setAlwaysOnTop(true);
			setAlwaysOnTop(false);
    	} else if(e.getSource() == toolBar.btnOptions){
    		int x = (int)((JButton)e.getSource()).getLocationOnScreen().getX();
    		int y = (int)((JButton)e.getSource()).getLocationOnScreen().getY() + 45;  
    		optionsDialog.display(x, y);
    	} else if(e.getSource() == toolBar.btnSwitchUser) {
    		//TODO
    		//Stop all running hosted applications for the previous user
    		performSwitchUserTasks();
    		
    	} else if (e.getSource() == toolBar.btnExit) {
			HostConfigurator hostConfig = HostConfigurator.getHostConfigurator();
    		hostConfig.runHostShutdownSequence();    		    		
		}else if(e.getSource() == toolBar.btnCancel){
			Application app = getSelectedApplication();
			if(!app.cancelProcessing()){
				new ExceptionDialog("Selected application processing cannot be canceled.", 
						"Application state must be INPROGRESS or SUSPENDED.",
						"Host Dialog");
			}
		}else if(e.getSource() == toolBar.btnSuspend){
			Application app = getSelectedApplication();
			if(!app.suspendProcessing()){
				new ExceptionDialog("Selected application processing cannot be suspended.", 
						"Application state must be INPROGRESS but is " + app.getState().toString() + ".",
						"Host Dialog");
			}
		}else if(e.getSource() == toolBar.btnExitApp){
			Application app = getSelectedApplication();
			app.shutDown();
		}
	}
    
    Application getSelectedApplication(){
    	int index = sideTabbedPane.getSelectedIndex();
		UUID uuid = ((PanelUUID)sideTabbedPane.getComponentAt(index)).getUUID();
		Application app = ApplicationManagerFactory.getInstance().getApplication(uuid);
		return app;
    }
    
    
    public static Rectangle getApplicationPreferredSize(){
    	int appXPosition;
		int appYPosition;
		int appWidth;
		int appHeight;					
		if(HostConfigurator.OS.contains("Windows") || HostConfigurator.OS.contains("Mac OS X")){
			appXPosition = (int) hostPanel.getLocationOnScreen().getX();
			appYPosition = (int) hostPanel.getLocationOnScreen().getY();
			appWidth = (int) hostPanel.getBounds().getWidth();
			appHeight = (int) hostPanel.getBounds().getHeight();
		} else {
			appXPosition = 0;
			appYPosition = 0;
			appWidth = (int)screenSize.getWidth();
			appHeight = (int)screenSize.getHeight();
		}
		appScreenSize.setBounds(appXPosition, appYPosition, appWidth, appHeight);
		return appScreenSize;
    }
       
    public static void addTab(String appName, UUID appUUID){
    	PanelUUID panel =  new PanelUUID(appUUID);
    	panel.setBackground(xipColor);
    	VerticalTextIcon.addTab(sideTabbedPane, appName, panel);
    	int tabCount = sideTabbedPane.getTabCount();
    	sideTabbedPane.setSelectedIndex(tabCount - 1); 
    	toolBar.switchButtons(1);    	
    }
    
    public static void removeTab(UUID appUUID){
    	synchronized(sideTabbedPane){
    		int tabCount = sideTabbedPane.getTabCount();    	
        	for(int i = 0; i < tabCount; i++){
        		UUID tabUUID = ((PanelUUID)sideTabbedPane.getComponentAt(i)).getUUID();
        		if(appUUID.equals(tabUUID)){  
        			sideTabbedPane.remove(i);					
        			sideTabbedPane.setSelectedIndex(0);
                	sideTabbedPane.repaint();
        			toolBar.switchButtons(0);
        			return;
        		}
        	}
    	}
    }
    
    
    class SideTabMouseAdapter extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1){
				if(e.getSource() == sideTabbedPane){					
					int i = (((JTabbedPane)e.getSource()).getSelectedIndex());
					UUID uuid = ((PanelUUID)sideTabbedPane.getComponentAt(i)).getUUID();
					if (sideTabbedPane.getSelectedIndex() == 0){
						toolBar.switchButtons(0);
						bringToFront();
					} else if (sideTabbedPane.getSelectedIndex() != 0){																
						Application app = ApplicationManagerFactory.getInstance().getApplication(uuid);			
						bringToBack();
						app.bringToFront();					
						toolBar.switchButtons(1);						
					}else {
						setAlwaysOnTop(true);
						setAlwaysOnTop(false);
					}					
				}
			}
		}
	}
    
    private void bringToFront() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(this != null) {
                    toFront();
                    repaint();
                }
            }
        });
    }
    
    private void bringToBack() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(this != null) {
                    toBack();
                    repaint();
                }
            }
        });
    }
    
    public void setUserName(String userName){
    	this.userName = userName;
    }
	
	/*public void deiconify() {
        int state = getExtendedState();
        state &= ~Frame.ICONIFIED;
        setExtendedState(state);
    }*/
 
    public void iconify() {
        int state = getExtendedState();
        state |= Frame.ICONIFIED;
        setExtendedState(state);
    }
    
    public static HostIconBar getHostIconBar(){
    	return toolBar;
    }
    
    public Component getSelectedSearchTab() {
    	return tabPaneCenter.getSelectedComponent();
    }
    
    
    void performSwitchUserTasks() {
    	HostConfigurator hostConfig = HostConfigurator.getHostConfigurator();
    	List<Application> activeApps = hostConfig.getActiveApplications();
    	int numOfActiveApplications = activeApps.size();
    	if(numOfActiveApplications != 0 && hostConfig.getUser().equals("Guest") == false) {
    		new SwitchUserDialog(new Frame(), activeApps);
    	} else if (numOfActiveApplications == 0 && hostConfig.getUser().equals("Guest") == false) {
    		resetPanels();
    		HostConfigurator.getHostConfigurator().logNewUser(false);
    	} else if (hostConfig.getUser().equals("Guest")){
    		HostConfigurator.getHostConfigurator().logNewUser(false);
    	}
    }
    
    public void resetPanels(){
    	avt2extPanel.resetPanel();
    	dicomPanel.resetPanel();
    	gridPanel.resetPanel();
    	localFileSystemPanel.resetPanel();
    	//TODO: reset XSDPanel, GlobalPanel and Worklist
    }
}
