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
public class RemoveGridLocationTest extends TestCase {
	GridManager gridMgr;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */	
	protected void setUp() {	
		gridMgr = new GridManagerImpl();
	}	
	//GridManagerImpl 1A of the basic flow. Location parameters are correct and
	//location exists on the list.
	public void testRemoveGridLocation1A(){
		GridLocation gridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location");
		gridMgr.addGridLocation(gridLoc);
		boolean blnRemove = gridMgr.removeGridLocation(gridLoc);
		assertTrue("Location to be removed is correct and exists on the list though it was not removed", blnRemove);
	}
	//GridManagerImpl of 1B alternative flow. Location to be removed is null
	public void testRemoveGridLocation1B(){
		GridLocation gridLoc = null;
		boolean blnRemove = gridMgr.removeGridLocation(gridLoc);
		assertFalse("Location to be removed is null though it was removed", blnRemove);	
	}
	//GridManagerImpl of 1C alternative flow. Location has valid value but does not exist on the list
	public void testRemoveGridLocation1C() {
		GridLocation gridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "DICOM", "Test Location");
		boolean blnRemove = gridMgr.removeGridLocation(gridLoc);
		assertFalse("Location is correct, does not exist on the list though removed produces boolean true", blnRemove);
	}
}
