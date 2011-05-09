/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class LoadGridLocationsTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	GridManager gridMgr;
	protected void setUp() throws Exception {
		gridMgr = new GridManagerImpl();
	}
	
	//GridManager 1A - Basic flow: file exists and found, XML is valid (JDOM can be created), locations data is valid
	public void testLoadGridLocations1A() throws IOException, JDOMException, IllegalArgumentException{
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1A.xml");
		boolean blnLoad = gridMgr.loadGridLocations(file);
		assertTrue("Perfect conditions though locations not loaded.", blnLoad);
	}
	//GridManager 1B - Alternative flow: file does not exist or cannot be found
	public void testLoadGridLocations1B() throws JDOMException, IllegalArgumentException{
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locationsDoesNotExist.xml");			
		try{
			gridMgr.loadGridLocations(file);
			fail("File cannot be found but system reports loaded locations.");			
		} catch (IOException e) {
			assertTrue(true);
		}
	}
	//GridManagerl 1C - Alternative flow: XML is not valid. JDOM document cannot be created
	public void testLoadGridLocations1C() throws IOException, IllegalArgumentException{
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1C.xml");		
		try{
			gridMgr.loadGridLocations(file);
			fail("XML is not valid but load locations worked");			
		} catch (JDOMException e) {
			assertTrue(true);
		}
	}
	//GridManager 1D = Alternative flow: XML file contains invalid data ('address') to create GridLocation
	public void testLoadGridLocations1D() throws IOException, JDOMException{
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1D.xml");				
		boolean blnLoad = gridMgr.loadGridLocations(file);		
		assertTrue("XML conatins invalid data but load locations worked", blnLoad);							
	}
	//GridManager 1E = Alternative flow: XML file contains invalid data ('type') to create GridLocation
	//type = null or empty string
	public void testLoadGridLocations1E() throws IOException, JDOMException{
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1E.xml");				
		boolean blnLoad = gridMgr.loadGridLocations(file);		
		assertTrue("XML conatins invalid data but load locations worked", blnLoad);							
	}
	
	//GridManager 1F = Alternative flow: XML file contains incomplete data to create GridLocation
	public void testLoadGridLocations1F() throws IOException, JDOMException{
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1F.xml");				
		boolean blnLoad = gridMgr.loadGridLocations(file);		
		assertTrue("XML is missing data but load location worked", blnLoad);
	}
	//GridManager 1G = Alternative flow: XML file is null
	public void testLoadGridLocations1G() throws IOException, JDOMException, IllegalArgumentException{
		File file = null;		
		try{
			gridMgr.loadGridLocations(file);
			fail("XML is missing data but load location worked");			
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	//GridSearchUtil 1I = Alternative flow: XML file is not specified e.g. new File("");
	public void testLoadGridLocations1I() throws IOException, JDOMException, IllegalArgumentException{
		File file = new File("");		
		try{
			gridMgr.loadGridLocations(file);
			fail("XML is missing data but load location worked");			
		} catch (JDOMParseException e) {
			assertTrue(true);
		}
	}

}
