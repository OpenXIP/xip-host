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
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import edu.wustl.xipHost.application.EditApplicationDialog;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;

/**
 * @author Jaroslaw Krych
 *
 */
public class OptionsDialog extends JDialog implements ActionListener{
		String ADD_CMD = "Add application";
		String VIEW_CMD = "View applications";					
		String HOST_CMD = "Host settings";
		
		public JButton btnAdd = new JButton(ADD_CMD);
		public JButton btnView = new JButton(VIEW_CMD);							
		public JButton btnHostSet = new JButton(HOST_CMD);
		JPanel btnPanel = new JPanel();
		Color xipBtn = new Color(56, 73, 150);
		Color xipColor = new Color(51, 51, 102);
		
		public OptionsDialog(JFrame owner){			
			super(owner, "Options", true);			
			btnPanel.setLayout(new GridLayout(3, 1));			
			btnAdd.setPreferredSize(new Dimension(150, 25));
			btnView.setPreferredSize(new Dimension(150, 25));
			btnHostSet.setPreferredSize(new Dimension(150, 25));
			btnAdd.setBackground(xipBtn);
			btnAdd.setForeground(Color.BLACK);
			btnView.setBackground(xipBtn);
			btnView.setForeground(Color.BLACK);
			btnHostSet.setBackground(xipBtn);
			btnHostSet.setForeground(Color.BLACK);
		    btnPanel.add(btnAdd);				
		    btnPanel.add(btnView);			       							   			    
		    btnPanel.add(btnHostSet);
		    btnPanel.setBackground(xipColor);
		    add(btnPanel);
		    btnAdd.addActionListener(this);
		    btnView.addActionListener(this);
		    btnHostSet.addActionListener(this);
		    setVisible(false);		               
		    setResizable(true);
		    pack();
		    addWindowListener(new WindowAdapter(){	         
    			public void windowClosing(WindowEvent e){    	        	 	        	    				
    				close();    				
		        }	         
			});
		}
		
		public void display(int x, int y){
			setLocation(x, y);
			setVisible(true);
		}
		
		public void close(){
			setVisible(false);
		}
		
		public static void main (String [] args){
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
			OptionsDialog optionDialog = new OptionsDialog(new JFrame());
			optionDialog.display(10, 20);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btnAdd){
				EditApplicationDialog appDialog = new EditApplicationDialog(new JFrame(), null);
				appDialog.setVisible(true);
				close();
			}else if(e.getSource() == btnView){
				Application [] apps;
				ApplicationManager appMgr = ApplicationManagerFactory.getInstance();
				List<Application> listApps = new ArrayList<Application>();
				List<Application> validApps = appMgr.getApplications();
				List<Application> notValidApps = appMgr.getNotValidApplications();
				listApps.addAll(validApps);
				listApps.addAll(notValidApps);
				apps = new Application[listApps.size()];
				listApps.toArray(apps);				
				ApplicationListDialog appList = new ApplicationListDialog(new JFrame(), apps);
				appList.display();
				close();
			}else if(e.getSource() == btnHostSet){				
				Point point = btnHostSet.getLocationOnScreen();
				new UnderDevelopmentDialog(point);
			}	
		}
			
}
