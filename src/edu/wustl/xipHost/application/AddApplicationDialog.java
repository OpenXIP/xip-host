/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.FileChooser;
import edu.wustl.xipHost.gui.HostMainWindow;


public class AddApplicationDialog extends JDialog implements ActionListener {
	JLabel lblName = new JLabel("Name");
	JLabel lblPath = new JLabel("Path");
	JLabel lblVendor = new JLabel("Vendor");
	JLabel lblVersion = new JLabel("Version");
	JLabel lblIconFile = new JLabel("Icon");
	JLabel lblType = new JLabel("Type");
	JLabel lblRequiresGUI = new JLabel("Requires GUI");
	JLabel lblWG23DataModelType = new JLabel("WG23 data model Type");
	JLabel lblConcurrentInstances = new JLabel("Allowable concurrent Instances");
	JLabel lblIterationTarget = new JLabel("Iteration target");
	ImageIcon icon =  new ImageIcon("./gif/Open24.gif");
	JLabel lblImageExePath = new JLabel(icon);
	JLabel lblImageIconFile = new JLabel(icon);	
	JTextField txtName = new JTextField("", 20);
	JTextField txtPath = new JTextField("", 20);
	JTextField txtVendor = new JTextField("", 20);
	JTextField txtVersion = new JTextField("", 20);
	JTextField txtIconFile = new JTextField("", 20);
	JTextField txtType = new JTextField("", 20);
	JTextField txtRequiresGUI = new JTextField("", 20);
	JTextField txtWG23DataModelType = new JTextField("", 20);
	JTextField txtConcurrentInstances = new JTextField("", 20);
	JTextField txtIterationTarget = new JTextField("", 20);
	JButton btnOK = new JButton("OK");
	JPanel panel = new JPanel();
	Application app;
	FileChooser fileChooser = new FileChooser(false);	
	Color xipBtn = new Color(56, 73, 150);
	/**
	 * @param owner
	 */
	public AddApplicationDialog(Frame owner){		
		super(owner, "Add application", true);
		panel.add(lblName);
		panel.add(txtName);
		panel.add(lblPath);
		panel.add(txtPath);
		panel.add(lblImageExePath);
		panel.add(lblVendor);
		panel.add(txtVendor);
		panel.add(lblVersion);
		panel.add(txtVersion);
		panel.add(lblIconFile);
		panel.add(txtIconFile);
		panel.add(lblImageIconFile);
		panel.add(lblType);
		panel.add(txtType);
		panel.add(lblRequiresGUI);
		panel.add(txtRequiresGUI);
		panel.add(lblWG23DataModelType);
		panel.add(txtWG23DataModelType);
		panel.add(lblConcurrentInstances);
		panel.add(txtConcurrentInstances);
		panel.add(lblIterationTarget);
		panel.add(txtIterationTarget);
		panel.add(btnOK);
		lblName.setForeground(Color.WHITE);
		lblPath.setForeground(Color.WHITE);		
		lblVendor.setForeground(Color.WHITE);
		lblVersion.setForeground(Color.WHITE);
		lblIconFile.setForeground(Color.WHITE);	
		lblType.setForeground(Color.WHITE);
		lblRequiresGUI.setForeground(Color.WHITE);
		lblWG23DataModelType.setForeground(Color.WHITE);
		lblConcurrentInstances.setForeground(Color.WHITE);
		lblIterationTarget.setForeground(Color.WHITE);
		panel.setBackground(xipBtn);
		btnOK.setPreferredSize(new Dimension(100, 25));		
		btnOK.addActionListener(this);
		lblImageExePath.setToolTipText("Select path");		
		lblImageExePath.addMouseListener(			
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){						
						fileChooser.displayFileChooser();						
						String path = null;					
						try {
							path = fileChooser.getSelectedFiles()[0].getCanonicalPath();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
						/* It should be just one file for single selection
						 * Provide exception handling
						 * */
						txtPath.setText(path);
						txtPath.setCaretPosition(0);
					}
				}
		);
		lblImageIconFile.setToolTipText("Select path");
		lblImageIconFile.addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){						
						fileChooser.displayFileChooser();						
						String path = null;					
						try {
							path = fileChooser.getSelectedFiles()[0].getCanonicalPath();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
						/* It should be just one file for single selection
						 * Provide exception handling
						 * */
						txtIconFile.setText(path);
						txtIconFile.setCaretPosition(0);
					}
				}
		);
		buildLayout();
		add(panel);
		setResizable(false);								
		//this.setPreferredSize(new Dimension(300, 200));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
		pack();
		setVisible(true);
		addWindowListener (
	            new WindowAdapter(){
	                public void windowClosing(WindowEvent e){	                	
	                	dispose();
	                }
	            }
	        );
	}
	
	public static void main(String[] args) {
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
    	JFrame frame = new JFrame();		
		new AddApplicationDialog(frame);						
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnOK){						
			try{
				app = new Application(txtName.getText(), new File(txtPath.getText()), txtVendor.getText(), txtVersion.getText(), new File(txtIconFile.getText()), 
						txtType.getText(), Boolean.getBoolean(txtRequiresGUI.getText()), txtWG23DataModelType.getText(), 
						Integer.valueOf(txtConcurrentInstances.getText()), IterationTarget.valueOf(txtIterationTarget.getText()));
				ApplicationManager appMgr = ApplicationManagerFactory.getInstance();
				app.setDoSave(true);
				appMgr.addApplication(app);
				
				if(SwingUtilities.isEventDispatchThread()){
					HostMainWindow.getHostIconBar().getApplicationBar().addApplicationIcon(app);
				} else {
					Runnable doWorkRunnable = new Runnable() {
					    public void run() { 
					    	HostMainWindow.getHostIconBar().getApplicationBar().addApplicationIcon(app);
					    }
					};
					SwingUtilities.invokeLater(doWorkRunnable);
				}
			}catch (IllegalArgumentException e1){
				new ExceptionDialog("Cannot create new application.", 
						"Ensure applications parameters are valid.",
						"Add Application Dialog");
				return;
			}			
			dispose();
		}else{
			app = null;
		}
	}
	
	public Application getApplication(){
		return app;
	}

	public void buildLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);        

        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 30;
        constraints.insets.left = 20;
        constraints.insets.right = 15;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblName, constraints);       
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblPath, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblVendor, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblVersion, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblIconFile, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblType, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblRequiresGUI, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblWG23DataModelType, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblConcurrentInstances, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.insets.top = 10;
        constraints.insets.bottom = 20;        
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(lblIterationTarget, constraints); 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 30;
        constraints.insets.left = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtName, constraints);       
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtPath, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtVendor, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtVersion, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtIconFile, constraints);  
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtType, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 6;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtRequiresGUI, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtWG23DataModelType, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 8;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtConcurrentInstances, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 9;
        constraints.insets.top = 10;
        constraints.insets.bottom = 15;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(txtIterationTarget, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 10;      
        constraints.insets.top = 10;
        constraints.insets.bottom = 20;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnOK, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0; 
        constraints.insets.left = 10; 
        constraints.insets.right = 20;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblImageExePath, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.insets.top = 10;
        constraints.insets.bottom = 0; 
        constraints.insets.left = 10; 
        constraints.insets.right = 20;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblImageIconFile, constraints);
	}
	
}
