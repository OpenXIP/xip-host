package edu.wustl.xipHost.worklist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.nema.dicom.PS3_19.ArrayOfObjectDescriptor;
import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.Patient;
import org.nema.dicom.PS3_19.Series;
import org.nema.dicom.PS3_19.Study;
import org.nema.dicom.PS3_19.UUID;

import edu.wustl.xipHost.wg23.Uuid;
import edu.wustl.xipHost.wg23.WG23DataModel;
import junit.framework.TestCase;

public class MakeWG23DataModelTest extends TestCase {
	WorklistEntry worklistEntry;
	List<File> filesPrev;
	List<File> filesCurr;
	protected void setUp() throws Exception {
		super.setUp();
		//Prepare dataset for testing
		filesPrev = new ArrayList<File>();
		filesCurr = new ArrayList<File>();
		filesPrev.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.19624.dcm"));
		filesPrev.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.19625.dcm"));
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20034.dcm"));
		filesCurr.add(new File("./test-content/WorlistDataset/1.3.6.1.4.1.9328.50.1.20038.dcm"));
		worklistEntry = new WorklistEntry();
	}
	
	//WorklistEntry 1Aa - basic flow. Prev and curr data sets are valid, not null, sizes > 0.
	//One patient, two studies, two series
	public void testMakeWg23DataModel1Aa() {						
		worklistEntry.setStudyInstanceUIDPrev("1.3.6.1.4.1.9328.50.1.19618");
		worklistEntry.setStudyInstanceUIDCurr("1.3.6.1.4.1.9328.50.1.19623");
		worklistEntry.setSeriesInstanceUIDPrev("1.3.6.1.4.1.9328.50.1.20035");
		worklistEntry.setSeriesInstanceUIDCurr("1.3.6.1.4.1.9328.50.1.20036");
		WG23DataModel dm = worklistEntry.makeWG23DataModel(filesPrev, filesCurr);
		//Veryfy number of groups, group content, and object locators
		AvailableData availableData = dm.getAvailableData();	
		boolean blnNumGroups = false;
		boolean blnPatientName = false;		
		
		boolean blnObjDescsg1 = false;
		boolean blnUUIDObjDescsg1 = true;
		boolean blnMimeTypeObjDescsg1 = true;
		boolean blnClassUIDObjDescsg1 = true;
		boolean blnTransferSyntaxUIDObjDescsg1 = true;
		boolean blnModalityObjDescsg1 = true;
						
		boolean blnObjDescsg2 = false;
		boolean blnUUIDObjDescsg2 = true;
		boolean blnMimeTypeObjDescsg2 = true;
		boolean blnClassUIDObjDescsg2 = true;
		boolean blnTransferSyntaxUIDObjDescsg2 = true;
		boolean blnModalityObjDescsg2 = true;
		
		boolean blnUUIDMatched = false;
		
		List<Patient> listOfPatients = availableData.getPatients().getPatient();
		List<Study> listOfStudies = listOfPatients.get(0).getStudies().getStudy();
		List<Series> listOfSeriesPrev = listOfStudies.get(0).getSeries().getSeries();								
		List<Series> listOfSeriesCurr = listOfStudies.get(1).getSeries().getSeries();
		if(listOfPatients.size() == 1 && listOfStudies.size() == 2 && (listOfSeriesPrev.size() + listOfSeriesCurr.size()) == 2){
			blnNumGroups = true;
			/*
			 * validate data for group1
			 */									
			if(listOfPatients.get(0).getName() == null){blnPatientName = true;}			
			Series group1 = listOfSeriesPrev.get(0);									
			ArrayOfObjectDescriptor descriptorsG1 = group1.getObjectDescriptors();		
			List<ObjectDescriptor> listObjDescsG1 = descriptorsG1.getObjectDescriptor();			
			if(listObjDescsG1.size() == 2){
				blnObjDescsg1 = true;
				//ObjectDesc UUID must be <> empty
				for(int i = 0; i < 2; i++){
					if(listObjDescsG1.get(i).getDescriptorUuid() != null){
						if(listObjDescsG1.get(i).getDescriptorUuid().toString().isEmpty()){
							blnUUIDObjDescsg1 = false;
						}
					}else{
						blnUUIDObjDescsg1 = false;
					}
					if(listObjDescsG1.get(i).getMimeType() != null){
						if(listObjDescsG1.get(i).getMimeType().getType().isEmpty()){
							blnMimeTypeObjDescsg1 = false;
						}
					}else{
						blnMimeTypeObjDescsg1 = false;
					}					
					if(listObjDescsG1.get(i).getClassUID() != null){
						if(listObjDescsG1.get(i).getClassUID().getUid().isEmpty()){
							blnClassUIDObjDescsg1 = false;
						}
					}else{
						blnClassUIDObjDescsg1 = false;
					}										
					if(listObjDescsG1.get(i).getTransferSyntaxUID() != null){
						if(listObjDescsG1.get(i).getTransferSyntaxUID().getUid().isEmpty()){
							blnTransferSyntaxUIDObjDescsg1 = false;
						}
					}
					if(listObjDescsG1.get(i).getModality() != null){
						if(listObjDescsG1.get(i).getModality().getModality().isEmpty()){
							blnModalityObjDescsg1 = false;
						}
					}else{
						blnModalityObjDescsg1 = false;
					}
				}				
			}
			
			/*
			 * validate data for group 2
			 */
			Series group2 = listOfSeriesCurr.get(0);											
			ArrayOfObjectDescriptor descriptorsG2 = group2.getObjectDescriptors();		
			List<ObjectDescriptor> listObjDescsG2 = descriptorsG2.getObjectDescriptor();			
			if(listObjDescsG2.size() == 2){
				blnObjDescsg2 = true;
				//ObjectDesc UUID must be <> empty
				for(int i = 0; i < 2; i++){
					if(listObjDescsG2.get(i).getDescriptorUuid() != null){
						if(listObjDescsG2.get(i).getDescriptorUuid().getUuid().isEmpty()){
							blnUUIDObjDescsg2 = false;
						}
					}else{
						blnUUIDObjDescsg2 = false;
					}
					if(listObjDescsG2.get(i).getMimeType() != null){
						if(listObjDescsG2.get(i).getMimeType().getType().isEmpty()){
							blnMimeTypeObjDescsg2 = false;
						}
					}else{
						blnMimeTypeObjDescsg2 = false;
					}					
					if(listObjDescsG2.get(i).getClassUID() != null){
						if(listObjDescsG2.get(i).getClassUID().getUid().isEmpty()){
							blnClassUIDObjDescsg2 = false;
						}
					}else{
						blnClassUIDObjDescsg2 = false;
					}
					if(listObjDescsG2.get(i).getTransferSyntaxUID() != null){
						if(listObjDescsG2.get(i).getTransferSyntaxUID().getUid().isEmpty()){
							blnTransferSyntaxUIDObjDescsg2 = false;
						}
					}
					if(listObjDescsG2.get(i).getModality() != null){
						if(listObjDescsG2.get(i).getModality().getModality().isEmpty()){
							blnModalityObjDescsg2 = false;
						}
					}else{
						blnModalityObjDescsg2 = false;
					}
				}				
			}
			
			ObjectLocator[] objLocs = dm.getObjectLocators();
			
			for(int i = 0; i < listObjDescsG1.size(); i++){
				UUID uuid = listObjDescsG1.get(i).getDescriptorUuid();
				for(int j = 0; j < objLocs.length; j++){
					if(objLocs[j].getSource().equals(uuid)){
						blnUUIDMatched = true;
					}
				}
			}
			for(int i = 0; i < listObjDescsG2.size(); i++){
				UUID uuid = listObjDescsG2.get(i).getDescriptorUuid();
				for(int j = 0; j < objLocs.length; j++){
					if(objLocs[j].getSource().equals(uuid)){
						blnUUIDMatched = true;
					}
				}
			}
		}
		assertTrue(blnNumGroups);
		assertTrue(blnPatientName);		
		assertTrue(blnObjDescsg1);
		assertTrue(blnUUIDObjDescsg1);
		assertTrue(blnTransferSyntaxUIDObjDescsg1);
		assertTrue(blnMimeTypeObjDescsg1);
		assertTrue(blnClassUIDObjDescsg1);
		assertTrue(blnModalityObjDescsg1);
		assertTrue(blnObjDescsg2);
		assertTrue(blnUUIDObjDescsg2);
		assertTrue(blnMimeTypeObjDescsg2);
		assertTrue(blnClassUIDObjDescsg2);
		assertTrue(blnTransferSyntaxUIDObjDescsg2);
		assertTrue(blnModalityObjDescsg2);
		
		//verify number of object locators is the same as number of object descs in all groups
		assertEquals("Number of object locators is not 4.", 4, dm.getObjectLocators().length);
		//verify there is one object descriptors UUIDs much those for object locators
		assertTrue("ObjDescs UUID do not match those of obj locs.", blnUUIDMatched);
		
	}
	
