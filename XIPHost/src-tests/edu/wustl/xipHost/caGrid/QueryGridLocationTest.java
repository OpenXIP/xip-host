/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.HashMap;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.dataModel.SearchResult;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Series;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryGridLocationTest extends TestCase {
	GridManager gridMgr;
	//GridLocation gridLoc = new GridLocation("http://imaging-stage.nci.nih.gov/wsrf/services/cagrid/NCIACoreService", Type.DICOM, "OSU");
	GridLocation gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "OSU");
	CQLQuery cqlQuery = null;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		//try to simulate gid in form of stub or mock object
		super.setUp();
		gridMgr = new GridManagerImpl();
		 
		/* Create CQL for grid query */
		HashMap<String, String> queryHashMap = new HashMap<String, String>();																		
		queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());		
		//queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Patient.class.getCanonicalName());
		queryHashMap.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", "1.3.6.1.4.1.9328.50.1.9263");
		//queryHashMap.put("gov.nih.nci.ncia.domain.Patient.patientId", "E09615");
		//queryHashMap.put("gov.nih.nci.ncia.domain.Patient.patientName", "E09615");
		
		HashmapToCQLQuery h2cql;
		try {
			h2cql = new HashmapToCQLQuery(new ModelMap());			
			try {
				cqlQuery = h2cql.makeCQLQuery(queryHashMap);
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ModelMapException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//GridManagerImpl - query grid 1A - basic flow. 
	//CQL statement and GridLocation are valid and network is on.
	public void testQueryGridLocation1A() throws org.apache.axis.types.URI.MalformedURIException, RemoteException, ConnectException {										              				
		SearchResult result = null;
		result = gridMgr.query(cqlQuery, gridLoc);		
		//Check if return type is an instance of DicomSearchResult and is not null						                
        Boolean blnType = result instanceof SearchResult; //blnType will be false when result would be null        	         		
		assertTrue("Expected returned value should be CQLQueryResultsIterator but system did not define it this way.", blnType);
	}

	//GridManagerImpl - query grid 1B - alternative flow. 
	//CQL statement is invalid and GridLocation is valid and network is on.
	public void testQueryGridLocation1B() throws org.apache.axis.types.URI.MalformedURIException, ConnectException {										              
		/* Create CQL for grid query */
		HashMap<String, String> queryHashMap = new HashMap<String, String>();																		
		if (queryHashMap.isEmpty()) {			
			queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());
			queryHashMap.put("gov.nih.nci.ncia.domain.Series.generalSeriesInstanceUID", "1.3.6.1.4.1.9328.50.1.9263");
			//"gov.nih.nci.ncia.domain.Series.generalSeriesInstanceUID" is incorrect
		}else{
 
		}
		HashmapToCQLQuery h2cql;
		try {
			h2cql = new HashmapToCQLQuery(new ModelMap());
			
			try {
				cqlQuery = h2cql.makeCQLQuery(queryHashMap);
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ModelMapException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}		
		try{
			gridMgr.query(cqlQuery, gridLoc);
			//assertNull("CQL statement was invalid. Returned result should be null but system did not define it this way.", result);
			fail("CQL statement was invalid but system failed to report an error.");
		}catch(RemoteException e){
			assertTrue(true);
		}				
	}
	
	//GridManagerImpl - query grid 1C - alternative flow. 
	//CQL statement and GridLocation are valid but network is off.
	public void testQueryGridLocation1C() {
		//simulate network problems
		//1. No connection
		//2. Connection broken
		fail();        
	}
	
	//GridManagerImpl - query grid 1D - alternative flow. 
	//CQL statement is valid but GridLocation does not exist and network is on.
	public void testQueryGridLocation1D() throws RemoteException, ConnectException {										              
		gridLoc = new GridLocation("http://127.0.0.1", Type.DICOM, "Test Location");	//There is no repository at http://127.0.0.1 		
		try{
			gridMgr.query(cqlQuery, gridLoc);
			fail("Invalid GridLocation.");
		}catch (org.apache.axis.types.URI.MalformedURIException e) {
			assertTrue(true);
		} 		
	}
	
	//GridManagerImpl - query grid 1E - alternative flow. 
	//CQL statement is null and GridLocation is valid.
	public void testQueryGridLocation1E() throws org.apache.axis.types.URI.MalformedURIException, ConnectException {										              
		try{
			gridMgr.query(null, gridLoc);
			//assertNull("CQL statement was invalid. Returned result should be null but system did not define it this way.", result);
			fail("CQL statement is null but system failed to report an error.");
		}catch(RemoteException e){
			assertTrue(true);
		}
	}
	
	//GridManagerImpl - query grid 1F - alternative flow. 
	//CQL statement is valid but GridLocation is null.
	public void testQueryGridLocation1F() throws org.apache.axis.types.URI.MalformedURIException, RemoteException, ConnectException {										              		
		SearchResult result = gridMgr.query(cqlQuery, null);
		assertNull("GridLocation was null. Returned value should be null but is not.", result);
	}
	
	//GridManagerImpl - query grid 1G - alternative flow. 
	//CQL statement is valid and GridLocation is valid. Test if location desc is not null and not empty string.
	public void testQueryGridLocation1G() throws org.apache.axis.types.URI.MalformedURIException, RemoteException, ConnectException {										              		
		SearchResult result = gridMgr.query(cqlQuery, gridLoc);
		Boolean isLocDescValid = new Boolean(result.toString() != null && !result.toString().isEmpty());
		assertTrue("Location description is missing in teh returned value.", isLocDescValid);
	}
	
}
