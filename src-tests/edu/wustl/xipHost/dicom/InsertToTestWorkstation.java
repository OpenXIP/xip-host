/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JFrame;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class InsertToTestWorkstation {

	public InsertToTestWorkstation() {
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fileChooser.showOpenDialog(frame);	
		File[] files = fileChooser.getSelectedFiles();
		if(files == null){
			return;
		}		
		PacsLocation loc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "XIPHost test database");
		DicomManager dicomMgr = DicomManagerFactory.getInstance();
		Properties workstation2Prop = new Properties();
		try {
			workstation2Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"));
			workstation2Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION2").getCanonicalPath());	
		} catch (FileNotFoundException e1) {
			System.err.println(e1.getMessage());
			System.exit(0);
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			System.exit(0);
		}
		dicomMgr.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest", workstation2Prop);
		dicomMgr.submit(files, loc);
	}

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		new InsertToTestWorkstation();				
		System.exit(0);

	}
}
