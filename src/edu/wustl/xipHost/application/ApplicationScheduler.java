/**
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.util.Timer;
import java.util.TimerTask;
/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationScheduler {
		
	    private final Timer timer = new Timer();
	    private final int ms = 0;

	    Application app;
	    public ApplicationScheduler(Application application) {
	       app = application;
	    }

	    public void start() {
	        timer.schedule(new TimerTask() {
	            public void run() {
	            	app.runShutDownSequence();
	            	timer.cancel();	            		                
	            }	            
	        }, ms);	        
	    }
}
