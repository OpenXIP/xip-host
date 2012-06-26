/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Jarek Krych
 *
 */
public class DicomServerStartup implements Runnable {
	DicomManager dicomMgr;
	Properties workstation1Prop = new Properties();
	/**
	 * 
	 */
	public DicomServerStartup() {
		dicomMgr = DicomManagerFactory.getInstance();
		
		try {
			workstation1Prop.load(new FileInputStream("./pixelmed-server-hsqldb/workstation1.properties"));
		} catch (FileNotFoundException e1) {
			//logger.error(e1, e1);
			
		} catch (IOException e1) {
			//logger.error(e1, e1);
			
		}
	}

	@Override
	public void run() {
		dicomMgr.runDicomStartupSequence("./pixelmed-server-hsqldb/server", workstation1Prop);
		DicomServerStartupEvent event = new DicomServerStartupEvent(this);
		listener.dicomServerOn(event);
		
	}
	
	DicomServerStartupListener listener;
	public void addDicomServerStartupListener(DicomServerStartupListener listener) {
		this.listener = listener;
	}
	
	

}
