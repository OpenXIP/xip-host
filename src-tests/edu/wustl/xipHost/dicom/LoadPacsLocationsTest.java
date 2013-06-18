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
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.IOException;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import junit.framework.TestCase;

public class LoadPacsLocationsTest extends TestCase {
	DicomManager dicomMgr;
	protected void setUp() throws Exception {
		dicomMgr = DicomManagerFactory.getInstance();	
	}

	//DicomManagerImpl 1A - Basic flow: config file exists and found, XML is valid (JDOM can be created), locations data is valid
	public void testLoadPacsLocations1A() throws IOException, JDOMException, IllegalArgumentException{
		File file = new File("./src-tests/edu/wustl/xipHost/dicom/pacs_locations1A.xml");
		boolean blnLoad = dicomMgr.loadPacsLocations(file);
		assertTrue("Perfect conditions though locations not loaded.", blnLoad);
	}
	//DicomManagerImpl 1B - Alternative flow: config file does not exist or cannot be found
	public void testLoadPacsLocations1B() throws JDOMException, IllegalArgumentException{
		File file = new File("./src-tests/edu/wustl/xipHost/dicom/pacs_locationsDoesNotExist.xml");			
		try{
			dicomMgr.loadPacsLocations(file);
			fail("File cannot be found but system reports loaded locations.");			
		} catch (IOException e) {
			assertTrue(true);
		}
	}
	//DicomManagerImpl 1C - Alternative flow: config XML is not valid. JDOM document cannot be created
	public void testLoadPacsLocations1C() throws IOException, IllegalArgumentException{
		File file = new File("./src-tests/edu/wustl/xipHost/dicom/pacs_locations1C.xml");		
		try{
			dicomMgr.loadPacsLocations(file);
			fail("XML is not valid but load locations worked");			
		} catch (JDOMException e) {
			assertTrue(true);
		}
	}
	//DicomManagerImpl 1D = Alternative flow: config XML file contains invalid data to create PacsLocation
	//All valid PACS locations will be loaded, invalid ignored (printed out for information) 
	public void testLoadPacsLocations1D() throws IOException, JDOMException{
		File file = new File("./src-tests/edu/wustl/xipHost/dicom/pacs_locations1D.xml");				
		boolean blnLoad = dicomMgr.loadPacsLocations(file);		
		assertTrue("XML conatins invalid data but load locations worked", blnLoad);							
	}
	//DicomManagerImpl 1E = Alternative flow: config XML file contains incomplete data to create PacsLocation
	//All valid PACS locations will be loaded, invalid ignored (printed out for information)
	public void testLoadPacsLocations1E() throws IOException, JDOMException{
		File file = new File("./src-tests/edu/wustl/xipHost/dicom/pacs_locations1E.xml");				
		boolean blnLoad = dicomMgr.loadPacsLocations(file);
		assertTrue("XML is missing data but load location worked", blnLoad);
	}
	//DicomManagerImpl 1F = Alternative flow: config XML file is null
	public void testLoadPacsLocations1F() throws IOException, JDOMException, IllegalArgumentException{
		File file = null;		
		try{
			dicomMgr.loadPacsLocations(file);
			fail("XML is missing data but load location worked");			
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	//DicomManagerImpl 1G = Alternative flow: config XML file is not specified e.g. new File("");
	public void testLoadPacsLocations1G() throws IOException, JDOMException, IllegalArgumentException{
		File file = new File("");		
		try{
			dicomMgr.loadPacsLocations(file);
			fail("XML is missing data but load location worked");			
		} catch (JDOMParseException e) {
			assertTrue(true);
		}
	}
}
