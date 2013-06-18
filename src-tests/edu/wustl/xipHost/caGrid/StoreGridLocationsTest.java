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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreGridLocationsTest extends TestCase {
	GridManager gridMgr;	
	List<GridLocation> locations;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		gridMgr = new GridManagerImpl();		
		GridLocation gridLoc1 = new GridLocation("http://127.0.0.1", Type.DICOM, "DICOM", "Test Location");
		GridLocation gridLoc2 = new GridLocation("http://127.0.0.1", Type.AIM, "AIM-0.9", "Test Loc");
		locations = new ArrayList<GridLocation>();
		locations.add(gridLoc1);
		locations.add(gridLoc2);
	}
	
	//GridManager 1A of the basic flow. All parameters: pacs locations and output file are correct.
	public void testStoreGridAddress1A() throws FileNotFoundException {								
		File outputFile = new File("./src-tests/edu/wustl/xipHost/caGrid/gridStoreTest1A.xml");
		Boolean blnStore = gridMgr.storeGridLocations(locations, outputFile);
		assertTrue("Parameters are correct but locations were not stored.", blnStore);
	}
	//GridManager 1B alternative flow. Directory for the output file does not exist.
	public void testStoreGridAddress1B() {				
		File outputFile = new File("./src-tests/edu/wustl/xipHost/caGridTest/gridStoreTest1B.xml");
		try{
			gridMgr.storeGridLocations(locations, outputFile);
		}catch(FileNotFoundException e){
			assertTrue(true);
		}
	}
	//GridManager 1C alternative flow. List of locations is null.
	public void testStoreGridAddress1C() throws FileNotFoundException {				
		File outputFile = new File("./src-tests/edu/wustl/xipHost/caGrid/gridStoreTest1C.xml");
		Boolean blnStore = gridMgr.storeGridLocations(null, outputFile);
		assertFalse("Directory for the putput file does not exists but system stored locations.", blnStore);			
	}
	//GridManager 1D alternative flow. List of locations is empty.
	public void testStoreGridAddress1D() throws FileNotFoundException{				
		File outputFile = new File("./src-tests/edu/wustl/xipHost/caGrid/gridStoreTest1D.xml");
		locations = new ArrayList<GridLocation>();
		Boolean blnStore = gridMgr.storeGridLocations(locations, outputFile);
		assertTrue("System should create XML file with no grid locations in it.", blnStore);
	}
}
