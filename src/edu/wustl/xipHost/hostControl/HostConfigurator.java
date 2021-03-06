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

package edu.wustl.xipHost.hostControl;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.ws.Endpoint;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.State;
import org.xmldb.api.base.XMLDBException;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.application.ApplicationTerminationEvent;
import edu.wustl.xipHost.application.ApplicationTerminationListener;
import edu.wustl.xipHost.hostLogin.GridLogin;
import edu.wustl.xipHost.hostLogin.LoginDialog;
import edu.wustl.xipHost.hostLogin.STSLogin;
import edu.wustl.xipHost.hostLogin.XUALogin;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.pdq.PDQManager;
import edu.wustl.xipHost.pdq.PDQManagerFactory;
import edu.wustl.xipHost.caGrid.GridManager;
import edu.wustl.xipHost.caGrid.GridManagerFactory;
import edu.wustl.xipHost.dicom.DicomManager;
import edu.wustl.xipHost.gui.ConfigPanel;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.worklist.Worklist;
import edu.wustl.xipHost.worklist.WorklistFactory;
import edu.wustl.xipHost.xds.XDSManager;
import edu.wustl.xipHost.xds.XDSManagerFactory;
import edu.wustl.xipHost.hostLogin.Login;

public class HostConfigurator implements ApplicationTerminationListener {
	final static Logger logger = Logger.getLogger(HostConfigurator.class);	 
	File hostTmpDir;
	File hostOutDir;
	File hostConfig;
	HostMainWindow mainWindow;
	ConfigPanel configPanel = new ConfigPanel(new JFrame()); 		//ConfigPanel is used to specify tmp and output dirs	
	GridManager gridMgr;
	DicomManager dicomMgr;		
	ApplicationManager appMgr;
	PDQManager pdqMgr;
	XDSManager xdsMgr;
	File xipApplicationsConfig;	
	public static final String OS = System.getProperty("os.name");
	String userName = "xip";
	ApplicationTerminationListener applicationTerminationListener;

	public HostConfigurator(){
		applicationTerminationListener = this;
	}
	public boolean runHostStartupSequence(){		
		logger.info("Launching XIP Host. Platform " + OS);
		hostConfig = new File("./config/xipConfig.xml");
		if(loadHostConfigParameters(hostConfig) == false){
			new ExceptionDialog("Unable to load Host configuration parameters.", 
					"Ensure host config file config file is valid.",
					"Host Startup Dialog");
			System.exit(0);
		}

		if(loadPixelmedSavedImagesFolder(serverConfig) == false){		
			new ExceptionDialog("Unable to load Pixelmed/HSQLQB configuration parameters.", 
					"Ensure Pixelmed/HSQLQB config file is valid.",
					"Host DB Startup Dialog");
			System.exit(0);
		}

		//if config contains displayConfigDialog true -> displayConfigDialog
		if(getDisplayStartUp()){
			displayConfigDialog();			
		}		
		hostTmpDir = createSubTmpDir(getParentOfTmpDir());		
		hostOutDir = createSubOutDir(getParentOfOutDir());
		prop.setProperty("Application.SavedImagesFolderName", getPixelmedSavedImagesFolder());		
		
		logNewUser(true);

		//run GridManagerImpl startup
		gridMgr = GridManagerFactory.getInstance();
		gridMgr.runGridStartupSequence();				
		//test for gridMgr == null				
        gridMgr.setImportDirectory(hostTmpDir);		

        //run PDQManager startup
		pdqMgr = PDQManagerFactory.getInstance();
		pdqMgr.runStartupSequence();		    	    	

		//run XDSManager startup
		xdsMgr = XDSManagerFactory.getInstance();
		xdsMgr.runStartupSequence();		    	    	
				
    	//run WorkList startup
		Worklist worklist = WorklistFactory.getInstance();        		
		String path = "./config/worklist.xml";
		File xmlWorklistFile = new File(path);					
		worklist.loadWorklist(xmlWorklistFile);		
				
    	appMgr = ApplicationManagerFactory.getInstance();
    	appMgr.addApplicationTerminationListener(applicationTerminationListener);
    	xipApplicationsConfig = new File("./config/applications.xml");	
    	try {
			appMgr.loadApplications(xipApplicationsConfig);
			//Load test applications, RECIST is currently supported on Windows only			
			boolean loadTestApps = false;
			if(loadTestApps){
				loadTestApplications();
			}
		} catch (JDOMException e) {
			new ExceptionDialog("Unable to load applications.", 
					"Ensure applications xml config file exists and is valid.",
					"Host Startup Dialog");
			System.exit(0);
		} catch (IOException e) {
			new ExceptionDialog("Unable to load applications.", 
					"Ensure applications xml config file exists and is valid.",
					"Host Startup Dialog");
			System.exit(0);
		}
		//hostOutDir and hostTmpDir are hold in static variables in ApplicationManager
		appMgr.setOutputDir(hostOutDir);		
		appMgr.setTmpDir(hostTmpDir);		
		
		//XindiceManager is used to register XMLDB database used to store and manage
		//XML Native Models
		XindiceManager xm = XindiceManagerFactory.getInstance();
		try {
			xm.startup();
		} catch (XMLDBException e) {
			//TODO Auto-generated catch block
			//Q: what to do if Xindice is not launched
			//1. Go with no native model support or 
			//2. prompt user and exit
			e.printStackTrace();
		}				
		mainWindow = new HostMainWindow();											
		mainWindow.setUserName(userName);			
		mainWindow.display();					
		return true;			
	}
			