	//WorklistEntry 1Ab - basic flow. Prev and curr data sets are valid, not null, sizes > 0.
	//Prev data set contains only non-dicom item
	public void testMakeWg23DataModel1Ab() {					
		worklistEntry.setStudyInstanceUIDPrev("1.3.6.1.4.1.9328.50.1.19618");
		worklistEntry.setStudyInstanceUIDCurr("1.3.6.1.4.1.9328.50.1.19623");
		worklistEntry.setSeriesInstanceUIDPrev("1.3.6.1.4.1.9328.50.1.20035");
		worklistEntry.setSeriesInstanceUIDCurr("1.3.6.1.4.1.9328.50.1.20036");		
		filesPrev = new ArrayList<File>();
		filesPrev.add(new File("./src-tests/edu/wustl/xipHost/localFileSystem/aim.xml"));
		WG23DataModel dm = worklistEntry.makeWG23DataModel(filesPrev, filesCurr);
		AvailableData availableData = dm.getAvailableData();
		Series group = availableData.getPatients().getPatient().get(0).getStudies().getStudy().get(0).getSeries().getSeries().get(0);
		int numOfObjDescs = group.getObjectDescriptors().getObjectDescriptor().size();		
		assertEquals("Number of obj descs for the first group is not 1.", 1, numOfObjDescs);
		assertNotNull("System failed to produce a valid data model when set of files contain non dicom items", dm);
		//check if dm contains an item with the mime type text/xml
		List<ObjectDescriptor> listDescs = group.getObjectDescriptors().getObjectDescriptor();
		String mime = listDescs.get(0).getMimeType().getType();
		assertEquals("System was unable to determine mime type for the xml file.", "text/xml", mime);
	}
	
