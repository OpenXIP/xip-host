/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
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
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nema.dicom.wg23.ObjectLocator;

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

	//GridRetrieveNCIA - retrieve dicom from the NBIA 1A - basic flow. 
	//CQL statement, GridLocation and import directory are valid and network is on.
	//CQL statement constructed for the series level 
	//study and series InstanceUIDs must be included in CQL statement
	@Test
	public void testRetrieveDicomData1A() throws IOException {
		//String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.4717";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.4718";
		Retrieve gridRetrieve = new GridRetrieveNCIA(seriesInstanceUID, gridLoc, importDir);
		gridRetrieve.addRetrieveListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		//To ensure that the right dicom were retrieved get files and scan each item seriesInstanceUID
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
	
	/* Creates CQL for grid retrieve - only for testing purposes */
	protected CQLQuery createCQLQuery(String studyInstanceUID, String seriesInstanceUID){
		HashMap<String, String> query = new HashMap<String, String>();		
		if(studyInstanceUID != null && seriesInstanceUID != null){
			query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
			query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", studyInstanceUID);
			query.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", seriesInstanceUID);
		}else if(studyInstanceUID != null && seriesInstanceUID == null){				
			query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Study.class.getCanonicalName());
			query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", studyInstanceUID);
		}else{
			
		}
		/* Convert hash map to SQL */
		HashmapToCQLQuery h2cql;
		CQLQuery cqlQuery = null;	
		try {
			h2cql = new HashmapToCQLQuery(new ModelMap());							
			cqlQuery = h2cql.makeCQLQuery(query);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ModelMapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return cqlQuery;
	}

	Map<String, ObjectLocator> objectLocators;
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		objectLocators = (Map<String, ObjectLocator>) e.getSource();
	}

}
