/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class CreateCollectionXMTest extends TestCase {	
	String collectionName = "NativeModelTestCollection";
	XindiceManager xm;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		xm = XindiceManagerFactory.getInstance(); 
		xm.startup();
	}
	
	public void tearDown() throws Exception {
		super.tearDown();				
		xm.shutdown();
	}

	//XindiceManager 1A - basic flow. collectionName is valid.
	//Result: boolean true
	public void testCreateCollection1A(){
		assertTrue("Name is valid but system was unable to create collection.", xm.createCollection(collectionName));
		xm.deleteCollection(collectionName);
	}
	
	//XindiceManager 1B - alternative flow. collectionName is empty.
	//Result: boolean false
	public void testCreateCollection1B(){
		assertFalse("Name is empty but system created collection.", xm.createCollection(""));
	}
	
	//XindiceManager 1C - alternative flow. collectionName is null.
	//Result: boolean false
	public void testCreateCollection1C(){
		assertFalse("Name is null but system created collection.", xm.createCollection(null));
	}
	
	//XindiceManager 1D - alternative flow. collectionName contains invalid characters.
	//Result: boolean false
	public void testCreateCollection1D(){
		assertFalse("Name contains invalid characters but system created collection.", xm.createCollection("testColl&"));		
	}	
}
