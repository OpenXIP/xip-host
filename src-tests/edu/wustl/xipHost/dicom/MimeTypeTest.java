/**
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class MimeTypeTest extends TestCase{
	File dcmFile;
	
	public void setUp() throws Exception {
		super.setUp();	
		dcmFile = new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.19624.dcm");
	}

	
	public void tearDown() throws Exception {
		super.setUp();	
		
	}

	//DicomUtil 1A - Basic flow. File existis and is a dicom type
	//Result: "application/dicom"
	public void testMimeType1A() throws IOException {
		String mime = DicomUtil.mimeType(dcmFile);
		assertEquals("", "application/dicom", mime);
	}
	
	//DicomUtil 1B - Alternative flow. File is null
	//Result: FileNotFoundException
	public void testMimeType1B() {
		try{
			DicomUtil.mimeType(null);
		}catch(IOException e){
			assertTrue(true);
		}		
	}
	
	//DicomUtil 1C - Alternative flow. File does not exist
	//Result: FileNotFoundException
	public void testMimeType1C() {
		try{
			DicomUtil.mimeType(new File("./src-tests/edu/wustl/xipHost/dicom/nonexisting.dcm"));
		}catch(IOException e){
			assertTrue(true);
		}		
	}
	
	//DicomUtil 1D - Alternative flow. File is a dicom file but does not have a dcm extention
	//Result: "application/dicom"
	public void testMimeType1D() throws IOException {		
		String mime = DicomUtil.mimeType(new File("./src-tests/edu/wustl/xipHost/dicom/IN000349"));
		assertEquals("", "application/dicom", mime);		
	}
	
	//DicomUtil 1E - Alternative flow. File is not a dicom file
	//Result: String with mime type non "application/dicom"
	public void testMimeType1E() throws IOException {		
		String mime = DicomUtil.mimeType(new File("./src-tests/edu/wustl/xipHost/dicom/test.txt"));
		boolean isDCM = false;
		if(mime.equalsIgnoreCase("application/dicom")){
			isDCM = true;
		}
		assertFalse("File is not dicom but mime was application/dicom.", isDCM);
	}
}

