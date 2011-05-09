/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestDicomPkgeAll {
	
	
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from caGrid package.");
		suite.addTestSuite(AddPacsLocationTest.class);
		suite.addTestSuite(LoadPacsLocationsTest.class);
		suite.addTestSuite(MimeTypeTest.class);
		suite.addTestSuite(ModifyPacsLocationTest.class);
		suite.addTestSuite(PacsLocationTest.class);
		suite.addTestSuite(RemovePacsLocationTest.class);
		suite.addTestSuite(StorePacsLocationTest.class);
		suite.addTest(QueryPacsLocationTest.suite());
		return suite;		
	}
}
