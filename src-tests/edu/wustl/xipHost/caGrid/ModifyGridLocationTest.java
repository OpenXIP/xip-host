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
public class ModifyGridLocationTest extends TestCase {
	GridManager gridMgr;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */	
	protected void setUp() {	
		gridMgr = new GridManagerImpl();
	}
	//GridManagerImpl 1A of the basic flow. Parameters to be modified are correct and location exists
	public void testModifyGridLocation1A() {
		GridLocation oldGridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location 1");
		GridLocation newGridLoc = new GridLocation("http://10.252.175.31", Type.DICOM, "DICOM", "Test Location 2");
		gridMgr.addGridLocation(oldGridLoc);
		boolean blnModify = gridMgr.modifyGridLocation(oldGridLoc, newGridLoc);
		assertTrue("Location, modification values are OK, and location exists but system was unable to modify data.", blnModify);
	}
	//GridManagerImpl of 1B alternative flow. Location to be modified does not exist. 
	//Expected return value is ArrayIndexOutOfBoundsException
	public void testModifyGridLocation1B() {
		GridLocation oldGridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location 1");
		GridLocation newGridLoc = new GridLocation("http://10.252.175.31", Type.DICOM, "DICOM", "Test Location 2");				
		boolean blnModify =	gridMgr.modifyGridLocation(oldGridLoc, newGridLoc);			
		assertFalse("GridLocation to be modified does not exist but system claims it found it.", blnModify);		
	}
	//GridManagerImpl of 1C alternative flow. Location exists but replacement values are not correct
	public void testModifyGridLocation1C() {
		GridLocation oldGridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location 2");
		gridMgr.addGridLocation(oldGridLoc);
		try {								
			gridMgr.modifyGridLocation(oldGridLoc, new GridLocation("10", null, " ", " "));
			fail("Location is OK and exists, modification values are not correctOK and system was able to modify data.");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
}
