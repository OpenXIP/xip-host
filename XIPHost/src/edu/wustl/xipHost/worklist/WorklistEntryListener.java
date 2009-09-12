/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.util.EventListener;


/**
 * @author Jaroslaw Krych
 *
 */
public interface WorklistEntryListener extends EventListener {
	public void worklistEntryDataAvailable(WorklistEntryEvent e);
}
