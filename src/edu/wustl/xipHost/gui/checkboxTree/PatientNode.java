/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Jaroslaw Krych
 *
 */
public class PatientNode extends DefaultMutableTreeNode {
	JCheckBox checkBox;
	boolean isSelected;
	JPanel panel;
	
	public PatientNode(Object userObject){
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
