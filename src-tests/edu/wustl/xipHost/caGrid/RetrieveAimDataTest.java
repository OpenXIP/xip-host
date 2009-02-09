/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;


import java.io.File;
import java.io.IOException;
import java.util.List;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.hostControl.Util;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.helper.AIMDataServiceHelper;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveAimDataTest extends TestCase implements GridRetrieveListener {
	GridLocation gridLoc;
	CQLQuery cqlQuery = null;
	File importDir;	
	AimRetrieve aimRetrieve;
	AimHelper aimHelper = new AimHelper();
	
	protected void setUp() throws Exception {
		super.setUp();
		gridLoc = new GridLocation("http://ividemo.bmi.ohio-state.edu:8081/wsrf/services/cagrid/AIMDataService", Type.AIM, "AIM Server Ohio State University AIM_1_rv_1.9");
		importDir = new File("./src-tests/edu/wustl/xipHost/caGrid/TmpXIP_Test");
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
	
	
	//AimRetrieve 1A - basic flow. CQL statement, String url and import directory are valid and network is on.
	//Result: List<File>
	//Info: if studyInstanceUID and seriesInstanceUID could not be found returned value from retrieve would be null;
	public void testRetrieveAIMData1A() throws IOException{				
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.10934";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.11018";		
		CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(studyInstanceUID, seriesInstanceUID, null);
		aimRetrieve = new AimRetrieve(aimCQL, gridLoc, importDir);
		aimRetrieve.addGridRetrieveListener(this);
		Thread t = new Thread(aimRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = aimRetrieve.getRetrievedFiles();		
		boolean isRetrieveOK = true;
		if(items.size() == 0){isRetrieveOK = false;}
		for(int i = 0; i < items.size(); i ++){									
			aimHelper.unmarshall(items.get(i));
			String refStudyInstanceUID = aimHelper.getStudyInstanceUID();			
			String refSeriesInstanceUID = aimHelper.getSeriesInstanceUID();			
			if(!studyInstanceUID.equalsIgnoreCase(refStudyInstanceUID) && !seriesInstanceUID.equalsIgnoreCase(refSeriesInstanceUID)){
				isRetrieveOK = false;
				break;
			}
		}
		assertTrue("Either no data or wrong data was retrieved. See study and series InstanceUIDs.", isRetrieveOK);
	}
	
	//AimRetrieve 1Ba - alternative flow. CQL statement is invalid (e.g. seriesInstanceUID but not studyInstanceUID)
	//String url and import directory are valid and network is on.
	//Result: null
	public void testRetrieveAIMData1Ba() throws IOException{		
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.11018";
		CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery("", seriesInstanceUID, null);
		aimRetrieve = new AimRetrieve(aimCQL, gridLoc, importDir);
		aimRetrieve.addGridRetrieveListener(this);
		Thread t = new Thread(aimRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = aimRetrieve.getRetrievedFiles();		
		assertNull("Invalid AIM CQL but system did not report an error.", items);		
	}
	
	//AimRetrieve 1Bb - alternative flow. CQL statement is null
	//String url and import directory are valid and network is on.
	//Result: null
	public void testRetrieveAIMData1Bb() throws IOException{
		AimRetrieve aimRetrieve = new AimRetrieve(cqlQuery, gridLoc, importDir);
		List<File> result = aimRetrieve.retrieve();
		assertNull("Supplied CQL was null and returned value should be null.", result);
	}

	//AimRetrieve 1Ca - alternative flow. CQL statement is valid,
	//String url is invalid and import directory is valid and network is on.
	//Result: null
	public void testRetrieveDicomData1B1() throws IOException{
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.10934";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.11018";
		CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(studyInstanceUID, seriesInstanceUID, null);
		gridLoc = new GridLocation("http://", Type.AIM, "AIM Server Ohio State University AIM_1_rv_1.9");
		aimRetrieve = new AimRetrieve(aimCQL, gridLoc, importDir);
		aimRetrieve.addGridRetrieveListener(this);
		Thread t = new Thread(aimRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = aimRetrieve.getRetrievedFiles();		
		assertNull("Invalid grid locations but system did not detect the error.", items);	
	}
	
	//AimRetrieve 1Cb - alternative flow. CQL statement is valid
	//String url is null import directory is valid and network is on.
	//Result: null
	public void testRetrieveAIMData1Cb() throws IOException{
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.10934";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.11018";
		CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(studyInstanceUID, seriesInstanceUID, null);
		gridLoc = new GridLocation(null, Type.AIM, "AIM Server Ohio State University AIM_1_rv_1.9");
		aimRetrieve = new AimRetrieve(aimCQL, gridLoc, importDir);
		aimRetrieve.addGridRetrieveListener(this);
		Thread t = new Thread(aimRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		List<File> items = aimRetrieve.getRetrievedFiles();		
		assertNull("Grid locations' URL is null but system did not detect the error.", items);			
	}
	
	//AimRetrieve 1D - alternative flow. CQL statement and String url are valid 
	//but import directory does not exist and network is on.
	//Result: IOException
	public void testRetrieveAIMData1D(){
		String studyInstanceUID = "1.3.6.1.4.1.9328.50.1.10934";
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.11018";
		CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(studyInstanceUID, seriesInstanceUID, null);
		gridLoc = new GridLocation(null, Type.AIM, "AIM Server Ohio State University AIM_1_rv_1.9");
		importDir = new File("./src-tests/edu/wustl/xipHost/caGrid/TmpXIP_Test_NonExisting");
		try {
			aimRetrieve = new AimRetrieve(aimCQL, gridLoc, importDir);
			aimRetrieve.addGridRetrieveListener(this);
			Thread t = new Thread(aimRetrieve);
			t.start();			
			t.join();
			fail("Import directory does not exist but system did not report it.");
		} catch (InterruptedException e) {			
			fail("Import directory does not exist but system did not report it.");
		} catch (IOException e) {
			//Change to an existing import dir so tearDown can properly clean the test case
			importDir = new File("./src-tests/edu/wustl/xipHost/caGrid/TmpXIP_Test");
			assertTrue(true);
		}				
	}

	public void importedFilesAvailable(GridRetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}
}
