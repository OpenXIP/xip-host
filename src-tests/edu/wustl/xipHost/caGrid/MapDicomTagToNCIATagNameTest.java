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

import java.io.FileInputStream;
import java.util.Map;
import junit.framework.TestCase;

public class MapDicomTagToNCIATagNameTest extends TestCase {

	GridUtil util;
	Map<String, String> map;
	protected void setUp() throws Exception {
		 util = new GridUtil();
		 FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");
		 map = util.loadNCIAModelMap(fis);
	}
	
	//GridUtil 1A of the basic flow. NCIAModelMap file exists and is correct.
	//Test if tag (0x0010,0x0010) maps to "gov.nih.nci.ncia.domain.Patient.patientName".
	public void testMapDicomTagToNCIATagName1A()  {						
		String nciaName = util.mapDicomTagToNCIATagName("(0x0010,0x0010)");		
		assertEquals("Data is valid but mapping failed.", "gov.nih.nci.ncia.domain.Patient.patientName", nciaName);		
	}
	
	//GridUtil 1B of the basic flow. NCIAModelMap file exists and is correct.
	//Test if tag (0x0010,0x2180) maps to "gov.nih.nci.ncia.domain.Study.occupation"
	public void testMapDicomTagToNCIATagName1B()  {						
		String nciaName = util.mapDicomTagToNCIATagName("(0x0010,0x2180)");		
		assertEquals("Data is valid but mapping failed.", "gov.nih.nci.ncia.domain.Study.occupation", nciaName);		
	}
	
	//GridUtil 1C of the basic flow. NCIAModelMap file exists and is correct.
	//Test if tag (0x0088,0x0140) maps to "gov.nih.nci.ncia.domain.Image.storageMediaFileSetUID"
	public void testMapDicomTagToNCIATagName1C()  {						
		String nciaName = util.mapDicomTagToNCIATagName("(0x0088,0x0140)");		
		assertEquals("Data is valid but mapping failed.", "gov.nih.nci.ncia.domain.Image.storageMediaFileSetUID", nciaName);		
	}
	
	//GridUtil 1D of the basic flow. NCIAModelMap file exists and is correct.
	//Test if tag (0x0020,0x000e) maps to "gov.nih.nci.ncia.domain.Series.instanceUID"
	//Test for resistance to upper and lower case tags
	public void testMapDicomTagToNCIATagName1D()  {						
		String nciaName = util.mapDicomTagToNCIATagName("(0x0020,0x000e)");		
		assertEquals("Data is valid but mapping failed.", "gov.nih.nci.ncia.domain.Series.instanceUID", nciaName);		
	}
	
	//GridUtil 1E - test of the alternative flow. NCIAModelMap file exists, is correct but no tag found in the file. 
	public void testMapDicomTagToNCIATagName1E()  {						
		String nciaName = util.mapDicomTagToNCIATagName("(0x0088,0x0000)");		
		assertNull("Tag is not part of NCIAModelMap but mapping returned a value", nciaName);		
	}	
}
