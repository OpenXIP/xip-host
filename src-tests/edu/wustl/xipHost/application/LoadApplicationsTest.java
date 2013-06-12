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

import java.io.File;
import java.io.IOException;
import org.jdom.JDOMException;
import junit.framework.TestCase;

public class LoadApplicationsTest extends TestCase {
	ApplicationManager mgr;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		mgr.getApplications().removeAll(mgr.getApplications());			
	}
	
	//ApplicationManager 1A - Basic flow: file exists, XML is valid (JDOM can be created), XML contains valid data
	public void testLoadApplications1A() throws JDOMException, IOException {
		File file = new File("./src-tests/edu/wustl/xipHost/application/applicationsTest1A.xml");
		boolean blnLoad = mgr.loadApplications(file);
		assertTrue("Perfect conditions though applications not loaded.", blnLoad);		
		assertTrue("Number of added applications is different than 5.", mgr.getNumberOfApplications() == 5);
	}

	//ApplicationManager 1Ba - Alternative flow: file is null
	public void testLoadApplications1Ba() throws JDOMException, IOException {
		File file = null;
		boolean blnLoad = mgr.loadApplications(file);
		assertFalse("File was null but system was able to load applications.", blnLoad);		
	}
	
	//ApplicationManager 1Bb - Alternative flow: file does not exist
	public void testLoadApplications1Bb() throws JDOMException, IOException {
		File file = new File("./src-tests/edu/wustl/xipHost/application/fileDoesNotExist.xml");
		boolean blnLoad = mgr.loadApplications(file);
		assertFalse("File does not exist but system was able to load applications.", blnLoad);		
	}
	
	//ApplicationManager 1Ca - Alternative flow: xml file does not contain enclosing tag '/'
	public void testLoadApplications1Ca() throws JDOMException, IOException {
		File file = new File("./src-tests/edu/wustl/xipHost/application/applicationsTest1Ca.xml");
		try{
			mgr.loadApplications(file);
			fail("XML file is not valid.");
		}catch (JDOMException e){
			assertTrue(true);
		}						
	}
	
	//ApplicationManager 1Cb - Alternative flow: xml file does not contain one of the xml elements to form valid Application
	//Expected result: applications added to not valid application List
	public void testLoadApplications1Cb() throws JDOMException, IOException {
		File file = new File("./src-tests/edu/wustl/xipHost/application/applicationsTest1Cb.xml");
		//when xml is incomplete system should create not valid Application and add it to the List of notValidApplications
		assertTrue("System failed to load applications", mgr.loadApplications(file));
		//assertEquals("Number of valid applications should be 1, but it is: " + mgr.getApplications().size(), mgr.getApplications(), 1);
		int numOfNotValidApplications = mgr.getNotValidApplications().size();
		assertEquals("Number of not valid applications should be 2, but it is: " + numOfNotValidApplications, numOfNotValidApplications, 2);
	}
	
	//ApplicationManager 1D - Alternative flow: xml file is valid but does not contain any applications
	public void testLoadApplications1D() throws JDOMException, IOException {
		File file = new File("./src-tests/edu/wustl/xipHost/application/applicationsTest1D.xml");
		//wheb xml is incomplete system should catch IllegalArgumentException and ignore the application
		//for which exception was caught
		assertTrue("System failed to load zero applications", mgr.loadApplications(file));		
	}
	
}
