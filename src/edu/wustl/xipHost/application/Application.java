/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.ws.Endpoint;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.nema.dicom.wg23.ArrayOfString;
import org.nema.dicom.wg23.ArrayOfUUID;
import org.nema.dicom.wg23.Host;
import org.nema.dicom.wg23.ModelSetDescriptor;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Patient;
import org.nema.dicom.wg23.QueryResult;
import org.nema.dicom.wg23.Rectangle;
import org.nema.dicom.wg23.Series;
import org.nema.dicom.wg23.State;
import org.nema.dicom.wg23.Study;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.avt2ext.AVTRetrieve2;
import edu.wustl.xipHost.avt2ext.AVTUtil;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.iterator.RetrieveTarget;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.iterator.IteratorElementEvent;
import edu.wustl.xipHost.iterator.IteratorEvent;
import edu.wustl.xipHost.iterator.NotificationRunner;
import edu.wustl.xipHost.iterator.TargetElement;
import edu.wustl.xipHost.iterator.TargetIteratorRunner;
import edu.wustl.xipHost.iterator.TargetIteratorListener;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.hostControl.Util;
import edu.wustl.xipHost.hostControl.XindiceManager;
import edu.wustl.xipHost.hostControl.XindiceManagerFactory;
import edu.wustl.xipHost.wg23.ClientToApplication;
import edu.wustl.xipHost.wg23.HostImpl;
import edu.wustl.xipHost.wg23.NativeModelListener;
import edu.wustl.xipHost.wg23.NativeModelRunner;
import edu.wustl.xipHost.wg23.StateExecutor;
import edu.wustl.xipHost.wg23.WG23DataModel;

public class Application implements NativeModelListener, TargetIteratorListener, DataAccessListener {	
	final static Logger logger = Logger.getLogger(Application.class);
	UUID id;
	String name;
	File exePath;
	String vendor;
	String version;
	File iconFile;
	String type;
	boolean requiresGUI;
	String wg23DataModelType;
	int concurrentInstances;
	IterationTarget iterationTarget;
	int numStateNotificationThreads = 2;
	ExecutorService exeService = Executors.newFixedThreadPool(numStateNotificationThreads);	
	AVTUtil util = new AVTUtil();
	
	/* Application is a WG23 compatibile application*/	
	public Application(String name, File exePath, String vendor, String version, File iconFile,
			String type, boolean requiresGUI, String wg23DataModelType, int concurrentInstances, IterationTarget iterationTarget){								
		if(name == null || exePath == null || vendor == null || version == null ||
				type == null || wg23DataModelType == null || iterationTarget == null){
			throw new IllegalArgumentException("Application parameters are invalid: " + 
					name + " , " + exePath + " , " + vendor + " , " + version + 
					type + " , " + requiresGUI + " , " + wg23DataModelType + " , " + iterationTarget);	
		} else if(name.isEmpty() || name.trim().length() == 0 || exePath.exists() == false ||
				type.isEmpty() || wg23DataModelType.isEmpty() || concurrentInstances == 0){
			try {
				throw new IllegalArgumentException("Application parameters are invalid: " + 
						name + " , " + exePath.getCanonicalPath() + " , " + vendor + " , " + version);
			} catch (IOException e) {
				throw new IllegalArgumentException("Application exePath is invalid. Application name: " + 
						name);
			}
		} else{
			id = UUID.randomUUID();
			this.name = name;
			this.exePath = exePath;
			this.vendor = vendor;
			this.version = version;
			if(iconFile != null && iconFile.exists()){
				this.iconFile = iconFile;
			}else{
				this.iconFile = null;
			}
			this.type = type;
			this.requiresGUI = requiresGUI;
			this.wg23DataModelType = wg23DataModelType;
			this.concurrentInstances = concurrentInstances;
			this.iterationTarget = iterationTarget;
		}		
	}
	
	
	//verify this pattern
	/*public boolean verifyFileName(String fileName){		
		String str = "/ \\ : * ? \" < > | ,  ";		
        Pattern filePattern = Pattern.compile(str);             
        boolean matches = filePattern.matcher(fileName).matches();
        return matches;
    }
	
	public static void main (String args[]){
		Application app = new Application("ApplicationTest", new File("test.txt"), "", "");
		System.out.println(app.getExePath().getName());
		System.out.println(app.verifyFileName(app.getExePath().getName()));
	}*/		
		
