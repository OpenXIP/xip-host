/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.util.EventObject;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Jaroslaw Krych
 *
 */
public class NodeSelectionEvent2 extends EventObject {

	public NodeSelectionEvent2(DefaultMutableTreeNode node) {
		super(node);
	}

}
