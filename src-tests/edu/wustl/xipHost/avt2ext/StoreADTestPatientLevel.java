/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.jdom.JDOMException;
import org.nema.dicom.PS3_19.ArrayOfObjectDescriptor;
import org.nema.dicom.PS3_19.ArrayOfObjectLocator;
import org.nema.dicom.PS3_19.ArrayOfPatient;
import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.MimeType;
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.Patient;
import org.nema.dicom.PS3_19.UID;

import edu.wustl.xipHost.wg23.Uuid;
import org.xml.sax.SAXException;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.util.DicomParser;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt2ext.AVTFactory;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.DataStore;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreADTestPatientLevel extends TestCase {
	ADFacade adService = AVTFactory.getADServiceInstance();
	AvailableData availableData;
	
	protected void setUp() throws Exception {
		super.setUp();	
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//CAUTION: all AVTStore test should be run on empty AD database.
	//AVTStore 1A - basic flow. Perfect conditions. AIM objects to store are valid XML strings.
	//DICOM SEG object are valid. There is one txt file (txt file are not stored). 
	//AvailableData contains objects descriptors at the patient level.
	public void testStoreAimToAD_1A() throws IOException, JDOMException, InterruptedException, SAXException{
		availableData = new AvailableData();
		ArrayOfPatient arrayOfPatient = new ArrayOfPatient();
		List<Patient> listPatients = arrayOfPatient.getPatient();
		Patient patient = new Patient();
		patient.setName("PatientTest");
		ArrayOfObjectDescriptor arrayOfObjectDescPatient = new ArrayOfObjectDescriptor();
		List<ObjectDescriptor> listObjectDescs = arrayOfObjectDescPatient.getObjectDescriptor();
		//ObjectDescriptor for AIM
		ObjectDescriptor objDesc1 = new ObjectDescriptor();					
		Uuid objDescUUID1 = new Uuid();
		objDescUUID1.setUuid(UUID.randomUUID().toString());
		objDesc1.setDescriptorUuid(objDescUUID1);
		MimeType xmlMime = new MimeType();
		xmlMime.setType("text/xml");
		objDesc1.setMimeType(xmlMime);																						
		listObjectDescs.add(objDesc1);
		//ObjectDescriptor for DICOM SEG
		ObjectDescriptor objDesc2 = new ObjectDescriptor();					
		Uuid objDescUUID2 = new Uuid();
		objDescUUID2.setUuid(UUID.randomUUID().toString());
		objDesc2.setDescriptorUuid(objDescUUID2);													
		MimeType dicomMime = new MimeType();
		dicomMime.setType("application/dicom");
		objDesc2.setMimeType(dicomMime);																							
		listObjectDescs.add(objDesc2);
		ObjectDescriptor objDesc3 = new ObjectDescriptor();					
		Uuid objDescUUID3 = new Uuid();
		objDescUUID3.setUuid(UUID.randomUUID().toString());
		objDesc3.setDescriptorUuid(objDescUUID2);																																				
		listObjectDescs.add(objDesc3);
				
		patient.setObjectDescriptors(arrayOfObjectDescPatient);
		listPatients.add(patient);
		availableData.setPatients(arrayOfPatient);
		
		ArrayOfObjectLocator arrayObjLocs = new ArrayOfObjectLocator();
		List<ObjectLocator> objLocs = arrayObjLocs.getObjectLocator();
		ObjectLocator objLoc1 = new ObjectLocator();
		objLoc1.setSource(objDescUUID1);
		objLoc1.setLocator(objDescUUID1);
		File file1 = new File("./test-content/AIM_2/Vasari-TCGA6330140190470283886.xml");
		String uri1 = file1.toURI().toURL().toExternalForm();
		objLoc1.setOffset(0L);
		objLoc1.setLength(file1.length());
		objLoc1.setURI(uri1);
		objLocs.add(objLoc1);
		
		ImageAnnotation annotation = AnnotationIO.loadAnnotationFromFile(file1);
		String annotationUID = annotation.getDescriptor().getUID();
		
		ObjectLocator objLoc2 = new ObjectLocator();
		objLoc2.setSource(objDescUUID2);
		objLoc2.setLocator(objDescUUID2);
		File file2 = new File("./test-content/AIM_2PlusDICOMSeg/nodule_1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.1.0.dcm");
		String uri2 = file2.toURI().toURL().toExternalForm();
		objLoc2.setOffset(0L);
		objLoc2.setLength(file2.length());
		objLoc2.setURI(uri2);
		UID file2TS = new UID();
		file2TS.setUid("1.2.840.10008.1.2.1"); // Explicit VR Little Endian (default)
		objLoc2.setTransferSyntax(file2TS);
		objLocs.add(objLoc2);
		DicomObject seg = DicomParser.read(file2);
		String dicomSegSOPInstanceUID = seg.getString(Tag.SOPInstanceUID);
		
		//Locator for testObject.txt file
		ObjectLocator objLoc3 = new ObjectLocator();
		objLoc3.setSource(objDescUUID3);
		objLoc3.setLocator(objDescUUID3);
		File file3 = new File("./src-tests/edu/wustl/xipHost/avt/testObject.txt");
		String uri3 = file3.toURI().toURL().toExternalForm();
		objLoc1.setOffset(0L);
		objLoc1.setLength(file1.length());
		objLoc3.setURI(uri3);
		objLocs.add(objLoc3);
		
		ApplicationStub appStub = new ApplicationStub("TestApp", new File("./src-tests/edu/wustl/xipHost/avt/applicationStub.bat"), "VendorTest", "", null,
				"analytical", true, "files", 1, IterationTarget.SERIES);
		appStub.setObjectLocators(arrayObjLocs);
		Application app = appStub;
		DataStore ds2 = new DataStore(availableData, app);
		Thread t = new Thread(ds2);
		t.start();
		t.join();
		ds2.getAVTStoreThread().join();
		ImageAnnotation loadedAnnotation = adService.getAnnotation(annotationUID);
		assertTrue(assertAnnotationEquals(annotation, loadedAnnotation));
		DicomObject loadedSeg = adService.getDicomObject(dicomSegSOPInstanceUID);
		assertTrue(assertDicomSegEquals(seg, loadedSeg));
	}
	
	boolean assertAnnotationEquals(ImageAnnotation expected, ImageAnnotation actual) throws SAXException, IOException{
		if(expected == actual){
			return true;
		}
		if(expected != null){
			if(actual != null){
				assertEquals(expected.getDescriptor(), actual.getDescriptor());		
				//assertXMLEqual(expected.getAIM(), actual.getAIM());
				return true;
			} else{
				fail("expected = " + expected + " while actual is null!");
				return false;
			}
		}
		return false;
	}
	
	boolean assertDicomSegEquals(DicomObject expected, DicomObject actual){
		if(expected == actual){
			return true;
		}
		if(expected != null){
			if(actual != null){
				assertEquals(expected.getString(Tag.SOPInstanceUID), actual.getString(Tag.SOPInstanceUID));		
				return true;
			} else{
				fail("expected = " + expected + " while actual is null!");
				return false;
			}
		}
		return false;
	}
	
}
