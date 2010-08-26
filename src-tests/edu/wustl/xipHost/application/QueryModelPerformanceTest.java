/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.nema.dicom.wg23.QueryResult;
import org.nema.dicom.wg23.Uuid;

import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.XindiceManager;
import edu.wustl.xipHost.hostControl.XindiceManagerFactory;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryModelPerformanceTest extends TestCase {
	XindiceManager xm;
	String collectionName;
	Uuid objUUID;	
	String xpath;
	SAXBuilder builder = new SAXBuilder();
	Document doc;
	File nativeModelFile;
	List<Uuid> modelUUIDs;
	List<String> xpaths;
	Application app;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();		
		objUUID = new Uuid();		
		xpath = "/";		
		nativeModelFile = new File("./src-tests/edu/wustl/xipHost/hostControl/testQuery.xml");
		doc = builder.build(nativeModelFile);
		modelUUIDs = new ArrayList<Uuid>();
		xpaths = new ArrayList<String>();
		app = new Application("Application1", new File("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
		collectionName = app.getID().toString();
		xm = XindiceManagerFactory.getInstance();
		xm.startup();
		xm.createCollection(collectionName);
		Uuid modelUUID = new Uuid();
		for(int i = 0; i < 124; i++){
			objUUID.setUuid(String.valueOf(i));
			modelUUID.setUuid("wg23NM-"+ String.valueOf(i));
			modelUUIDs.add(modelUUID);
			xm.addDocument(doc, collectionName, objUUID);
		}		
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
	
	//Application 1A - basic flow. collection exists, model name is valid and xpath is valid
	//Performance test
	//Result: String[]
	public void testQueryModelPerformance1A(){		
		xpaths.add("/DICOM_DATASET/ELEMENT[@name=\"SOPInstanceUID\"]/value[@number=\"1\"]/text()");
		//xpaths.add("/");
		long time1 = System.currentTimeMillis();
		List<QueryResult> results = app.queryModel(modelUUIDs, xpaths);						
		long time2 = System.currentTimeMillis();
		long totalTime = (time2 - time1);
		System.out.println("Total query time " + totalTime);
		assertNotNull("Perfect condictions but system did not return query results.", results);
		assertTrue("Number of returned results shoul de greater than zero.", results.size() > 0);
		Boolean bln = totalTime < new Double(600.0);
		assertTrue("Query did not return results in 600 ms.", bln);
	}		
}
