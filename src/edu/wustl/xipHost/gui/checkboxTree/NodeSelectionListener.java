/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface NodeSelectionListener extends EventListener {
	public void nodeSelected(NodeSelectionEvent event);
}
