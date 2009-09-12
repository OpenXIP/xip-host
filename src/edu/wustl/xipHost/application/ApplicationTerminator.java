/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;


/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationTerminator implements Runnable{

	Application app;
	public ApplicationTerminator(Application application){
		app = application;
	}
		
	public void run() {		
		ApplicationScheduler appScheduler = new ApplicationScheduler(app);
		appScheduler.start();
	}

}
