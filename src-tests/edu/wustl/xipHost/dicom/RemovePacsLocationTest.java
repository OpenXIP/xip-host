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
public class RemovePacsLocationTest extends TestCase {
	DicomManager dicomMgr;
	protected void setUp() {	
		dicomMgr = DicomManagerFactory.getInstance();	
	}	
	//DicomManagerImpl 1A of the basic flow. Parameters 'pacsLocation' to be removed is correct and
	//pacsLocation exists on the list.
	public void testRemovePacsLocation1A() {
		PacsLocation pacsLoc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WorkStation1");
		dicomMgr.addPacsLocation(pacsLoc);
		boolean blnRemove = dicomMgr.removePacsLocation(pacsLoc);
		assertTrue("Pacs location to be removed is correct and exists on the list though it was not removed", blnRemove);
	}
	//DicomManagerImpl of 1B alternative flow. 'pacsLocation' to be removed is null
	public void testRemovePacsLocation1B(){
		PacsLocation pacsLoc = null;
		boolean blnRemove = dicomMgr.removePacsLocation(pacsLoc);
		assertFalse("Pacs location to be removed is null though it was removed", blnRemove);	
	}
	//DicomManagerImpl of 1C alternative flow. 'pacsLocation' has valid value but does not exist on the list
	public void testRemovePacsLocation1C() {
		PacsLocation pacsLoc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WorkStation1");
		boolean blnRemove = dicomMgr.removePacsLocation(pacsLoc);
		assertFalse("Pacs location is correct, does not exist on the list though removed produces boolean true", blnRemove);
	}
}
