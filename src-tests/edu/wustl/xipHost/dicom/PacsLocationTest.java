/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class PacsLocationTest extends TestCase{
	
	//GlobalSearchUtil 1A of the basic flow. All parameters are correct.
	public void testValidate1A() throws IllegalArgumentException {				
		PacsLocation loc = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WashU WS1");
		assertEquals("Parameters correct but object was not created.", loc, loc);
		//assertNotNull("Parameters correct but created object is null.", loc);
	}
	
	//GlobalSearchUtil 1B alternative flow. Parameters are missing.
	public void testValidate1B() {				
		try {			
			new PacsLocation(null, 0, null, null);
			fail("Parameters are missing");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
	//GlobalSearchUtil 1C alternative flow. Parameters are invalid, address invalid, port negative and strings are empty 
	//or start from white space.
	public void testValidate1C() {				
		try {
			new PacsLocation("10", -3001, " ", " ");
			fail("Invalid parameters");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
	//GlobalSearchUtil 1D alternative flow. Only address is invalid.
	public void testValidate1D() {				
		try {
			new PacsLocation("10.252.175", 3001, "WORKSTATION1", "Test location");
			fail("Invalid parameters");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}		
	}
}
