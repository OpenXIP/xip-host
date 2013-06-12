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
