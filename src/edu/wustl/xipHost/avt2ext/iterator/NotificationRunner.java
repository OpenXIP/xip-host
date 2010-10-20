/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt2ext.AVTUtil;
import edu.wustl.xipHost.wg23.WG23DataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class NotificationRunner implements Runnable {
	Application application;
	TargetElement element;
	AVTUtil util = new AVTUtil();
	/**
	 * 
	 */
	public NotificationRunner(Application application, TargetElement element) {
		this.application = application;
		this.element = element;
	}

	@Override
	public void run() {
		WG23DataModel wg23data = util.getWG23DataModel(element);
		application.getClientToApplication().notifyDataAvailable(wg23data.getAvailableData(), true);
	}
}
