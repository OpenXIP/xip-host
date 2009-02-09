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
public class DeleteHostTmpFilesTest extends TestCase {
	File parentOfTmpDir;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		parentOfTmpDir = new File("test-content/TestDeleteHostTmpFiles");
		if(parentOfTmpDir.exists() == false){
			parentOfTmpDir.mkdir();			
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Util.delete(parentOfTmpDir);
	}

	//Util 1A - basic flow. parentOfTmpDir exists, subcontent is in XIPHost format
	//- starts with "TmpXIP" and ends with ".tmp".
	//Result: empty parentOfTmpDir and return boolean true
	public void testDeleteHostTmpFiles1A() throws IOException {
		new File("test-content/TestDeleteHostTmpFiles/TmpXIP12345.tmp/DICOM-XIPHOST1234.tmp").mkdirs();
		new File("test-content/TestDeleteHostTmpFiles/TmpXIP12345.tmp/DICOM-XIPHOST1234.tmp/test1.txt").createNewFile();
		new File("test-content/TestDeleteHostTmpFiles/TmpXIP12345.tmp/DICOM-XIPHOST1234.tmp/test2.txt").createNewFile();
		new File("test-content/TestDeleteHostTmpFiles/TmpXIP12345.tmp/DICOM-XIPHOST2345.tmp").mkdir();
		new File("test-content/TestDeleteHostTmpFiles/TmpXIP12345.tmp/DICOM-XIPHOST2345.tmp/test1.txt").createNewFile();
		new File("test-content/TestDeleteHostTmpFiles/TmpXIP12345.tmp/DICOM-XIPHOST2345.tmp/test2.txt").createNewFile();
		boolean bln = Util.deleteHostTmpFiles(parentOfTmpDir);
		boolean isEmpty = false;
		if(parentOfTmpDir.listFiles().length == 0){
			isEmpty = true;
		}
		assertTrue("Perfect conditions but system unable to delete the TMP content", bln);
		assertTrue("There should not be any remaining files in parentOfTmpDir", isEmpty);
	}
	
	//Util 1B - alternative flow. parentOfTmpDir does not exist
	//Result: return boolean true
	public void testDeleteHostTmpFiles1B() {
		parentOfTmpDir = new File("test-content/TestDeleteHostTmpFilesNonExisting");
		assertFalse("parentOfTmpDir does not exist. Return value should be boolean false.", Util.deleteHostTmpFiles(parentOfTmpDir));
	}
	
	//Util 1Ca - alternative flow. parentOfTmpDir exists and contains 
	//sub dirs not matching XIPHost created format
	//Result: delete dirs that conform to teh format, preserve otherwise, return true 
	public void testDeleteHostTmpFiles1Ca() throws IOException {
		new File("test-content/TestDeleteHostTmpFiles/_TmpXIP12345.tmp/DICOM-XIPHOST1234.tmp").mkdirs();
		new File("test-content/TestDeleteHostTmpFiles/XIP12345").mkdir();		
		boolean bln = Util.deleteHostTmpFiles(parentOfTmpDir);		
		int numDirs = parentOfTmpDir.listFiles().length; 		
		assertTrue("Error when deleteing tmp content with subdirs having non host format.", bln);
		assertEquals("Wrong number of preserved subdirs.", 2, numDirs);		
	}
	
	//Util 1Cb - alternative flow. parentOfTmpDir exists and contains  files in its directory, no subdirs
	//Result: preserve file content of parentOfTmpDir and return boolean true
	public void testDeleteHostTmpFiles1Cb() throws IOException {		
		new File("test-content/TestDeleteHostTmpFiles/test1.txt").createNewFile();
		new File("test-content/TestDeleteHostTmpFiles/test2.txt").createNewFile();
		boolean bln = Util.deleteHostTmpFiles(parentOfTmpDir);						
		int numDirs = parentOfTmpDir.listFiles().length; 		
		assertTrue("Error when deleteing tmp content with subdirs having non host format.", bln);
		assertEquals("Wrong number of preserved files.", 2, numDirs);		
	}
	
}
