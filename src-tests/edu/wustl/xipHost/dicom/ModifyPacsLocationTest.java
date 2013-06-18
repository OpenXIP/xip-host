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
public class ModifyPacsLocationTest extends TestCase {
	DicomManager dicomMgr;
	protected void setUp() {	
		dicomMgr = DicomManagerFactory.getInstance();	
	}
	//DicomManagerImpl 1A of the basic flow. Parameters 'pacsLocation' to be modified are correct and location exists
	public void testModifyPacsLocation1A(){
		PacsLocation oldPacsLoc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WorskStation1");
		PacsLocation newPacsLoc = new PacsLocation("10.252.175.31", 2350, "BERLIN", "Steve Moore BERLIN");
		dicomMgr.addPacsLocation(oldPacsLoc);
		boolean blnModify = dicomMgr.modifyPacsLocation(oldPacsLoc, newPacsLoc);
		assertTrue("Location, modification values are OK, and location exists but system was unable to modify data.", blnModify);
	}
	//DicomManagerImpl of 1B alternative flow. 'pacsLocation' to be modified does not exist. 
	//Expected return value is ArrayIndexOutOfBoundsException
	public void testModifyPacsLocation1B() {
		PacsLocation oldPacsLoc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WorkStation1");
		PacsLocation newPacsLoc = new PacsLocation("10.252.175.31", 2350, "BERLIN", "Steve Moore BERLIN");		
		boolean blnModify = dicomMgr.modifyPacsLocation(oldPacsLoc, newPacsLoc);			
		assertFalse("Pacs location to be modified does not exist", blnModify);		
	}
	//DicomManagerImpl of 1C alternative flow. 'pacsLocation' exists but replacement values are not correct
	public void testModifyPacsLocation1C() {
		PacsLocation oldPacsLoc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WashU WS1");
		dicomMgr.addPacsLocation(oldPacsLoc);
		//new PacsLocation("10", -1, null);				
		try {
			dicomMgr.modifyPacsLocation(oldPacsLoc, new PacsLocation("10.2", 1, " ", " "));
			fail("Location is OK and exists, modification values are not correctOK and system was able to modify data.");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
}
