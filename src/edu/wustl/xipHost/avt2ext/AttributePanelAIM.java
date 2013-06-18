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

package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AttributePanelAIM extends JPanel implements FocusListener {	
	GridBagLayout layout;
	Map<String, Object> searchCriteria;
	Map<String, String> labelPath;
	
	public AttributePanelAIM() {
		searchCriteria = new HashMap<String, Object>();
		labelPath = new HashMap<String, String>();
		searchCriteria.put("Reader identity", "");		
		searchCriteria.put("Imaging Observation Characteristic", "");
		//searchCriteria.put("Nodule placement", "");
		//searchCriteria.put("Nodule Density", "");
		//searchCriteria.put("Nodule Set", "");
		//searchCriteria.put("Session", "");
		searchCriteria.put("Tool used for markup", "");		
		//searchCriteria.put("Version of the segmentation algorithm", "");		
		searchCriteria.put("Annotation Type", "");		
		
		labelPath.put("Reader identity", "User.name");
		labelPath.put("Imaging Observation Characteristic", "ImagingObservationCharacteristic.codeMeaning");
		/*
		labelPath.put("Nodule Type", "ImagingObservationCharacteristic.codeMeaning");
		labelPath.put("Nodule placement", "ImagingObservationCharacteristic.codeMeaning");
		labelPath.put("Nodule Density", "ImagingObservationCharacteristic.codeMeaning");
		labelPath.put("Nodule Set", "ImagingObservationCharacteristic.codeMeaning");
		labelPath.put("Session", "ImagingObservationCharacteristic.codeMeaning");
		*/		
		labelPath.put("Tool used for markup", "Equipment.manufacturerModelName");
		//labelPath.put("Version of the segmentation algorithm", "ImagingObservationCharacteristic.codeMeaning");
		labelPath.put("Annotation Type", "ImageAnnotation.codeMeaning");
		
		layout = new GridBagLayout();
		setLayout(layout);
		setBackground(new Color(156, 162, 189));
		setForeground(Color.WHITE);
		addAttributesToPanel();
	}

	
		
	private void addAttributesToPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTHWEST;
		
		int rowCountInPanel = 0;
		//add AIM criteria
		addInformationEntityLabelToPanel(rowCountInPanel++, constraints);
		Set<String> keys = searchCriteria.keySet();
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext()){
			String key = iter.next();
			addAttributeToPanel(rowCountInPanel++, key, constraints);			
		}		
	}
	
	Font font_1 = new Font("Tahoma", 0, 12);
	private void addInformationEntityLabelToPanel(int row, GridBagConstraints constraints) {
		JLabel label = new JLabel("AIM" + "____________________");
		label.setFont(font_1);
		constraints.gridy = row;
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
		layout.setConstraints(label, constraints);
		add(label);
	}
	
	private void addAttributeToPanel(int row, String name, GridBagConstraints constraints) {
		JLabel label = new JLabel(name);
		JTextField text = new TextFieldForAttribute(name);
		text.addFocusListener(this);
		label.setFont(font_1);
		label.setForeground(Color.WHITE);
		text.setFont(font_1);
				
		constraints.gridy = row;
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
		layout.setConstraints(label, constraints);
		add(label);
		constraints.gridx = 2;
		layout.setConstraints(text, constraints);
		add(text);
	}
		
	private class TextFieldForAttribute extends JTextField {
		String attKey;

		public TextFieldForAttribute(String attKey) {
			super(20);
			this.attKey = attKey;
		}		
		void replaceAttributeValue(String text) {
			if (!text.equalsIgnoreCase("null")) {	
				searchCriteria.put(attKey, text);
			}
		}
	}
	
	public Map<String, Object> getSearchCriteria() { 		
		Map<String, Object> aimSearchCriteria = new HashMap<String, Object>();
		Set<String> keys = searchCriteria.keySet();
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String value = (String) searchCriteria.get(key);
			String pathKey = labelPath.get(key);
			if(!value.isEmpty()){
				aimSearchCriteria.put(pathKey, value);
			}			
		}		
		return aimSearchCriteria;
	}

	public void focusGained(FocusEvent e) {			
		
	}

	public void focusLost(FocusEvent e) {				
		((TextFieldForAttribute)e.getSource()).replaceAttributeValue(((JTextField)e.getSource()).getText());
	}	
}
