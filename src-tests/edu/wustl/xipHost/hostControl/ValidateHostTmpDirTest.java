/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class ValidateHostTmpDirTest extends TestCase {	
	File hostTmpDir;
	protected void setUp() throws Exception {
		super.setUp();		 		
		hostTmpDir = new File("./test-content/TmpXIP23699.tmp");
		if(hostTmpDir.exists() == false){
			hostTmpDir.mkdir();
		}
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		//hostTmpDir.delete();
	}
	
	//Util 1A - basic flow. Parent of hostTmpDir exists and hostTmpDir conforms to prefix=xip, suffix=.tmp format.
	//Result: boolean true
	public void testValidateHostTmpDir1A() {		
		assertTrue("Perfect conditions but host was unable to validate dir.", Util.validateHostTmpDir(hostTmpDir));		
	}
	
	//Util 1B - alternative flow. Parent of hostTmpDir is null.
	//Result: boolean false
	public void testValidateHostTmpDir1B() {				
		assertFalse("parentOfTmpDir is null but Host validated dir.", Util.validateHostTmpDir(null));		
	}
	
	//Util 1C - alternative flow. Parent of hostTmpDir exists but hostTmpDir does not conform to prefix=xip, suffix=.tmp format.
	//Result: boolean false
	public void testValidateHostTmpDir1C() {				
		hostTmpDir = new File("./test-content/TestNonConformant/_TmpXIP23699.tmp");
		if(hostTmpDir.exists() == false){
			hostTmpDir.mkdirs();
		}
		assertFalse("", Util.validateHostTmpDir(hostTmpDir));		
	}
	
}
