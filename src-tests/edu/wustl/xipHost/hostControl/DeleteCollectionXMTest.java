/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class DeleteCollectionXMTest extends TestCase {	
	String collectionName = "NativeModelTestCollection"; 
	XindiceManager xm;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		xm = XindiceManagerFactory.getInstance();
		xm.startup();
		xm.createCollection(collectionName);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		xm.shutdown();
	}

	//XindiceManager 1A - basic flow. collectionName is valid.
	//Result: boolean true
	public void testDeleteCollection1A(){
		assertTrue("Name is valid but system was unable to delete collection.", xm.deleteCollection(collectionName));		
	}
	
	//XindiceManager 1A - alternative flow. collectionName is valid but does not exist.
	//Result: boolean false
	public void testDeleteCollection1B(){
		assertFalse("Name is valid but collectionn does not exists and system was able to delete collection.", xm.deleteCollection("NonExisting"));
		xm.deleteCollection(collectionName);
	}
}
