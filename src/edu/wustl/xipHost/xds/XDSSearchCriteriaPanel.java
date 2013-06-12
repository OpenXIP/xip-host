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

package edu.wustl.xipHost.xds;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.dicom.AttributePanel;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * XSDSearchCriteriaPanel contains only patient criteria needed to query patient ID
 * @author Jaroslaw Krych
 *
 */
public class XDSSearchCriteriaPanel extends JPanel implements ActionListener {	
	JPanel btnPanel = new JPanel();
	JButton btnSearchPatientID = new JButton("Search patients");
	JButton btnSearch = new JButton("Search Documents");	
	JButton btnCancel = new JButton("Cancel");
	AttributePanel panel;
	JScrollPane attEntryPanel;
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	
	JList patientList;
	JScrollPane listScroller;
	DefaultListModel listModel;
	Font font = new Font("Tahoma", 0, 12); 
	
	public XDSSearchCriteriaPanel(){		
		//ApplicationFrameQuery appFrame = new ApplicationFrameQuery(null);
		AttributeList list = DicomUtil.constructEmptyPatientAttributeList();		
		panel = new AttributePanel(list);
		panel.setBackground(new Color(156, 162, 189));
		panel.setForeground(Color.WHITE);
		attEntryPanel = new JScrollPane(panel);		
		attEntryPanel.setPreferredSize(new Dimension(500, 250));		
		add(attEntryPanel);
		listModel = new DefaultListModel();				
		patientList = new JList(listModel);
		patientList.setFont(font);
		patientList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);		
		patientList.setVisibleRowCount(0);		
		listScroller = new JScrollPane(patientList);
		int height = (int) (HostConfigurator.adjustForResolution() - attEntryPanel.getPreferredSize().getHeight() - 75);		
		listScroller.setPreferredSize(new Dimension(500, height));
		patientList.setBackground(new Color(156, 162, 189));
		patientList.setForeground(Color.BLACK);
	    add(listScroller);		
	    btnSearchPatientID.setPreferredSize(new Dimension(200, 25));
	    btnSearchPatientID.setBackground(xipBtn);
	    btnSearchPatientID.setForeground(Color.BLACK);
	    add( btnSearchPatientID);
	    btnSearch.setPreferredSize(new Dimension(150, 25));
		btnSearch.setBackground(xipBtn);
		btnSearch.setForeground(Color.BLACK);
		btnSearch.setEnabled(false);
		btnCancel.setPreferredSize(new Dimension(100, 25));
		btnCancel.setBackground(xipBtn);
		btnCancel.setForeground(Color.BLACK);				
		btnCancel.addActionListener(this);
		btnPanel.add(btnSearch);				
		btnPanel.add(btnCancel);
		btnPanel.setBackground(xipColor);
		add(btnPanel);
		this.setBackground(xipColor);
		buildLayout();				
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
		setVisible(true);
	}
	
	void buildLayout(){		
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);         
        
		constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 15;
        constraints.insets.left = 10;
        constraints.insets.right = 10;                
        //constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(attEntryPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 20;
        constraints.insets.bottom = 10;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnSearchPatientID, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;        
        constraints.insets.bottom = 10;        
        layout.setConstraints(listScroller, constraints);  
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets.top = 10;        
        constraints.insets.bottom = 10;        
        //constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(btnPanel, constraints);
	}
	
	public AttributeList getFilterList(){
		return panel.getFilterList();
	}
	
	
	public static void main(String args[]){
		XDSSearchCriteriaPanel panel = new XDSSearchCriteriaPanel();
		JDialog dialog = new JDialog(new JFrame(), "Criteria", true);
		dialog.getContentPane().add(panel);		
		dialog.setPreferredSize(new Dimension(550, 900));
		dialog.pack();
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCancel){
			new UnderDevelopmentDialog(btnCancel.getLocationOnScreen());
		}
	}
	
	public boolean verifyCriteria(AttributeList list){
		if(list.size() > 0){			
			//Make sure values are not null and at least one is non empty String (except SpecificCharacterSet)
			DicomDictionary dictionary = AttributeList.getDictionary();
			Iterator iter = dictionary.getTagIterator();			
			boolean isEmpty = true;
			while(iter.hasNext()){
				AttributeTag attTag  = (AttributeTag)iter.next();
				String strAtt = attTag.toString();									
				String attValue = Attribute.getSingleStringValueOrEmptyString(list, attTag);
				if(attValue.equalsIgnoreCase("null")){
					return false;
				} else if (isEmpty == true && !attValue.isEmpty() && !strAtt.equalsIgnoreCase("(0x0008,0x0005)")){	//(0x0008,0x0005) is attribute tag SpecificCharacterSet
					//System.out.println(strAtt);
					isEmpty = false;
				}
			}			
			if(isEmpty == false){
				return true;
			}else{
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void setQueryButtonText(String text){
		btnSearch.setText(text);
	}
	public JButton getQueryButton(){
		return btnSearch;
	}
	public JButton getPatientIDQueryButton(){
		return btnSearchPatientID;
	}
	public JList getPatientList(){
		return patientList;
	}
	public DefaultListModel getListModel(){
		return listModel;
	}
	
	
}
