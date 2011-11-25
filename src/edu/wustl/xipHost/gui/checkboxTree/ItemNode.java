/**
 * Copyright (c) 2010-2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.dataModel.Item;

/**
 * @author Jaroslaw Krych
 *
 */
public class ItemNode extends DefaultMutableTreeNode {
	final static Logger logger = Logger.getLogger(ItemNode.class);
	JCheckBox checkBox;
	boolean isSelected;
	Item userObject;
	
	public ItemNode(Item userObject){
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
	public Item getUserObject(){
		return userObject;
	}
	
	NodeSelectionListener2 l;
	public void addNodeSelectionListener(NodeSelectionListener2 listener){
		l = listener;
	}
	
	public void updateNode(){
		NodeSelectionEvent2 event = new NodeSelectionEvent2(this);
		if(l != null){
			l.nodeSelected(event);
		} else {
			logger.warn("NULL NodeSelectionListener for ItemNode: " + this.toString());
		}
	}
}
