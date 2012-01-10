/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.openhealthtools.ihe.atna.auditor.IHEAuditor;
import org.xmldb.api.base.XMLDBException;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.application.ApplicationTerminationEvent;
import edu.wustl.xipHost.application.ApplicationTerminationListener;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.caGrid.GridManager;
import edu.wustl.xipHost.caGrid.GridManagerFactory;
import edu.wustl.xipHost.dicom.DicomManager;
import edu.wustl.xipHost.dicom.DicomManagerFactory;
import edu.wustl.xipHost.gui.ConfigPanel;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.LoginDialog;
import edu.wustl.xipHost.worklist.Worklist;
import edu.wustl.xipHost.worklist.WorklistFactory;
import edu.wustl.xipHost.xds.XDSManager;
import edu.wustl.xipHost.xds.XDSManagerFactory;

public class HostConfigurator implements ApplicationTerminationListener {
	final static Logger logger = Logger.getLogger(HostConfigurator.class);
	Login login = new Login();		 
	File hostTmpDir;
	File hostOutDir;
	File hostConfig;
	HostMainWindow mainWindow;
	ConfigPanel configPanel = new ConfigPanel(new JFrame()); 		//ConfigPanel is used to specify tmp and output dirs	
	GridManager gridMgr;
	DicomManager dicomMgr;		
	ApplicationManager appMgr;
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
		try {
			prop.store(new FileOutputStream(serverConfig), "Updated Application.SavedImagesFolderName");			
		} catch (FileNotFoundException e1) {
			System.exit(0);
		} catch (IOException e1) {
			System.exit(0);
		}
		dicomMgr = DicomManagerFactory.getInstance();
		Properties workstation1Prop = new Properties();
		try {
			workstation1Prop.load(new FileInputStream("./pixelmed-server-hsqldb/workstation1.properties"));
		} catch (FileNotFoundException e1) {
			logger.error(e1, e1);
			System.exit(0);
		} catch (IOException e1) {
			logger.error(e1, e1);
			System.exit(0);
		}
		dicomMgr.runDicomStartupSequence("./pixelmed-server-hsqldb/server", workstation1Prop);		    	    	
				
		// Set up default certificates for security.  Must be done after starting dicom, but before login.
		//TODO Move code to the configuration file, read entries from the configuration file, and move files to an XIP location.
		System.setProperty("javax.net.ssl.keyStore","/MESA/certificates/XIPkeystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword","caBIG2011");
		System.setProperty("javax.net.ssl.trustStore","/MESA/runtime/certs-ca-signed/2011_CA_Cert.jks");
		System.setProperty("javax.net.ssl.trustStorePassword","connectathon");
		System.setProperty("https.ciphersuites","TLS_RSA_WITH_AES_128_CBC_SHA");
		//System.setProperty("javax.net.debug","all");

		// Set up audit configuration.  Must be done before login.
		// TODO Get URI and possibly other parameters from the config file
		IHEAuditor.getAuditor().getConfig().setAuditorEnabled(false);
		if (auditRepositoryURL != ""){
		try {
				IHEAuditor.getAuditor().getConfig().setAuditRepositoryUri(new URI(auditRepositoryURL));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    System.out.println("URI to auditor improperly formed");
			}
			IHEAuditor.getAuditor().getConfig().setAuditSourceId(aeTitle);
			IHEAuditor.getAuditor().getConfig().setAuditorEnabled(true);
			// TODO figure out what should go here, or get from configuration
			IHEAuditor.getAuditor().getConfig().setAuditEnterpriseSiteId("IHE ERL");
			IHEAuditor.getAuditor().getConfig().setHumanRequestor("ltarbox");
			IHEAuditor.getAuditor().getConfig().setSystemUserId(userName);
			IHEAuditor.getAuditor().getConfig().setSystemUserName("Wash. Univ.");
		}

		LoginDialog loginDialog = new LoginDialog();
		loginDialog.setLogin(login);
		loginDialog.setModal(true);
		loginDialog.setVisible(true);
		userName = login.getUserName();		

		//run GridManagerImpl startup
		gridMgr = GridManagerFactory.getInstance();
		gridMgr.runGridStartupSequence();				
		//test for gridMgr == null				
        gridMgr.setImportDirectory(hostTmpDir);		

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
	String dicomStoragePort;
	String dicomStorageSecurePort;
	String dicomCommitPort;
	String dicomCommitSecurePort;
	String pdqSendFacilityOID;
	String pdqSendApplicationOID;
	String auditRepositoryURL;
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

				if(root.getChild("DicomStoragePort") == null){
					dicomStoragePort = "";
				}else if(root.getChild("DicomStoragePort").getValue().trim().isEmpty()){
					dicomStoragePort = "";
				}else{					
					dicomStoragePort = root.getChild("DicomStoragePort").getValue();																
				}

