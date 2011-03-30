/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hsqldb.Server;

import com.pixelmed.database.DatabaseInformationModel;
import com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.server.DicomAndWebStorageServer;

import edu.wustl.xipHost.dicom.server.Workstation2;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class InsertToTestWorkstation {
	DatabaseInformationModel dbModel;
	public InsertToTestWorkstation() {
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		fileChooser.setVisible(true);
		File[] files = fileChooser.getSelectedItems();
		if(files == null){
			return;
		}		
		PacsLocation loc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "XIPHost test database");
		Workstation2.startHSQLDB();
		Workstation2.startPixelmedServer();	
		dbModel = Workstation2.getDBModel();
		submit(files, loc);
	}

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		new InsertToTestWorkstation();				
		System.exit(0);

	}
	
	BasicDicomParser2 parser = new BasicDicomParser2();
	public boolean submit(File[] dicomFiles, PacsLocation location) {		
		for(int i = 0; i < dicomFiles.length; i++){
			AttributeList attList = parser.parse(dicomFiles[i]);					
			try {
				dbModel.insertObject(attList, dicomFiles[i].getCanonicalPath());
			} catch (DicomException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}		
		return true;
	}

}
