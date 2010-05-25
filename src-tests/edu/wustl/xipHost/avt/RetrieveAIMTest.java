/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dcm4che2.data.Tag;

import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;

import edu.wustl.xipHost.avt2ext.AVTFactory;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveAIMTest extends TestCase {
	ADFacade adService;	
	/**
	 * @param name
	 */
	public RetrieveAIMTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		adService = AVTFactory.getADServiceInstance();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testRetrieveAIM_1A(){
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		dicomCriteria.put(Tag.SeriesInstanceUID, "1.2.840.113704.1.111.4280.1226686843.113");
		List<ImageAnnotation> imageAnnots = adService.retrieveAnnotations(dicomCriteria, null);
		ImageAnnotation annot = (ImageAnnotation)imageAnnots.get(0);
		System.out.println(annot.getAIM());
	}

}
