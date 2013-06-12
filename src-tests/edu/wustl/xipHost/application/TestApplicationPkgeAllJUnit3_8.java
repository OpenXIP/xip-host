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
