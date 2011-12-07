/**
 * Copyright (c) 2010-2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import edu.wustl.xipHost.dataModel.Study;
/**
 * @author Jaroslaw Krych
 *
 */
public class StudyNode extends DefaultMutableTreeNode {
	final static Logger logger = Logger.getLogger(StudyNode.class);
	JCheckBox checkBox;
	boolean isSelected;
	Study userObject;
	
	public StudyNode(Study userObject){
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
	public Study getUserObject(){
		return userObject;
	}
	
	NodeSelectionListener l;
	public void addNodeSelectionListener(NodeSelectionListener listener){
		l = listener;
	}
	
	public void updateNode(){
		NodeSelectionEvent event = new NodeSelectionEvent(this);
		if(l != null){
			l.nodeSelected(event);
		} else {
			logger.warn("NULL NodeSelectionListener for StudyNode: " + this.toString());
		}
	}
}
