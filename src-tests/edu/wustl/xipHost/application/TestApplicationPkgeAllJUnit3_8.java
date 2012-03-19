/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestApplicationPkgeAllJUnit3_8 {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from Application package.");
		suite.addTestSuite(AddApplicationTest.class);
		suite.addTestSuite(CreateApplicationTest.class);
		suite.addTestSuite(GenerateNewApplicationServiceURLTest.class);
		suite.addTestSuite(GetModelSetDescriptorTest.class);
		suite.addTestSuite(LoadApplicationsTest.class);
		suite.addTestSuite(ModifyApplicationTest.class);
		//suite.addTestSuite(QueryModelPerformanceTest.class);
		suite.addTestSuite(QueryModelTest.class);
		suite.addTestSuite(RemoveApplicationTest.class);
		//suite.addTestSuite(ShutDownTest.class);
		return suite;	
	}
}
