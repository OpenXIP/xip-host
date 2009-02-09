/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import com.siemens.scr.avt.ad.connector.api.AIMDataService;
import com.siemens.scr.avt.ad.connector.jdbc.AIMDataServiceImp;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class EstablishConnectionWithADTest extends TestCase {
	AIMDataService aimDataService;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//AVTFactory 1A - basic flow. 
	//Expected result:
	public void testEstablishConnectionWithADToAD_1A() {
		 aimDataService = new AIMDataServiceImp("127.0.0.1", "50000", "AD", "db2user", "123");
		 fail("Not implemented yet");
	}
}
