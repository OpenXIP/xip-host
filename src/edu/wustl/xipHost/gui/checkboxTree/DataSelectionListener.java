/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface DataSelectionListener extends EventListener {
	public void dataSelectionChanged(DataSelectionEvent event);
}
