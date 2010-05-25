/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Jaroslaw Krych
 *
 */
public class AttributePanelPrivateTag extends JPanel implements FocusListener {
	Map<Integer, Object> dicomCriteria;
	Font font = new Font("Tahoma", 0, 12);
	JTextField keyField;
	JTextField valueField;
	
	public AttributePanelPrivateTag() {
		dicomCriteria = new HashMap<Integer, Object>();
		keyField = new JTextField("Private_01f1_1027");
		keyField.setHorizontalAlignment(JTextField.RIGHT);
		keyField.setColumns(20);
		keyField.setFont(font);
		keyField.addFocusListener(this);
		JLabel label = new JLabel("e.g. Private_01f1_1027, 0010x0010");
		label.setFont(font);
		valueField = new JTextField("");
		valueField.setColumns(20);
		valueField.setFont(font);
		valueField.addFocusListener(this);
		add(keyField);
		add(label);
		add(valueField);
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		setBackground(new Color(156, 162, 189));
		setForeground(Color.WHITE);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
		layout.setConstraints(keyField, constraints);
		
		constraints.gridy = 2;
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
		layout.setConstraints(label, constraints);
		
		constraints.gridy = 1;
		constraints.gridx = 2;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
		layout.setConstraints(valueField, constraints);
	}


	Integer getTagIntegerValue(String textTag) {
		if (!textTag.equalsIgnoreCase("null") && textTag.length() > 0) {
			//convert textTag to Integer
			textTag = textTag.replaceFirst("Private_", "");
			textTag = textTag.replaceAll("_", "");
			textTag = textTag.replaceAll("x", "");
			textTag = textTag.replaceAll("X", "");
			//System.out.println("Tag: " + textTag);
			int i = Integer.valueOf(textTag, 16).intValue();
			//System.out.println("Tag Integer value: " + i);
			//int testInt = Tag.PatientName;
			//System.out.println("Integer value should be: " + testInt);
			return i;
		}else{
			return null;
		}
	}
	
	Object replaceAttributeValue(String textTag) {
		return textTag;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		AttributePanelPrivateTag panel = new AttributePanelPrivateTag();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	Integer attKey = null;
	Object attValue = null;
	@Override
	public void focusLost(FocusEvent e) {
		String newTag = null;
		String newValue = null;
		if((JTextField)e.getSource() == keyField){
			newTag = (((JTextField)e.getSource()).getText());
			//System.out.println(newTag);
			attKey = getTagIntegerValue(newTag);
		}else if((JTextField)e.getSource() == valueField){
			newValue = (((JTextField)e.getSource()).getText());
			//System.out.println(newValue);
			attValue = newValue;
		}	
		if(attKey != null && attValue != null){
			//There should be only one key and one value in dicomCriteria
			//When reentering private tag's attribute duplicates would be created without clear() of teh map
			dicomCriteria.clear();
			dicomCriteria.put(attKey, attValue);
		}		
	}
	
	public Map<Integer, Object> getSearchCriteria(){
		return dicomCriteria;
	}
}
