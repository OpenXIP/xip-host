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
	Object userObject;
	
	public ItemNode(Object userObject){
		super(userObject);
		this.userObject = userObject;
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
	
	public String toString(){															
		String itemDesc = userObject.toString();
		if(itemDesc == null){
			itemDesc = "";
		}else{
			
		}	
		return itemDesc;						
	}
	public Object getUserObject(){
		return userObject;
	}
}
