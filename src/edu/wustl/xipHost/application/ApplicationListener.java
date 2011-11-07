/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface ApplicationListener extends EventListener {
	public void launchApplication(ApplicationEvent event, ApplicationTerminationListener listener);
}
