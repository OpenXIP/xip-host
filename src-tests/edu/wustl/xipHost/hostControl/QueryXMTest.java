/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import edu.wustl.xipHost.wg23.Uuid;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryXMTest extends TestCase {
	XindiceManager xm;
	String collectionName;
	Uuid objUUID;
	Uuid modelUUID;
	String xpath;
	SAXBuilder builder = new SAXBuilder();
	Document doc;
	File nativeModelFile;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		xm = XindiceManagerFactory.getInstance();
		xm.startup();
		collectionName = "123";
		objUUID = new Uuid();
		objUUID.setUuid("123456");
		modelUUID = new Uuid();
		modelUUID.setUuid("wg23NM-" + objUUID.getUuid());
		//xpath = "/ns:DicomDataSet/ns:DicomAttribute[@keyword=\"SOPInstanceUID\"]/ns:Value[@number=\"1\"]/text()";				
		xpath = "/DicomDataSet/DicomAttribute[@keyword=\"SOPInstanceUID\"]/Value[@number=\"1\"]/text()";
		nativeModelFile = new File("./src-tests/edu/wustl/xipHost/hostControl/testQuery.xml");
		doc = builder.build(nativeModelFile);		
		xm.createCollection(collectionName);
		xm.addDocument(doc, collectionName, objUUID);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		xm.deleteAllDocuments(collectionName);
		xm.deleteCollection(collectionName);
		xm.shutdown();
	}

	//XindiceManager 1A - basic flow. collection exists, model name is valid and xpath is valid
	//Result: String[]
	public void testCreateCollection1A(){				
		String[] results = xm.query(collectionName, modelUUID, xpath);		
		assertNotNull("Perfect condictions but system did not return query results.", results);
		assertTrue("Number of returned results shoul de greater than zero.", results.length > 0);
	}
	
	//XindiceManager 1Aa - basic flow. collection exists, model name is valid and xpath is valid
	//Performance test
	//Result: String[]
	public void testCreateCollection1Aa(){				
		long time1 = System.currentTimeMillis();
		String[] results = xm.query(collectionName, modelUUID, xpath);		
		long time2 = System.currentTimeMillis();
		long totalTime = (time2 - time1);
		//System.out.println(totalTime);
		assertNotNull("Perfect condictions but system did not return query results.", results);
		assertTrue("Number of returned results shoul de greater than zero.", results.length > 0);
		Boolean bln = totalTime < new Double(120.0);
		assertTrue("Query did not run in less than 120 ms.", bln);
	}
	
	
	//XindiceManager 1A - alternative flow. collection does not exist, model name is valid and xpath is valid
	//Result: null
	public void testCreateCollection1B(){
		assertNull("Collacation name does not exist but system did not resport error.", xm.query("nonExistingName", modelUUID, xpath));
	}
	
	//XindiceManager 1C - alternative flow. collection exists, model name is null, xpath is valid
	//Result: null
	public void testCreateCollection1Ca(){
		String[] results = xm.query(collectionName, null, xpath);		
		assertNull("Model name is null but system did not detect error.", results);
	}
	
	//XindiceManager 1Da - alternative flow. collection exists, model name is valid, xpath is null
	//Result: null
	public void testCreateCollection1Da(){		
		String[] results = xm.query(collectionName, modelUUID, null);		
		assertNull("xpath is null but system did not detect error.", results);		
	}
	
	//XindiceManager 1Db - alternative flow. collection exists, model name is valid, xpath is empty
	//Result: null
	public void testCreateCollection1D(){		
		String[] results = xm.query(collectionName, modelUUID, "");		
		assertNull("xpath is empty", results);		
	}
}
