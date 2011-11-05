/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FilenameFilter;

import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class InsertDICOM {

	public static void main(String[] args) {
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		fileChooser.setVisible(true);
		//File[] files = fileChooser.getSelectedItems();
		File[] files = new File("./dicom-dataset-demo").listFiles(filter);
		if(files == null){
			return;
		}		
		PacsLocation loc = new PacsLocation("127.0.0.1", 3001, "WORKSTATION1", "XIPHost embedded database");
		//PacsLocation loc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "XIPHost test database");
		DicomManager dicomMgr = DicomManagerFactory.getInstance();
		dicomMgr.runDicomStartupSequence();
		DicomManagerFactory.getInstance().submit(files, loc);		
		System.exit(0);
	}
	
	static FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	        return !name.startsWith(".svn");
	    }
	};
}
