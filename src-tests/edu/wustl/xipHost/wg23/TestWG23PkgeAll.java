/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestWG23PkgeAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from wg23 package.");
		suite.addTestSuite(ChangeStateWG23Test.class);
		suite.addTestSuite(GetAsFileWG23Test.class);
		suite.addTestSuite(MakeNativeModelTest.class);
		return suite;
	}
}