	public UUID getID(){
		return id;
	}		
	public String getName(){
		return name;
	}
	public void setName(String name){
		if(name == null || name.isEmpty() || name.trim().length() == 0){
			throw new IllegalArgumentException("Invalid application name: " + name);
		}else{
			this.name = name;
		}		
	}
	public File getExePath(){
		return exePath;
	}
	public void setExePath(File path){
		if(path == null){
			throw new IllegalArgumentException("Invalid exePath name: " + path);
		}else{
			exePath = path;
		}		
	}
	public String getVendor(){
		return vendor;
	}
	public void setVendor(String vendor){
		if(vendor == null){
			throw new IllegalArgumentException("Invalid vendor: " + vendor);
		}else{
			this.vendor = vendor;
		}		
	}
	public String getVersion(){
		return version;
	}
	public void setVersion(String version){
		if(version == null){
			throw new IllegalArgumentException("Invalid version: " + version);
		}else{
			this.version = version;
		}		
	}
		
	public File getIconFile(){
		return iconFile;
	}
	public void setIconFile(File iconFile){
		if(iconFile == null){
			throw new IllegalArgumentException("Invalid exePath name: " + iconFile);
		}else{
			this.iconFile = iconFile;
		}	
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean requiresGUI() {
		return requiresGUI;
	}

	public void setRequiresGUI(boolean requiresGUI) {
		this.requiresGUI = requiresGUI;
	}

	public String getWG23DataModelType() {
		return wg23DataModelType;
	}

	public void setWG23DataModelType(String wg23DataModelType) {
		this.wg23DataModelType = wg23DataModelType;
	}

	public int getConcurrentInstances() {
		return concurrentInstances;
	}

	public void setConcurrentInstances(int concurrentInstances) {
		this.concurrentInstances = concurrentInstances;
	}

	public IterationTarget getIterationTarget() {
		return iterationTarget;
	}

	public void setIterationTarget(IterationTarget iterationTarget) {
		this.iterationTarget = iterationTarget;
	}
	
	//Each application has:
	//1. Out directories assigned
	//2. clientToApplication
	//3. Host scheleton (reciever)
	//4. Data assigned for processing
	//5. Data produced
	//when launching diploy service and set URLs
	
	ClientToApplication clientToApplication;
	public void startClientToApplication(){
		clientToApplication = new ClientToApplication(getApplicationServiceURL());
	}
	public ClientToApplication getClientToApplication(){
		return clientToApplication;
	}
	
	
	//Implementation HostImpl is used to be able to add WG23Listener
	//It is eventually casted to Host type
	Host host;
			
	//All loaded application by default will be saved again.
	//New instances of an application will be saved only when the save checkbox is selected
	Boolean doSave = true;
	public void setDoSave(boolean doSave){
		this.doSave = doSave;
	}
	public boolean getDoSave(){
		return doSave;
	}
	
	Endpoint hostEndpoint;
	URL hostServiceURL;
	URL appServiceURL;
	Thread threadNotification;
	public void launch(URL hostServiceURL, URL appServiceURL){						
		this.hostServiceURL = hostServiceURL;
		this.appServiceURL = appServiceURL;				
		setApplicationOutputDir(ApplicationManagerFactory.getInstance().getOutputDir());
		setApplicationTmpDir(ApplicationManagerFactory.getInstance().getTmpDir());
		setApplicationPreferredSize(HostMainWindow.getApplicationPreferredSize());
		//prepare native models
		//createNativeModels(getWG23DataModel());		
		//diploy host service				
		host = new HostImpl(this);	
		hostEndpoint = Endpoint.publish(hostServiceURL.toString(), host);
		// Ways of launching XIP application: exe, bat, class or jar
		//if(((String)getExePath().getName()).endsWith(".exe") || ((String)getExePath().getName()).endsWith(".bat")){
		try {
			if(getExePath().toURI().toURL().toExternalForm().endsWith(".exe") || getExePath().toURI().toURL().toExternalForm().endsWith(".bat")){
				try {																							
					Runtime.getRuntime().exec("cmd /c start /min " + getExePath().toURI().toURL().toExternalForm() + " " + "--hostURL" + " " + hostServiceURL.toURI().toURL().toExternalForm() + " " + "--applicationURL" + " " + appServiceURL.toURI().toURL().toExternalForm());		         
				} catch (IOException e) {			
					logger.error(e, e);				
				} catch (URISyntaxException e) {
					logger.error(e, e);	
				}			
			} else if (getExePath().toURI().toURL().toExternalForm().endsWith(".sh")){
				try {		
					//Runtime.getRuntime().exec("/bin/sh " + getExePath().getCanonicalPath() + " " + "--hostURL" + " " + hostServiceURL.toURI().toURL().toExternalForm() + " " + "--applicationURL" + " " + appServiceURL.toURI().toURL().toExternalForm());					
					System.out.println(getExePath().toURI().toURL().toExternalForm() + " " + "--hostURL" + " " + hostServiceURL.toURI().toURL().toExternalForm() + " " + "--applicationURL" + " " + appServiceURL.toURI().toURL().toExternalForm());
					Runtime.getRuntime().exec("open " + getExePath().toURI().toURL().toExternalForm() + " " + "--hostURL" + " " + hostServiceURL.toURI().toURL().toExternalForm() + " " + "--applicationURL" + " " + appServiceURL.toURI().toURL().toExternalForm());
				} catch (IOException e) {			
					logger.error(e, e);	
				} catch (URISyntaxException e) {
					logger.error(e, e);	
				}			
			} else {
				try {
					Runtime.getRuntime().exec(getExePath().toURI().toURL().toExternalForm() + " " + "--hostURL" + " " + hostServiceURL.toURI().toURL().toExternalForm() + " " + "--applicationURL" + " " + appServiceURL.toURI().toURL().toExternalForm());
				} catch (IOException e) {
					logger.error(e, e);	
				} catch (URISyntaxException e) {
					logger.error(e, e);	
				}		
			}
		} catch (MalformedURLException e) {
			logger.error(e, e);	
		}
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, getIterationTarget(), query, getApplicationTmpDir(), this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
		} catch(Exception e) {
			logger.error(e, e);
		}
	}	
	
