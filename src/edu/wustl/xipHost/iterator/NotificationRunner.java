/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
