/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestWorklistPkgeAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from worklist package.");
		suite.addTestSuite(MakeWG23DataModelTest.class);
		return suite;
	}
}
