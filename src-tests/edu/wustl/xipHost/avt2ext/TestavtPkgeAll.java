/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestavtPkgeAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from avt package.");
		suite.addTestSuite(QueryADTest.class);		
		return suite;
		
	}
}
