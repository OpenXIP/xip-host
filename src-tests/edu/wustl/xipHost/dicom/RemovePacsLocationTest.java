/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
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