	SAXBuilder builder = new SAXBuilder();
	Document document;
	Element root;
	String parentOfTmpDir;
	String parentOfOutDir;	
	Boolean displayStartup;
	String aeTitle;
	String pdqSendFacilityOID;
	String pdqSendApplicationOID;
	String auditRepositoryURL;
	String stsURL;
	String trustStoreLoc;
	String trustStorePswd;
	/**	
	 * (non-Javadoc)
	 * @see edu.wustl.xipHost.hostControl.HostManager#loadHostConfigParameters()
	 */
	public boolean loadHostConfigParameters (File hostConfigFile) {								
		if(hostConfigFile == null){
			return false;
		}else if(!hostConfigFile.exists()){
			return false;
		}else{
			try{				
				document = builder.build(hostConfigFile);
				root = document.getRootElement();				
				//path for the parent of TMP directory
				if(root.getChild("tmpDir") == null){
					parentOfTmpDir = "";
				}else if(root.getChild("tmpDir").getValue().trim().isEmpty() ||
						new File(root.getChild("tmpDir").getValue()).exists() == false){
					parentOfTmpDir = "";
				}else{					
					parentOfTmpDir = root.getChild("tmpDir").getValue();																
				}

				if(root.getChild("outputDir") == null){
					parentOfOutDir = "";
				}else if(root.getChild("outputDir").getValue().trim().isEmpty() ||
						new File(root.getChild("outputDir").getValue()).exists() == false){
					parentOfOutDir = "";
				}else{
					//path for the parent of output directory. 
					//parentOfOutDir used to store data produced by the xip application													                                                                       		        							
					parentOfOutDir = root.getChild("outputDir").getValue();	    							    							        					
				}

				if(root.getChild("displayStartup") == null){
					displayStartup = new Boolean(true);
				}else{
					if(root.getChild("displayStartup").getValue().equalsIgnoreCase("true") ||
							root.getChild("displayStartup").getValue().trim().isEmpty() ||
							parentOfTmpDir.isEmpty() ||
							parentOfOutDir.isEmpty() || parentOfTmpDir.equalsIgnoreCase(parentOfOutDir)){
		        		if(parentOfTmpDir.equalsIgnoreCase(parentOfOutDir)){
		        			parentOfTmpDir = "";
		        			parentOfOutDir = "";
		        		}
						displayStartup = new Boolean(true);
		        	}else if (root.getChild("displayStartup").getValue().equalsIgnoreCase("false")){
		        		displayStartup = new Boolean(false);
		        	}else{
		        		displayStartup = new Boolean(true);
		        	}
				}	        	        	

				if(root.getChild("AETitle") == null){
					aeTitle = "";
				}else if(root.getChild("AETitle").getValue().trim().isEmpty()){
					aeTitle = "";
				}else{					
					aeTitle = root.getChild("AETitle").getValue();																
				}
				
				if(root.getChild("AuditRepositoryURL") == null){
					auditRepositoryURL = "";
				}else if(root.getChild("AuditRepositoryURL").getValue().trim().isEmpty()){
					auditRepositoryURL = "";
				}else{					
					auditRepositoryURL = root.getChild("AuditRepositoryURL").getValue();																
				}

				if(root.getChild("PdqSendFacilityOID") == null){
					pdqSendFacilityOID = "";
				}else if(root.getChild("PdqSendFacilityOID").getValue().trim().isEmpty()){
					pdqSendFacilityOID = "";
				}else{					
					pdqSendFacilityOID = root.getChild("PdqSendFacilityOID").getValue();																
				}

				if(root.getChild("PdqSendApplicationOID") == null){
					pdqSendApplicationOID = "";
				}else if(root.getChild("PdqSendApplicationOID").getValue().trim().isEmpty()){
					pdqSendApplicationOID = "";
				}else{					
					pdqSendApplicationOID = root.getChild("PdqSendApplicationOID").getValue();																
				}
				if(root.getChild("StsUrl") == null){
					stsURL = "";
				} else {
					stsURL = (root.getChild("StsUrl").getValue());
				}
				if(root.getChild("HostTrustStoreFile") == null){
					trustStoreLoc = "";
				} else {
					trustStoreLoc = (root.getChild("HostTrustStoreFile").getValue());
				}
				if(root.getChild("HostTrustStorePswd") == null){
					trustStorePswd = "";
				} else {
					trustStorePswd = (root.getChild("HostTrustStorePswd").getValue());
				}
				if(root.getChild("useXUA") == null){
					useXUA = false;
				} else {
					useXUA = Boolean.valueOf(root.getChild("useXUA").getValue());
				}
				if(root.getChild("useNBIASecur") == null){
					useNBIASecur = false;
				} else {
					useNBIASecur = Boolean.valueOf(root.getChild("useNBIASecur").getValue());
				}
				if(root.getChild("useSTS") == null){
					useSTS = false;
				} else {
					useSTS = Boolean.valueOf(root.getChild("useSTS").getValue());
				}
			} catch (JDOMException e) {				
				return false;
			} catch (IOException e) {
				return false;
			}			
		}
		return true;
    }
	