	//WorklistEntry 1B - alternative flow. Prev data set is null, curr is valid.
	public void testMakeWg23DataModel1B() {			
		WG23DataModel dm = worklistEntry.makeWG23DataModel(null, filesCurr);
		assertNull("Previous data set is null but data model is not null",dm);
	}
	
	//WorklistEntry 1C - alternative flow. Prev data set is empty, curr is valid.
	public void testMakeWg23DataModel1C() {			
		filesPrev = new ArrayList<File>();		
		worklistEntry.setStudyInstanceUIDCurr("1.3.6.1.4.1.9328.50.1.19623");		
		worklistEntry.setSeriesInstanceUIDCurr("1.3.6.1.4.1.9328.50.1.20036");	
		WG23DataModel dm = worklistEntry.makeWG23DataModel(filesPrev, filesCurr);
		AvailableData availableData = dm.getAvailableData();
		Series group = availableData.getPatients().getPatient().get(0).getStudies().getStudy().get(0).getSeries().getSeries().get(0);				
		int numOfObjDescs = group.getObjectDescriptors().getObjectDescriptor().size();		
		assertEquals("Number of obj descs for the first group is not zero.", 0, numOfObjDescs);
		assertNotNull("System failed to produce a valid data model with zero obj descs for the first group", dm);		
	}	
}