	public Endpoint getHostEndpoint(){
		return hostEndpoint;
	}
		
	File appOutputDir;
	public void setApplicationOutputDir(File outDir){				
		try {
			appOutputDir = Util.create("xipOUT_" + getName() + "_", "", outDir);
		} catch (IOException e) {
			logger.error(e, e);
		}		
	}
	public File getApplicationOutputDir() {		
		return appOutputDir;
	}

	File appTmpDir;
	public void setApplicationTmpDir(File tmpDir){
		appTmpDir = tmpDir;
	}
	public File getApplicationTmpDir() {
		return appTmpDir;		
	}

	
	
	java.awt.Rectangle preferredSize;
	public void setApplicationPreferredSize(java.awt.Rectangle preferredSize){
		this.preferredSize = preferredSize;
	}
	
	
	State priorState = null;
	State state = null;
	boolean firstLaunch = true;
	int numberOfSentNotifications = 0;
	MultiMap wg23DataModelItems = new MultiValueMap();
	@SuppressWarnings("unchecked")
	public void setState(State state){
		priorState = this.state;
		this.state = state;
		logger.debug("\"" + getName() + "\"" + " state changed to: " + this.state);
		if (state.equals(State.IDLE)){
			if(firstLaunch){
				startClientToApplication();
				notifyAddSideTab();
				firstLaunch = false;
				StateExecutor stateExecutor = new StateExecutor(this);
				stateExecutor.setState(State.INPROGRESS);
				exeService.execute(stateExecutor);
			} else {
				synchronized(this){
					if(iter != null){
						boolean doInprogress = true;
						synchronized(targetElements){
							if(numberOfSentNotifications == targetElements.size()){
								doInprogress = false;
							}
						}
						if(doInprogress == false){
							StateExecutor stateExecutor = new StateExecutor(this);
							stateExecutor.setState(State.EXIT);
							exeService.execute(stateExecutor);
						} else {
							StateExecutor stateExecutor = new StateExecutor(this);
							stateExecutor.setState(State.INPROGRESS);
							exeService.execute(stateExecutor);
						}			
					} else {
						StateExecutor stateExecutor = new StateExecutor(this);
						stateExecutor.setState(State.INPROGRESS);
						exeService.execute(stateExecutor);
					}
				}
			}
		} else if(state.equals(State.INPROGRESS)){
			synchronized(targetElements){
				while(targetElements.size() == 0 || targetElements.size() <= numberOfSentNotifications){
					try {
						targetElements.wait();
					} catch (InterruptedException e) {
						logger.error(e, e);
					}
				}
				TargetElement element = targetElements.get(numberOfSentNotifications);
				WG23DataModel wg23data = util.getWG23DataModel(element);
				//Extract all ObjectDescriptors UUIDs and construct MultiValueMap containing UUID (key), ObjectDescriptor (value), ObjectLocator (value) 
				List<ObjectDescriptor> objDescsTopLevel = wg23data.getAvailableData().getObjectDescriptors().getObjectDescriptor();
				for(ObjectDescriptor objDesc : objDescsTopLevel){
					String uuid = objDesc.getUuid().getUuid();
					wg23DataModelItems.put(uuid, objDesc);
				}
				List<Patient> listPatients = wg23data.getAvailableData().getPatients().getPatient();
				for(Patient patient : listPatients){
					List<ObjectDescriptor> objDescsPatientLevel = patient.getObjectDescriptors().getObjectDescriptor();
					for(ObjectDescriptor objDesc : objDescsPatientLevel){
						String uuid = objDesc.getUuid().getUuid();
						wg23DataModelItems.put(uuid, objDesc);
					}
					List<Study> listStudies = patient.getStudies().getStudy();
					for(Study study : listStudies){
						List<ObjectDescriptor> objDescsStudyLevel = study.getObjectDescriptors().getObjectDescriptor();
						for(ObjectDescriptor objDesc : objDescsStudyLevel){
							String uuid = objDesc.getUuid().getUuid();
							wg23DataModelItems.put(uuid, objDesc);
						}
						List<Series> listSeries = study.getSeries().getSeries();
						for(Series series : listSeries){
							List<ObjectDescriptor> objDescsSeriesLevel = series.getObjectDescriptors().getObjectDescriptor();
							for(ObjectDescriptor objDesc : objDescsSeriesLevel){
								String uuid = objDesc.getUuid().getUuid();
								wg23DataModelItems.put(uuid, objDesc);
							}	
						}
					}
				}
				Iterator<String> iterMultiValueMap = wg23DataModelItems.keySet().iterator();
				ObjectLocator[] objLocators = wg23data.getObjectLocators();
				while(iterMultiValueMap.hasNext()){
					String uuid = iterMultiValueMap.next();
					for(int i = 0; i < objLocators.length; i++){
						ObjectLocator objLoc = objLocators[i];
						String locUUID = objLoc.getUuid().getUuid();
						if(locUUID.equalsIgnoreCase(uuid)){
							wg23DataModelItems.put(uuid, objLoc);
						}
					}
				}
				NotificationRunner runner = new NotificationRunner(this);
				runner.setAvailableData(wg23data.getAvailableData());
				threadNotification = new Thread(runner);
				threadNotification.start();
				numberOfSentNotifications++;
			}
		} else if (state.equals(State.EXIT)){
			//Application runShutDownSequence goes through ApplicationTerminator and Application Scheduler
			//ApplicationScheduler time is set to zero but other value could be used when shutdown delay is needed.
			ApplicationTerminator terminator = new ApplicationTerminator(this);
			Thread t = new Thread(terminator);
			t.start();	
			//reset application parameters for subsequent launch
			firstLaunch = true;
			retrievedTargetElements.clear();
			targetElements.clear();
			iter = null;
			numberOfSentNotifications = 0;
			retrievedTargetElements.clear();
		}
	}
	