				if(root.getChild("DicomStorageSecurePort") == null){
					dicomStorageSecurePort = "";
				}else if(root.getChild("DicomStorageSecurePort").getValue().trim().isEmpty()){
					dicomStorageSecurePort = "";
				}else{					
					dicomStorageSecurePort = root.getChild("DicomStorageSecurePort").getValue();																
				}

				if(root.getChild("DicomCommitPort") == null){
					dicomCommitPort = "";
				}else if(root.getChild("DicomCommitPort").getValue().trim().isEmpty()){
					dicomCommitPort = "";
				}else{					
					dicomCommitPort = root.getChild("DicomCommitPort").getValue();																
				}

				if(root.getChild("DicomCommitSecurePort") == null){
					dicomCommitSecurePort = "";
				}else if(root.getChild("DicomCommitSecurePort").getValue().trim().isEmpty()){
					dicomCommitSecurePort = "";
				}else{					
					dicomCommitSecurePort = root.getChild("DicomCommitSecurePort").getValue();																
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
	
	public String getDicomStoragePort(){
		return dicomStoragePort;
	}

	public void setDicomStoragePort(String dicomStoragePortIn){
		dicomStoragePort = dicomStoragePortIn;
	}
	
	public String getDicomStorageSecurePort(){
		return dicomStorageSecurePort;
	}

	public void setDicomStorageSecurePort(String dicomStorageSecurePortIn){
		dicomStorageSecurePort = dicomStorageSecurePortIn;
	}
	
	public String getDicomCommitPort(){
		return dicomCommitPort;
	}

	public void setDicomCommitPort(String dicomCommitPortIn){
		dicomCommitPort = dicomCommitPortIn;
	}
	
	public String getDicomCommitSecurePort(){
		return dicomCommitSecurePort;
	}

	public void setDicomCommitSecurePort(String dicomCommitSecurePortIn){
		dicomCommitSecurePort = dicomCommitSecurePortIn;
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
	
	
	List<Application> activeApplications = new ArrayList<Application>();
	public void runHostShutdownSequence(){		
		//TODO
		//Modify runHostShutdownSequence. Hosted application tabs are not removed, host terminates first, or could terminate first before hosted applications have a chance to terminate first
		//Host can terminate only if no applications are running (verify applications are not running)
		List<Application> applications = appMgr.getApplications();
		synchronized(activeApplications){
			for(Application app : applications){			
				State state = app.getState();			
				if(state != null && state.equals(State.EXIT) == false ){
					activeApplications.add(app);
					app.shutDown();
				}
			}
			while(activeApplications.size() != 0){
				try {
					activeApplications.wait();
				} catch (InterruptedException e) {
					logger.error(e,  e);
				}
			}
		}		
		logger.info("Shutting down XIP Host.");
		//Store Host configuration parameters
		storeHostConfigParameters(hostConfig);
		//Store Applications		
		appMgr.storeApplications(applications, xipApplicationsConfig);
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
		
		
		/*ThreadGroup root = Thread.currentThread().getThreadGroup().getParent(); 
    	while (root.getParent() != null) {
    		root = root.getParent();
        }*/
        // Visit each thread group  
        System.exit(0);	
	}
	
	@Override
	public void applicationTerminated(ApplicationTerminationEvent event) {
		Application application = (Application)event.getSource();
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
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);				
					appMgr.addApplication(new Application("TestApp_WG23FileAccess", exeFile, "", "", iconFile, "analytical", true, "files", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("TestApp_WG23NativeModel") == null){
				try{	
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPAppNativeModel.bat").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("TestApp_WG23NativeModel", exeFile, "", "", iconFile, "analytical", true, "native", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("RECIST_Adjudicator") == null){				
				try {
					String pathExe = new File("../XIPApp/bin/RECISTFollowUpAdjudicator.bat").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("RECIST_Adjudicator", exeFile, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}				
		}else{
			if(appMgr.getApplication("TestApp_WG23FileAccess") == null){
				try {
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.sh").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);				
					appMgr.addApplication(new Application("TestApp_WG23FileAccess", exeFile, "", "", iconFile, "analytical", true, "files", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("TestApp_WG23NativeModel") == null){
				try{	
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPAppNativeModel.sh").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("TestApp_WG23NativeModel", exeFile, "", "", iconFile, "analytical", true, "native", 1, IterationTarget.SERIES));
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
			preferredHeight = 470;
		}else if (height >= 1024 && height < 1200){
			preferredHeight = 600 - 100;
		}else if(height > 1200 && height <= 1440){
			preferredHeight = 800;
		}
		return preferredHeight;		
	}
}
