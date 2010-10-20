/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.application.Application;

/**
 * @author Jaroslaw Krych
 *
 */
public class StateExecutor implements Runnable{
	Application application;
	State state;
	
	public StateExecutor(Application application){
		this.application = application;
	}
	
	public void setState(State state){
		this.state = state;
	}
	
	public void run() {
		application.getClientToApplication().setState(state);		
	}
}
