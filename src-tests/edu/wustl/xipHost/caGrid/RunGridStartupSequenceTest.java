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
import java.io.IOException;
import org.jdom.JDOMException;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RunGridStartupSequenceTest extends TestCase {
	GridManager gridMgr;
	GridUtil util;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		gridMgr = new GridManagerImpl();
		util = new GridUtil();
	}

	//GridManagerImpl 1A - basic flow. loadGridLocations == true,
	//loadNCIAModelMap returned valid map.
	public void testRunGlobalSearchStartupSequence1A() throws IOException, JDOMException {										              		
		File file1 = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1A.xml");		
		File file2 = new File("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");		
		gridMgr.setGridLocationFile(file1);
		gridMgr.setNCIAModelMapFile(file2);
		boolean bln = gridMgr.runGridStartupSequence();
		assertTrue("All elemenst of startup squence were perfect but system failed to successfully complete it.", bln);
	}
		
	//GridManagerImpl 1B - alternative flow.  
	//loadGridLocations == false, file does not exist
	//loadNCIAModelMap returned valid map.
	public void testRunGlobalSearchStartupSequence1B() throws IOException, JDOMException {										              
		File file1 = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locationsDoesNotExist.xml");		
		File file2 = new File("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");		
		gridMgr.setGridLocationFile(file1);
		gridMgr.setNCIAModelMapFile(file2);
		boolean bln = gridMgr.runGridStartupSequence();
		assertFalse("System was able to startup global search without grid locations file.", bln);
	}
	
	//GridManagerImpl 1C - alternative flow.  
	//loadGridLocations == true
	//loadNCIAModelMap file could not be found.
	public void testRunGlobalSearchStartupSequence1C() throws IOException, JDOMException {										              
		File file1 = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1A.xml");		
		File file2 = new File("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMapDoesNotExist.properties");
		gridMgr.setGridLocationFile(file1);
		gridMgr.setNCIAModelMapFile(file2);
		boolean bln = gridMgr.runGridStartupSequence();
		assertFalse("System was able to startup global search without NCIAModelMap.properties file.", bln);
	}
	
	//GridManagerImpl 1D - alternative flow.  
	//loadGridLocations == true
	//loadNCIAModelMap file is null.
	//in case any configuration file is null the default file location will be taken. Specified in GlobalManagaer.
	public void testRunGlobalSearchStartupSequence1D() throws IOException, JDOMException {										              
		File file1 = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1A.xml");		
		File file2 = null;		
		gridMgr.setGridLocationFile(file1);
		gridMgr.setNCIAModelMapFile(file2);
		boolean bln = gridMgr.runGridStartupSequence();
		assertFalse("System was able to startup global search without NCIAModelMap.properties file.", bln);
	}
	
	//GridManagerImpl 1E - alternative flow. 	
	//loadGridLocations == true
	//loadNCIAModelMap file is null.
	public void testRunGlobalSearchStartupSequence1E() throws IOException, JDOMException {										              
		File file1 = new File("./src-tests/edu/wustl/xipHost/caGrid/grid_locations1A.xml");		
		File file2 = new File("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMapDoesNotExist.properties");
		gridMgr.setGridLocationFile(file1);
		gridMgr.setNCIAModelMapFile(file2);
		boolean bln = gridMgr.runGridStartupSequence();
		assertFalse("System was able to startup global search without pacs_Locations file.", bln);
	}
}
