/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.HashMap;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
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
public class QueryGridLocationTest extends TestCase implements QueryListener {
	//GridLocation gridLoc = new GridLocation("http://imaging-stage.nci.nih.gov/wsrf/services/cagrid/NCIACoreService", Type.DICOM, "OSU");
	//GridLocation gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "OSU");
	//GridLocation gridLoc = new GridLocation("http://node05.cci.emory.edu:8081/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "DICOM Server Emory");
	GridLocation gridLoc = new GridLocation("http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService", Type.DICOM, "NBIA-5.0", "NBIA Staging Server at NCI");
	CQLQuery cqlQuery = null;
	AttributeList criteria = new AttributeList();
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		//try to simulate gid in form of stub or mock object
		super.setUp();
		 
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
		GridQuery gridQuery = new GridQuery(cqlQuery, criteria, gridLoc, null, null);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		result = gridQuery.getSearchResult();		
		//Check if return type is an instance of DicomSearchResult and is not null						                
        Boolean blnType = result instanceof SearchResult; //blnType will be false when result would be null        	         		
		//assert if value of result is as expected        
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
		Query gridQuery = new GridQuery(cqlQuery, criteria, gridLoc, null, null);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}		 
		//assertNull("CQL statement was invalid. Returned result should be null but system did not define it this way.", result);
		fail("CQL statement was invalid but system failed to report an error.");				
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
		gridLoc = new GridLocation("http://127.0.0.1", Type.DICOM, "DICOM", "Test Location");	//There is no repository at http://127.0.0.1 		
		GridQuery gridQuery = new GridQuery(cqlQuery, criteria, gridLoc, null, null);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		fail("Invalid GridLocation."); 		
	}
	
	//GridManagerImpl - query grid 1E - alternative flow. 
	//CQL statement is null and GridLocation is valid.
	public void testQueryGridLocation1E() throws org.apache.axis.types.URI.MalformedURIException, ConnectException {										              
		GridQuery gridQuery = new GridQuery(null, criteria, gridLoc, null, null);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		//assertNull("CQL statement was invalid. Returned result should be null but system did not define it this way.", result);
		fail("CQL statement is null but system failed to report an error.");
	}
	
	//GridManagerImpl - query grid 1F - alternative flow. 
	//CQL statement is valid but GridLocation is null.
	public void testQueryGridLocation1F() throws org.apache.axis.types.URI.MalformedURIException, RemoteException, ConnectException {										              		
		GridQuery gridQuery = new GridQuery(cqlQuery, criteria, null, null, null);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		SearchResult result = gridQuery.getSearchResult();	
		assertNull("GridLocation was null. Returned value should be null but is not.", result);
	}
	
	//GridManagerImpl - query grid 1G - alternative flow. 
	//CQL statement is valid and GridLocation is valid. Test if location desc is not null and not empty string.
	public void testQueryGridLocation1G() throws org.apache.axis.types.URI.MalformedURIException, RemoteException, ConnectException {										              		
		GridQuery gridQuery = new GridQuery(cqlQuery, criteria, gridLoc, null, null);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		SearchResult result = gridQuery.getSearchResult();		
		Boolean isLocDescValid = new Boolean(result.getDataSourceDescription() != null && !result.getDataSourceDescription().isEmpty());
		assertTrue("Location description is missing in teh returned value.", isLocDescValid);
	}


	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryResultsAvailable(QueryEvent e) {
		// TODO Auto-generated method stub
		
	}
}
