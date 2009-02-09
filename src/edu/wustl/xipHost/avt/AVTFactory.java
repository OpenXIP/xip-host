/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import com.siemens.scr.avt.ad.connector.api.AIMDataService;
import com.siemens.scr.avt.ad.connector.jdbc.AIMDataServiceImp;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTFactory {
	private static AVTManager avtMgr = new AVTManagerImpl();
	private static AIMDataService aimDataService;				
	
	private AVTFactory(){};
	
	public static AIMDataService getAIMDataServiceInstance(){
		String serverName = avtMgr.getServerName();
		String serverPort = avtMgr.getServerPort();
		String dbName = avtMgr.getDatabaseName();
		String userName = avtMgr.getUserName();
		String password = avtMgr.getPassword();
		//aimDataService = new AIMDataServiceImp("127.0.0.1", "50000", "AD", "db2user", "123");
		aimDataService = new AIMDataServiceImp(serverName, serverPort, dbName, userName, password);
		return aimDataService;
	}
	
}
