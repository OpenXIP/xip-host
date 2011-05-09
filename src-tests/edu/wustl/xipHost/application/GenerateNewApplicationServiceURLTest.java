/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class GenerateNewApplicationServiceURLTest extends TestCase {	
	ApplicationManager mgr;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
	}
	//ApplicationManager 1A - basic flow. Port is available. 
	//Result: valid URL
	public void testGenerateNewApplicationServiceURL1A() throws IOException{				
		int port = 8060;		
		while(mgr.checkPort(port) == false){
			port++;			 			
		}
		assertTrue(mgr.checkPort(port));
		URL url = mgr.generateNewApplicationServiceURL();		
		assertEquals("", "http://localhost:" + port + "/ApplicationInterface?wsdl", url.toExternalForm());
	}
	
	//ApplicationManager 1B - alternative flow. Port is unavailable.
	//Result: valid URL
	public void testGenerateNewApplicationServiceURL1B() throws IOException{				
		int port = 8060;
		while(mgr.checkPort(port) == false){
			port++;			 			
		}
		ServerSocket sock = new ServerSocket(port);
		URL url = mgr.generateNewApplicationServiceURL();
		System.out.println(url.toExternalForm());
		assertNotNull(url);
		sock.close();
	}
}