	public State getState(){
		return state;
	}
	public State getPriorState(){
		return priorState;
	}
	
	WG23DataModel wg23dm = null;
	public void setData(WG23DataModel wg23DataModel){
		this.wg23dm = wg23DataModel;		
	}

	public WG23DataModel getWG23DataModel(){
		return wg23dm;
	}
	
	SearchResult selectedDataSearchResult;
	public void setSelectedDataSearchResult(SearchResult selectedDataSearchResult){
		this.selectedDataSearchResult = selectedDataSearchResult;
	}
	
	Query query;
	public void setQueryDataSource(Query query){
		this.query = query;
	}
	
	Retrieve retrieve;
	public void setRetrieveDataSource(Retrieve retrieve){
		this.retrieve = retrieve;
	}
	
	public Rectangle getApplicationPreferredSize() {		
		double x = preferredSize.getX();
		double y = preferredSize.getY();
		double width = preferredSize.getWidth();
		double height = preferredSize.getHeight();
		Rectangle rect = new Rectangle();
		rect.setRefPointX(new Double(x).intValue());
		rect.setRefPointY(new Double(y).intValue());
		rect.setWidth(new Double(width).intValue());
		rect.setHeight(new Double(height).intValue());
		return rect;
	}
	
	public URL getApplicationServiceURL(){
		return appServiceURL;
	}		
		
