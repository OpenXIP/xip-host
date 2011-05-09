/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class DeleteDirTest extends TestCase {
	File tmpDir;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();				
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}		
	
	//Util 1A - basic flow. File is directory and contains subdir with files.
	//Result: detele all subdirs, dir and return boolean true
	public void testDeleteDir1A() {
		tmpDir = new File("test-content/TestDelete/TmpXIP12345.tmp");
		if(tmpDir.exists() == false){
			tmpDir.mkdir();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp").mkdir();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp/test1.txt").mkdir();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp/test2.txt").mkdir();
		}
		assertTrue("", Util.delete(tmpDir));
		assertFalse("", tmpDir.exists());		
	}
	
	//Util 1B - alternative flow. File is directory and does not contain any subdirs.
	//Result: detele dir and return boolean true
	public void testDeleteDir1B() {		
		tmpDir = new File("test-content/TestDelete/TmpXIP23699.tmp");
		if(tmpDir.exists() == false){
			tmpDir.mkdir();
		}	
		assertTrue("", Util.delete(tmpDir));
		assertFalse("", tmpDir.exists());
	}
	
	//Util 1C - alternative flow. File is directory and contains subdir with subdir and files.
	//Result: detele all subdirs, dir and return boolean true
	public void testDeleteDir1C() throws IOException {
		tmpDir = new File("test-content/TestDelete/TmpXIP12345.tmp");
		if(tmpDir.exists() == false){
			tmpDir.mkdir();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp").mkdir();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp/TEST").mkdir();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp/TEST/test1.txt").createNewFile();
			new File("test-content/TestDelete/TmpXIP12345.tmp/DICOM-XIPHOST7021.tmp/TEST/test2.txt").createNewFile();
		}
		assertTrue("", Util.delete(tmpDir));
		assertFalse("", tmpDir.exists());		
	}
	
	//Util 1D - alternative flow. File is not directory. It is a file.
	//Result: delete file and retrun boolean true 
	public void testDeleteDir1D() throws IOException {
		tmpDir = new File("test-content/TestDelete/test.txt");
		if(tmpDir.exists() == false){
			tmpDir.createNewFile();						
		}
		assertTrue("", Util.delete(tmpDir));
	}

}
