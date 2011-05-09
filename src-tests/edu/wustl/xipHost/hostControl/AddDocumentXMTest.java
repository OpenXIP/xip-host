/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import org.jdom.Document;
import org.jdom.Element;
import org.nema.dicom.wg23.Uuid;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class AddDocumentXMTest extends TestCase {	
	XindiceManager xm;
	String collectionName = "NativeModelTestCollection";	
	Uuid objUUID; 
	Document doc;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		xm = XindiceManagerFactory.getInstance(); 
		xm.startup();
		xm.createCollection(collectionName);
		objUUID = new Uuid();
		objUUID.setUuid("123");	
		doc = new Document();
		Element root = new Element("DICOM_DATASET");
		doc.setRootElement(root);
		Element elem = new Element("test");
		elem.setText("test");
		root.addContent(elem);
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

	//XindiceManager 1A - basic flow. Document is a valid JDOM XML docuemnt and collectionName is valid.
	//Result: boolean true
	public void testAddDocument1A(){
		assertTrue("Perfect conditions but system was unable to add the document.", xm.addDocument(doc, collectionName, objUUID));
		
	}
		
	//XindiceManager 1B - alternative flow. Document is null and collectionName is valid.
	//Result: boolean false
	public void testAddDocument1B(){		
		assertFalse("File is null but system did not report error.", xm.addDocument(null, collectionName, objUUID));
	}
	
	//XindiceManager 1C - alternative flow. Document is valid and collection does not exist.
	//Result: boolean true
	public void testAddDocument1C(){		
		collectionName = "nonexisting";
		assertFalse("Cillecation does not exist but system did not report error.", xm.addDocument(doc, collectionName, objUUID));
		xm.deleteCollection("NativeModelTestCollection");
	}
}
