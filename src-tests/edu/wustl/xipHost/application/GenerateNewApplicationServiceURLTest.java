/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
