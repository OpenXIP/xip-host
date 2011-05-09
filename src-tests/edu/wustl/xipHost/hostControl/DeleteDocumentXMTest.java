/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;

import org.jdom.Document;
import org.jdom.Element;
import org.nema.dicom.wg23.Uuid;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class DeleteDocumentXMTest extends TestCase {	
	XindiceManager xm;
	String collectionName = "NativeModelTestCollection";
	File file = new File("./src-tests/edu/wustl/xipHost/hostControl/outputPixelmed.xml");
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	String id;
	protected void setUp() throws Exception {
		super.setUp();
		xm = XindiceManagerFactory.getInstance();
		xm.startup();
		xm.createCollection(collectionName);
		Uuid objUUID = new Uuid();
		objUUID.setUuid("123");	
		Document doc = new Document();
		Element root = new Element("DICOM_DATASET");
		doc.setRootElement(root);
		Element elem = new Element("test");
		elem.setText("test");
		root.addContent(elem);
		xm.addDocument(doc, collectionName, objUUID);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		xm.deleteCollection(collectionName);
		xm.shutdown();
	}

	//XindiceManager 1A - basic flow. String id and collectionName are valid.
	//Result: boolean true
	public void testDeleteDocument1A(){
		String id = xm.getModelUUIDs(collectionName)[0];
		assertTrue("Perfect conditions but system was unable to delete the document.", xm.deleteDocument(id, collectionName));
	}
	
	//XindiceManager 1B - alternative flow. Document with given id does not exist
	//and collectionName is valid.
	//Result: boolean false
	public void testDeleteDocument1B(){
		String id = "1234Test";
		assertFalse("Document with given id does not exist and system did not report errors.", xm.deleteDocument(id, collectionName));
	}
	
	//XindiceManager 1C - alternative flow. Document id is null
	//and collectionName is valid.
	//Result: boolean false
	public void testDeleteDocument1C(){
		String id = null;
		assertFalse("Document is null and system did not report errors.", xm.deleteDocument(id, collectionName));
	}
}
