/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
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
