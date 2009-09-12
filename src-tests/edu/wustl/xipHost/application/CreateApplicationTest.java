package edu.wustl.xipHost.application;

import java.io.File;

import junit.framework.TestCase;

public class CreateApplicationTest extends TestCase {

	File exePath;
	File iconFile;
	protected void setUp() throws Exception {
		super.setUp();
		exePath = new File("./src-tests/edu/wustl/xipHost/application/test.bat");
		iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png");
	}
	
	//Application 1A - basic flow. All parameters are valid.
	//Result: new application instance
	public void testCreateApplication1A() throws IllegalArgumentException {				
		Application app = new Application("Application1", exePath, "", "", iconFile);
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
		try{
			new Application("", exePath, "", "", iconFile);
			fail("Application name si empty");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//Application 1Bb - alternative flow. Application name is null, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Bb() {				
		try{
			new Application(null, exePath, "", "", iconFile);
			fail("Application name si empty");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//Application 1Bc - alternative flow. Application name is an empty character, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Bc() {				
		try{
			new Application(" ", exePath, "", "", iconFile);
			fail("Application name is an empty character");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//Application 1Ca - alternative flow. Application exePath is empty, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Ca() {				
		try{
			new Application("ApplicationTest", new File(""), "", "", iconFile);
			fail("Application exePath is an empty character");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//Application 1Cb - alternative flow. Application exePath is null, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Cb() {				
		try{
			new Application("ApplicationTest", null, "", "", iconFile);
			fail("Application exePath is null");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//Application 1Cc - alternative flow. Application exePath.getName contains illegal character, other parameters are valid.
	//Result: throws IllegalArgumentException
	public void testCreateApplication1Cc() {				
		try{
			new Application("ApplicationTest", new File("./src-tests/edu/wustl/xipHost/application/t?est.bat"), "", "", iconFile);
			fail("Application exePath name contains illegal characters");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//Application 1Da - alternative flow. Application iconFile is null other parameters are valid.
	//Result: new application instance is created with iconFile null (so not icon used for display)
	public void testCreateApplication1Da() {				
		Application app = new Application("Application1", exePath, "", "", null);
		//Application's iconFile should be set to applications name
		boolean isAppOK = false;
		if(app instanceof Application && 
				app.getIconFile() == null){
					isAppOK = true;
		}		
		assertTrue("Error when creating Application with null iconFile.", isAppOK);		
	}
	
	//Application 1Db - alternative flow. Application iconFile does not exist other parameters are valid.
	//Result: new application instance is created with iconFile null (so not icon used for display)
	public void testCreateApplication1Db() {				
		iconFile = new File("src-tests/edu/wustl/xipHost/application/testNoExisting.png");
		Application app = new Application("Application1", exePath, "", "", iconFile);
		//Application's iconFile should be set to applications name
		boolean isAppOK = false;
		if(app instanceof Application && 
				app.getIconFile() == null){
					isAppOK = true;
		}		
		assertTrue("Error when creating Application with null iconFile.", isAppOK);		
		
	}
}
