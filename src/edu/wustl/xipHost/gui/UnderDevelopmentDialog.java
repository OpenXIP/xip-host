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
import java.awt.GridLayout;
import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Jaroslaw Krych
 *
 */
public class UnderDevelopmentDialog {
	JPanel messagePanel = new JPanel();
	Font font = new Font("Tahoma", 0, 12); 
	JLabel lblLine_1;    
    JOptionPane optPane = new JOptionPane(null, JOptionPane.YES_OPTION);
    ImageIcon iconLogo =  new ImageIcon("./gif/XIP-Host-Corner-Logo-Red.PNG");
    Color xipColor = new Color(51, 51, 102);
   
    
	public UnderDevelopmentDialog(Point locationOnScreen){				
		messagePanel.setPreferredSize(new Dimension(250, 40));	
	    messagePanel.setLayout(new GridLayout(1, 1));	    
	    lblLine_1 = new JLabel("Under development", JLabel.LEFT);	    
	    lblLine_1.setFont(font);
	    lblLine_1.setForeground(Color.WHITE);
	    messagePanel.add(lblLine_1);
	    optPane.setMessageType(JOptionPane.PLAIN_MESSAGE);            
	    optPane.setMessage(messagePanel);
	    optPane.setIcon(iconLogo);	    
	    JPanel buttonPanel = (JPanel)optPane.getComponent(1);
	    JButton buttonOk = (JButton)buttonPanel.getComponent(0);
	    buttonOk.setPreferredSize(new Dimension(100, 25));            
	    applyOptionPaneBackground(optPane, xipColor);
	    JDialog d = optPane.createDialog(null, "Information");
	    int x = (int)locationOnScreen.getX();
	    int y = (int)locationOnScreen.getY();
	    d.setLocation(x - 200, y + 25);
		d.setVisible(true);
	}
	
	public static void applyOptionPaneBackground(JOptionPane optionPane, Color color) {
	    optionPane.setBackground(color);
	    for (Iterator i = getComponents(optionPane).iterator(); 
    	i.hasNext(); ) {
    		Component comp = (Component)i.next();
    		if (comp instanceof JPanel) {
    			comp.setBackground(color);
    		}
    	}
  	}
	
	public final static Collection<Component> getComponents(Container container) {
	    Collection<Component> components = new Vector<Component>();
		Component[] comp = container.getComponents();
		for (int i = 0, n = comp.length; i < n; i++) {
			components.add(comp[i]);
			if (comp[i] instanceof Container) {
				components.addAll(getComponents((Container) comp[i]));
			}
	    }
		return components;
	}	
}
