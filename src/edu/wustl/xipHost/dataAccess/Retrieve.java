/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import edu.wustl.xipHost.iterator.RetrieveTarget;
import edu.wustl.xipHost.iterator.TargetElement;

/**
 * @author Jaroslaw Krych
 *
 */
public interface Retrieve extends Runnable {
	public void addDataAccessListener(DataAccessListener l);
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget);
}
