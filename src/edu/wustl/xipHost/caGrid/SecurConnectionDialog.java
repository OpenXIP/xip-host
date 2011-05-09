/**
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.wustl.xipHost.gui.ProgressPanel;
import edu.wustl.xipHost.gui.SwingWorker;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.hostControl.Login;

/**
 * <font  face="Tahoma" size="2" color="Black">
 * GUI component, used to enable 'grid secured connection'<b></b>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class SecurConnectionDialog extends JDialog implements ActionListener{	
	private static final long serialVersionUID = 1L;
	JLabel lblUser = new JLabel();
	Font font_1 = new Font("Tahoma", 1, 13);
	public JButton btnOK = new JButton("Secure connection");
	JPanel panel = new JPanel();
	public ProgressPanel pg = new ProgressPanel();
	GridLogin cmdLogin = new GridLogin();	
	
	public SecurConnectionDialog(JFrame owner){
		super(owner, "Grid security", true);	
		lblUser.setText("User: " + HostConfigurator.getHostConfigurator().getUser());
		lblUser.setFont(font_1);
		btnOK.addActionListener(this);
		btnOK.setPreferredSize(new Dimension(150, 25));		
		pg.createAndShowGUI();
		buildLayout();
		panel.add(lblUser);
		panel.add(btnOK);
		panel.add(pg);
		add(panel);
		pack();
        setAlwaysOnTop(true);        
        addWindowListener(new WindowAdapter(){	         
			public void windowClosing(WindowEvent e){    	        	 
	        	setVisible(false);
				return;        	
	         }	         
		}); 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();                        
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
        setVisible(false);
	}
	
	
	void buildLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);        

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 20;
        constraints.insets.left = 20;
        constraints.insets.right = 20;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(lblUser, constraints);  
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 20;
        constraints.insets.left = 20;
        constraints.insets.right = 20;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnOK, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 20;
        constraints.insets.bottom = 15;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(pg, constraints);
	}
	
	public static void main(String[] args) {
		SecurConnectionDialog securDialog = new SecurConnectionDialog(new JFrame());
		securDialog.display();
	}
	public void display(){
		setVisible(true);
	}	
	
	public void actionPerformed(ActionEvent e) {						
			if(e.getSource() == btnOK){
				pg.getProgressBar().setString("Establishing secure connection ...");			
				Thread t = new Thread(pg);
				pg.getProgressBar().setIndeterminate(true);
				Login.setValidateGridSecur(true);
				class ConnectAndProgress extends SwingWorker{				
					public Object construct() {										
						String user = HostConfigurator.getHostConfigurator().getUser();
						String password = "123";								
						if (Login.validateUser(user, password)){
							
						}else{
							interrupt();							
						} 
						return null;
					}						
					public void finished() {					
						pg.isDone = true;
						pg.getProgressBar().setString("Securred connection established");
						pg.stopProgress();
						setVisible(false);
					}
				}    						
				new ConnectAndProgress().start();														
				t.start();
			}
	}			
}
