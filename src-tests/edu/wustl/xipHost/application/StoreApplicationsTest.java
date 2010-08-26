package edu.wustl.xipHost.application;

import java.io.File;

import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import junit.framework.TestCase;

public class StoreApplicationsTest extends TestCase {
	ApplicationManager mgr;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
		Application app1 = new Application("Application1", new File("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
		Application app2 = new Application("Application2", new File("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
		mgr.addApplication(app1);
		mgr.addApplication(app2);
	}

	//ApplicationManager 1A - basic flow. Application list is not empty, output file exists.
	public void testStoreApplications1A() {										
		File file = new File("./src-tests/edu/wustl/xipHost/application/storedTest1A.xml");
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), file);
		assertTrue("Parameters are valid, but system was unable to store applications.", blnStore);		
	}
	
	//ApplicationManager 1B - alternative flow. Application list is empty, output file exists.
	public void testStoreApplications1B() {										
		mgr = ApplicationManagerFactory.getInstance();
		File file = new File("./src-tests/edu/wustl/xipHost/application/storedTest1B.xml");
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), file);
		assertTrue("Parameters are valid (list is empty), but system was unable to store applications.", blnStore);		
	}
	
	//ApplicationManager 1C - alternative flow. Application list is not empty, 
	//directory of the output file does not exist.
	public void testStoreApplications1C() {												
		File file = new File("./src-tests/edu/wustl/xipHost/applicationTest/storedTest1C.xml");
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), file);
		assertFalse("Output dir does not exists, but system was able to store applications.", blnStore);		
	}
	
	//ApplicationManager 1D - alternative flow. Application list is not empty, 
	//directory of the output file is null.
	public void testStoreApplications1D() {														
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), null);
		assertFalse("Application list is null, but system was able to store applications.", blnStore);		
	}
}
