/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import edu.wustl.xipHost.dataModel.Series;

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
		Icon icon = new ImageIcon("./gif/arrow_icon.gif");
    	defaultRenderer.setLeafIcon(icon);
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
    	//JPanel panelCheckBox = null;
    	if(((DefaultMutableTreeNode)value).getUserObject() instanceof Series){
    		checkBox = ((SeriesNode)value).getCheckBox();
    		//panelCheckBox = ((SeriesNode)value).getPanel();
        	checkBox.setFont(font);
        	Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
     	    checkBox.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));     	   
    	}
    		       	
    	if (selected && checkBox != null) {
    		checkBox.setBackground(xipColor);
    		checkBox.setForeground(Color.WHITE);
    	} else if (selected == false && checkBox != null) {
    		checkBox.setBackground(xipLightBlue);
    		checkBox.setForeground(xipColor);
       		
    		//panelCheckBox.setBackground(xipLightBlue);
    		//panelCheckBox.setForeground(xipColor);
    	}
    	if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
	        if (userObject instanceof Series) {			        		        	
	        	checkBox.setText(stringValue);
	        	checkBox.setSelected(checkBox.isSelected());
	        	checkBox.setEnabled(tree.isEnabled());		        		
	        	returnValue = checkBox;
	        	//returnValue = panelCheckBox;
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
