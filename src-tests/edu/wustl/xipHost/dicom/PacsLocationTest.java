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

package edu.wustl.xipHost.dicom;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class PacsLocationTest extends TestCase{
	
	//GlobalSearchUtil 1A of the basic flow. All parameters are correct.
	public void testValidate1A() throws IllegalArgumentException {				
		PacsLocation loc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WashU WS1");
		assertEquals("Parameters correct but object was not created.", loc, loc);
		//assertNotNull("Parameters correct but created object is null.", loc);
	}
	
	//GlobalSearchUtil 1B alternative flow. Parameters are missing.
	public void testValidate1B() {				
		try {			
			new PacsLocation(null, 0, null, null);
			fail("Parameters are missing");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
	//GlobalSearchUtil 1C alternative flow. Parameters are invalid, address invalid, port negative and strings are empty 
	//or start from white space.
	public void testValidate1C() {				
		try {
			new PacsLocation("10", -3001, " ", " ");
			fail("Invalid parameters");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
	//GlobalSearchUtil 1D alternative flow. Only address is invalid.
	public void testValidate1D() {				
		try {
			new PacsLocation("10.252.175", 3001, "WORKSTATION1", "Test location");
			fail("Invalid parameters");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
}
