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

package edu.wustl.xipHost.hostControl;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class LoadHostConfigParametersTest extends TestCase {
	HostConfigurator hostConfigurator;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		hostConfigurator = new HostConfigurator();
	}

	//HostConfigurator 1A - basic flow. All parameters are correct. Config file exists and is valid.
	//Expected result: boolean true
	public void testLoadHostConfigurationParameters1A() {						
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfig1A.xml");
		boolean isLoadOK = hostConfigurator.loadHostConfigParameters(configFile);
		boolean areParamsOK = (hostConfigurator.getParentOfTmpDir().equalsIgnoreCase("./test-content/TmpXIP") &&
				hostConfigurator.getParentOfOutDir().equalsIgnoreCase("./test-content/OutXIP") &&
				hostConfigurator.getDisplayStartUp() == false);
		assertTrue("File exists and XML is valid but system was unable to load parameters.", isLoadOK && areParamsOK);
	}
	
	//HostConfigurator 1Ba - alternative flow. 
	//Config file does not exist
	//Excpected result; boolean false
	public void testLoadHostConfigurationParameters1Ba() {						
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfigNonExisting.xml");
		boolean isLoadOK = hostConfigurator.loadHostConfigParameters(configFile);		
		assertFalse("File does not exist but system did not report error.", isLoadOK);
	}

	//HostConfigurator 1Bb - alternative flow. 
	//Config file is null
	//Expected result: boolean false
	public void testLoadHostConfigurationParameters1Bb() {						
		File configFile = null;
		boolean isLoadOK = hostConfigurator.loadHostConfigParameters(configFile);		
		assertFalse("File is null but system did not report error.", isLoadOK);
	}
	
	//HostConfigurator 1Ca - alternative flow. 
	//Config file is valid XML but XML element is missing
	//Expected result: boolean true (load with empty parameters)
	public void testLoadHostConfigurationParameters1Ca() {						
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfig1C.xml");
		boolean isLoadOK = hostConfigurator.loadHostConfigParameters(configFile);		
		assertTrue("XML file is missing elements and system generated error when actually should assigned empty values.", isLoadOK);
	}

	//HostConfigurator 1Cb - alternative flow. 
	//Config file is not valid XML file (no closing tag)
	//Expected result: boolean true 
	public void testLoadHostConfigurationParameters1Cb() {						
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfig1Cb.xml");
		boolean isLoadOK = hostConfigurator.loadHostConfigParameters(configFile);		
		assertFalse("File is not valid XML file but system did not report error.", isLoadOK);
	}
	
	//HostConfigurator 1D - alternative flow. 
	//Config file exists, XML is valid, but elements are empty strings
	//Result: boolean true and displayOnStartup must be true
	public void testLoadHostConfigurationParameters1D() {						
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfig1D.xml");
		hostConfigurator.loadHostConfigParameters(configFile);		
		assertTrue("Config file elements are empty but system did not detect it.", hostConfigurator.getDisplayStartUp());
	}
	
	//HostConfigurator 1E - alternative flow. 
	//Config file exists, XML is valid, but TmpDir and OutputDir point to the same directory
	//Result: boolean true and displayOnStartup with empty directories
	public void testLoadHostConfigurationParameters1E() {						
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfig1E.xml");
		boolean isLoadOK =  hostConfigurator.loadHostConfigParameters(configFile);		
		boolean areParamsOK = (hostConfigurator.getParentOfTmpDir().equalsIgnoreCase("") &&
				hostConfigurator.getParentOfOutDir().equalsIgnoreCase("") &&
				hostConfigurator.getDisplayStartUp() == true);
		assertTrue("Unable to load config parameters when TmpDir and OutputDir point to the same dirs.", isLoadOK);
		assertTrue("Config parameters are not valid.", areParamsOK);
	}
	
	//HostConfigurator 1F - alternative flow. 
	//Config file exists, XML is valid, but TmpDir and OutputDir dirs do not exist
	//Result: boolean true and displayOnStartup with empty directories
	public void testLoadHostConfigurationParameters1F() {	
		File configFile = new File("./src-tests/edu/wustl/xipHost/hostControl/xipConfig1F.xml");
		boolean isLoadOK =  hostConfigurator.loadHostConfigParameters(configFile);		
		boolean areParamsOK = (hostConfigurator.getParentOfTmpDir().equalsIgnoreCase("") &&
				hostConfigurator.getParentOfOutDir().equalsIgnoreCase("") &&
				hostConfigurator.getDisplayStartUp() == true);
		assertTrue("Unable to load config parameters when TmpDir and OutputDir point to the same dirs.", isLoadOK);
		assertTrue("Config parameters are not valid.", areParamsOK);
	}
	
}
