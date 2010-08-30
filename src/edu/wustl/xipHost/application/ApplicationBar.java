/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationBar extends JPanel implements ActionListener {
	final static Logger logger = Logger.getLogger(ApplicationBar.class);
	
	public ApplicationBar() {
		setLayout(new FlowLayout(FlowLayout.LEADING));
	}

	public void setApplications(List<Application> applications){	
		removeAll();
		for(int i = 0; i < applications.size(); i++){
			Application app = applications.get(i);			
			addApplicationIcon(app);
		}
	}
	
	public void addApplicationIcon(Application app){
		ImageIcon iconFile;
		if(app.getIconFile() == null){
			iconFile = null;
		}else{
			iconFile = new ImageIcon(app.getIconFile().getPath());
		}
		final AppButton btn = new AppButton(app.getName(), iconFile);
		btn.setPreferredSize(new Dimension(150, 25));
		//btn.setOpaque(true);			
		btn.setApplicationUUID(app.getID());
		btn.setForeground(Color.BLACK);			
		btn.addActionListener(this);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Runnable doWorkRunnable = new Runnable() {
		    public void run() { 
		    	add(btn);
		    	repaint();
		    }
		};
		SwingUtilities.invokeLater(doWorkRunnable);
	}
	
	public class AppButton extends JButton{		
		public AppButton(String text, Icon icon){
			super(text, icon);
		}
		
		UUID appUUID;
		public void setApplicationUUID(UUID uuid){
			appUUID = uuid;
		}
		public UUID getApplicationUUID(){
			return appUUID;
		}
	}

	
	public static void main(String[] args) {
		List<Application> applications = new ArrayList<Application>();
		File exePath = new File("./src-tests/edu/wustl/xipHost/application/test.bat");
		Application test1 = new Application("Test1", exePath, "WashU", "1.0", null, "analytical", true, "files", 1, IterationTarget.SERIES);
		Application test2 = new Application("Test2", exePath, "WashU", "1.0", null, "analytical", true, "files", 1, IterationTarget.SERIES);
		Application test3 = new Application("Test3", exePath, "WashU", "1.0", null, "analytical", true, "files", 1, IterationTarget.SERIES);
		applications.add(test1);
		applications.add(test2);
		applications.add(test3);
		JFrame frame = new JFrame();			
		ApplicationBar panel = new ApplicationBar();
		panel.setApplications(applications);
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getPreferredSize();
        frame.setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
		frame.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//select current tab
		//check if selectedDataSearchresult is not null
		//check if DICOM and DICOM and SEG are selected.
		//setSelectedDataSearchResult on application
		
		AppButton btn = (AppButton)e.getSource();
		fireLaunchApplication(btn);
	}
	
	
	ApplicationListener listener;
	public void addApplicationListener(ApplicationListener l){
		listener = l;
	}
	
	void fireLaunchApplication(AppButton btn){
		ApplicationEvent event = new ApplicationEvent(btn);
		listener.launchApplication(event);
	}
}
