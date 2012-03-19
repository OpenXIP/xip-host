package edu.wustl.xipHost.application;

import java.io.File;
import java.util.UUID;
import edu.wustl.xipHost.iterator.IterationTarget;
import junit.framework.TestCase;

public class ModifyApplicationTest extends TestCase {
	ApplicationManager mgr;
	String exePath;
	File iconFile;
	protected void setUp() throws Exception {
		super.setUp();
		mgr = ApplicationManagerFactory.getInstance();
		exePath = new String("./src-tests/edu/wustl/xipHost/application/test.bat");
		iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png");
	}
	protected void tearDown() throws Exception {
		super.tearDown();
		mgr.getApplications().removeAll(mgr.getApplications());			
	}
	
	//ApplicationManager 1A - basic flow. Application UUID found, new parameters are correct.
	public void testModifyApplication1A() {						
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		Application modifiedApp = new Application("Modified", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		UUID uuid = app.getID();
		mgr.addApplication(app);
		assertTrue("UUID and modified parameters were correct, but system was unable to modify application.", mgr.modifyApplication(uuid, modifiedApp));
		//verify modification
		//just one application
		//new name, but the rest is the same
		assertTrue(mgr.getNumberOfApplications() == 1 && 
				mgr.getApplication(uuid).getName().equalsIgnoreCase("Modified") &&
				mgr.getApplication(uuid).getExePath().equals(modifiedApp.getExePath()) &&
				mgr.getApplication(uuid).getVendor().equalsIgnoreCase("") &&
				mgr.getApplication(uuid).getVersion().equalsIgnoreCase(""));
	}

	//ApplicationManager 1B - alternative flow. UUID not found, new parameters are correct.
	public void testModifyApplication1B() {						
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		Application modifiedApp = new Application("Modified", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);		
		mgr.addApplication(app);
		assertFalse("UUID not  found, but system modified application.", mgr.modifyApplication(UUID.randomUUID(), modifiedApp));		
	}
	
	//ApplicationManager 1C - alternative flow. UUID found, but new parameters are incorrect.
	//testing setters
	public void testModifyApplication1C() {						
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);		
		app.setExePath(null);
		assertFalse("Application exePath set to NULL. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());
	}
	
	//ApplicationManager 1E - alternative flow. UUID is null, new parameters are correct 
	public void testModifyApplication1E() {						
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);		
		Application modifiedApp = new Application("Modified", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		UUID uuid = null;
		mgr.addApplication(app);		
		assertFalse("UUID was null but system modified application.", mgr.modifyApplication(uuid, modifiedApp));				
	}
	
}
