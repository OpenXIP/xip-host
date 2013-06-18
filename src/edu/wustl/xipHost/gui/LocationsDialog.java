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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.caGrid.GridManager;
import edu.wustl.xipHost.caGrid.GridManagerFactory;
import edu.wustl.xipHost.caGrid.GridLocation.Type;


/**
 * @author Jaroslaw Krych
 *
 */
public class LocationsDialog extends JDialog implements ActionListener , KeyListener{
	DefaultComboBoxModel comboModel;	
	JComboBox list;	
	DescPanel descPanel = new DescPanel();
	JPanel btnPanel = new JPanel();
	JButton btnAdd = new JButton("Add New");
	JButton btnModify = new JButton("Modify");
	JButton btnDelete = new JButton("Delete");
	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Font font_1 = new Font("Tahoma", 0, 12);	
	GridManager gridMgr;
	
	public LocationsDialog(Frame owner, List<GridLocation> gridLocations){
		super(owner, "Service Locations", true);			
		getContentPane().setBackground(xipColor);
		comboModel = new DefaultComboBoxModel();
		list = new JComboBox(comboModel);
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		list.setRenderer(renderer);
		list.setMaximumRowCount(10);		
		list.setPreferredSize(new Dimension(465, 25));
		list.setFont(font_1);
		list.setEditable(false);		
		list.addActionListener(this);
		gridMgr = GridManagerFactory.getInstance();		
		for(int i = 0; i < gridLocations.size(); i++){
			comboModel.addElement(gridLocations.get(i));
		}
		list.addActionListener(this);
		list.setSelectedIndex(0);		
		add(list);
		
		btnAdd.setPreferredSize(new Dimension(100, 25));
		btnModify.setPreferredSize(new Dimension(100, 25));
		btnDelete.setPreferredSize(new Dimension(100, 25));
		btnAdd.setBackground(xipBtn);
		btnModify.setBackground(xipBtn);
		btnDelete.setBackground(xipBtn);
		btnAdd.setForeground(Color.WHITE);
		btnModify.setForeground(Color.WHITE);
		btnDelete.setForeground(Color.WHITE);
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnDelete.addActionListener(this);
		btnPanel.add(btnAdd);
		btnPanel.add(btnModify);
		btnPanel.add(btnDelete);
		btnPanel.setBackground(xipColor);
		descPanel.setBackground(xipLightBlue);
		list.addKeyListener(this);
		btnAdd.addKeyListener(this);
		btnModify.addKeyListener(this);
		btnDelete.addKeyListener(this);
		descPanel.txtURL.addKeyListener(this);
		descPanel.txtName.addKeyListener(this);
		add(descPanel);
		add(btnPanel);			
		buildLayout();
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();               								
		Dimension windowSize = getPreferredSize();		
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
        addWindowListener(new WindowAdapter(){	         
			public void windowClosing(WindowEvent e){    	        	 					
				dispose();	
	        }	         
		}); 
        setResizable(false);
        requestFocus();
        setVisible(true);        
	}
	
	
	public void buildLayout(){		
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        //Insets(top, left, bottom, right) 
        constraints.insets = new Insets(20, 80, 0, 80);        
        layout.setConstraints(list, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        //Insets(top, left, bottom, right) 
        constraints.insets = new Insets(30, 80, 0, 80);        
        layout.setConstraints(descPanel, constraints);
	
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        //Insets(top, left, bottom, right) 
        constraints.insets = new Insets(20, 80, 10, 80);        
        layout.setConstraints(btnPanel, constraints);
	}
	
	static Container dialog;
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		List<GridLocation> locs = new ArrayList<GridLocation>();
		GridLocation gridLoc1 = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location 1");
		GridLocation gridLoc2 = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location 2");
		locs.add(gridLoc1);
		locs.add(gridLoc2);				
		LocationsDialog locDialog = new LocationsDialog(new JFrame(), locs);
		dialog = locDialog.getContentPane();
		locDialog.addWindowListener(new WindowAdapter(){	         
			public void windowClosing(WindowEvent e){    	        	 					
				System.exit(0);	
	        }	         
		}); 		
		locDialog.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();               								
		Dimension windowSize = locDialog.getPreferredSize();		
        locDialog.setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnAdd){
			String strURL = descPanel.txtURL.getText();
			String strName = descPanel.txtName.getText();
			Type type = (Type) descPanel.type.getSelectedItem();
			String protocolVersion = descPanel.txtProtocolVersion.getText();
			GridLocation loc = null;
			if(verifyInput()){				
				loc = new GridLocation(strURL, type, protocolVersion, strName);
			}else{
				return;
			}
			if(gridMgr.exists(loc)){				
				descPanel.txtURL.setText(loc.getAddress());
				descPanel.txtName.setText(loc.getShortName());
				if(loc.getType().equals(Type.DICOM)){
					descPanel.type.setSelectedIndex(0);
				}else{
					descPanel.type.setSelectedIndex(1);
				}	
				descPanel.lblURLError.setText("Duplicate name");
				//update(getGraphics());
				return;
			}			
			//If location does not exist add to the list and comboModel
			gridMgr.addGridLocation(loc);
			comboModel.addElement(loc);
			comboModel.setSelectedItem(loc);
			list.update(list.getGraphics());
		}else if(e.getSource() == btnModify){
			String strURL = descPanel.txtURL.getText();
			String strName = descPanel.txtName.getText();
			Type type = (Type) descPanel.type.getSelectedItem();
			String protocolVersion = descPanel.txtProtocolVersion.getText();
			GridLocation oldLoc = (GridLocation) list.getSelectedItem();
			GridLocation newLoc = null;			
			try{
				newLoc = new GridLocation(strURL, type, protocolVersion, strName);
			}catch (IllegalArgumentException excep){
				if(descPanel.lblURL.getText().trim().isEmpty()){descPanel.lblURL.setForeground(Color.RED);}
				if(descPanel.lblName.getText().trim().isEmpty()){descPanel.lblName.setForeground(Color.RED);}
				list.update(list.getGraphics());
				return;
			}
			for(int i = 0; i < gridMgr.getGridLocations().size(); i++){
				GridLocation gridLoc = gridMgr.getGridLocations().get(i);
				if(gridLoc.getShortName().equalsIgnoreCase(strName)){
					//TODO inform loc exists. Ask if to overrite
					final JOptionPane optionPane = new JOptionPane(
			                "Location " + strName + " already exists.\n"
			                + "Would you like to overrite " + strName + "?\n",
			                JOptionPane.QUESTION_MESSAGE,
			                JOptionPane.YES_NO_OPTION);
					final JDialog dialog = new JDialog(this, "Modify location dialog", true);
					dialog.setContentPane(optionPane);
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);					
					optionPane.addPropertyChangeListener(
							new PropertyChangeListener() {
								public void propertyChange(PropertyChangeEvent e) {
									String prop = e.getPropertyName();
									if (dialog.isVisible() && (e.getSource() == optionPane)
											&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {										
										dialog.setVisible(false);
									}
								}
							});
					dialog.getContentPane().setBackground(xipColor);
					dialog.pack();
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();               								
					Dimension windowSize = dialog.getPreferredSize();		
			        dialog.setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
					dialog.setVisible(true);
					int value = ((Integer)optionPane.getValue()).intValue();
					if (value == JOptionPane.YES_OPTION) {
						gridMgr.modifyGridLocation(oldLoc, newLoc);			
						comboModel.removeElement(oldLoc);
						comboModel.addElement(newLoc);
						comboModel.setSelectedItem(newLoc);
						list.update(list.getGraphics());
					} else if (value == JOptionPane.NO_OPTION) {					   
						return;
					}					
				}
			}						
		}else if(e.getSource() == btnDelete){
			GridLocation loc = (GridLocation) list.getSelectedItem();	
			if(gridMgr.getGridLocations().contains(loc)){
				gridMgr.removeGridLocation(loc);
				comboModel.removeElement(loc);
				list.setSelectedIndex(0);
				list.update(list.getGraphics());
			}	
		}else if(e.getSource() == list){
			GridLocation loc = (GridLocation) list.getSelectedItem();
			descPanel.txtURL.setText(loc.getAddress());
			descPanel.txtName.setText(loc.getShortName());
			if(loc.getType().equals(Type.DICOM)){
				descPanel.type.setSelectedIndex(0);
			}else{
				descPanel.type.setSelectedIndex(1);
			}			
		}		
	}
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(440, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof GridLocation) {
			    	renderer.setText(((GridLocation)value).getShortName());
			    	renderer.setBackground(Color.WHITE);
			    }
			    if(cellHasFocus || isSelected){
			    	renderer.setBackground(xipLightBlue);
			    	renderer.setForeground(Color.WHITE);
			    	renderer.setBorder(new LineBorder(Color.DARK_GRAY));
			    }else{
			    	renderer.setBorder(null);
			    }			    
			    renderer.setPreferredSize(preferredSize);
			    return renderer;
			  }		
	}
	
	
	class DescPanel extends JPanel{
		JLabel lblURL = new JLabel("URL");
		JLabel lblURLError = new JLabel("");
		JLabel lblName = new JLabel("Name");
		JLabel lblNameError = new JLabel("");
		JLabel lblType = new JLabel("Type");	
		JTextField txtURL = new JTextField("", 45);	
		JTextField txtName = new JTextField("", 45);
		JComboBox type = new JComboBox();
		JLabel lblProtocolVersion = new JLabel("Protocol Version");
		JLabel lblProtocolVersionError = new JLabel("");
		JTextField txtProtocolVersion = new JTextField("", 45);
		Border border = BorderFactory.createLoweredBevelBorder();
		public DescPanel(){									
			lblURL.setForeground(Color.WHITE);
			lblURLError.setForeground(Color.RED);
			lblName.setForeground(Color.WHITE);
			lblNameError.setForeground(Color.RED);
			lblType.setForeground(Color.WHITE);						
			lblProtocolVersion.setForeground(Color.WHITE);
			lblProtocolVersionError.setForeground(Color.RED);
			add(lblURL);			
			add(lblURLError);
			add(txtURL);
			add(lblName);
			add(lblNameError);
			add(txtName);
			add(lblType);
			type.addItem(Type.DICOM);
			type.addItem(Type.AIM);
			type.setSelectedIndex(0);
			add(type);			
			add(lblProtocolVersion);
			add(lblProtocolVersionError);
			add(txtProtocolVersion);
			setBorder(border);
			buildLayout();
		}
		public void buildLayout(){		
			GridBagLayout layout = new GridBagLayout();
	        GridBagConstraints constraints = new GridBagConstraints();
	        setLayout(layout);
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 0;
	        constraints.gridy = 0;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(10, 10, 0, 10);
	        constraints.anchor = GridBagConstraints.EAST;
	        layout.setConstraints(lblURL, constraints);	        
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 0;
	        constraints.gridy = 2;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(5, 10, 0, 10);
	        constraints.anchor = GridBagConstraints.EAST;
	        layout.setConstraints(lblName, constraints);
		
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 0;
	        constraints.gridy = 4;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(5, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.EAST;
	        layout.setConstraints(lblType, constraints);
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 1;
	        constraints.gridy = 0;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(10, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.CENTER;
	        layout.setConstraints(txtURL, constraints);
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 1;
	        constraints.gridy = 1;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(10, 10, 0, 10);
	        constraints.anchor = GridBagConstraints.WEST;
	        layout.setConstraints(lblURLError, constraints);
	        
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 1;
	        constraints.gridy = 2;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(5, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.CENTER;
	        layout.setConstraints(txtName, constraints);
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 1;
	        constraints.gridy = 3;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(5, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.WEST;
	        layout.setConstraints(lblNameError, constraints);
	        
	        constraints.fill = GridBagConstraints.NONE;        
	        constraints.gridx = 1;
	        constraints.gridy = 4;
	        //Insets(top, left, bottom, right) 
	        constraints.insets = new Insets(5, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.WEST;
	        layout.setConstraints(type, constraints);
	        
		}		
	}


	public void keyPressed(KeyEvent e) {
		int nKeyCode = e.getKeyCode();
		if(nKeyCode == 27){
			dispose();
		}else if(e.getSource() == descPanel.txtURL || e.getSource() == descPanel.txtName){			
			descPanel.lblURLError.setText("");						
		}		
	}


	public void keyReleased(KeyEvent arg0) {		
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}
	
	boolean verifyInput(){
		String strURL = descPanel.txtURL.getText();
		String strName = descPanel.txtName.getText();		
		if(strURL.trim().isEmpty()){
			descPanel.lblURLError.setText("Invalid URL");			
			return false;
		}
		if(strName.trim().isEmpty()){
			descPanel.lblName.setText("Invalid name");			
			return false;
		}
		return true;		
	}
}
