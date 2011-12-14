/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import javax.swing.JFrame;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class PreloadDICOMtoDICOMDataSource {

	public static void main(String[] args) {
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		JFrame frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fileChooser.showOpenDialog(frame);	
		File[] files = fileChooser.getSelectedFiles();
		if(files == null){
			return;
		}		
		PacsLocation loc = new PacsLocation("127.0.0.1", 3001, "WORKSTATION1", "XIPHost embedded database");
		//PacsLocation loc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "XIPHost test database");
		DicomManager dicomMgr = DicomManagerFactory.getInstance();
		dicomMgr.runDicomStartupSequence();
		dicomMgr.submit(files, loc);
		dicomMgr.runDicomShutDownSequence();
		System.exit(0);
	}
}
