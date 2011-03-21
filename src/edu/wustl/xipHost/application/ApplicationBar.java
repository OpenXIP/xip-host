/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.HostConfigurator;
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
		//ImageIcon imageIcon = new ImageIcon("./gif/app2-32x32.png");
		//JLabel label = new JLabel(imageIcon);
		final AppButton btn = new AppButton(app.getName(), iconFile);
		double preferredWidth = btn.getPreferredSize().getWidth();
		if(preferredWidth < 100){
			btn.setPreferredSize(new Dimension(100, 25));
		} else {
			btn.setPreferredSize(new Dimension((int)preferredWidth, 25));
		}
		btn.setApplicationUUID(app.getID());
		btn.setForeground(Color.BLACK);			
		btn.addActionListener(this);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(btn);
		//add(label);
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
		AppButton btn = (AppButton)e.getSource();
		fireLaunchApplication(btn);
	}
	
	
	List<ApplicationListener> listenerList = new ArrayList<ApplicationListener>();
	public void addApplicationListener(ApplicationListener l){
		listenerList.add(l);
	}
	
	void fireLaunchApplication(AppButton btn){
		ApplicationEvent event = new ApplicationEvent(btn);
		Component inputDialog = HostConfigurator.getHostConfigurator().getMainWindow().getInputDialog();  
		if( inputDialog != null){
			for (ApplicationListener listener : listenerList){
				// Only launch applications for the currently active (selected, visible) search tab
				if (inputDialog == listener){
					listener.launchApplication(event);
				}
			}
		} else {
			Component component = HostConfigurator.getHostConfigurator().getMainWindow().getSelectedSearchTab();	
			for (ApplicationListener listener : listenerList){
				// Only launch applications for the currently active (selected, visible) search tab
				if (component == listener){
					listener.launchApplication(event);
				}
			}
		}
	}
}
