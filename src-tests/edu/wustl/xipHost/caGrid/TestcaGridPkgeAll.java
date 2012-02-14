/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestcaGridPkgeAll {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running JUnit 3.8 tests from caGrid package.");
		suite.addTestSuite(AddGridLocationTest.class);
		suite.addTestSuite(GridLocationTest.class);
		suite.addTestSuite(LoadGridLocationsTest.class);
		suite.addTestSuite(ModifyGridLocationTest.class);
		suite.addTestSuite(RemoveGridLocationTest.class);		
		suite.addTestSuite(StoreGridLocationsTest.class);
		suite.addTestSuite(RunGridStartupSequenceTest.class);
		
		
		//suite.addTestSuite(ConvertToCQLTest.class);
		//suite.addTestSuite(LoadNCIAModelMapTest.class);
		//suite.addTestSuite(MapDicomTagToNCIATagNameTest.class);
		//suite.addTestSuite(QueryGridLocationTest.class);
		//suite.addTestSuite(RetrieveDicomDataTest.class);
		return suite;
		
	}
}
