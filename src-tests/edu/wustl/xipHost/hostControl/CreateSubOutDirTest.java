/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class CreateSubOutDirTest extends TestCase {
	HostConfigurator hostConfigurator;
	File outFile;
	protected void setUp() throws Exception {
		super.setUp();
		hostConfigurator = new HostConfigurator();
		//create parent dir in test dir
		//clean at shutdown
		outFile = new File("./src-tests/edu/wustl/xipHost/hostControl", "OutXIPTest");		
		if(outFile.exists() == false){
			outFile.mkdir();
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		outFile.delete();
	}
	
	//HostConfigurator 1A - basic flow. parentOfOutDir exists.
	//Result: null
	public void testCreateSubOutDir1A() throws IOException {								
		String parentOfOutDir = outFile.getCanonicalPath();
		File subDir = hostConfigurator.createSubOutDir(parentOfOutDir);		
		assertNotNull("Parent dir exists but system was unable to create sub dir.", subDir);
	}
	
	
	//HostConfigurator 1B - alternative flow. parentOfOutDir does not exist.
	//Result: null
	public void testCreateSubOutDir1B() {								
		String parentOfOutDir = "/XIPOutTEST";
		if(new File(parentOfOutDir).exists() == false){
			File subDir = hostConfigurator.createSubOutDir(parentOfOutDir);		
			assertNull("Parent dir does not exist but system was unable to detect it.", subDir);
		}else{
			fail("/XIPOutTEST dir exists");
		}
	}
	

}
