/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;

import edu.wustl.xipHost.application.Application;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class MakeNativeModelTest extends TestCase {
	NativeModelRunner nmRunner;
	File file;
	String strFileURL;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();		
		ObjectLocator objLoc = new ObjectLocator();
		Uuid uuid = new Uuid();
		uuid.setUuid("1");
		objLoc.setUuid(uuid);
		//file = new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20034.dcm");
		file = new File("./src-tests/edu/wustl/xipHost/wg23/dcm_with_SQ.dcm");
		strFileURL = file.toURI().toURL().toExternalForm();
		objLoc.setUri(strFileURL);		
		nmRunner = new NativeModelRunner(objLoc);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//NativeModelRunner 1A - basic flow. strFileURL is valid and points to existing dcm file
	//Result: JDOM Document
	public void testMakeNativeModel1A() {
		Document doc = nmRunner.makeNativeModel(strFileURL);		
		assertNotNull("Perfect conditions but system unable to create NativeModel", doc);
		String str = new XMLOutputter().outputString(doc);
		System.out.println(str);
	}
	
	//NativeModelRunner 1Ba - alternative flow. strFileURL is null
	//Result: JDOM Document = null
	public void testMakeNativeModelBa() {
		Document doc = nmRunner.makeNativeModel(null);		
		assertNull("strFileURL is null but system did not catch the error", doc);		
	}	
	
	//NativeModelRunner 1Bb - alternative flow. strFileURL is an empty string
	//Result: JDOM Document = null
	public void testMakeNativeModelBb() {
		Document doc = nmRunner.makeNativeModel("  ");		
		assertNull("strFileURL is an empty string but system did not catch the error", doc);		
	}
	
	//NativeModelRunner 1Bc - alternative flow. strFileURL is not valid
	//Result: JDOM Document = null
	public void testMakeNativeModelBc() {		
		Document doc = nmRunner.makeNativeModel("  file:/");		
		assertNull("strFileURL is is not valid but system did not catch the error", doc);		
	}
	
	//NativeModelRunner 1C - alternative flow. strFileURL is valid but point to nonexisting file
	//Result: JDOM Document = null
	public void testMakeNativeModelC() {		
		Document doc = nmRunner.makeNativeModel("./src-tests/edu/wustl/xipHost/wg23/nonexistingFile.txt");		
		assertNull("strFileURL points to nonexisting file but system did not catch the error", doc);		
	}
	
	//NativeModelRunner 1D - alternative flow. strFileURL is valid but point to non dcm file
	//Result: JDOM Document = null
	public void testMakeNativeModelD() throws MalformedURLException {
		file = new File("./src-tests/edu/wustl/xipHost/wg23/nondcm.txt");
		strFileURL = file.toURI().toURL().toExternalForm();
		Document doc = nmRunner.makeNativeModel(strFileURL);		
		assertNull("strFileURL points to nonexisting file but system did not catch the error", doc);		
	}
	
}
