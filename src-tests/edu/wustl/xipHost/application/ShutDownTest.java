/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
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
		app = new ApplicationMock("Application1", new String("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
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
