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

package edu.wustl.xipHost.avt2ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.iterator.IteratorElementEvent;
import edu.wustl.xipHost.iterator.IteratorEvent;
import edu.wustl.xipHost.iterator.TargetElement;
import edu.wustl.xipHost.iterator.TargetIteratorListener;
import edu.wustl.xipHost.iterator.TargetIteratorRunner;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveDICOMwithAVTRetrieve implements RetrieveListener, TargetIteratorListener {
	final static Logger logger = Logger.getLogger(RetrieveDICOMwithAVTRetrieve.class);
	static Application app;
	static SearchResultSetupAVTADRetrieve result;
	static List<Uuid> listUUIDs;
	static Query avtQuery;
	static SearchResult selectedDataSearchResult;
	static File avt2ext_retrieve_dir;
	
	@Before
	public void setUpBeforeTest(){
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.PATIENT, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 1){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		app.setTargetElements(targetElements);
		app.setDataSourceDomainName("edu.wustl.xipHost.avt2ext.AVTRetrieve");
		app.setApplicationTmpDir(avt2ext_retrieve_dir);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String exePath = new String("./src-tests/edu/wustl/xipHost/application/test.bat");
		String iconFile = new File("./src-tests/edu/wustl/xipHost/application/test.png").getAbsolutePath();
		app = new Application("Application1", exePath, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES);
		app.setNumberOfSentNotifications(1);
		avtQuery = new AVTQueryStub(null, null, null, null, null);
		result = new SearchResultSetupAVTADRetrieve();
		selectedDataSearchResult = result.getSearchResult();
		DOMConfigurator.configure("log4j.xml");
		avt2ext_retrieve_dir = new File("./test-content/AVT2Retrieve");
		if(avt2ext_retrieve_dir.exists() == false){
			avt2ext_retrieve_dir.mkdir();
		} else {
			File[] files = avt2ext_retrieve_dir.listFiles();
			if(files.length > 0) {
				for(int i = 0 ; i < files.length; i++) {
					File file = files[i];
					file.delete();
				}
			}
		}
	}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDownAfterTest() throws Exception {
		File[] files = avt2ext_retrieve_dir.listFiles();
		for(int i = 0 ; i < files.length; i++) {
			File file = files[i];
			file.delete();
		}
	}

	//INFO: dataset must be preloaded prior to running these JUnit tests from AD_Preload_JUnit_Tests. Use PreloadDICOM and PreloadAIM utility classes in avt2ext to preload database.
	//Application.retrieveAndGetLocators() - basic flow. RetrieveTarget.DICOM.
	//Result: There should be three DICOM objects retrieved from AD database.
	@Test
	public void testRetrieveDICOM() {
		RetrieveTarget retrieveTarget = RetrieveTarget.DICOM;
		app.setRetrieveTarget(retrieveTarget);
		listUUIDs = result.getDICOMObjectsUUIDs();
		List<ObjectLocator> retrievedObjects = app.retrieveAndGetLocators(listUUIDs);
		assertEquals("Number of retrieved items should be 3 but is: " + retrievedObjects.size(), retrievedObjects.size(), 3);
		//Check retrieved file names
		boolean isFileNameCorrect = false;
		if(retrievedObjects.size() == 3){
			for(ObjectLocator objLoc : retrievedObjects) {
				String fileName = new File(objLoc.getUri()).getName();
				if(fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492790.3") || 
						fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492801.5") || 
						fileName.equalsIgnoreCase("1.2.840.113704.1.111.4044.1226687286.21577")){
					isFileNameCorrect = true;
					if(isFileNameCorrect == false) {
						fail("Item: " + objLoc.getUuid().getUuid() + " " + objLoc.getUri() + " shouldn't be retrieved.");
					}
				}
			}
		}
		assertTrue("Retrieved items are not as expected.", isFileNameCorrect);
	}
	
	//INFO: dataset must be preloaded prior to running these JUnit tests from AD_Preload_JUnit_Tests. Use PreloadDICOM and PreloadAIM utility classes in avt2ext to preload database.
	//Application.retrieveAndGetLocators() - basic flow. RetrieveTarget.AIM_SEG.
	//Result: There should be two DICOM SEG and one AIM object retrieved from AD database.
	@Test
	public void testRetrieveAIM() {
		RetrieveTarget retrieveTarget = RetrieveTarget.AIM_SEG;
		app.setRetrieveTarget(retrieveTarget);
		listUUIDs = result.getAIMandSEGObjectsUUIDs();
		List<ObjectLocator> retrievedObjects = app.retrieveAndGetLocators(listUUIDs);
		assertEquals("Number of retrieved items should be 3 but is: " + retrievedObjects.size(), retrievedObjects.size(), 3);
		//Check retrieved file names
		boolean isFileNameCorrect = false;
		if(retrievedObjects.size() == 3){
			for(ObjectLocator objLoc : retrievedObjects) {
				String fileName = new File(objLoc.getUri()).getName();
				if(fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492790.3") || 
						fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492801.5") || 
						fileName.equalsIgnoreCase("1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.3.0")){
					isFileNameCorrect = true;
					if(isFileNameCorrect == false) {
						fail("Item: " + objLoc.getUuid().getUuid() + " " + objLoc.getUri() + " shouldn't be retrieved.");
					}
				}
			}
		}
		assertTrue("Retrieved items are not as expected.", isFileNameCorrect);
	}
	
	//INFO: dataset must be preloaded prior to running these JUnit tests from AD_Preload_JUnit_Tests. Use PreloadDICOM and PreloadAIM utility classes in avt2ext to preload database.
	//Application.retrieveAndGetLocators() - basic flow. RetrieveTarget.DICOM_AIM_SEG.
	//Result: There should be four DICOM and one AIM objects retrieved from AD database.
	@Test
	public void testRetrieveDICOMandAIMSEG() {
		RetrieveTarget retrieveTarget = RetrieveTarget.DICOM_AIM_SEG;
		app.setRetrieveTarget(retrieveTarget);
		listUUIDs = result.getDICOMandAIMandSEGObjectsUUIDs();
		List<ObjectLocator> retrievedObjects = app.retrieveAndGetLocators(listUUIDs);
		assertEquals("Number of retrieved items should be 4 but is: " + retrievedObjects.size(), retrievedObjects.size(), 4);
		//Check retrieved file names
		boolean isFileNameCorrect = false;
		if(retrievedObjects.size() == 4){
			for(ObjectLocator objLoc : retrievedObjects) {
				String fileName = new File(objLoc.getUri()).getName();
				if(fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492790.3") || 
						fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492801.5") || 
						fileName.equalsIgnoreCase("1.2.840.113704.1.111.4044.1226687286.21577") ||
						fileName.equalsIgnoreCase("1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.3.0")){
					isFileNameCorrect = true;
					if(isFileNameCorrect == false) {
						fail("Item: " + objLoc.getUuid().getUuid() + " " + objLoc.getUri() + " shouldn't be retrieved.");
					}
				}
			}
		}
		assertTrue("Retrieved items are not as expected.", isFileNameCorrect);
	}
	
	//INFO: dataset must be preloaded prior to running these JUnit tests from AD_Preload_JUnit_Tests. Use PreloadDICOM and PreloadAIM utility classes in avt2ext to preload database.
	//Application.retrieveAndGetLocators() - alternative flow. Number of UUIDs sent from hosted application is greater than UUIDs on the Host (Host can't find UUID). 
	//Result: There should be four DICOM and one AIM objects retrieved from AD database. There shoul be a logging message for the UUID of the object that is not found in AD. 
	@Test
	public void testRetrieveUUIDNotFound() {
		RetrieveTarget retrieveTarget = RetrieveTarget.DICOM_AIM_SEG;
		app.setRetrieveTarget(retrieveTarget);
		listUUIDs = result.getDICOMandAIMandSEGObjectsUUIDs();
		Uuid notFoundUUID = new Uuid();
		notFoundUUID.setUuid(UUID.randomUUID().toString());
		listUUIDs.add(notFoundUUID);
		List<ObjectLocator> retrievedObjects = app.retrieveAndGetLocators(listUUIDs);
		assertEquals("Number of retrieved items should be 4 but is: " + retrievedObjects.size(), retrievedObjects.size(), 4);
		//Check retrieved file names
		boolean isFileNameCorrect = false;
		if(retrievedObjects.size() == 4){
			for(ObjectLocator objLoc : retrievedObjects) {
				String fileName = new File(objLoc.getUri()).getName();
				if(fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492790.3") || 
						fileName.equalsIgnoreCase("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492801.5") || 
						fileName.equalsIgnoreCase("1.2.840.113704.1.111.4044.1226687286.21577") ||
						fileName.equalsIgnoreCase("1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.3.0")){
					isFileNameCorrect = true;
					if(isFileNameCorrect == false) {
						fail("Item: " + objLoc.getUuid().getUuid() + " " + objLoc.getUri() + " shouldn't be retrieved.");
					}
				}
			}
		}
		assertTrue("Retrieved items are not as expected.", isFileNameCorrect);
	}
	

	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		
	}
	
	Iterator<TargetElement> iter;
	@SuppressWarnings("unchecked")
	@Override
	public void fullIteratorAvailable(IteratorEvent e) {		
		iter = (Iterator<TargetElement>) e.getSource();
	}
	
	List<TargetElement> targetElements = new ArrayList<TargetElement>();
	@Override
	public void targetElementAvailable(IteratorElementEvent e) {
		synchronized(targetElements){
			TargetElement element = (TargetElement) e.getSource();
			logger.debug("TargetElement available. ID: " + element.getId() + " at time " + System.currentTimeMillis());
			targetElements.add(element);
			targetElements.notify();
		}
	}
}
