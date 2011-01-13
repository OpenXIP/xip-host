/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.hsqldb.Server;
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
	
	Server hsqldbServer;
	private void startHSQLDB() {
		hsqldbServer = new Server();
		hsqldbServer.putPropertiesFromFile("./pixelmed-server-hsqldb/testServer");
		hsqldbServer.start();
	}
	
	DicomAndWebStorageServer server;
	Properties prop = new Properties();
	private void startPixelmedServer(){
		
		try {
			prop.load(new FileInputStream("./pixelmed-server-hsqldb/workstation2.properties"));
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
	
	public boolean isRunning(){		
		if(hsqldbServer == null){
			return false;
		}else{
			int i = hsqldbServer.getState();
			//String str = hsqldbServer.getStateDescriptor();
			//System.out.println("State id: " + i + " Desc: " + str);				
			return (i == 1);
		}		
	}
	
	public void shutDown(){
		//hsqldbServer.stop();
		hsqldbServer.shutdown();
		
	}
	
	
	public static void main(String[] args) {
		Workstation2 ws2 = new Workstation2();
		ws2.run();
		ws2.isRunning();
	}

}
