/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
	
	NodeSelectionListener l;
	public void addNodeSelectionListener(NodeSelectionListener listener){
		l = listener;
	}
	
	public void updateNode(){
		NodeSelectionEvent event = new NodeSelectionEvent(this);
		if(l != null){
			l.nodeSelected(event);
		} else {
			logger.warn("NULL NodeSelectionListener for ItemNode: " + this.toString());
		}
	}
}
