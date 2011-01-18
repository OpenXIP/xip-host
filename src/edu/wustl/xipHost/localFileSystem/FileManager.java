/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.io.File;
import java.util.List;

import edu.wustl.xipHost.wg23.WG23DataModel;


/**
 * @author Jaroslaw Krych
 *
 */
public interface FileManager {
	public void run(File[] files);
}
