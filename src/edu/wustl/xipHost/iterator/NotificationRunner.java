/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import javax.xml.ws.WebServiceException;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.AvailableData;
import edu.wustl.xipHost.application.Application;

/**
 * @author Jaroslaw Krych
 *
 */
public class NotificationRunner implements Runnable {
	final static Logger logger = Logger.getLogger(NotificationRunner.class);
	Application application;
	/**
	 * 
	 */
	public NotificationRunner(Application application) {
		this.application = application;
	}

	AvailableData availableData;
	public void setAvailableData(AvailableData availableData){
		this.availableData = availableData;
	}
	
	@Override
	public void run() {
		try{
			application.getClientToApplication().notifyDataAvailable(availableData, true);
		} catch (WebServiceException e) {
			logger.error(e,  e);
		}
		
	}
}
