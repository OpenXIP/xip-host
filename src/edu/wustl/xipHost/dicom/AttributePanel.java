/**
 * Copyright (c) 2001-2005, David A. Clunie DBA Pixelmed Publishing. All rights reserved.
 */
package edu.wustl.xipHost.dicom;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.StringAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TextAttribute;

/**
 * AttributePanel is modified com.pixelmed.query.FilterPanel.
 * Modification was made  by Washington University in St. Louis. Modification author: Jaroslaw Krych 
 * Class provides a graphical user interface for editing 
 * the string values of DICOM attributes in list.
 *
 * <p>The list is ordered by the information entity of the attributes (patient,
 * study, series, instance), then sorted alphabetically by name within that
 * grouping.</p>
 *
 * <p>The class is not aware of any underlying matching key type semantics and allows
 * the user to enter the string literally, so to enter a wild card the user must know
 * and use the appropriate special characters. For example, entering "*Smith* for the
 * PatientName attribute value should match all patients whose name includes Smith.</p>
 *
 * <p>Note also that it is not necessary for the user to type tab or return after editing each field in
 * order to have the attribute value updated in the list that is returned, every key stroke is reflected
 * immediately in the corresponding attribute.</p>
 *
 * <p>Specific Character Set is specifically excluded from the displayed list, since it is not a
 *  matching key but rather describes the encoding of the list.</p>
 *
 * <p>UID attributes were formerly filtered out from the list, but it turns out to be useful
 * to be able to match on these, for instance on SOP Classes in Study.</p>
 *
 * @author	dclunie
 * </font>
 */
public class AttributePanel extends JPanel implements FocusListener {
	final static Logger logger = Logger.getLogger(AttributePanel.class);
	AttributeList filterList;
	GridBagLayout layout;
		
	public AttributePanel(AttributeList list) {
		super();
		layout = new GridBagLayout();
		setLayout(layout);
		filterList = list;
		addAttributesToPanel();
	}
	
	private void addAttributesToPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTHWEST;
		DicomDictionary dictionary = AttributeList.getDictionary();
		
		int rowCountInPanel = 0;
		for (int ie = 0; ie < iterateByInformationEntity.length; ie++) {
			InformationEntity whichIE = iterateByInformationEntity[ie];			
			TreeSet<String> names = new TreeSet<String>();			// sorted within TreeSet by name
			Iterator i = filterList.values().iterator();
			while (i.hasNext()) {
				Attribute a  = (Attribute)i.next();				
				if (a != null && (a instanceof StringAttribute || a instanceof TextAttribute)) {					
					AttributeTag t = a.getTag();					
					if (!t.equals(TagFromName.SpecificCharacterSet) && dictionary.getInformationEntityFromTag(t) == whichIE) {
						names.add(dictionary.getNameFromTag(t));						
					}
				}
			}
			// now add the sorted names for this IE to the panel ...
			i = names.iterator();
			if (i.hasNext()) {
				addInformationEntityLabelToPanel(rowCountInPanel++, whichIE, constraints);
			}
			while (i.hasNext()) {
				addAttributeToPanel(rowCountInPanel++, (String)i.next(), constraints);
			}
		}
	}
	
	Font font_1 = new Font("Tahoma", 0, 12); 
	private void addInformationEntityLabelToPanel(int row,InformationEntity ie,GridBagConstraints constraints) {
		JLabel label = new JLabel(ie.toString() + "____________________");
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
		DicomDictionary dictionary = AttributeList.getDictionary();
		AttributeTag t = dictionary.getTagFromName(name);
		Attribute a = filterList.get(t);
		JLabel label = new JLabel(name);
		JTextField text = new TextFieldForAttribute(a);
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
	
	private static InformationEntity iterateByInformationEntity[] = {
			InformationEntity.PATIENT,
			InformationEntity.STUDY,
			InformationEntity.PROCEDURESTEP,
			InformationEntity.SERIES,
			InformationEntity.CONCATENATION,
			InformationEntity.INSTANCE,
			InformationEntity.FRAME
	};
	
	
	private class TextFieldForAttribute extends JTextField {
		Attribute attribute;

		public TextFieldForAttribute(Attribute a) {
			super(20);
			attribute = a;
			setText(a.getSingleStringValueOrEmptyString());
		}
		
		void replaceAttributeValue(String text) {
			try {
				attribute.removeValues();
				if (text != null && text.length() > 0) {
					attribute.addValue(text);
					//Update filter list
					AttributeTag t = attribute.getTag();
					filterList.put(t, attribute);
				}
			} catch (DicomException e) {
				logger.error(e, e);
			}
		}				
	}
	
	public AttributeList getFilterList() { 		
		return filterList;
	}

	public void focusGained(FocusEvent e) {			
		
	}

	public void focusLost(FocusEvent e) {				
		((TextFieldForAttribute)e.getSource()).replaceAttributeValue(((JTextField)e.getSource()).getText());
	}
}
