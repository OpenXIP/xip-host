package edu.wustl.xipHost.localFileSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nema.dicom.wg23.ArrayOfObjectDescriptor;
import org.nema.dicom.wg23.ArrayOfPatient;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.Patient;
import org.nema.dicom.wg23.Study;
import edu.wustl.xipHost.wg23.WG23DataModel;
import junit.framework.TestCase;

public class MakeWG23DataModelTest extends TestCase {
	/* voided on 05/09/2011
	FileManagerImpl mgr;	
	List<File> filesCurr;
	protected void setUp() throws Exception {
		super.setUp();
		//Prepare dataset for testing		
		filesCurr = new ArrayList<File>();
		//One patient, two studies and two series
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20034.dcm"));
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.19624.dcm"));
		mgr = new FileManagerImpl();
	}
	*/
	/* voided on 01/17/2011
	
	//FileManagerImpl 1Aa - basic flow. Data sets are valid, not null, size > 0.
	public void testMakeWg23DataModel1Aa() {						
		File[] files = new File[filesCurr.size()];		
		filesCurr.toArray(files);
		mgr.parse(files);
		while(mgr.isParsingCompleted() == false){
			//wait untill parsing of all the items is completed
		}
		int numPatients = 0;
		int numStudies = 0;
		int numSeries = 0;
		WG23DataModel dm = mgr.makeWG23DataModel(filesCurr);
		//Veryfy number of groups, group content, and object locators
		AvailableData availableData = dm.getAvailableData();		
		List<Patient> listPatients = availableData.getPatients().getPatient();
		numPatients = listPatients.size();
		for(int i = 0 ; i < numPatients; i++){
			if(listPatients.get(i).getStudies() != null){
				List<Study> listStudies = listPatients.get(i).getStudies().getStudy();
				numStudies = numStudies + listStudies.size();
			}						
		}
		for(int i = 0 ; i < numPatients; i++){
			Patient patient = listPatients.get(i);		
			if(listPatients.get(i).getStudies() != null){
				List<Study> listStudy = patient.getStudies().getStudy();
				for(int j = 0; j < listStudy.size(); j++){
					if(listStudy.get(j).getSeries() != null){
						numSeries = numSeries + listStudy.get(j).getSeries().getSeries().size();
					}					
				}
			}			
		}
		assertEquals(1, numPatients);
		assertEquals(2, numStudies);
		assertEquals(2, numSeries);
		
		//verify number of object locators is the same as number of object descs in all groups
		//assertEquals("Number of object locators is not 2.", 2, dm.getObjectLocators().length);
		//verify there is one object descriptors UUIDs much those for object locators
		//assertTrue("ObjDescs UUID do not match those of obj locs.", blnUUIDMatched);
		
	}
	
	//FileManagerImpl 1Ab - basic flow. Data set does not contain dicom items but has one non-dicom item.
	public void testMakeWg23DataModel1Ab() {			
		filesCurr = new ArrayList<File>();
		filesCurr.add(new File("./src-tests/edu/wustl/xipHost/localFileSystem/aim.xml"));
		File[] files = new File[filesCurr.size()];		
		filesCurr.toArray(files);
		mgr.parse(files);	
		while(mgr.isParsingCompleted() == false){
			//wait untill parsing of all the items is completed
		}
		WG23DataModel dm = mgr.makeWG23DataModel(filesCurr);
		AvailableData availableData = dm.getAvailableData();
		//get descriptors
		int numOfObjDescs = availableData.getObjectDescriptors().getObjectDescriptor().size();		
		assertEquals("Number of obj descs for the first group is not 1.", 1, numOfObjDescs);
		assertNotNull("System failed to produce a valid data model when set of files contain non dicom items", dm);
		//check if dm contains an item with the mime type text/xml
		List<ObjectDescriptor> listDescs = availableData.getObjectDescriptors().getObjectDescriptor();
		String mime = listDescs.get(0).getMimeType();
		assertEquals("System was unable to determine mime type for the xml file.", "text/xml", mime);
	}	
	
	//WorklistEntry 1Ac - basic flow. Prev and curr data sets are valid, not null, sizes > 0 and
	//data set belongs to one patient, two studies, two series.
	public void testMakeWg23DataModel1Ac() {				
		filesCurr = new ArrayList<File>();
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.19624.dcm"));
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.19625.dcm"));
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20034.dcm"));
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20038.dcm"));
		File[] files = new File[filesCurr.size()];		
		filesCurr.toArray(files);
		mgr.parse(files);	
		while(mgr.isParsingCompleted() == false){
			//wait untill parsing of all the items is completed
		}
		WG23DataModel dm = mgr.makeWG23DataModel(filesCurr);
		AvailableData availableData = dm.getAvailableData();		
		int numPatients = availableData.getPatients().getPatient().size();
		int numStudies = availableData.getPatients().getPatient().get(0).getStudies().getStudy().size();
		int numSeries = availableData.getPatients().getPatient().get(0).getStudies().getStudy().get(0).getSeries().getSeries().size() +
							availableData.getPatients().getPatient().get(0).getStudies().getStudy().get(1).getSeries().getSeries().size();		
		assertEquals("Invalid number of patients.", 1, numPatients);
		assertEquals("Invalid number of studies.", 2, numStudies);
		assertEquals("Invalid number of series.", 2, numSeries);
	}
		
	//FileManagerImpl 1B - alternative flow. Data set is null
	public void testMakeWg23DataModel1B() {			
		WG23DataModel dm = mgr.makeWG23DataModel(null);
		assertNull("Data set is null but data model is not null",dm);
	}
	
	//FileManagerImpl 1C - alternative flow. Data set is empty.
	public void testMakeWg23DataModel1C() {			
		filesCurr = new ArrayList<File>();		
		WG23DataModel dm = mgr.makeWG23DataModel(filesCurr);
		AvailableData availableData = dm.getAvailableData();
		ArrayOfPatient arrayOfPatients = availableData.getPatients();
		ArrayOfObjectDescriptor arrayObjDescs = availableData.getObjectDescriptors();		
		assertNull("Invalid object descs for the AvailableData object.", arrayObjDescs);
		assertNull("Invalid patients.", arrayOfPatients);
		assertNotNull("System failed to produce a valid data model.", dm);		
	}	
	
	*/
}
