package edu.wustl.xipHost.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import edu.wustl.xipHost.iterator.IterationTarget;

public class StoreApplicationsTest {
	ApplicationManager mgr;
	Application app1;
	Application app2;
	
	@Before
	public void setUp() throws Exception {
		mgr = ApplicationManagerFactory.getInstance();
		app1 = new Application("Application1", new String("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
		app2 = new Application("Application2", new String("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
	}
	
	@After
	public void tearDown() throws Exception {
		mgr.getApplications().clear();
	}

	//ApplicationManager 1A - basic flow. Application list is not empty, output file exists.
	@Test
	public void testStoreApplications1A() {										
		mgr.addApplication(app1);
		mgr.addApplication(app2);
		File file = new File("./src-tests/edu/wustl/xipHost/application/storedTest1A.xml");
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), file);
		assertTrue("Parameters are valid, but system was unable to store applications.", blnStore);		
	}
	
	//ApplicationManager 1B - alternative flow. Application list is empty, output file exists.
	@Test
	public void testStoreApplications1B() {										
		mgr = ApplicationManagerFactory.getInstance();
		mgr.getApplications().clear();
		File file = new File("./src-tests/edu/wustl/xipHost/application/storedTest1B.xml");
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), file);
		assertTrue("Parameters are valid (list is empty), but system was unable to store applications.", blnStore);		
	}
	
	//ApplicationManager 1C - alternative flow. Application list is not empty, 
	//directory of the output file does not exist.
	@Test
	public void testStoreApplications1C() {	
		mgr.addApplication(app1);
		mgr.addApplication(app2);
		File file = new File("./src-tests/edu/wustl/xipHost/application/storedTest1C.xml");
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), file);
		assertTrue("Output dir does not exists, but system was able to store applications.", blnStore);		
	}
	
	//ApplicationManager 1D - alternative flow. Application list is not empty, 
	//directory of the output file is null.
	@Test
	public void testStoreApplications1D() {														
		mgr.addApplication(app1);
		mgr.addApplication(app2);
		boolean blnStore = mgr.storeApplications(mgr.getApplications(), null);
		assertFalse("Application list is null, but system was able to store applications.", blnStore);		
	}
	
	//ApplicationManager 1D - alternative flow. Application parameters are not valid, 
	//Expected result: application should be stored in configuration file
	@Test
	public void testStoreApplications1E() {														
		Application  app = new Application(" ", null, "", "", null, "rendering", true, "files", 1, IterationTarget.SERIES);
		mgr.getNotValidApplications().add(app);
		File file = new File("./src-tests/edu/wustl/xipHost/application/storedTest1E.xml");
		boolean blnStore = mgr.storeApplications(mgr.getNotValidApplications(), file);
		assertTrue("System was unable to store application with null values of its parameters.", blnStore);		
	}
}
