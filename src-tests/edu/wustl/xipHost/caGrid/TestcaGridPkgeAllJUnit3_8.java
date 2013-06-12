/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.caGrid;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestcaGridPkgeAllJUnit3_8 {
	public static Test suite(){
		TestSuite suite = new TestSuite("Running JUnit 3.8 tests from caGrid package.");
		suite.addTestSuite(AddGridLocationTest.class);
		suite.addTestSuite(GridLocationTest.class);
		suite.addTestSuite(LoadGridLocationsTest.class);
		suite.addTestSuite(ModifyGridLocationTest.class);
		suite.addTestSuite(RemoveGridLocationTest.class);		
		suite.addTestSuite(StoreGridLocationsTest.class);
		suite.addTestSuite(RunGridStartupSequenceTest.class);
		suite.addTestSuite(ConvertToCQLTest.class);
		suite.addTestSuite(LoadNCIAModelMapTest.class);
		suite.addTestSuite(MapDicomTagToNCIATagNameTest.class);
		//suite.addTestSuite(QueryGridLocationTest.class);
		//suite.addTestSuite(RetrieveDicomDataTest.class);
		//suite.addTestSuite(StoreAimObjectsTest.class);
		return suite;
		
	}
}
