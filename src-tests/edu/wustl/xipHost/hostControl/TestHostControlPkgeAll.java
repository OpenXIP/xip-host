/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestHostControlPkgeAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running all tests from hostControl package.");
		suite.addTestSuite(AddDocumentXMTest.class);
		suite.addTestSuite(CreateCollectionXMTest.class);
		suite.addTestSuite(CreateSubOutDirTest.class);
		suite.addTestSuite(CreateSubTmpDirTest.class);
		suite.addTestSuite(DeleteCollectionXMTest.class);
		suite.addTestSuite(DeleteDirTest.class);
		suite.addTestSuite(DeleteDocumentXMTest.class);
		suite.addTestSuite(DeleteHostTmpFilesTest.class);
		suite.addTestSuite(LoadHostConfigParametersTest.class);
		suite.addTestSuite(QueryXMTest.class);
		suite.addTestSuite(ValidateHostTmpDirTest.class);
		return suite;	
	}
}
