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
import edu.wustl.xipHost.iterator.IterationTarget;
import junit.framework.TestCase;

public class AddApplicationTest extends TestCase {
	ApplicationManager mgr;
	String exePath;
	String iconFile;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
		exePath = "./src-tests/edu/wustl/xipHost/application/test.bat";
		iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png").getAbsolutePath();
	}
	protected void tearDown() throws Exception {
		super.tearDown();	
		mgr.getApplications().removeAll(mgr.getApplications());		
	}
	
	//ApplicationManager 1A - basic flow. Application parameters are correct.
	public void testAddApplication1A() {						
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		assertTrue("Application parameters were correct but system failed to add application.", mgr.addApplication(app));
		int numOfValidApps = mgr.getApplications().size();
		assertEquals("All Application paremeters were valid but system failed to add application to the list of valid applications.", 1, numOfValidApps);
	}
	
	//ApplicationManager 1B - alternative flow. Application with the same name is already on the list.
	//Result: system should add new application. ID is used to identify applications.
	public void testAddApplication1B() {						
		Application app1 = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		Application app2 = new Application("Application2", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		mgr.addApplication(app1);
		assertTrue(mgr.addApplication(app2));
		int numOfValidApps = mgr.getApplications().size();
		assertEquals("Applications paremeters were valid but system failed to add applications to the list of valid applications.", 2, numOfValidApps);
	}
}
