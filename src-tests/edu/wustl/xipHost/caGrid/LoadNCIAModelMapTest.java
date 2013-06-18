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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

public class LoadNCIAModelMapTest extends TestCase {
	GridUtil util;
	protected void setUp() throws Exception {
		util = new GridUtil();
	}
	
	//GridUtil 1A of the basic flow. NCIAModelMap file exists and is correct.
	public void testLoadNCIAModelMap1A() throws IOException {						
		FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");
		Map<String, String> map = util.loadNCIAModelMap(fis);
		assertNotNull("NCIAMoelMap file exists is is valid but load failed.", map);
		assertSame("Data is valid but load did not return correct type.", java.util.HashMap.class, map.getClass());		
	}

	//GridUtil 1B of the basic flow. NCIAModelMap file does not exist.
	public void testLoadNCIAModelMap1B() throws IOException {
		try {			
			FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMapVOID.properties");
			util.loadNCIAModelMap(fis);
			fail("File does not exists but system did not catch the exception.");
		} catch (FileNotFoundException e){
			assertTrue(true);
		}
	}	
}
