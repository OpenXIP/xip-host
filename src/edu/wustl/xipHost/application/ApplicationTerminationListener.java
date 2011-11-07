/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface ApplicationTerminationListener extends EventListener {
	public void applicationTerminated(ApplicationTerminationEvent event);
}
