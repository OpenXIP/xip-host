package edu.wustl.xipHost.application;

import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.wg23.ClientToApplication;

public class ClientToApplicationMock extends ClientToApplication{

	
	
	public ClientToApplicationMock() {
		super();		
	}
	
	public boolean setState(State newState) {
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
