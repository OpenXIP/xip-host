/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jws.WebService;
import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfQueryResult;
import org.nema.dicom.wg23.ArrayOfString;
import org.nema.dicom.wg23.ArrayOfUUID;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.Host;
import org.nema.dicom.wg23.ModelSetDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.QueryResult;
import org.nema.dicom.wg23.Rectangle;
import org.nema.dicom.wg23.State;
import org.nema.dicom.wg23.Status;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationTerminator;
import edu.wustl.xipHost.hostControl.DataStore;


/**
 * @author Jaroslaw Krych
 *
 */
@WebService(
		serviceName = "HostService",
        portName="HostPort",
        targetNamespace = "http://wg23.dicom.nema.org/",
        endpointInterface = "org.nema.dicom.wg23.Host")
public class HostImpl implements Host{	
	
	Application app;
	WG23DataModel wg23dm;	
	//ClientToApplication clientToApplication;
	
	public HostImpl(Application application){
		app = application;		
	}
	
	public Uid generateUID() {
		Uid uid = new Uid();
		UUID id = UUID.randomUUID();
		uid.setUid(id.toString());
		return uid;
	}

	public ModelSetDescriptor getAsModels(ArrayOfUUID uuids, Uid classUID, Uid transferSyntaxUID) {	
		//TODO make use of transfersyntaxUID is not empty
		List<Uuid> objUUIDs = uuids.getUuid();		
		return  app.getModelSetDescriptor(objUUIDs);		
	}

	public Rectangle getAvailableScreen(Rectangle appPreferredScreen) {
		Rectangle size = app.getApplicationPreferredSize();		
		return size;
	}

	public ArrayOfObjectLocator getDataAsFile(ArrayOfUUID uuids, boolean includeBulkData){ 			
		//TODO make use of includeBulkData
		ArrayOfObjectLocator arrayObjLoc = new ArrayOfObjectLocator();
		wg23dm = app.getWG23DataModel();
		ObjectLocator[] objLocs = wg23dm.getObjectLocators();		
		if(uuids == null){
			return arrayObjLoc;
		}
		List<Uuid> listUUIDs = uuids.getUuid();								
		List<ObjectLocator> listObjLocs = arrayObjLoc.getObjectLocator();				
		for(int i = 0; i < listUUIDs.size(); i++){			
			for(int j = 0; j < objLocs.length; j++){								
				if(objLocs[j].getUuid().getUuid().equalsIgnoreCase(listUUIDs.get(i).getUuid())){										
					ObjectLocator obj = objLocs[j];
					listObjLocs.add(obj);
				}
			}
		}						
		return arrayObjLoc;
	}

	public ArrayOfObjectLocator getDataAsSpecificTypeFile(ArrayOfUUID objectUUIDs, String mimeType, Uid transferSyntaxUID, boolean includeBulkData) {
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

	public boolean notifyDataAvailable(AvailableData availableData, boolean lastData) {				
		DataStore ds = new DataStore(availableData, app);
		Thread t = new Thread(ds);
		t.start();		
		return true;
	}


	ExecutorService stateExeService = Executors.newFixedThreadPool(1);
	public void notifyStateChanged(State newState) {		
		try {
			changeState(newState);
			//TODO return 
			StateExecutor stateExecutor = new StateExecutor(this);
			stateExeService.execute(stateExecutor);							
		} catch (StateChangeException e) {			
			//TODO what to do when state cannot be changed
			e.printStackTrace();
		}
	}

		
	/**
	 * Method is an implementation of WG23Listener
	 * It changes application state. Each state change results in new action.
	 * Allowable changes: 
	 * null -> IDLE, COMPLETED -> IDLE,
	 * IDLE -> INPROGRESS, SUSPENDED -> INPROGRESS,
	 * INPROGRESS -> COMPLETED,
	 * INPROGRESS -> CANCELED, SUSPENDED -> CANCELED,
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
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            default: 
            	throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);            	
        }
        //System.out.println("HostImpl l. 190 Application: " + app.getState().toString());
	}
	
	void fireStateChangedAction(){		
		State priorState = app.getPriorState();
		//When IDLE from null
		if((priorState == null || priorState.equals(State.EXIT)) && app.getState().equals(State.IDLE)){            			            			
			//add vertical tab
			app.startClientToApplication();
			app.notifyAddSideTab();
			//Graphics g = HostConfigurator.getHostConfigurator().getMainWindow().getContentPane().getGraphics();
			//HostConfigurator.getHostConfigurator().getMainWindow().update(g);			
			app.getClientToApplication().setState(State.INPROGRESS);
		}
		//When IDLE from COMPLETED
		else if(priorState != null && priorState.equals(State.COMPLETED) && app.getState().equals(State.IDLE)){
			//
		}
		//When INPROGRESS from IDLE
		else if(priorState != null && priorState.equals(State.IDLE) && app.getState().equals(State.INPROGRESS)){
			AvailableData availableData = new AvailableData();
			wg23dm = app.getWG23DataModel();
			availableData = wg23dm.getAvailableData();			            			
			app.getClientToApplication().notifyDataAvailable(availableData, true);
		}else if (priorState != null && priorState.equals(State.INPROGRESS) && app.getState().equals(State.COMPLETED)){
			//TODO Do something when COMPLETED
			//getAvailableData from teh application and then set the state to IDLE
			app.getClientToApplication().setState(State.IDLE);
		}else if (priorState != null && priorState.equals(State.INPROGRESS) && app.getState().equals(State.CANCELED)){
			//TODO Do something when CANCELED
		}else if (priorState != null && priorState.equals(State.CANCELED) && app.getState().equals(State.IDLE)){
			//TODO "Do something when from CANCELED to IDLE"
		}
		//EXIT from IDLE
		else if(priorState != null && priorState.equals(State.IDLE) && app.getState().equals(State.EXIT)){
			//Application runShutDownSequence is run through ApplicationTerminator and Application Scheduler
			//ApplicationScheduler time is set to zero but other value could be used when shutdown delay is needed.
			ApplicationTerminator terminator = new ApplicationTerminator(app);
			Thread t = new Thread(terminator);
			t.start();						
		}
	}

	public void notifyStatus(Status newStatus) {
		// TODO Auto-generated method stub
		
	}

	public ArrayOfQueryResult queryModel(ArrayOfUUID objUUIDs, ArrayOfString modelXpaths, boolean includeBulkDataPointers) {
		//TODO make use of includeBulkDataPointers
		List<Uuid> modelUUIDs = objUUIDs.getUuid();
		/*for(int i = 0; i < modelUUIDs.size(); i++){
			System.out.println(modelUUIDs.get(i).getUuid());
		}
		System.out.println("------------------------------------");*/
		List<String> listXPaths = modelXpaths.getString();				
		List<QueryResult> results = app.queryModel(modelUUIDs, listXPaths);		
		ArrayOfQueryResult arrayResults = new ArrayOfQueryResult();		
		List<QueryResult> listResults = arrayResults.getQueryResult();
		for(int i = 0; i < results.size(); i++){
			listResults.add(results.get(i));
		}		
		return arrayResults;
	}
}
