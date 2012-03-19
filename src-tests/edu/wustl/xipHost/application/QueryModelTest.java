/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.nema.dicom.wg23.QueryResult;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.XindiceManager;
import edu.wustl.xipHost.hostControl.XindiceManagerFactory;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryModelTest extends TestCase {
	XindiceManager xm;
	String collectionName;
	Uuid objUUID;		
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
		nativeModelFile = new File("./src-tests/edu/wustl/xipHost/application/testQuery.xml");
		doc = builder.build(nativeModelFile);
		modelUUIDs = new ArrayList<Uuid>();
		xpaths = new ArrayList<String>();
		xpaths.add("/DICOM_DATASET/ELEMENT[@name=\"SOPInstanceUID\"]/value[@number=\"1\"]/text()");
		//xpaths.add("//ELEMENT");
		xpaths.add("/DICOM_DATASET/ELEMENT[@name=\"SOPInstanceUID\"]/value[@number=\"2\"]/text()");
		app = new Application("Application1", new String("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
		collectionName = app.getID().toString();
		xm = XindiceManagerFactory.getInstance();
		xm.startup();
		xm.createCollection(collectionName);
		Uuid modelUUID = new Uuid();
		for(int i = 0; i < 5; i++){
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
		
	//Application 1A - basic flow. list of uuids is valid and list of xpaths is valid
	//Result: return non empty List<QueryResult>.
	public void testQueryModel1A(){
		List<QueryResult> results = app.queryModel(modelUUIDs, xpaths);
		//System.out.println(results.get(0).getResults().getString().get(0));
		//System.out.println(results.get(0).getResults().getString().get(1));
		int n = results.size();
		assertEquals("Number of QueryResults is invalid.", modelUUIDs.size() * xpaths.size(), n);
		boolean bln = true;
		for(int i = 0; i < results.size(); i++){
			int numOfResultsInQueryResult = results.get(i).getResults().getString().size();
			if (numOfResultsInQueryResult != 0 && numOfResultsInQueryResult != 2){
				System.out.println(numOfResultsInQueryResult);
				bln = false;
			}
		}
		assertTrue("QueryResults do not have a right number of returned Strings. It should be one for each QueryResult.", bln);
		//Check if return result string has a right value
		boolean blnIsNotEmpty = results.get(0).getResults().getString().get(0).isEmpty(); 
		assertFalse("Returned value should not be empty.", blnIsNotEmpty);
		
	}

	//Application 2A - alternative flow. list is null and list of xpaths is valid
	//Result: return empty List<QueryResult>.
	public void testQueryModel2A(){
		List<QueryResult> results = app.queryModel(null, xpaths);
		int n = results.size();
		assertEquals("Number of QueryResults is invalid.", 0, n);
	}
	
	//Application 2B - alternative flow. list is empty and list of xpaths is valid
	//Result: return empty List<QueryResult>.
	public void testQueryModel2B(){
		modelUUIDs.clear();
		List<QueryResult> results = app.queryModel(modelUUIDs, xpaths);
		int n = results.size();
		assertEquals("Number of QueryResults is invalid.", 0, n);
	}
	
	//Application 3A - alternative flow. list of UUIDs is valid but list of xpaths is null
	//Result: return empty List<QueryResult>.
	public void testQueryModel3A(){
		List<QueryResult> results = app.queryModel(modelUUIDs, null);
		int n = results.size();
		assertEquals("Number of QueryResults is invalid.", 0, n);
	}
	
	//Application 3B - alternative flow. list of UUIDs is valid but list of xpaths is empty
	//Result: return empty List<QueryResult>.
	public void testQueryModel3B(){
		xpaths.clear();
		List<QueryResult> results = app.queryModel(modelUUIDs, xpaths);
		int n = results.size();
		assertEquals("Number of QueryResults is invalid.", 0, n);
	}
}
