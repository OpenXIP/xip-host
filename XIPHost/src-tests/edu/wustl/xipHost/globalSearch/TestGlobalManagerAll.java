/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.globalSearch;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestGlobalManagerAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all globalSearch package tests");
		suite.addTestSuite(QueryPacsInThreadTest.class);		
		suite.addTestSuite(SearchCriteriaDialogVerifyCriteriaTest.class);
		return suite;
		
	}
}
