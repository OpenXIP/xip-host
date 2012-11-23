/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.nema.dicom.PS3_19.ModelSetDescriptor;
import org.nema.dicom.PS3_19.UUID;
import edu.wustl.xipHost.wg23.Uuid;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.XindiceManager;
import edu.wustl.xipHost.hostControl.XindiceManagerFactory;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class GetModelSetDescriptorTest extends TestCase {
	XindiceManager xm;
	String collectionName; 
	Application app;
	List<UUID> objUUIDs;
	File exePath = new File("./src-tests/edu/wustl/xipHost/application/test.bat");
	File iconFile = new File("src-tests/edu/wustl/xipHost/application/test.png");
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		xm = XindiceManagerFactory.getInstance(); 
		xm.startup();
		app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		collectionName = app.getID().toString();
		xm.createCollection(collectionName);
		Uuid uuid1 = new Uuid();
		uuid1.setUuid("1234");		
		Document doc = new Document();
		Element root = new Element("DICOM_DATASET");
		doc.setRootElement(root);
		Element elem = new Element("test");
		elem.setText("test");
		root.addContent(elem);
		xm.addDocument(doc, collectionName, uuid1);		
		objUUIDs = new ArrayList<UUID>();
		Uuid uuid2 = new Uuid();
		uuid2.setUuid("2345");
		objUUIDs.add(uuid1);
		objUUIDs.add(uuid2);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		xm.deleteCollection(collectionName);
		xm.shutdown();
	}

	//Application 1A - basic flow. Objects uuids are valid.
	public void testGetModelSetDescriptor1A() {	
		ModelSetDescriptor msd = app.getModelSetDescriptor(objUUIDs);
		List<UUID> models = msd.getModels().getUUID();
		int num1 = models.size();
		List<UUID> failedObjs = msd.getFailedSourceObjects().getUUID();
		int num2 = failedObjs.size();
		assertEquals("Number of models uuid should be 1.", 1, num1);
		assertEquals("Number of faile source objects should be 1.", 1, num2);		
	}
	
	//Application 1B - alternative flow. Object uuid is null.
	public void testGetModelSetDescriptor1B() {	
		Uuid uuid3 = null;
		objUUIDs.add(uuid3);
		ModelSetDescriptor msd = app.getModelSetDescriptor(objUUIDs);
		List<UUID> models = msd.getModels().getUUID();
		int num1 = models.size();
		List<UUID> failedObjs = msd.getFailedSourceObjects().getUUID();
		int num2 = failedObjs.size();
		assertEquals("Number of models uuid should be 1.", 1, num1);
		assertEquals("Number of faile source objects should be 1.", 1, num2);
	}
	
	//Application 1C - alternative flow. Object uuid string is null.
	public void testGetModelSetDescriptor1C() {	
		Uuid uuid3 = new Uuid();
		objUUIDs.add(uuid3);
		ModelSetDescriptor msd = app.getModelSetDescriptor(objUUIDs);
		List<UUID> models = msd.getModels().getUUID();
		int num1 = models.size();
		List<UUID> failedObjs = msd.getFailedSourceObjects().getUUID();
		int num2 = failedObjs.size();
		assertEquals("Number of models uuid should be 1.", 1, num1);
		assertEquals("Number of faile source objects should be 1.", 1, num2);
	}
	
}
