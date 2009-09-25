/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreAimObjectsTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	//AimStore 1A of the basic flow. All parameters: object locators and grid location are correct.
	public void testStoreAimObjects1A() throws IOException, URISyntaxException {		
		List<ObjectLocator> objLocs = new ArrayList<ObjectLocator>();
		ObjectLocator objLoc = new ObjectLocator();
		Uuid objUUID = new Uuid();
		objUUID.setUuid(UUID.randomUUID().toString());
		objLoc.setUuid(objUUID);
		File file = new File("./test-content/AIM_2/Vasari-TCGA6330140190470283886.xml");
		String uri = file.toURI().toURL().toExternalForm();
		objLoc.setUri(uri);
		objLocs.add(objLoc);
		AimStore aimStore = new AimStore(objLocs, null);
		Thread t = new Thread(aimStore);
		t.start();
	}

}