	public void notifyAddSideTab(){	
		HostMainWindow.addTab(getName(), getID());
	}
		
	public void bringToFront(){
		clientToApplication.bringToFront();
	}
	
	public boolean shutDown(){		
		if(getState().equals(State.IDLE)){
			if(getClientToApplication().setState(State.EXIT)){
				return true;
			}		
		}else{
			if(cancelProcessing()){
				return shutDown();
			}			
		}
		return false;
	}
	
	public void runShutDownSequence(){
		HostMainWindow.removeTab(getID());		
		if(getHostEndpoint() != null){
			getHostEndpoint().stop();
		}	
		/* voided to make compatibile with the iterator
		//Delete documents from Xindice created for this application
		XindiceManagerFactory.getInstance().deleteAllDocuments(getID().toString());
		//Delete collection created for this application
		XindiceManagerFactory.getInstance().deleteCollection(getID().toString());
		*/
	}
	
	public boolean cancelProcessing(){
		if(getState().equals(State.INPROGRESS) || getState().equals(State.SUSPENDED)){
			return getClientToApplication().setState(State.CANCELED);			
		}else{
			return false;
		}		
	}
	public boolean suspendProcessing(){
		if(getState().equals(State.INPROGRESS)){
			return getClientToApplication().setState(State.SUSPENDED);			
		}else{
			return false;
		}
	}
	
	
	/**
	 * Method is used to create XML native models for all object locators
	 * found  in WG23DataModel.
	 * It uses threads and add NativeModelListener to the NativeModelRunner
	 * @param wg23dm
	 */
	void createNativeModels(WG23DataModel wg23dm){
		if(XindiceManagerFactory.getInstance().createCollection(getID().toString())){
			ObjectLocator[] objLocs = wg23dm.getObjectLocators();
			for (int i = 0; i < objLocs.length; i++){										
				boolean isDICOM = false;
				try {
					isDICOM = DicomUtil.isDICOM(new File(new URI(objLocs[i].getUri())));
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isDICOM){
					NativeModelRunner nmRunner;
					nmRunner = new NativeModelRunner(objLocs[i]);
					nmRunner.addNativeModelListener(this);
					Thread t = new Thread(nmRunner);
					t.start();
				}								
			}
		}else{
			//TODO
			//Action when system cannot create collection
		}
		
	}
	
	/**
	 * Adds JDOM Document to Xindice collection.
	 * Only valid documents (e.g. not null, with root element) will be added
	 * (non-Javadoc)
	 * @see edu.wustl.xipHost.wg23.NativeModelListener#nativeModelAvailable(org.jdom.Document, org.nema.dicom.wg23.Uuid)
	 */
	public void nativeModelAvailable(Document doc, Uuid objUUID) {				
		XindiceManagerFactory.getInstance().addDocument(doc, getID().toString(), objUUID);		
	}
		
	public void nativeModelAvailable(String xmlNativeModel) {
		// Ignore in XIP Host. 
		// Used by AVT AD		
	}	

	/**
	 * Method returns ModelSetDescriptor containing UUID of native models
	 * as well as UUID of object locators for which native models could
	 * not be created
	 * @param objUUIDs
	 * @return
	 */	
	 public ModelSetDescriptor getModelSetDescriptor(List<Uuid> objUUIDs){				
		String[] models = XindiceManagerFactory.getInstance().getModelUUIDs(getID().toString());							
		List<String> listModels = Arrays.asList(models);
		ModelSetDescriptor msd = new ModelSetDescriptor();
		ArrayOfUUID uuidsModels = new ArrayOfUUID();
		List<Uuid> listUUIDs = uuidsModels.getUuid();
		ArrayOfUUID uuidsFailed = new ArrayOfUUID();
		List<Uuid> listUUIDsFailed = uuidsFailed.getUuid();
		for(int i = 0; i < objUUIDs.size(); i++){
			Uuid uuid = new Uuid();
			if(objUUIDs.get(i) == null || objUUIDs.get(i).getUuid() == null){
				//do not add anything to model set descriptor
			}else if(objUUIDs.get(i).getUuid().toString().trim().isEmpty()){
				//do not add anything to model set descriptor
			}else if(listModels.contains("wg23NM-"+ objUUIDs.get(i).getUuid())){				
				int index = listModels.indexOf("wg23NM-"+ objUUIDs.get(i).getUuid());
				uuid.setUuid(listModels.get(index));
				listUUIDs.add(uuid);
			}else{
				uuid.setUuid(objUUIDs.get(i).getUuid());
				listUUIDsFailed.add(uuid);
			}		
		}					
		msd.setModels(uuidsModels);
		msd.setFailedSourceObjects(uuidsFailed);		
		return msd;
	}
	
