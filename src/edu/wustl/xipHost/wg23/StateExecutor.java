/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

/**
 * @author Jaroslaw Krych
 *
 */
public class StateExecutor implements Runnable{
	HostImpl hostImpl;
	
	public StateExecutor(HostImpl hostImpl){
		this.hostImpl = hostImpl;
	}
	
	public void run() {
		hostImpl.fireStateChangedAction();		
	}

}
