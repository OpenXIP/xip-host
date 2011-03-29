/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface RetrieveListener extends EventListener {
	public void retrieveResultsAvailable(RetrieveEvent e);
}
