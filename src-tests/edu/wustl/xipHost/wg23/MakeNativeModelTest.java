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

package edu.wustl.xipHost.wg23;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;

import com.pixelmed.dicom.DicomInputStream;

import edu.wustl.xipHost.application.Application;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class MakeNativeModelTest extends TestCase implements NativeModelListener{
	NativeModelRunner nmRunner;
	File file;
	
	
	DicomInputStream dicomInputStream;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();		
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//NativeModelRunner 1A - basic flow. Constructor accepting ObjectLocator is used.
	//ObjectLocator is valid and points to existing dcm file
	//Result: JDOM Document
	public void testMakeNativeModel1A()throws Exception {
		ObjectLocator objLoc = new ObjectLocator();
		Uuid uuid = new Uuid();
		uuid.setUuid("1");
		objLoc.setUuid(uuid);
		file = new File("./src-tests/edu/wustl/xipHost/wg23/dcm_with_SQ.dcm");
		String strFileURL = file.toURI().toURL().toExternalForm();
		objLoc.setUri(strFileURL);		
		nmRunner = new NativeModelRunner(objLoc);		
		nmRunner.addNativeModelListener(this);
		nmRunner.run();			
		assertNotNull("Perfect conditions but system unable to create NativeModel", doc);				
	}
	
	//NativeModelRunner 1B - basic flow. Constructor accepting InputStream is used.
	//InputStream is valid.
	//Result: String XML
	public void testMakeNativeModel1B()throws Exception {
		file = new File("src-tests/edu/wustl/xipHost/wg23/dcm_with_SQ.dcm");
		String strFileURL = file.toURI().toURL().toExternalForm();
		strFileURL = file.getAbsolutePath();
		InputStream inputStream = new FileInputStream(strFileURL);		
		nmRunner = new NativeModelRunner(inputStream);
		nmRunner.addNativeModelListener(this);
		nmRunner.run();
		assertTrue("Perfect conditions but system unable to create NativeModel - empty String", !xml.isEmpty());
		assertNotNull("Perfect conditions but system unable to create NativeModel", xml);
	}
	
	//NativeModelRunner 1Ba - alternative flow. ObjectLocator is null
	//Result: IllegalArgumentException
	public void testMakeNativeModelAa() throws IOException {
		ObjectLocator objLoc = null;
		try{
			nmRunner = new NativeModelRunner(objLoc);
			fail("ObjectLocator == null supplied to the constructor.");
		}catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}	
	
	//NativeModelRunner 1Ab - alternative flow. ObjectLocator's URI is an empty string
	//Result: IllegalArgumentException
	public void testMakeNativeModelBb() throws IOException {
		ObjectLocator objLoc = new ObjectLocator();
		Uuid uuid = new Uuid();
		uuid.setUuid("1");
		objLoc.setUuid(uuid);		
		objLoc.setUri("");		
		try{
			nmRunner = new NativeModelRunner(objLoc);
			fail("ObjectLocator's URI was empty.");
		}catch (IllegalArgumentException e){
			assertTrue(true);
		}				
	}
	
	//NativeModelRunner 1Ac - alternative flow. ObjectLocator's URI points to nonexisting file
	//Result: IllegalArgumentException
	public void testMakeNativeModelBc() throws IOException {		
		ObjectLocator objLoc = new ObjectLocator();
		Uuid uuid = new Uuid();
		uuid.setUuid("1");
		objLoc.setUuid(uuid);		
		objLoc.setUri("./src-tests/edu/wustl/xipHost/wg23/nonexistingFile.txt");	
		try{
			nmRunner = new NativeModelRunner(objLoc);
			fail("ObjectLocator's URI points to nonexisting file.");
		}catch (IllegalArgumentException e){
			assertTrue(true);
		}					
	}
	
	//NativeModelRunner 1Ad - alternative flow. ObjectLocator's URI is valid but points to non dcm file
	//Result: IllegalArgumentException
	public void testMakeNativeModelD() throws IOException {
		ObjectLocator objLoc = new ObjectLocator();
		Uuid uuid = new Uuid();
		uuid.setUuid("1");
		objLoc.setUuid(uuid);		
		objLoc.setUri("./src-tests/edu/wustl/xipHost/wg23/nondcm.txt");			
		try{
			nmRunner = new NativeModelRunner(objLoc);
			fail("ObjectLocator points to non DCM.");
		}catch (IllegalArgumentException e){
			assertTrue(true);
		}			
	}
	
	//NativeModelRunner 1Ba - alternative flow. InputStream supplied to the constructor is null 
	//Result: IllegalArgumentException
	public void testMakeNativeModelBa() throws IOException {
		InputStream inputStream = null;					
		try{
			nmRunner = new NativeModelRunner(inputStream);
			fail("InputStream is null.");
		}catch (IllegalArgumentException e){
			assertTrue(true);
		}			
	}
	
	Document doc;
	public void nativeModelAvailable(Document doc, Uuid objUUID) {
		this.doc = doc;			
	}

	String xml;	
	public void nativeModelAvailable(String xmlNativeModel) {
		xml = xmlNativeModel;
		System.out.println(xml);		
	}
	
}
