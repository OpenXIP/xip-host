package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
import edu.wustl.xipHost.dicom.BasicDicomParser2;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.hostControl.Util;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import junit.framework.TestCase;

public class RetrieveDicomDataTest extends TestCase implements QueryListener{
	GridLocation gridLoc;
	CQLQuery cqlQuery = null;
	BasicDicomParser2 parser;
	File importDir;	
	
	protected void setUp() throws Exception {
		super.setUp();
		gridLoc = new GridLocation("http://140.254.80.50:50020/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University");
		parser = new BasicDicomParser2();
		importDir = new File("./test-content/TmpXIP_Test");
		if(importDir.exists() == false){
			boolean success = importDir.mkdir();
		    if (!success) {
		        fail("System could not create import directory.");
		    }
		}	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if(importDir == null){return;}
		if(importDir.exists()){
			Util.delete(importDir);
		}				
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
	
	//GridManagerImpl - retrieve dicom from the grid 1A - basic flow. 
	//CQL statement, GridLocation and import directory are valid and network is on.
	//CQL statement constructed for the series level 
	//study and series InstanceUIDs must be included in CQL statement
	public void testRetrieveDicomData1A() throws IOException {
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University caGrid_1.2");
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.20035";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.20036";
		CQLQuery cql = createCQLQuery(studyInstanceUID, seriesInstanceUID);				
		GridRetrieve gridRetrieve = new GridRetrieve(cql, gridLoc, importDir);
		//gridRetrieve.addDataAccessListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = gridRetrieve.getRetrievedFiles();
		
		//to ensure that the right dicom were retrieved get files and scann each item seriesInstanceUID
		boolean isRetrieveOK = true;
		if(items.size() == 0){isRetrieveOK = false;}
		for(int i = 0; i < items.size(); i ++){
			String mimeType = DicomUtil.mimeType(items.get(i));
			if(mimeType.equalsIgnoreCase("application/dicom")){				
				parser.parse(items.get(i));
				String actualSeriesInstanceUID = parser.getSeriesInstanceUID();
				if(!actualSeriesInstanceUID.equalsIgnoreCase(seriesInstanceUID)){
					isRetrieveOK = false;
					break;
				}
			}						
		}
		assertTrue("Wrong data retrieved. See seriesInstanceUID.", isRetrieveOK);
	}
	
	//GridManagerImpl - retrieve dicom from the grid 1B - alternative flow. 
	//CQL statement, GridLocation and import directory are valid and network is on.
	//CQL statement constructed for study level 
	//only studyInstanceUID is included in CQL statement
	public void testRetrieveDicomData1B() throws IOException {
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University caGrid_1.2");
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.20035";
		String seriesInstanceUID = null;
		CQLQuery cql = createCQLQuery(studyInstanceUID, seriesInstanceUID);				
		GridRetrieve gridRetrieve = new GridRetrieve(cql, gridLoc, importDir);
		//gridRetrieve.addDataAccessListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = gridRetrieve.getRetrievedFiles();		
		//to ensure that the right dicom were retrieved get files and scann each item seriesInstanceUID
		boolean isRetrieveOK = true;
		if(items.size() == 0){isRetrieveOK = false;}
		for(int i = 0; i < items.size(); i ++){
			String mimeType = DicomUtil.mimeType(items.get(i));
			if(mimeType.equalsIgnoreCase("application/dicom")){				
				parser.parse(items.get(i));
				String actualStudyInstanceUID = parser.getStudyInstanceUID();
				if(!actualStudyInstanceUID.equalsIgnoreCase(studyInstanceUID)){
					isRetrieveOK = false;
					break;
				}
			}						
		}
		assertTrue("Wrong data retrieved. See seriesInstanceUID.", isRetrieveOK);
	}
	
	//GridManagerImpl - retrieve dicom from the grid 1Ca - alternative flow. 
	//CQL statement is invalid, GridLocation and import directory are valid and network is on.
	public void testRetrieveDicomData1Ca() throws IOException {
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University caGrid_1.2");
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.20035";
		String seriesInstanceUID = null;
		//Crears invalid CQL: see generalSeriesInstanceUID
		HashMap<String, String> query = new HashMap<String, String>();				
		query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
		query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", studyInstanceUID);
		query.put("gov.nih.nci.ncia.domain.Series.generalSeriesInstanceUID", seriesInstanceUID);		
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
		GridRetrieve gridRetrieve = new GridRetrieve(cqlQuery, gridLoc, importDir);
		//gridRetrieve.addDataAccessListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = gridRetrieve.getRetrievedFiles();		
		assertEquals("CQL statement was invalid and no data was expected to be retrived when in fact some data was imported.", 0, items.size());				
	}
	
	//GridManagerImpl - retrieve dicom from the grid 1Cb - alternative flow. 
	//CQL statement is null, GridLocation and import directory are valid and network is on.
	public void testRetrieveDicomData1Cb() throws IOException {
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University caGrid_1.2");			
		GridRetrieve gridRetrieve = new GridRetrieve(null, gridLoc, importDir);
		//gridRetrieve.addDataAccessListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = gridRetrieve.getRetrievedFiles();
		assertEquals("CQL statement was invalid and no data was expected to be retrived when in fact some data was imported.", 0, items.size());
	}

	//GridManagerImpl - retrieve dicom from the grid 1D - alternative flow. 
	//CQL statement is valid, GridLocation is null and import directory is valid. Network is on.
	public void testRetrieveDicomData1D() throws IOException {		
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.20035";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.20036";
		CQLQuery cql = createCQLQuery(studyInstanceUID, seriesInstanceUID);		
		GridRetrieve gridRetrieve = new GridRetrieve(cql, null, importDir);
		//gridRetrieve.addDataAccessListener(this);
		Thread t = new Thread(gridRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = gridRetrieve.getRetrievedFiles();
		assertEquals("CQL statement was invalid and no data was expected to be retrived when in fact some data was imported.", 0, items.size());
	}
	
	//GridManagerImpl - retrieve dicom from the grid 1Ea - alternative flow. 
	//CQL statement is valid, GridLocation is valid and import directory does not exist. Network is on.
	public void testRetrieveDicomData1Ea() {
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University caGrid_1.2");		
		if(importDir.exists()){
			Util.delete(importDir);
		}
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.20035";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.20036";
		CQLQuery cql = createCQLQuery(studyInstanceUID, seriesInstanceUID);
		try{			
			GridRetrieve gridRetrieve = new GridRetrieve(cql, gridLoc, importDir);
			//gridRetrieve.addDataAccessListener(this);
			Thread t = new Thread(gridRetrieve);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}			
			fail("Import directory does not exist but system did not report it.");
		}catch(IOException e){
			assertTrue(true);
		}		
	}
	
	//GridManagerImpl - retrieve dicom from the grid 1Eb - alternative flow. 
	//CQL statement is valid, GridLocation is valid and import directory is null. Network is on.
	public void testRetrieveDicomData1Eb() throws IOException {
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8080/wsrf/services/cagrid/DICOMDataService", Type.DICOM, "DICOM", "Ohio State University caGrid_1.2");		
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.20035";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.20036";
		CQLQuery cql = createCQLQuery(studyInstanceUID, seriesInstanceUID);
		try{			
			GridRetrieve gridRetrieve = new GridRetrieve(cql, gridLoc, null);
			//gridRetrieve.addDataAccessListener(this);
			Thread t = new Thread(gridRetrieve);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}			
			fail("Import directory does not exist but system did not report it.");
		}catch(NullPointerException e){
			assertTrue(true);
		} 
	}

	public void importedFilesAvailable(GridRetrieveEvent e) {
				
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
