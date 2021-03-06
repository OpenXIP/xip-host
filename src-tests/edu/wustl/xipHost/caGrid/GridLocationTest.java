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

import edu.wustl.xipHost.caGrid.GridLocation.Type;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridLocationTest extends TestCase {
	
	//GridLocation 1A of the basic flow. All parameters are correct.
	public void testValidate1A() throws IllegalArgumentException {				
		GridLocation loc = new GridLocation("http://140.254.80.50:50020/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "OSU");
		assertEquals("Parameters correct but object was not created.", loc, loc);
		//assertNotNull("Parameters correct but created object is null.", loc);
	}
	//GridLocation 1B alternative flow. Parameters are missing.
	public void testValidate1B() {				
		try {			
			//new GridLocation(null, null, null);
			new GridLocation("http://140.254.80.50:50020/wsrf/services/cagrid/DICOMDataService", null, "DICOM", "OSU");
			fail("Parameters are missing");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
	//GridLocation 1C alternative flow. Parameters are invalid.
	public void testValidate1C() {				
		try {
			new GridLocation(" ", Type.AIM, "AIM-0.9", " ");
			fail("Invalid parameters");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
	//GlobalSearchUtil 1D alternative flow. Address is invalid.
	//It has IP that cannot be converted to URL
	public void testValidate1D() {				
		try {
			new GridLocation("10.252.175.60", Type.DICOM, "DICOM", "Test location");
			fail("Invalid parameters");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
}