	File serverConfig = new File("./pixelmed-server-hsqldb/workstation1.properties");
	Properties prop = new Properties();
	String pixelmedSavedImagesFolder;
	boolean loadPixelmedSavedImagesFolder(File serverConfig){
		if(serverConfig == null){return false;}		
		try {
			prop.load(new FileInputStream(serverConfig));
			pixelmedSavedImagesFolder = prop.getProperty("Application.SavedImagesFolderName");
			if(new File(pixelmedSavedImagesFolder).exists() == false){
				pixelmedSavedImagesFolder = "";
				displayStartup = new Boolean(true);
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public String getParentOfOutDir(){
		return parentOfOutDir;
	}
	public void setParentOfOutDir(String newDir){
		parentOfOutDir = newDir;
	}
	
	public String getParentOfTmpDir(){
		return parentOfTmpDir;
	}
	
	public File getHostTmpDir(){
		return hostTmpDir;
	}
	
	public void setParentOfTmpDir(String newDir){
		parentOfTmpDir = newDir;
	}
	
	public String getPixelmedSavedImagesFolder(){
		return pixelmedSavedImagesFolder;
	}
	public void setPixelmedSavedImagesFolder(String pixelmedDir){
		pixelmedSavedImagesFolder = pixelmedDir;
	}
	
	public Boolean getDisplayStartUp(){
		return displayStartup;
	}
	public void setDisplayStartUp(Boolean display){
		displayStartup = display;
	}
	
	public String getUser(){
		return userName;
	}
	
	public String getAETitle(){
		return aeTitle;
	}
	
	public String getAuditRepositoryURL(){
		return auditRepositoryURL;
	}

	public void setAuditRepositoryURL(String auditRepositoryURLIn){
		auditRepositoryURL = auditRepositoryURLIn;
	}
	
	public String getPDQSendFacilityOID(){
		return pdqSendFacilityOID;
	}

	public void setPDQSendFacilityOIDL(String pdqSendFacilityOIDIn){
		pdqSendFacilityOID = pdqSendFacilityOIDIn;
	}
	
	public String getpdqSendApplicationOID(){
		return pdqSendApplicationOID;
	}

	public void setpdqSendApplicationOID(String pdqSendApplicationOIDIn){
		pdqSendApplicationOID = pdqSendApplicationOIDIn;
	}
	
	/**
	 * method creates subdirectory under parent of tmp directory.
	 * Creating sub directory is meant to prevent situations when tmp dirs
	 * would be created directly on system main dir path e.g. C:\ 
	 * @return
	 */
	File createSubTmpDir(String parentOfTmpDir){
		if(parentOfTmpDir == null || parentOfTmpDir.trim().isEmpty()){return null;}
		try {			
			File tmpFile = Util.create("TmpXIP", ".tmp", new File(parentOfTmpDir));			
			tmpFile.deleteOnExit();			
			return tmpFile;					
		} catch (IOException e) {						
			return null;
		}
	}	
	
	/**
	 * method creates subdirectory under parent of output directory.
	 * Creating sub directory is meant to prevent situations when output dirs
	 * would be created directly on system main dir path e.g. C:\ 
	 * @return
	 */
	File createSubOutDir(String parentOfOutDir){
		if(parentOfOutDir == null || parentOfOutDir.trim().isEmpty()){return null;}		
		if(new File(parentOfOutDir).exists() == false){return null;}
		File outFile = new File(parentOfOutDir, "OutputXIP");
		if(!outFile.exists()){
			outFile.mkdir();
		}
		return outFile;
	}
	
	
	void displayConfigDialog(){							
		configPanel.setParentOfTmpDir(getParentOfTmpDir());
		configPanel.setParentOfOutDir(getParentOfOutDir());
		configPanel.setPixelmedSavedImagesDir(getPixelmedSavedImagesFolder());
		configPanel.setDisplayStartup(getDisplayStartUp());
		configPanel.display();    		
		setParentOfTmpDir(configPanel.getParentOfTmpDir());
		setParentOfOutDir(configPanel.getParentOfOutDir());
		setPixelmedSavedImagesFolder(configPanel.getPixelmedSavedImagesDir());
		setDisplayStartUp(configPanel.getDisplayStartup());				
	}	
	
	public void storeHostConfigParameters(File hostConfigFile) {      			
		root.getChild("tmpDir").setText(parentOfTmpDir);
		root.getChild("outputDir").setText(parentOfOutDir);
		root.getChild("displayStartup").setText(displayStartup.toString());	
		try {
			FileOutputStream outStream = new FileOutputStream(hostConfigFile);
			XMLOutputter outToXMLFile = new XMLOutputter();
	    	outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();                       
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
	}
		
	List<URL []> EPRs = new ArrayList<URL []>();
    int numOfDeplayedServices = 0;   
	Endpoint ep;
        
	static HostConfigurator hostConfigurator;
	public static void main (String [] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
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
		
		//Turn off commons loggin for better performance
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		DOMConfigurator.configure("log4j.xml");
		hostConfigurator = new HostConfigurator();
		boolean startupOK = hostConfigurator.runHostStartupSequence();
		if(startupOK == false){			
			logger.fatal("XIPHost startup error. System exits.");
			System.exit(0);
		}		
		/*final long MEGABYTE = 1024L * 1024L;
		System.out.println("Total heap size: " + (Runtime.getRuntime().maxMemory())/MEGABYTE);
		System.out.println("Used heap size: " + (Runtime.getRuntime().totalMemory())/MEGABYTE);
		System.out.println("Free heap size: " + (Runtime.getRuntime().freeMemory())/MEGABYTE);*/
	}
	
	public static HostConfigurator getHostConfigurator(){
		return hostConfigurator;
	}
	
	public HostMainWindow getMainWindow(){
		return mainWindow;
	}
	
	
	List<Application> activeApplications;
	public void runHostShutdownSequence(){		
		//TODO
		//Modify runHostShutdownSequence. Hosted application tabs are not removed, host terminates first, or could terminate first before hosted applications have a chance to terminate first
		//Host can terminate only if no applications are running (verify applications are not running)
		activeApplications = getActiveApplications();
		terminateActiveApplications(activeApplications);
		logger.info("Shutting down XIP Host.");
		//Store Host configuration parameters
		storeHostConfigParameters(hostConfig);
		List<Application> applications = appMgr.getApplications();
		List<Application> notValidApplications = appMgr.getNotValidApplications();
		List<Application> appsToStore = new ArrayList<Application>();
		appsToStore.addAll(applications);
		appsToStore.addAll(notValidApplications);
		//Store Applications		
		appMgr.storeApplications(appsToStore, xipApplicationsConfig);
		//Perform Grid shutdown that includes store grid locations
		if(gridMgr.runGridShutDownSequence() == false){
			new ExceptionDialog("Error when storing grid locations.", 
					"System will save any modifications made to grid locations.",
					"Host Shutdown Dialog");
		}
		
		//Clear content of TmpDir but do not delete TmpDir itself
		File dir = new File(getParentOfTmpDir());				
		boolean bln = Util.deleteHostTmpFiles(dir);
		if(bln == false){
			new ExceptionDialog("Not all content of Host TMP directory " + hostTmpDir + " was cleared.", 
					"Only subdirs starting with 'TmpXIP' and ending with '.tmp' and their content is deleted.",
					"Host Shutdown Dialog");
		}			
		XindiceManagerFactory.getInstance().shutdown();
		//Clear Xindice directory. Ensures all documents and collections are cleared even when application
		//does not terminate properly
		Util.delete(new File("./db"));
		//Run DICOM shutdown sequence
		//dicomMgr.runDicomShutDownSequence("jdbc:hsqldb:./pixelmed-server-hsqldb/hsqldb/data/ws1db", "sa", "");
		logger.info("XIPHost exits. Thank you for using XIP Host.");
        System.exit(0);	
	}
	
	public void terminateActiveApplications(List<Application> applications){
		for(Application app : applications){			
			app.shutDown();
		}
		synchronized(activeApplications) {
			while(activeApplications.size() != 0){
				try {
					activeApplications.wait();
				} catch (InterruptedException e) {
					logger.error(e,  e);
				}
			}	
		}
	}
	
	public List<Application> getActiveApplications(){
		activeApplications = new ArrayList<Application>();
		List<Application> applications = appMgr.getApplications();
		synchronized(activeApplications){
			for(Application app : applications){			
				State state = app.getState();			
				if(state != null && state.equals(State.EXIT) == false ){
					activeApplications.add(app);
				}
			}
			return activeApplications;
		}	
	}
	
	
	@Override
	public void applicationTerminated(ApplicationTerminationEvent event) {
		Application application = (Application)event.getSource();
		if(activeApplications == null) {
			activeApplications = getActiveApplications();
		}
		synchronized(activeApplications){
			activeApplications.remove(application);
			activeApplications.notify();
		}
	}
	
	public ApplicationTerminationListener getApplicationTerminationListener(){
		return applicationTerminationListener;
	}
	
	
	void loadTestApplications(){
		if(OS.contains("Windows")){								
			if(appMgr.getApplication("TestApp_WG23FileAccess") == null){
				try {
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.bat").getCanonicalPath();
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);				
					appMgr.addApplication(new Application("TestApp_WG23FileAccess", pathExe, "", "", iconFile.getAbsolutePath(), "analytical", true, "files", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("TestApp_WG23NativeModel") == null){
				try{	
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPAppNativeModel.bat").getCanonicalPath();
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("TestApp_WG23NativeModel", pathExe, "", "", iconFile.getAbsolutePath(), "analytical", true, "native", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("RECIST_Adjudicator") == null){				
				try {
					String pathExe = new File("../XIPApp/bin/RECISTFollowUpAdjudicator.bat").getCanonicalPath();
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("RECIST_Adjudicator", pathExe, "", "", iconFile.getAbsolutePath(), "rendering", true, "files", 1, IterationTarget.SERIES));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}				
		}else{
			if(appMgr.getApplication("TestApp_WG23FileAccess") == null){
				try {
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.sh").getCanonicalPath();
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);				
					appMgr.addApplication(new Application("TestApp_WG23FileAccess", pathExe, "", "", iconFile.getAbsolutePath(), "analytical", true, "files", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("TestApp_WG23NativeModel") == null){
				try{	
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPAppNativeModel.sh").getCanonicalPath();
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("TestApp_WG23NativeModel", pathExe, "", "", iconFile.getAbsolutePath(), "analytical", true, "native", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	public static int adjustForResolution(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//logger.debug("Sceen size: Width " + screenSize.getWidth() + ", Height " + screenSize.getHeight());
		int height = (int)screenSize.getHeight();
		int preferredHeight = 600;
		if (height < 768 && height >= 600 ){
			preferredHeight = 350;
		}else if(height < 1024 && height >= 768 ){
			preferredHeight = 450;
		}else if (height >= 1024 && height < 1200){
			preferredHeight = 600 - 100;
		}else if(height > 1200 && height <= 1440){
			preferredHeight = 800;
		}
		return preferredHeight;		
	}
	
	static Login login;
	public static Login getLogin(){
		return login;
	}
	
	boolean useXUA;
	boolean useNBIASecur;
	boolean useSTS;
	public void logNewUser(boolean enableExitOnEsc){
		if(useXUA == false && useNBIASecur == false && useSTS == false){
			useSTS = true;
		}
		if (useSTS) {
			login = new STSLogin(stsURL, trustStoreLoc, trustStorePswd);
		} else if(useXUA) {
			login = new XUALogin(stsURL, trustStoreLoc, trustStorePswd);
		} else if (useNBIASecur) {
			login = new GridLogin();
		}
		LoginDialog loginDialog = new LoginDialog();
		loginDialog.setEnableExitOnEsc(enableExitOnEsc);
		loginDialog.setLogin(login);
		loginDialog.setModal(true);
		loginDialog.setVisible(true);
		userName = login.getUsername();
		if(mainWindow != null){
			HostMainWindow.getHostIconBar().setUserName(userName);
		}
	}
	
	public void resetPanels(){
		mainWindow.resetPanels();
	}
}
