/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.hsqldb.Server;

import com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.server.DicomAndWebStorageServer;

/**
 * <font  face="Tahoma" size="2">
 * Starts test HSQLDB and Pixelmed servers<br></br> 
 * @version	August 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class Workstation2 {		
		
	public void run(){
		startHSQLDB();		
		startPixelmedServer();
	}
	
	static Server hsqldbServer;
	public static void startHSQLDB() {
		hsqldbServer = new Server();
		hsqldbServer.putPropertiesFromFile("./src-tests/edu/wustl/xipHost/dicom/server/serverTest");
		hsqldbServer.start();
	}
	
	static DicomAndWebStorageServer server;
	static Properties prop = new Properties();
	public static void startPixelmedServer(){		
		try {
			try{
				prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"));
				prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION2").getCanonicalPath());					
				prop.store(new FileOutputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"), "Updated Application.SavedImagesFolderName");			
			} catch (FileNotFoundException e1) {
				System.exit(0);
			} catch (IOException e1) {
				System.exit(0);
			}
			server = new DicomAndWebStorageServer(prop);			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	public static boolean isRunning(){		
		if(hsqldbServer == null){
			return false;
		}else{
			int i = hsqldbServer.getState();
			//String str = hsqldbServer.getStateDescriptor();
			//System.out.println("State id: " + i + " Desc: " + str);				
			return (i == 1);
		}		
	}
	
	public static void stopHSQLDB(){				
		hsqldbServer.shutdown();
	}
	
	public static PatientStudySeriesConcatenationInstanceModel getDBModel(){
		
		try {
			return new PatientStudySeriesConcatenationInstanceModel(prop.getProperty("Application.DatabaseFileName"));
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		 
	}
	
	public static void main(String[] args) {		
		startHSQLDB();
		startPixelmedServer();		
	}	
}
