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
public class ADInsertData {
	AIMDataService aimDataService = new AIMDataServiceImp("127.0.0.1", "50000", "AD", "db2", "wustl");
	String[] strAIMs = new String[4];
	public ADInsertData(){
		
		aimDataService.storeImageAnnotations(strAIMs);
	}
	
	public static void main(String[] args) {
		new ADInsertData();
	}

}
