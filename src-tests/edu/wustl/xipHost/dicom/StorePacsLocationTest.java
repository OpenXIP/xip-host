/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StorePacsLocationTest extends TestCase {
	DicomManager dicomMgr;	
	List<PacsLocation> locations;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		dicomMgr = DicomManagerFactory.getInstance();		
		PacsLocation pacsLoc1 = new PacsLocation("10.252.175.60", 3001, "WORKSTATION1", "WUSTL workstation 1");
		PacsLocation pacsLoc2 = new PacsLocation("127.0.0.1", 3001, "WS1", "WUSTL ws 1");
		locations = new ArrayList<PacsLocation>();
		locations.add(pacsLoc1);
		locations.add(pacsLoc2);
	}
	
	//DicomManagerImpl 1A of the basic flow. All parameters: pacs locations and output file are correct.
	public void testStorePacsAddress1A() throws FileNotFoundException {								
		File outputFile = new File("./src-tests/edu/wustl/xipHost/dicom/pacsStoreTest1A.xml");
		Boolean blnStore = dicomMgr.storePacsLocations(locations, outputFile);
		assertTrue("Parameters are correct but locations were not stored.", blnStore);
	}
	//DicomManagerImpl 1B alternative flow. Directory for the output file does not exist.
	public void testStorePacsAddress1B() {				
		File outputFile = new File("./src-tests/edu/wustl/xipHost/dicomSearchTest/pacsStoreTest1B.xml");
		try{
			dicomMgr.storePacsLocations(locations, outputFile);
		}catch(FileNotFoundException e){
			assertTrue(true);
		}
	}
	//DicomManagerImpl 1C alternative flow. List of locations is null.
	public void testStorePacsAddress1C() throws FileNotFoundException {				
		File outputFile = new File("./src-tests/edu/wustl/xipHost/dicom/pacsStoreTest1C.xml");
		Boolean blnStore = dicomMgr.storePacsLocations(null, outputFile);
		assertFalse("Directory for the putput file does not exists but system stored locations.", blnStore);			
	}
	//DicomManagerImpl 1D alternative flow. List of locations is empty.
	public void testStorePacsAddress1D() throws FileNotFoundException{				
		File outputFile = new File("./src-tests/edu/wustl/xipHost/dicom/pacsStoreTest1D.xml");
		locations = new ArrayList<PacsLocation>();
		Boolean blnStore = dicomMgr.storePacsLocations(locations, outputFile);
		assertTrue("System should create XML file with no pacs locations in it.", blnStore);
	}
	
}
