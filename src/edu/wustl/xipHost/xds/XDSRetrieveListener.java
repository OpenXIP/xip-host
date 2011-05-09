/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;

/**
 * @author Jaroslaw Krych
 *
 */
public interface XDSRetrieveListener {
	public boolean documentsAvailable(File xdsRetrievedFile);
}
