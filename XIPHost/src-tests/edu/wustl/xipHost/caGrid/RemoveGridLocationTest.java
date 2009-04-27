/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
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
		GridLocation gridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "Test Location");
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
		GridLocation gridLoc = new GridLocation("http://10.252.175.60", Type.DICOM, "Test Location");
		boolean blnRemove = gridMgr.removeGridLocation(gridLoc);
		assertFalse("Location is correct, does not exist on the list though removed produces boolean true", blnRemove);
	}
}
