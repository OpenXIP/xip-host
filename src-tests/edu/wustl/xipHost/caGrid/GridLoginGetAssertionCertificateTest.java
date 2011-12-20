/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridLoginGetAssertionCertificateTest {
	static GridLogin gridLogin;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 gridLogin = new GridLogin();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUpBeforeTest() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDownAfterTest() throws Exception {
	}

	//Class under test: GridLogin - basic flow. User name and password, are valid. Connection get be established.
	//Result: assertion certificate.
	@Test
	public void testGetAssertionCertificate() {
		String userName = "";
		String password = "";
		gridLogin.getAssertionCertificate(userName, password);
		//Depending what getAssertionCertificate should return, assert the result.
		//If return object is xml file, check if it is not null, or some attributes of XML.
		
	}

}
