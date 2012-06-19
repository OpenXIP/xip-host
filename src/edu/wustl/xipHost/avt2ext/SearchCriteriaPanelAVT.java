/**
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dicom.AttributePanel;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.BasicSearchCriteriaVerifier;
import edu.wustl.xipHost.gui.SearchCriteriaVerifier;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * @author Jaroslaw Krych
 *
 */
public class SearchCriteriaPanelAVT extends JPanel implements ActionListener{
	final static Logger logger = Logger.getLogger(SearchCriteriaPanelAVT.class);
	SearchCriteriaVerifier searchCriteriaVerifier;
	JPanel btnPanel = new JPanel();
	JButton btnSearch = new JButton("Search");
	JButton btnCancel = new JButton("Cancel");
	AttributePanelAIM panelAVT;
	AttributePanel panelDICOM;
	AttributePanelPrivateTag panelPrivateTag;
	JScrollPane attEntryPanel;
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	GridBagLayout layout = new GridBagLayout();
	JPanel panel = new JPanel();
	
	public SearchCriteriaPanelAVT(){
		panelAVT = new AttributePanelAIM();
		AttributeList list = DicomUtil.constructEmptyAttributeList();		
		panelDICOM = new AttributePanel(list);
		panelPrivateTag = new AttributePanelPrivateTag();
		// default criteria verifier is basic one unless different one is set.
		searchCriteriaVerifier = new BasicSearchCriteriaVerifier();		
		panelAVT.setBackground(new Color(156, 162, 189));
		panelAVT.setForeground(Color.WHITE);
		panelDICOM.setBackground(new Color(156, 162, 189));
		panelDICOM.setForeground(Color.WHITE);
		panelPrivateTag.setBackground(new Color(156, 162, 189));
		panelPrivateTag.setForeground(Color.WHITE);
		buildAttributePanelLayout();
		panel.setLayout(layout);
		panel.add(panelAVT);
		panel.add(panelDICOM);
		panel.add(panelPrivateTag);
		panel.setBackground(new Color(156, 162, 189));
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
	
	void buildAttributePanelLayout(){
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets.left = 10;
        constraints.insets.right = 10;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
		layout.setConstraints(panelAVT, constraints);
		constraints.gridy = 2;
		constraints.gridx = 1;
		layout.setConstraints(panelDICOM, constraints);
		constraints.gridy = 3;
		constraints.gridx = 1;
		layout.setConstraints(panelPrivateTag, constraints);
	}
	
	public static void main(String args[]){
		SearchCriteriaPanelAVT panel = new SearchCriteriaPanelAVT();
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
	
	public boolean verifyCriteria(AttributeList list, Map<String, Object> aimCriteria){		
		boolean blnAimCriteria = false;
		Set<String> keys = aimCriteria.keySet();
		Iterator<String> iter = keys.iterator();
		boolean isAimCriteriaEmpty = true;
		while(iter.hasNext()){
			String key = iter.next();
			String value = (String) aimCriteria.get(key);
			if(value.equalsIgnoreCase("null")){
				logger.warn("Verification result - NULL query criteria - INVALID.");
				return false;
			} else if (isAimCriteriaEmpty == true && !value.isEmpty()){
				logger.debug(key + ": " + aimCriteria.get(key));
				isAimCriteriaEmpty = false;
			}
		}			
		if(isAimCriteriaEmpty == false){
			logger.debug("Verification result - AIM query criteria - VALID.");
			blnAimCriteria = true;
		}
		boolean blnDicomCriteria = searchCriteriaVerifier.verifyCriteria(list);
		boolean criteria = false;
		if(blnAimCriteria == true || blnDicomCriteria == true){
			criteria = true;
		}
		return criteria;
	}
	
	public void setQueryButtonText(String text){
		btnSearch.setText(text);
	}
	public JButton getQueryButton(){
		return btnSearch;
	}
	
	public AttributeList getFilterList(){
		return panelDICOM.getFilterList();
	}
	
	public void resetCriteriaPanel(){	
		panel.remove(panelAVT);
		panelAVT = new AttributePanelAIM();
		panel.add(panelAVT);
		panel.remove(panelDICOM);
		AttributeList list = DicomUtil.constructEmptyAttributeList();	
		panelDICOM = new AttributePanel(list);
		panel.add(panelDICOM);
		panel.remove(panelPrivateTag);
		panelPrivateTag = new AttributePanelPrivateTag();
		panel.add(panelPrivateTag);
		buildAttributePanelLayout();
		panel.repaint();
		attEntryPanel.getVerticalScrollBar().setValue(0);
	}
	
}
