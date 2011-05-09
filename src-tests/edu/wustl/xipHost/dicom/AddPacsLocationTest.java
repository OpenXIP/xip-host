/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class AddPacsLocationTest extends TestCase {
	DicomManager dicomMgr;
	
	protected void setUp(){	
		dicomMgr = DicomManagerFactory.getInstance();		
	}
	//DicomManagerImpl 1A of the basic flow. All parameters are correct.
	public void testAddPacsAddress1A() {						
		PacsLocation pacsLoc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WUSTL workstation 1");
		boolean blnAdd = dicomMgr.addPacsLocation(pacsLoc);
		assertTrue("Address, port, AETitle correct but location not added", blnAdd);					
	}
	//DicomManagerImpl of 1B alternative flow. Parameters are missing.
	public void testAddPacsAddress1B() {						
		try {
			dicomMgr.addPacsLocation(new PacsLocation(null, 0, null, null));
			fail("Parameters are missing but location was added");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}						
	}
	//DicomManagerImpl of 1C alternative flow. Parameters are invalid or missing.
	public void testAddPacsAddress1C() {				
		//new PacsLocation("10.", 1, " ");
		try {
			dicomMgr.addPacsLocation(new PacsLocation("10", -1, "%?_", " "));
			fail("Invalid or missing parameters but location added");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}					
	}
	//DicomManagerImpl of 1D of alternative flow. All parameters are correct, but address was already added before
	public void testAddPacsAddress1D(){				
		PacsLocation pacsLoc= new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WUSTL workstation 1");
		dicomMgr.addPacsLocation(pacsLoc);		
		boolean blnAdd = dicomMgr.addPacsLocation(pacsLoc);
		assertFalse("Location already existed but was added again", blnAdd);						
	}
	//DicomManagerImpl of 1E alternative flow. Location short name is missing.
	public void testAddPacsAddress1E() {				
		try {
			dicomMgr.addPacsLocation(new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", " "));
			fail("Missing short name but location added");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}					
	}
}
