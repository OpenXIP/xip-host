/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.IOException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class GenerateNewApplicationServiceURLTest extends TestCase {	
	ApplicationManager mgr;
	
	@BeforeClass
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
	}
	
	@AfterClass
	protected void tearDown() throws Exception {
		super.tearDown();	
		//TODO Close socket
		//get ServerSocket then close()
	}
	
	//ApplicationManager 1A - basic flow. Port is automatically chosen, first open/available. 
	//Result: valid URL
	@Test
	public void testGenerateNewApplicationServiceURL1A() throws IOException{				
		URL url = mgr.generateNewApplicationServiceURL();
		String protocol = url.getProtocol();
		String host = url.getHost();
		String path = url.getPath();
		assertEquals("Expected protocol is http. Actual protocol is " + protocol, "http", protocol);
		assertEquals("Expected host is localhost. Actual host is " + host, "localhost", host);
		assertEquals("Expected path is /ApplicationInterface. Actual path is " + path, "/ApplicationInterface", path);
	}
}
