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

import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.iterator.IterationTarget;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class ShutDownTest extends TestCase {
	Application app;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		app = new ApplicationMock("Application1", new String("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", "src-tests/edu/wustl/xipHost/application/test.png",
				"rendering", true, "files", 1, IterationTarget.SERIES);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//Application 1A - basic flow. Application State is IDLE.
	//Result: boolean true
	public void testShutDownApplication1A() {
		app.setState(State.IDLE);
		assertTrue("", app.shutDown());		
	}
	
	//Application 1B - alternative flow. Application State is not IDLE.
	//and cannot be brought to IDLE
	//Result: boolean true
	public void testShutDownApplication1B() {
		app.setState(State.COMPLETED);
		assertFalse("", app.shutDown());		
	}
	
	//Application 1C - alternative flow. Application State is not IDLE.
	//but it can be brought to IDLE
	//Result: boolean true
	public void testShutDownApplication1C() {
		app.setState(State.INPROGRESS);
		assertTrue("", app.shutDown());		
	}	
}
