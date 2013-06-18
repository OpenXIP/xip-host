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

package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dicom.AttributePanel;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * @author Jaroslaw Krych
 *
 */
public class SearchCriteriaPanel extends JPanel implements ActionListener{
	private SearchCriteriaVerifier searchCriteriaVerifier;
	JPanel btnPanel = new JPanel();
	JButton btnSearch = new JButton("Search");
	JButton btnCancel = new JButton("Cancel");
	AttributePanel panel;	
	JScrollPane attEntryPanel;
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	
	public SearchCriteriaPanel(){
		// default criteria verifier is basic one unless different one is set.
		searchCriteriaVerifier = new BasicSearchCriteriaVerifier();
		AttributeList list = DicomUtil.constructEmptyAttributeList();		
		panel = new AttributePanel(list);
		panel.setBackground(new Color(156, 162, 189));
		panel.setForeground(Color.WHITE);
		attEntryPanel = new JScrollPane(panel);
		attEntryPanel.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));		
		add(attEntryPanel);
		btnSearch.setPreferredSize(new Dimension(120, 25));
		btnSearch.setBackground(xipBtn);
		btnSearch.setForeground(Color.BLACK);
		btnCancel.setPreferredSize(new Dimension(100, 25));
		btnCancel.setBackground(xipBtn);
		btnCancel.setForeground(Color.BLACK);
		btnSearch.addActionListener(this);
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
        constraints.insets.top = 10;
        constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.bottom = 0;        
        //constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(attEntryPanel, constraints);  
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 10;        
        constraints.insets.bottom = 10;        
        //constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(btnPanel, constraints);
	}
	
	public AttributeList getFilterList(){
		return panel.getFilterList();
	}
	
	
	public static void main(String args[]){
		SearchCriteriaPanel panel = new SearchCriteriaPanel();
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
		return searchCriteriaVerifier.verifyCriteria(list);
	}
	
	public void setQueryButtonText(String text){
		btnSearch.setText(text);
	}
	public JButton getQueryButton(){
		return btnSearch;
	}
	
	public void resetCriteriaPanel(){
		remove(attEntryPanel);
		AttributeList list = DicomUtil.constructEmptyAttributeList();	
		panel = new AttributePanel(list);
		attEntryPanel = new JScrollPane(panel);
		attEntryPanel.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));		
		add(attEntryPanel);
		repaint();
	}
}
