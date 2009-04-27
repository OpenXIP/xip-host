/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.dicom.FilterPanel;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * @author Jaroslaw Krych
 *
 */
public class SearchCriteriaPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	JPanel btnPanel = new JPanel();
	JButton btnSearch = new JButton("Search");
	JButton btnCancel = new JButton("Cancel");
	FilterPanel panel;
	JScrollPane attEntryPanel;
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	
	public SearchCriteriaPanel(){		
		//ApplicationFrameQuery appFrame = new ApplicationFrameQuery(null);
		AttributeList list = DicomUtil.constructEmptyAttributeList();		
		panel = new FilterPanel(list);
		panel.setBackground(new Color(156, 162, 189));
		panel.setForeground(Color.WHITE);
		attEntryPanel = new JScrollPane(panel);
		attEntryPanel.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));		
		add(attEntryPanel);
		btnSearch.setPreferredSize(new Dimension(120, 25));
		btnSearch.setBackground(xipBtn);
		btnSearch.setForeground(Color.WHITE);
		btnCancel.setPreferredSize(new Dimension(100, 25));
		btnCancel.setBackground(xipBtn);
		btnCancel.setForeground(Color.WHITE);
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
		if(e.getSource() == btnSearch){						
			AttributeList criteria = getFilterList();										
			if(verifyCriteria(criteria)){																									
				//setVisible(false);
			} else {
				//if no criteria specified do ...
				
			}
		}else if(e.getSource() == btnCancel){
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
					//System.out.println("JK: " + strAtt + " " + attValue);
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
}
