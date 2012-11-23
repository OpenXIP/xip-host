package edu.wustl.xipHost.application;

import org.nema.dicom.PS3_19.State;
import edu.wustl.xipHost.wg23.ClientToApplication;

public class ClientToApplicationMock extends ClientToApplication{

	
	
	public ClientToApplicationMock() {
		super();		
	}
	
	public Boolean setState(State newState) {
		if(newState.equals(State.CANCELED)){
			app.setState(State.CANCELED);
			app.setState(State.IDLE);
		}
		return true;
	}
	
	Application app;
	public void setApplication(Application app){
		this.app = app;
	}

}
