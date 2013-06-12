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

package edu.wustl.xipHost.localFileSystem;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RunFileRunnerTest extends TestCase implements DicomParseListener {
	DICOMFileRunner runner;
	String[][] result;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//FileRunner 1A - basic flow. Item is a valid dicom file.
	//Result: resulting String[][] is noy null has length equal 1
	public void testRunFileRunner1A(){
		runner = new DICOMFileRunner(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20034.dcm"));
		runner.addDicomParseListener(this);
		runner.run();
		int size = result.length;
		assertNotNull("Error when parsing valid dicom file", result);
		assertTrue("Error when parsing valid dicom file.", size > 0);
		assertTrue("Error when parsing valid dicom file.", !result[0][1].isEmpty());
	}
	
	//FileRunner 1B - alternative flow. Item is a not a dicom file.
	//Result: null
	public void testRunFileRunner1B(){
		runner = new DICOMFileRunner(new File("./src-tests/edu/wustl/xipHost/dicom/text.txt"));
		runner.addDicomParseListener(this);
		runner.run();
		assertNull("System did not recognize non dicom file.", result);
	}
	
	//FileRunner 1C - alternative flow. Item is null.
	//Result: null
	public void testRunFileRunner1C(){
		runner = new DICOMFileRunner(null);
		runner.addDicomParseListener(this);
		runner.run();
		assertNull("System was unable to handle item equals null.", result);
	}

	public void dicomAvailable(DicomParseEvent e) {
		DICOMFileRunner source = (DICOMFileRunner)e.getSource();
		result = source.getParsingResult();
		
	}

	public void nondicomAvailable(DicomParseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