	/**
	 * queryResults list hold teh values from queryResultAvailable
	 */
	List<QueryResult> queryResults;
	public List<QueryResult> queryModel(List<Uuid> modelUUIDs, List<String> listXPaths){
		queryResults = new ArrayList<QueryResult>();
		if(modelUUIDs == null || listXPaths == null){
			return queryResults;
		}
		String collectionName = getID().toString();		
		XindiceManager xm = XindiceManagerFactory.getInstance();			
		for(int i = 0; i < listXPaths.size(); i++){
			for(int j = 0; j < modelUUIDs.size(); j++){
				//String[] results = xm.query(service, collectionName, modelUUIDs.get(j), listXPaths.get(i));
				String[] results = xm.query(collectionName, modelUUIDs.get(j), listXPaths.get(i));
				QueryResult queryResult = new QueryResult();
				queryResult.setModel(modelUUIDs.get(j));
				queryResult.setXpath(listXPaths.get(i));
				ArrayOfString arrayOfString = new ArrayOfString();
				List<String> listString = arrayOfString.getString();
				for(int k = 0; k < results.length; k++){							
					listString.add(results[k]);
				}		
				queryResult.setResults(arrayOfString);	
				queryResults.add(queryResult);
			}
		}				
		return queryResults;		
	}

	Iterator<TargetElement> iter;
	@SuppressWarnings("unchecked")
	@Override
	public void fullIteratorAvailable(IteratorEvent e) {
		iter = (Iterator<TargetElement>)e.getSource();
		logger.debug("Full TargetIterator available at time " + System.currentTimeMillis());
	}

	List<TargetElement> targetElements = new ArrayList<TargetElement>();
	@Override
	public void targetElementAvailable(IteratorElementEvent e) {
		synchronized(targetElements){
			TargetElement element = (TargetElement) e.getSource();
			logger.debug("TargetElement available. ID: " + element.getId() + " at time " + System.currentTimeMillis());
			targetElements.add(element);
			targetElements.notify();
		}
	}
	
	
	public List<ObjectLocator> retrieveAndGetLocators(List<Uuid> listUUIDs){
		//Start data retrieval related to the element	
		RetrieveTarget retrieveTarget = RetrieveTarget.DICOM_AND_AIM;
		TargetElement element = null;
		synchronized(targetElements){
			element = targetElements.get(numberOfSentNotifications - 1);
			retrieve.setRetrieve(element, retrieveTarget);
			retrieve.addDataAccessListener(this);
		}			
		Thread t = new Thread(retrieve);
		t.start();
		//Wait for actual data being retrieved before sending file pointers
		String retrievedElementID = element.getId();
		synchronized(retrievedTargetElements){
			while(!retrievedTargetElements.contains(retrievedElementID)){
				try {
					retrievedTargetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}		
		if(listUUIDs == null){
			return new ArrayList<ObjectLocator>();
		} else {
			ArrayList<ObjectLocator> listObjLocs = new ArrayList<ObjectLocator>();
			for(Uuid uuid : listUUIDs){
				String strUuid = uuid.getUuid();
				Collection<?> objs = (Collection<?>) wg23DataModelItems.get(strUuid);
				Iterator<?> iterObjs = objs.iterator();
				while(iterObjs.hasNext()){
					Object object = iterObjs.next();
					if(object instanceof ObjectLocator){
						ObjectLocator objLoc = (ObjectLocator)object;
						listObjLocs.add(objLoc);
						logger.debug("Item location: " + strUuid + " " + objLoc.getUri());
					}
				}
			}
			return listObjLocs;
		}		
	}
	
	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void queryResultsAvailable(QueryEvent e) {
		// TODO Auto-generated method stub
		
	}

	List<String> retrievedTargetElements = new ArrayList<String>();
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		synchronized(retrievedTargetElements){
			String elementID = (String)e.getSource();
			retrievedTargetElements.add(elementID);
			retrievedTargetElements.notify();
			logger.debug("Data retrived for TargetElement: " + elementID + " at time: " + System.currentTimeMillis());
		}
	}
}
