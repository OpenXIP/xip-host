/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestLocalFileSystemPkgeAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from localFileSystem package.");
		suite.addTestSuite(RunFileRunnerTest.class);
		return suite;		
	}
}
