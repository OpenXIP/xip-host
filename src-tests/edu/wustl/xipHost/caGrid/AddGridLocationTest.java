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
public class AddGridLocationTest extends TestCase {
	GridManager gridMgr;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		gridMgr = new GridManagerImpl();
	}

	//GlobalSearchUtil of 1B alternative flow. Parameters are missing.
	public void testAddGridAddress1B() {				
		try {
			gridMgr.addGridLocation(new GridLocation(null, null, null, null));
			fail("Parameters are missing but location was added");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}			
	}
	//GlobalSearchUtil of 1C alternative flow. Parameters are invalid or missing.
	public void testAddGridAddress1C() {						
		//new PacsLocation("10.", 1, " ");
		try {
			gridMgr.addGridLocation(new GridLocation(" ", null, " ", " "));
			fail("Invalid or missing parameters but location added");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}						
	}
	// GlobalSearchUtil of 1D of alternative flow. All parameters are correct, but address was already added before
	public void testAddGridAddress1D(){				
		GridLocation gridLoc = new GridLocation("http://127.0.0.1", Type.DICOM, "NBIA-4.2", "Test Location");
		gridMgr.addGridLocation(gridLoc);		
		boolean blnAdd = gridMgr.addGridLocation(gridLoc);
		assertFalse("Location already existed but was added again", blnAdd);						
	}
}
