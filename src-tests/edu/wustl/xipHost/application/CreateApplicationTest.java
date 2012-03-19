package edu.wustl.xipHost.application;

import java.io.File;

import edu.wustl.xipHost.iterator.IterationTarget;

import junit.framework.TestCase;

public class CreateApplicationTest extends TestCase {

	String exePath;
	File iconFile;
	protected void setUp() throws Exception {
		super.setUp();
		exePath = new String("./src-tests/edu/wustl/xipHost/application/test.bat");
		iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png");
	}
	
	//Application 1A - basic flow. All parameters are valid.
	//Result: new application instance
	public void testCreateApplication1A() throws IllegalArgumentException {				
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		//Application's id, name, exePath must be non empty, and application must be an instance of Application
		boolean isAppOK = false;
		if(app instanceof Application && 
				app.getID().toString().isEmpty() == false && 
				app.getName().isEmpty() == false &&
				!app.getVendor().equalsIgnoreCase("null") &&
				!app.getVersion().equalsIgnoreCase("null")){
					isAppOK = true;
		}		
		assertTrue("All parameters were correct but system was unable to create 'Application'.", isAppOK);
	}

	//Application 1Ba - alternative flow. Application name is empty, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Ba() throws IllegalArgumentException {				
			Application  app = new Application("", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
			assertFalse("Application name is empty. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());	
	}
	
	//Application 1Bb - alternative flow. Application name is null, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Bb() {
		Application  app = new Application(null, exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		assertFalse("Application name is NULL. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());				
	}
	
	//Application 1Bc - alternative flow. Application name is an empty character, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Bc() {				
		Application  app = new Application(" ", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		assertFalse("Application name has an empty char. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());				
	}
	
	//Application 1Ca - alternative flow. Application exePath is empty, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Ca() {				
		Application  app = new Application("ApplicationTest", "", "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		assertFalse("Application exePath is empty. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());				
	}
	
	//Application 1Cb - alternative flow. Application exePath is null, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Cb() {				
		Application app = new Application("ApplicationTest", null, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		assertFalse("Application exePath is NULL. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());							
	}
	
	//Application 1Cc - alternative flow. Application exePath contains illegal character, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Cc() {				
		Application app = new Application("ApplicationTest", "./src-tests/edu/wustl/xipHost/application/t?est.bat", "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		assertFalse("Application exePath contains illegal char. Application should be set to isValid = false, but is: isValid = " + app.isValid(), app.isValid());		
	}
	
	//Application 1Da - alternative flow. Application iconFile is null other parameters are valid.
	//Result: new application instance is created with iconFile null (so not icon used for display)
	public void testCreateApplication1Da() {				
		Application app = new Application("Application1", exePath, "", "", null, "rendering", true, "files", 1, IterationTarget.SERIES);	
		assertTrue("Error when creating Application with null iconFile.", app.isValid());		
	}
	
	//Application 1Db - alternative flow. Application iconFile does not exist other parameters are valid.
	//Result: new application instance is created with iconFile null (so not icon used for display)
	public void testCreateApplication1Db() {				
		iconFile = new File("src-tests/edu/wustl/xipHost/application/testNoExisting.png");
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);	
		assertTrue("Error when creating Application with null iconFile.", app.isValid());		
	}
	
	//Application 1E - alternative flow. Application attribute concurrentInstances is set to 0.
	//Result: new application instance should be created but isValid shoul dbe set to false.
	public void testCreateApplication1E() {				
		iconFile = new File("src-tests/edu/wustl/xipHost/application/testNoExisting.png");
		Application app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 0, IterationTarget.SERIES);	
		assertFalse("Error when creating Application with concurrent instances attribute set to 0.", app.isValid());		
	}
}
