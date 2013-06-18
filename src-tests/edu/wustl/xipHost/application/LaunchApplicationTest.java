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

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jaroslaw Krych
 *
 */
public class LaunchApplicationTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			String[] cmdarray = {"open", "file:/Users/krych/Documents/workspace2/XIPHost/src-tests/edu/wustl/xipHost/application/test.sh"};
			//String[] cmdarray = {"open", "/Users/krych/Documents/workspace2/XIPApp/src/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.sh"};
			Runtime.getRuntime().exec(cmdarray) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
