/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.dcm4che2.util.UIDUtils;
import org.nema.dicom.PS3_19.ArrayOfMimeType;
import org.nema.dicom.PS3_19.ArrayOfObjectLocator;
import org.nema.dicom.PS3_19.ArrayOfQueryResult;
import org.nema.dicom.PS3_19.ArrayOfQueryResultInfoSet;
import org.nema.dicom.PS3_19.ArrayOfUID;
import org.nema.dicom.PS3_19.ArrayOfUUID;
import org.nema.dicom.PS3_19.ArrayOfstring;
import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.IHostService20100825;
import org.nema.dicom.PS3_19.ModelSetDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.QueryResult;
import org.nema.dicom.PS3_19.Rectangle;
import org.nema.dicom.PS3_19.State;
import org.nema.dicom.PS3_19.Status;
import org.nema.dicom.PS3_19.UID;
import org.nema.dicom.PS3_19.UUID;

import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.hostControl.DataStore;


/**
 * @author Jaroslaw Krych
 *
 */
@WebService(
		serviceName = "HostService-20100825",
        portName="HostServiceBinding",
        targetNamespace = "http://dicom.nema.org/PS3.19/HostService-20100825",
        endpointInterface = "org.nema.dicom.PS3_19.IHostService20100825")
public class HostImpl implements IHostService20100825{	
	final static Logger logger = Logger.getLogger(HostImpl.class);
	Application app;
	List<ObjectLocator> objLocs;	
	//ClientToApplication clientToApplication;
	
	public HostImpl(Application application){
		app = application;		
	}
	
	@Override
	public UID generateUID() {
		UID uid = new UID();
		uid.setUid(UIDUtils.createUID());
		return uid;
	}

	@Override
	public Rectangle getAvailableScreen(Rectangle appPreferredScreen) {
		Rectangle size = app.getApplicationPreferredSize();		
		return size;
	}

	@Override
	public String getOutputLocation(ArrayOfstring preferredProtocols) {
		// TODO Auto-generated method stub
		return null;
	}

	ExecutorService stateExeService = Executors.newFixedThreadPool(1);

	@Override
	public void notifyStateChanged(State newState) {		
		logger.debug("Requested state change to " + newState.toString() + " of \"" + app.getName() + "\"");
		try {
			changeState(newState);					
		} catch (StateChangeException e) {			
			//TODO what to do when state cannot be changed
			e.printStackTrace();
		}
	}

		
	/**
	 * Method is an implementation of WG23Listener
	 * It changes application state. Each state change results in new action.
	 * Allowable changes: 
	 * null -> IDLE, 
	 * COMPLETED -> IDLE,
	 * CANCELED -> IDLE,
	 * IDLE -> INPROGRESS, 
	 * SUSPENDED -> INPROGRESS,
	 * INPROGRESS -> COMPLETED,
	 * INPROGRESS -> CANCELED, 
	 * SUSPENDED -> CANCELED,
	 * INPROGRESS -> SUSPENDED,
	 * IDLE -> EXIT 
	 * @throws StateChangeException 
	 * @throws StateChangeException 
	 * 
	 */
	public void changeState(State state) throws StateChangeException  {		
		State currState = app.getState();		
		if(state == null){throw new StateChangeException("Requested state: " + state + ", current app state: " + currState);}
        switch (State.valueOf(state.toString())) {
            case IDLE:  
            	if(currState == null || currState.equals(State.COMPLETED) 
            			|| currState.equals(State.CANCELED) || currState.equals(State.EXIT)){            		
            		app.setState(state);            		          		
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;
            case INPROGRESS: 
            	if(currState != null && (currState.equals(State.IDLE) || currState.equals(State.SUSPENDED))){            		
            		app.setState(state);            		
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;            	
            case COMPLETED:  
            	if(currState != null && currState.equals(State.INPROGRESS)){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            case CANCELED:  
            	if(currState != null && (currState.equals(State.INPROGRESS) || currState.equals(State.SUSPENDED))){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            	
            case SUSPENDED:  
            	if(currState != null && currState.equals(State.INPROGRESS)){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            case EXIT:  
            	if(currState.equals(State.IDLE)){
            		app.setState(state);
            	}else{
            		// throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            default: 
            	throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);            	
        }
        //System.out.println("HostImpl l. 190 Application: " + app.getState().toString());
	}
	

	@Override
	public void notifyStatus(Status newStatus) {
		// TODO Auto-generated method stub
	}


	@Override
	public Boolean notifyDataAvailable(AvailableData data, Boolean lastData) {
		DataStore ds = new DataStore(data, app);
		Thread t = new Thread(ds);
		t.start();		
		return true;
	}

	@Override
	public ArrayOfObjectLocator getData(ArrayOfUUID objects,
			ArrayOfUID acceptableTransferSyntaxes, Boolean includeBulkData) {
		// TODO Auto-generated method stub
		// Get corresponding object locators for uuids 
		//TODO make use of includeBulkData and acceptableTransferSyntaxes
		ArrayOfObjectLocator arrayObjLoc = new ArrayOfObjectLocator();
		List<UUID> listUUIDs = objects.getUUID();
		objLocs = app.retrieveAndGetLocators(listUUIDs);
		arrayObjLoc.getObjectLocator().addAll(objLocs);				
		return arrayObjLoc;
	}

	@Override
	public void releaseData(ArrayOfUUID objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ModelSetDescriptor getAsModels(ArrayOfUUID objects, UID classUID,
			ArrayOfMimeType supportedInfoSetTypes) {
		// TODO make use of supportedInfoSetTypes
		List<UUID> objUUIDs = objects.getUUID();		
		return  app.getModelSetDescriptor(objUUIDs);		
	}

	@Override
	public void releaseModels(ArrayOfUUID models) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayOfQueryResult queryModel(ArrayOfUUID models,
			ArrayOfstring xPaths) {
		// TODO Auto-generated method stub
		List<UUID> modelUUIDs = models.getUUID();
		/*for(int i = 0; i < modelUUIDs.size(); i++){
			System.out.println(modelUUIDs.get(i).getUuid());
		}
		System.out.println("------------------------------------");*/
		List<String> listXPaths = xPaths.getString();				
		List<QueryResult> results = app.queryModel(modelUUIDs, listXPaths);		
		ArrayOfQueryResult arrayResults = new ArrayOfQueryResult();		
		List<QueryResult> listResults = arrayResults.getQueryResult();
		for(int i = 0; i < results.size(); i++){
			listResults.add(results.get(i));
		}		
		return arrayResults;
	}

	@Override
	public ArrayOfQueryResultInfoSet queryInfoSet(ArrayOfUUID models,
			ArrayOfstring xPaths) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOutputDir() {
		String appOutDir = null;		
		try {
			appOutDir = app.getApplicationOutputDir().toURI().toURL().toExternalForm();
			//appOutDir = app.getApplicationOutputDir().getCanonicalPath();
		} catch (MalformedURLException e) {

		}		
		return appOutDir;
	}

	public String getTmpDir() {
		String appTmpDir = null;;
		try {
			appTmpDir = app.getApplicationTmpDir().toURI().toURL().toExternalForm();
			//appTmpDir = app.getApplicationTmpDir().getCanonicalPath();
		} catch (MalformedURLException e) {
			
		}		
		return appTmpDir;				
	}

}
