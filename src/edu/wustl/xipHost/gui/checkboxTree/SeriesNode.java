/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Jaroslaw Krych
 *
 */
public class SeriesNode extends DefaultMutableTreeNode {
	JCheckBox checkBox;
	boolean isSelected;
	JPanel panel;
	
	public SeriesNode(Object userObject){
		super(userObject);
		checkBox = new JCheckBox();
		
		/*
		JLabel label2 = new JLabel(new ImageIcon("gif/arrow_icon.gif"));	 			
		panel = new JPanel();
		panel.add(checkBox);
		panel.add(label2);
		//panel.setOpaque(true);		 		
		 */
	}
	
	public void setSelected(boolean selected){
		isSelected = selected;
	}
	
	public JCheckBox getCheckBox(){
		return checkBox;
	}
	
	/*
	public JPanel getPanel(){
		return panel;
	}
	*/
}
