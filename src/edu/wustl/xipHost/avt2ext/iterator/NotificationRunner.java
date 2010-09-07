/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import org.nema.dicom.wg23.AvailableData;

import edu.wustl.xipHost.wg23.ClientToApplication;

/**
 * @author Jaroslaw Krych
 *
 */
public class NotificationRunner implements Runnable {
	ClientToApplication clientToApplication;
	AvailableData availableData;
	boolean lastData;
	/**
	 * 
	 */
	public NotificationRunner(ClientToApplication clientToApplication, AvailableData availableData, boolean lastData) {
		this.clientToApplication = clientToApplication;
		this.availableData = availableData;
		this.lastData = lastData;
	}

	@Override
	public void run() {
		clientToApplication.notifyDataAvailable(availableData, lastData);
	}

}
