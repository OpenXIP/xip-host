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

package edu.wustl.xipHost.caGrid;

import static org.junit.Assert.*;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dicom.BasicDicomParser2;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.hostControl.Util;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.dcm4che2.data.Tag;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveNBIATest implements RetrieveListener {
	static GridLocation gridLoc;
	static CQLQuery cqlQuery = null;
	static BasicDicomParser2 parser;
	static File importDir;	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");	
		gridLoc = new GridLocation("http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService", Type.DICOM, "NBIA-5.0", "NBIA Production Server at NCI");
		parser = new BasicDicomParser2();
		importDir = new File("./test-content/TmpXIP_Test");
		if(importDir.exists() == false){
			boolean success = importDir.mkdir();
		    if (!success) {
		        fail("System could not create import directory.");
		    }
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(importDir == null){return;}
		if(importDir.exists()){
			Util.delete(importDir);
		}	
	}

	//GridRetrieveNBIA - retrieve DICOM from the NBIA 1A - basic flow. 
	//CQL statement, GridLocation and import directory are valid and network is on.
	//CQL statement constructed for the Series level 
	//Study and Series InstanceUIDs must be included in CQL statement
	@Test
	public void testRetrieveDicomData1A() throws IOException {
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.4718";
		Retrieve gridRetrieve = new GridRetrieveNBIA();
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		Map<String, Object> aimCriteria = new HashMap<String, Object>();
		dicomCriteria.put(Tag.SeriesInstanceUID, seriesInstanceUID);
		gridRetrieve.setCriteria(dicomCriteria, aimCriteria);
		gridRetrieve.setImportDir(importDir);
		GridManagerFactory.getInstance().setSelectedGridLocation(gridLoc);
		List<ObjectDescriptor> objectDescs = new ArrayList<ObjectDescriptor>();
		ObjectDescriptor objDesc1 = new ObjectDescriptor();
		Uuid objDescUUID = new Uuid();
		objDescUUID.setUuid(UUID.randomUUID().toString());
		objDesc1.setUuid(objDescUUID);
		objDesc1.setMimeType("application/dicom");			
		ObjectDescriptor objDesc2 = new ObjectDescriptor();
		Uuid objDescUUID2 = new Uuid();
		objDescUUID2.setUuid(UUID.randomUUID().toString());
		objDesc2.setUuid(objDescUUID2);
		objDesc2.setMimeType("application/dicom");			
		objectDescs.add(objDesc1);
		objectDescs.add(objDesc2);
		gridRetrieve.setObjectDescriptors(objectDescs);
		gridRetrieve.addRetrieveListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		//To ensure that the right DICOM were retrieved get files, scan each of them and assert seriesInstanceUIDs
		boolean isRetrieveOK = true;
		if(objectLocators.size() == 0){isRetrieveOK = false;}
		Iterator<String> iter = objectLocators.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			ObjectLocator objLoc = objectLocators.get(key);
			File file = new File(objLoc.getUri());
			String mimeType = DicomUtil.mimeType(file);
			if(mimeType.equalsIgnoreCase("application/dicom")){				
				parser.parse(file);
				String actualSeriesInstanceUID = parser.getSeriesInstanceUID();
				if(!actualSeriesInstanceUID.equalsIgnoreCase(seriesInstanceUID)){
					isRetrieveOK = false;
					break;
				}
			}	
		}
		assertTrue("Wrong data retrieved. See seriesInstanceUID.", isRetrieveOK);
	}

	Map<String, ObjectLocator> objectLocators;
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		objectLocators = (Map<String, ObjectLocator>) e.getSource();
	}

}
