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

package edu.wustl.xipHost.xds.CheckBoxTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

/**
 * @author Jaroslaw Krych
 *
 */
public class CheckBoxTreeRenderer implements TreeCellRenderer {
	Color xipColor = new Color(51, 51, 102);	
	
	//defaultRenderer renders nodes other than Study and Series
	DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	
	Font font = new Font("Tahoma", 0, 12);
	Color xipLightBlue = new Color(156, 162, 189);	
	Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;
	
	public CheckBoxTreeRenderer() {		   				   
	    selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
	    selectionForeground = UIManager.getColor("Tree.selectionForeground");
	    selectionBackground = UIManager.getColor("Tree.selectionBackground");
	    //textForeground = UIManager.getColor("Tree.textForeground");
	    //textBackground = UIManager.getColor("Tree.textBackground");
	    textForeground = Color.WHITE;
	    textBackground = backGround;
	    
	    //defaultRenderer.setBackgroundNonSelectionColor(backGround);
	   
	    defaultRenderer.setBackgroundNonSelectionColor(xipLightBlue);
	    defaultRenderer.setTextNonSelectionColor(xipColor);
	    defaultRenderer.setBackgroundSelectionColor(xipColor);
	    defaultRenderer.setTextSelectionColor(Color.WHITE);		
	    defaultRenderer.setBorderSelectionColor(Color.BLACK);		    
	}				
		
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component returnValue = null;		    			
    	String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);		    	
    	JCheckBox checkBox = null;
    	if(((DefaultMutableTreeNode)value).getUserObject() instanceof Item){
    		checkBox = ((ItemNode)value).getCheckBox();
        	checkBox.setFont(font);
        	Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
     	    checkBox.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
    	}
    		       	
    	if (selected && checkBox != null) {
    		//checkBox.setForeground(selectionForeground);
    		//checkBox.setBackground(selectionBackground);
    		checkBox.setBackground(xipColor);
    		checkBox.setForeground(Color.WHITE);
    	} else if (selected == false && checkBox != null) {
    		//checkBox.setForeground(textForeground);
    		//checkBox.setBackground(textBackground);
    		checkBox.setBackground(xipLightBlue);
    		checkBox.setForeground(xipColor);
    	}
    	if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
	        if (userObject instanceof Item) {			        		        	
	        	checkBox.setText(stringValue);
	        	checkBox.setSelected(checkBox.isSelected());
	        	checkBox.setEnabled(tree.isEnabled());		        		
	        	returnValue = checkBox;
	        	return returnValue;
	        } else{		        		        			        	
	        	return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	        }
    	}	    		    		    	
    	return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}
	
	Color backGround;
	Color foreGround;
	public void setNodeColor(Color backGround, Color foreGround){
		this.backGround = backGround;
		this.foreGround = foreGround;
	}
}
