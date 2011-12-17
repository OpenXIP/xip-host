/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.pixelmed.dicom.AgeStringAttribute;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DateTimeAttribute;
import com.pixelmed.dicom.DecimalStringAttribute;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.LongTextAttribute;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.ShortTextAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TimeAttribute;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

/**
 * @author Jaroslaw Krych
 *
 */
public class TestServerSetup {
	final static Logger logger = Logger.getLogger(TestServerSetup.class);
	AttributeList criteria;	
	PacsLocation pacsLoc;
	DicomManagerImpl dicomMgr;
	
	public TestServerSetup() {
		setUp();
	}
	
	public void setUp(){
		dicomMgr = new DicomManagerImpl();
		pacsLoc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "WashU WS2");		
		criteria = new AttributeList();
		try {
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			{ AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); a.addValue("*"); criteria.put(t,a); }			
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.PatientBirthDate; Attribute a = new DateAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientSex; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientBirthTime; Attribute a = new TimeAttribute(t); criteria.put(t,a); }			
			{ AttributeTag t = TagFromName.PatientComments; Attribute a = new LongTextAttribute(t,specificCharacterSet); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.StudyID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.StudyDescription; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.ModalitiesInStudy; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.StudyDate; Attribute a = new DateAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.StudyTime; Attribute a = new TimeAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ReferringPhysicianName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AccessionNumber; Attribute a = new ShortStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PhysiciansOfRecord; Attribute a = new PersonNameAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PhysiciansReadingStudyIdentificationSequence; Attribute a = new PersonNameAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AdmittingDiagnosesDescription; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientAge; Attribute a = new AgeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientSize; Attribute a = new DecimalStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientWeight; Attribute a = new DecimalStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.Occupation; Attribute a = new ShortStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AdditionalPatientHistory; Attribute a = new LongTextAttribute(t,specificCharacterSet); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.SeriesDescription; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SeriesNumber; Attribute a = new IntegerStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.Modality; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.SeriesDate; Attribute a = new DateAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SeriesTime; Attribute a = new TimeAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.PerformingPhysicianName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ProtocolName; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.OperatorsName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.Laterality; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.BodyPartExamined; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.Manufacturer; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ManufacturerModelName; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.StationName; Attribute a = new ShortStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.InstitutionName; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.InstitutionalDepartmentName; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.InstanceNumber; Attribute a = new IntegerStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ImageComments; Attribute a = new LongTextAttribute(t,specificCharacterSet); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.ContentDate; Attribute a = new DateAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ContentTime; Attribute a = new TimeAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ImageType; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionNumber; Attribute a = new IntegerStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionDate; Attribute a = new DateAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionTime; Attribute a = new TimeAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionDateTime; Attribute a = new DateTimeAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.DerivationDescription; Attribute a = new ShortTextAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.QualityControlImage; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.BurnedInAnnotation; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.LossyImageCompression; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.LossyImageCompressionRatio; Attribute a = new DecimalStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.LossyImageCompressionMethod; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ContrastBolusAgent; Attribute a = new LongStringAttribute(t,specificCharacterSet); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.NumberOfFrames; Attribute a = new IntegerStringAttribute(t); criteria.put(t,a); }

			{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SOPClassUID; Attribute a = new UniqueIdentifierAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SpecificCharacterSet; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); a.addValue(characterSets[0]); }
			
			{ AttributeTag t = TagFromName.Exposure; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SpiralPitchFactor; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SingleCollimationWidth; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.TotalCollimationWidth; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.SliceThickness; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
			{ AttributeTag t = TagFromName.ConvolutionKernel; Attribute a = new CodeStringAttribute(t); criteria.put(t,a); }
		}
		catch (Exception e) {
			logger.error(e,	e);			
		}
		//Make sure serverTest doesn't use serverTest.properties extension. Server properties wouldn't load properly.
		dicomMgr.startHSQLDB("./src-tests/edu/wustl/xipHost/dicom/server/serverTest");
		Properties workstation2Prop = new Properties();
		try {
			workstation2Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"));
			workstation2Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION2").getCanonicalPath());
		} catch (FileNotFoundException e1) {
			logger.error(e1, e1);	
			System.exit(0);
		} catch (IOException e1) {
			logger.error(e1, e1);
			System.exit(0);
		}
		dicomMgr.startPixelmedServer(workstation2Prop);
	}

	public AttributeList getCriteria(){
		return criteria;
	}
	
	public PacsLocation getLocation(){
		return pacsLoc;
	}
	
	public void shutDownTestServer(){
		dicomMgr.closeDicomServer("jdbc:hsqldb:./src-tests/edu/wustl/xipHost/dicom/server/hsqldb/data/ws2db", "sa", "");
	}
	
	public static void main(String [] args){
		TestServerSetup setUp = new TestServerSetup();
		setUp.shutDownTestServer();
	}
}
