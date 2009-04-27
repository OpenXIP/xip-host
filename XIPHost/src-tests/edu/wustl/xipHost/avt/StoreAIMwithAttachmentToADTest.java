/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.jdom.JDOMException;

import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.io.DicomIO;
import com.siemens.scr.avt.ad.util.DicomParser;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreAIMwithAttachmentToADTest extends TestCase {

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
	
	// - basic flow. Perfect condistions. 
	public void testStoreAimWithAttachmentToAD_1A() throws IOException, JDOMException {
		File originalDicom = new File("./test-content/AIM_rv9_Test/1.3.6.1.4.1.9328.50.1.10717.dcm");
		File aimFile = new File("./test-content/AIM_rv9_Test/test_aim.xml");
		File dicomSeg = new File("./test-content/AIM_rv9_Test/test_aim.dcm");
		DicomObject dob = DicomParser.read(originalDicom);
		DicomIO.saveOrUpdateDicom(dob);
		ImageAnnotation imageAnnotation = AnnotationIO.loadAnnotationWithAttachment(aimFile, dicomSeg);
		AnnotationIO.saveOrUpdateAnnotation(imageAnnotation);		
		//Perform actual assertion instead of just returning true 
		assert(true);
	}
	
}
