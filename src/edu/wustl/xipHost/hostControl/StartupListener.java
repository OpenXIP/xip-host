/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.util.EventListener;

/**
 * @author Jarek Krych
 *
 */
public interface StartupListener extends EventListener {
	void startupTasksCompleted(StartupEvent event);
}