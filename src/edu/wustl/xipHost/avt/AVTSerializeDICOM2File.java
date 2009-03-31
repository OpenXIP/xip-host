/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;

import com.siemens.scr.avt.ad.io.DicomIO;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTSerializeDICOM2File implements Runnable {
	String dicomUID;
	String fileName;
	
	public AVTSerializeDICOM2File(String dicomUID, String fileName){
		this.dicomUID = dicomUID;
		this.fileName = fileName;
	}
	
	@Override
	public void run() {
		try {
			DicomIO.dumpDicom2File(dicomUID, fileName);
			File file = new File(fileName);
			notifyDICOMAvailable(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	void notifyDICOMAvailable(File file){
		try {
			System.out.println(file.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
