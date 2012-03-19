package edu.wustl.xipHost.wg23;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfUUID;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.wg23.HostImpl;
import edu.wustl.xipHost.wg23.WG23DataModel;
import edu.wustl.xipHost.worklist.WorklistEntry;
import junit.framework.TestCase;

/**
 * Set of tests for getDataAsFile(ArrayOfUUID) of WG23 interface
 * @author Jaroslaw Krych
 *
 */
public class GetAsFileWG23Test extends TestCase {
	Application app;
	WG23DataModel dm;
	HostImpl hostImpl;
	protected void setUp() throws Exception {
		super.setUp();
		app = new Application("Application1", new String("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", "src-tests/edu/wustl/xipHost/application/test.png",
				"rendering", true, "files", 1, IterationTarget.SERIES);
		List<File> filesPrev = new ArrayList<File>();
		List<File> filesCurr = new ArrayList<File>();
		filesPrev.add(new File("./src-tests/edu/wustl/xipHost/worklist/1.3.6.1.4.1.9328.50.1.20034.dcm"));
		filesPrev.add(new File("./src-tests/edu/wustl/xipHost/worklist/1.3.6.1.4.1.9328.50.1.23505.dcm"));
		filesCurr.add(new File("./src-tests/edu/wustl/xipHost/worklist/1.3.6.1.4.1.9328.50.1.20034.dcm"));
		filesCurr.add(new File("./src-tests/edu/wustl/xipHost/worklist/1.3.6.1.4.1.9328.50.1.23509.dcm"));
		WorklistEntry worklistEntry = new WorklistEntry();
		dm = worklistEntry.makeWG23DataModel(filesPrev, filesCurr);
		app.setData(dm);
		hostImpl = new HostImpl(app);		
	}
	
	//Application 1A - basic flow. ArrayOfUUID (incoming) is valid and UUIDs are valid.
	//Number of supplied UUID is 2 out of 4 object descriptors (so request if only for a subset of ObjLocs)
	//Result: ArrayOfObjectLocator
	public void testGetAsFile1A() {								
		ObjectLocator[] objLocs = dm.getObjectLocators();
		ArrayOfUUID uuids = new ArrayOfUUID();
		List<Uuid> listUUIDs = uuids.getUuid();						
		for(int i = 0; i < 2; i++){
			ObjectLocator obj = objLocs[i];
			listUUIDs.add(obj.getUuid());
		}
		ArrayOfObjectLocator arrayOfObjLocs = hostImpl.getDataAsFile(uuids, true);
		//Verify number of returned ObjLocs equals number of supplied UUIDs
		int numOfObjLocs = arrayOfObjLocs.getObjectLocator().size();
		assertEquals("Number of returned ObjLocs should be equal to the number of supplied UUIDs", uuids.getUuid().size(), numOfObjLocs);
		//Verrify if values are as expected
		List<ObjectLocator> returnedListObjLocs = arrayOfObjLocs.getObjectLocator();
		int numOfGoodURIs = 0;
		for(int i = 0; i < returnedListObjLocs.size(); i++){
			Uuid retUUID = returnedListObjLocs.get(i).getUuid();
			String retURI = returnedListObjLocs.get(i).getUri();
			for(int j = 0; j < objLocs.length; j++){
				if(objLocs[j].getUuid().equals(retUUID)){
					if(objLocs[j].getUri().equalsIgnoreCase(retURI)){
						numOfGoodURIs++;
					}					
				}
			}			
		}
		assertEquals("Number of good URIs should be two.", 2, numOfGoodURIs);
	}

	//Application 1B - alternative flow. ArrayOfUUID (incoming) is null.
	//Result: Return empty ArrayOfObjectLocator
	public void testGetAsFile1B() {
		ArrayOfObjectLocator arrayOfObjLocs = hostImpl.getDataAsFile(null, true);
		int numOfObjLocs = arrayOfObjLocs.getObjectLocator().size();
		assertEquals("Number of returned ObjLocs should be zero", 0, numOfObjLocs);
	}
	
	//Application 1C - alternative flow. ArrayOfUUID (incoming) is empty.
	//Result: Return empty ArrayOfObjectLocator
	public void testGetAsFile1C() {
		ArrayOfUUID uuids = new ArrayOfUUID();
		ArrayOfObjectLocator arrayOfObjLocs = hostImpl.getDataAsFile(uuids, true);
		int numOfObjLocs = arrayOfObjLocs.getObjectLocator().size();
		assertEquals("Number of returned ObjLocs should be zero", 0, numOfObjLocs);
	}
	
	//Application 1Da - alternative flow. ArrayOfUUID (incoming) contains UUIDs that cannot be identified
	//as well as those that are valid.
	//Result: Return ArrayOfObjectLocator with object locators found for specified UUIDs
	//UUIDs which cannot be found will be ignored
	public void testGetAsFile1Da() {
		ArrayOfUUID uuids = new ArrayOfUUID();
		List<Uuid> listUUIDs = uuids.getUuid();
		Uuid uuid1 = new Uuid();
		uuid1.setUuid("1234-Test");
		listUUIDs.add(uuid1);
		ObjectLocator[] objLocs = dm.getObjectLocators();
		Uuid uuid2 = objLocs[0].getUuid();
		listUUIDs.add(uuid2);
		ArrayOfObjectLocator arrayOfObjLocs = hostImpl.getDataAsFile(uuids, true);
		int numOfObjLocs = arrayOfObjLocs.getObjectLocator().size();
		assertEquals("Number of returned ObjLocs should be one", 1, numOfObjLocs);
	}
	
	//Application 1Da - alternative flow. ArrayOfUUID (incoming) contains UUIDs that cannot be identified.
	//Result: Return empty ArrayOfObjectLocator
	public void testGetAsFile1Db() {
		ArrayOfUUID uuids = new ArrayOfUUID();
		List<Uuid> listUUIDs = uuids.getUuid();
		Uuid uuid1 = new Uuid();
		uuid1.setUuid("1234-Test");
		listUUIDs.add(uuid1);
		ArrayOfObjectLocator arrayOfObjLocs = hostImpl.getDataAsFile(uuids, true);
		int numOfObjLocs = arrayOfObjLocs.getObjectLocator().size();
		assertEquals("Number of returned ObjLocs should be zero", 0, numOfObjLocs);
	}
	
}
