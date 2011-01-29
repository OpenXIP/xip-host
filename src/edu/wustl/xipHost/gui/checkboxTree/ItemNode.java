/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Jaroslaw Krych
 *
 */
public class ItemNode extends DefaultMutableTreeNode {
	JCheckBox checkBox;
	boolean isSelected;
	
	public ItemNode(Object userObject){
		super(userObject);
		checkBox = new JCheckBox();
	}
	
	public void setSelected(boolean selected){
		isSelected = selected;
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public JCheckBox getCheckBox(){
		return checkBox;
	}

}
