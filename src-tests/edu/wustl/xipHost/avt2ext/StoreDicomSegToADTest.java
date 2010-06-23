/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che2.data.DicomObject;
import org.jdom.JDOMException;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.util.DicomParser;

import edu.wustl.xipHost.avt2ext.AVTFactory;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreDicomSegToADTest extends TestCase {
	ADFacade adService = AVTFactory.getADServiceInstance();
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
	public void testStoreDicomSegToAD_1A() throws IOException, JDOMException {
		File dicomSeg = new File("./test-content/AIM_2PlusDICOMSeg/nodule_1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.1.0.dcm");
		List<DicomObject> dicoms = new ArrayList<DicomObject>();
		DicomObject seg = DicomParser.read(dicomSeg);		
		dicoms.add(seg);
		adService.saveDicoms(dicoms);
		assert(true);
	}
	
}
