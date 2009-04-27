/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class CreateSubTmpDirTest extends TestCase {
	HostConfigurator hostConfigurator;
	File tmpFile;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		hostConfigurator = new HostConfigurator();
		//create parent dir in test dir
		//clean at shutdown
		tmpFile = new File("./src-tests/edu/wustl/xipHost/hostControl", "TmpXIPTest");		
		if(tmpFile.exists() == false){
			tmpFile.mkdir();
		}
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		tmpFile.delete();
		
	}
	
	//HostConfigurator 1A - basic flow. parentOfTmpDir is valid.
	//Result: non null file
	public void testCreateSubTmpDir1A() throws IOException {								
		String parentOfTmpDir = tmpFile.getCanonicalPath();
		File subDir = hostConfigurator.createSubTmpDir(parentOfTmpDir);		
		assertNotNull("Parent dir exists but system was unable to create sub dir.", subDir);
	}
	
	//HostConfigurator 1B - alternative flow. parentOfTmpDir is null.
	//Result: null
	public void testCreateSubTmpDir1B() {								
		String parentOfTmpDir = null;
		File subDir = hostConfigurator.createSubTmpDir(parentOfTmpDir);		
		assertNull("Parent dir is null but system was unable to detect it.", subDir);
	}
	
	//HostConfigurator 1C - alternative flow. parentOfTmpDir is an empty string.
	//Result: null
	public void testCreateSubTmpDir1C() {								
		String parentOfTmpDir = "";
		File subDir = hostConfigurator.createSubTmpDir(parentOfTmpDir);		
		assertNull("Parent dir is an empty String but system was unable to detect it.", subDir);
	}
	
	//HostConfigurator 1D - alternative flow. parentOfTmpDir does not exist.
	//Result: null
	public void testCreateSubTmpDir1D() {								
		String parentOfTmpDir = "/XIPTmpTEST";
		if(new File(parentOfTmpDir).exists() == false){
			File subDir = hostConfigurator.createSubTmpDir(parentOfTmpDir);		
			assertNull("Parent dir does not exist but system was unable to detect it.", subDir);
		}else{
			fail("/XIPTmpTEST dir exists");
		}
	}
	
}
