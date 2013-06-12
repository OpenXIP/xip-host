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
import java.util.UUID;
import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.iterator.IterationTarget;
import junit.framework.TestCase;

public class RemoveApplicationTest extends TestCase {
	ApplicationManager mgr;
	Application app;
	String exePath;
	String iconFile;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
		exePath = new String("./src-tests/edu/wustl/xipHost/application/test.bat");
		iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png").getAbsolutePath();
	}
	
	protected void tearDown() throws Exception {
		
	}

	//ApplicationManager 1A - basic flow. Application UUID found, State = null.
	public void testRemoveApplication1A() {						
		app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);		
		UUID uuid = app.getID();
		mgr.addApplication(app);		
		boolean btnRemove = mgr.removeApplication(uuid);
		assertTrue("UUID and State were correct, but system was unable to remove application.", btnRemove);
		//verify removal
		//Number of application must be zero
		assertTrue("All parameters were correct but system did not remove application.", mgr.getNumberOfApplications() == 0);
	}
	
	//ApplicationManager 1Ba - alternative flow. Application UUID not found, State = null.
	public void testRemoveApplication1Ba() {
		app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);				
		mgr.addApplication(app);		
		boolean btnRemove = mgr.removeApplication(UUID.randomUUID());
		assertFalse("UUID not found but system did not report problem.", btnRemove);		
	}

	//ApplicationManager 1Bb - alternative flow. Application UUID is null, State = null.
	public void testRemoveApplication1Bb() {
		app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);				
		mgr.addApplication(app);		
		boolean btnRemove = mgr.removeApplication(null);
		assertFalse("UUID is null but system did not report problem.", btnRemove);		
	}
}
