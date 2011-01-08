/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface DataAccessListener extends EventListener{
	public void queryResultsAvailable(QueryEvent e);
	public void retriveResultsAvailable(RetrieveEvent e);
}
