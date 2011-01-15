/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dcm4che2.data.Tag;
import com.pixelmed.dicom.AgeStringAttribute;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DateAttribute;
import com.pixelmed.dicom.DateTimeAttribute;
import com.pixelmed.dicom.DecimalStringAttribute;
import com.pixelmed.dicom.DicomDictionary;
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

import eu.medsea.util.MimeUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomUtil {

	 /*
     * Enhance mimeType by Macintosh DICM file type
     */
	public static String mimeType(File file) throws IOException{		
    	if(file == null){
    		throw new FileNotFoundException();
    	}
    	String mimeType = new String();
		byte[] data;
		String magicNum = new String();
		Boolean isDCM = file.getAbsolutePath().endsWith(".dcm");
		try {
			String filePath = file.getAbsolutePath();
			InputStream in = new FileInputStream(filePath);
			int size = in.available();
			if(size > 128 + 4){
				data = new byte[size];
				in.read(data);
				magicNum = new String(data, 128, 4);
			}			
			if(magicNum.equalsIgnoreCase("DICM") || isDCM == true){
				mimeType = "application/dicom";
			}else{				
				//mimeType = new MimetypesFileTypeMap().getContentType(file);
				mimeType = MimeUtil.getMimeType(file);
			}
		} catch (IOException e) {
			throw new IOException();	
		}				
		return mimeType;
	}
	
	public static boolean isDICOM(File file){
		try {
			if(mimeType(file).equalsIgnoreCase("application/dicom")){
				return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}
	
	public static AttributeList constructEmptyAttributeList() {
		AttributeList filter = new AttributeList();
		try {
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			{ AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }			
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }

			{ AttributeTag t = TagFromName.PatientBirthDate; Attribute a = new DateAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientSex; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientBirthTime; Attribute a = new TimeAttribute(t); filter.put(t,a); }
			//kills Leonardo ... { AttributeTag t = TagFromName.OtherPatientID; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			//kills Leonardo ... { AttributeTag t = TagFromName.OtherPatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }
			//kills Leonardo ... { AttributeTag t = TagFromName.EthnicGroup; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientComments; Attribute a = new LongTextAttribute(t,specificCharacterSet); filter.put(t,a); }

			{ AttributeTag t = TagFromName.StudyID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.StudyDescription; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }

			{ AttributeTag t = TagFromName.ModalitiesInStudy; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.StudyDate; Attribute a = new DateAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.StudyTime; Attribute a = new TimeAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ReferringPhysicianName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AccessionNumber; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PhysicianOfRecord; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PhysicianReadingStudy; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AdmittingDiagnosesDescription; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientAge; Attribute a = new AgeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientSize; Attribute a = new DecimalStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientWeight; Attribute a = new DecimalStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.Occupation; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AdditionalPatientHistory; Attribute a = new LongTextAttribute(t,specificCharacterSet); filter.put(t,a); }

			{ AttributeTag t = TagFromName.SeriesDescription; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SeriesNumber; Attribute a = new IntegerStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.Modality; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }

			{ AttributeTag t = TagFromName.SeriesDate; Attribute a = new DateAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SeriesTime; Attribute a = new TimeAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PerformingPhysicianName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ProtocolName; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.OperatorName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.Laterality; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.BodyPartExamined; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.Manufacturer; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ManufacturerModelName; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.StationName; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.InstitutionName; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.InstitutionalDepartmentName; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }

			{ AttributeTag t = TagFromName.InstanceNumber; Attribute a = new IntegerStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ImageComments; Attribute a = new LongTextAttribute(t,specificCharacterSet); filter.put(t,a); }

			{ AttributeTag t = TagFromName.ContentDate; Attribute a = new DateAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ContentTime; Attribute a = new TimeAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ImageType; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionNumber; Attribute a = new IntegerStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionDate; Attribute a = new DateAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionTime; Attribute a = new TimeAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.AcquisitionDateTime; Attribute a = new DateTimeAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.DerivationDescription; Attribute a = new ShortTextAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.QualityControlImage; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.BurnedInAnnotation; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.LossyImageCompression; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.LossyImageCompressionRatio; Attribute a = new DecimalStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.LossyImageCompressionMethod; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ContrastBolusAgent; Attribute a = new LongStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.NumberOfFrames; Attribute a = new IntegerStringAttribute(t); filter.put(t,a); }

			{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SOPClassUID; Attribute a = new UniqueIdentifierAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SpecificCharacterSet; Attribute a = new CodeStringAttribute(t); filter.put(t,a); a.addValue(characterSets[0]); }
			
			{ AttributeTag t = TagFromName.Exposure; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SpiralPitchFactor; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SingleCollimationWidth; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.TotalCollimationWidth; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.SliceThickness; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.ConvolutionKernel; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			
		}catch (Exception e) {
			return null;
		}
		return filter;
	}
	
	public static AttributeList constructEmptyPatientAttributeList() {
		AttributeList filter = new AttributeList();
		try {
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			{ AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }			
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.IssuerOfPatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientBirthDate; Attribute a = new DateAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientSex; Attribute a = new CodeStringAttribute(t); filter.put(t,a); }
			{ AttributeTag t = TagFromName.PatientAddress; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }			
			{ AttributeTag t = TagFromName.PatientTelephoneNumber; Attribute a = new ShortStringAttribute(t,specificCharacterSet); filter.put(t,a); }			
			{ AttributeTag t = TagFromName.PatientMotherBirthName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); filter.put(t,a); }			
			{ AttributeTag t = TagFromName.SpecificCharacterSet; Attribute a = new CodeStringAttribute(t); filter.put(t,a); a.addValue(characterSets[0]); }
		}catch (Exception e) {
			return null;
		}
		return filter;
	}
	
	public static Map<Integer, Object> convertToADDicomCriteria(AttributeList criteria){
		Map<Integer, Object> adCriteria = new HashMap<Integer, Object>();
		/*DicomDictionary dictionary = AttributeList.getDictionary();
	    Iterator<?> iter = dictionary.getTagIterator();        
	    String strAtt = null;
	    String attValue = null;
	    while(iter.hasNext()){
	    	AttributeTag attTag  = (AttributeTag)iter.next();					    	
	    	strAtt = attTag.toString();									
			attValue = Attribute.getSingleStringValueOrEmptyString(criteria, attTag);			
			if(!attValue.isEmpty()){
				System.out.println(strAtt + " " + attValue);				
			}
	    }*/	    	    
	    String patientName = criteria.get(TagFromName.PatientName).getDelimitedStringValuesOrEmptyString();
	    if(!patientName.isEmpty()){adCriteria.put(Tag.PatientName, patientName);}			
	    String patientID = criteria.get(TagFromName.PatientID).getDelimitedStringValuesOrEmptyString();
	    if(!patientID.isEmpty()){adCriteria.put(Tag.PatientID, patientID);}
		String patientBirthDate = criteria.get(TagFromName.PatientBirthDate).getDelimitedStringValuesOrEmptyString();
		if(!patientBirthDate.isEmpty()){adCriteria.put(Tag.PatientBirthDate, patientBirthDate);}
		String patientSex = criteria.get(TagFromName.PatientSex).getDelimitedStringValuesOrEmptyString();
		if(!patientSex.isEmpty()){adCriteria.put(Tag.PatientSex, patientSex);}
		String patientBirthTime = criteria.get(TagFromName.PatientBirthTime).getDelimitedStringValuesOrEmptyString();
		if(!patientBirthTime.isEmpty()){adCriteria.put(Tag.PatientBirthTime, patientBirthTime);}
		String patientComments = criteria.get(TagFromName.PatientComments).getDelimitedStringValuesOrEmptyString();
		if(!patientComments.isEmpty()){adCriteria.put(Tag.PatientComments, patientComments);}
		String studyID = criteria.get(TagFromName.StudyID).getDelimitedStringValuesOrEmptyString();
		if(!studyID.isEmpty()){adCriteria.put(Tag.StudyID, studyID);}
		String studyDescription = criteria.get(TagFromName.StudyDescription).getDelimitedStringValuesOrEmptyString();
		if(!studyDescription.isEmpty()){adCriteria.put(Tag.StudyDescription, studyDescription);}
		String modalitiesInStudy = criteria.get(TagFromName.ModalitiesInStudy).getDelimitedStringValuesOrEmptyString();
		if(!modalitiesInStudy.isEmpty()){adCriteria.put(Tag.ModalitiesInStudy, modalitiesInStudy);}
		String studyDate = criteria.get(TagFromName.StudyDate).getDelimitedStringValuesOrEmptyString();
		if(!studyDate.isEmpty()){adCriteria.put(Tag.StudyDate, studyDate);}
		String studyTime = criteria.get(TagFromName.StudyTime).getDelimitedStringValuesOrEmptyString();
		if(!studyTime.isEmpty()){adCriteria.put(Tag.StudyTime, studyTime);}
		String referringPhysicianName = criteria.get(TagFromName.ReferringPhysicianName).getDelimitedStringValuesOrEmptyString();
		if(!referringPhysicianName.isEmpty()){adCriteria.put(Tag.ReferringPhysicianName, referringPhysicianName);}
		String accessionNumber = criteria.get(TagFromName.AccessionNumber).getDelimitedStringValuesOrEmptyString();
		if(!accessionNumber.isEmpty()){adCriteria.put(Tag.AccessionNumber, accessionNumber);}
		String physicianOfRecord = criteria.get(TagFromName.PhysicianOfRecord).getDelimitedStringValuesOrEmptyString();
		if(!physicianOfRecord.isEmpty()){adCriteria.put(Tag.PhysiciansOfRecord, physicianOfRecord);}
		String physicianReadingStudy = criteria.get(TagFromName.PhysicianReadingStudy).getDelimitedStringValuesOrEmptyString();
		if(!physicianReadingStudy.isEmpty()){adCriteria.put(Tag.PhysiciansReadingStudyIdentificationSequence, physicianReadingStudy);}		
		String admittingDiagnosesDescription = criteria.get(TagFromName.AdmittingDiagnosesDescription).getDelimitedStringValuesOrEmptyString();
		if(!admittingDiagnosesDescription.isEmpty()){adCriteria.put(Tag.AdmittingDiagnosesDescription, admittingDiagnosesDescription);}
		String patientAge = criteria.get(TagFromName.PatientAge).getDelimitedStringValuesOrEmptyString();
		if(!patientAge.isEmpty()){adCriteria.put(Tag.PatientAge, patientAge);}
		String patientSize = criteria.get(TagFromName.PatientSize).getDelimitedStringValuesOrEmptyString();
		if(!patientSize.isEmpty()){adCriteria.put(Tag.PatientSize, patientSize);}
		String patientWeight = criteria.get(TagFromName.PatientWeight).getDelimitedStringValuesOrEmptyString();
		if(!patientWeight.isEmpty()){adCriteria.put(Tag.PatientWeight, patientWeight);}
		String occupation = criteria.get(TagFromName.Occupation).getDelimitedStringValuesOrEmptyString();
		if(!occupation.isEmpty()){adCriteria.put(Tag.Occupation, occupation);}
		String additionalPatientHistory = criteria.get(TagFromName.AdditionalPatientHistory).getDelimitedStringValuesOrEmptyString();
		if(!additionalPatientHistory.isEmpty()){adCriteria.put(Tag.AdditionalPatientHistory, additionalPatientHistory);}
		String seriesDescription = criteria.get(TagFromName.SeriesDescription).getDelimitedStringValuesOrEmptyString();
		if(!seriesDescription.isEmpty()){adCriteria.put(Tag.SeriesDescription, seriesDescription);}
		String seriesNumber = criteria.get(TagFromName.SeriesNumber).getDelimitedStringValuesOrEmptyString();
		if(!seriesNumber.isEmpty()){adCriteria.put(Tag.SeriesNumber, seriesNumber);}
		String modality = criteria.get(TagFromName.Modality).getDelimitedStringValuesOrEmptyString();
		if(!modality.isEmpty()){adCriteria.put(Tag.Modality, modality);}
		String seriesDate = criteria.get(TagFromName.SeriesDate).getDelimitedStringValuesOrEmptyString();
		if(!seriesDate.isEmpty()){adCriteria.put(Tag.SeriesDate, seriesDate);}		
		String seriesTime = criteria.get(TagFromName.SeriesTime).getDelimitedStringValuesOrEmptyString();
		if(!seriesTime.isEmpty()){adCriteria.put(Tag.SeriesTime, seriesTime);}
		String performingPhysicianName = criteria.get(TagFromName.PerformingPhysicianName).getDelimitedStringValuesOrEmptyString();
		if(!performingPhysicianName.isEmpty()){adCriteria.put(Tag.PerformingPhysicianName, performingPhysicianName);}
		String protocolName = criteria.get(TagFromName.ProtocolName).getDelimitedStringValuesOrEmptyString();
		if(!protocolName.isEmpty()){adCriteria.put(Tag.ProtocolName, protocolName);}
		String operatorName = criteria.get(TagFromName.OperatorName).getDelimitedStringValuesOrEmptyString();
		if(!operatorName.isEmpty()){adCriteria.put(Tag.OperatorName, operatorName);}
		String laterality = criteria.get(TagFromName.Laterality).getDelimitedStringValuesOrEmptyString();
		if(!laterality.isEmpty()){adCriteria.put(Tag.Laterality, laterality);}
		String bodyPartExamined = criteria.get(TagFromName.BodyPartExamined).getDelimitedStringValuesOrEmptyString();
		if(!bodyPartExamined.isEmpty()){adCriteria.put(Tag.BodyPartExamined, bodyPartExamined);}
		String manufacturer = criteria.get(TagFromName.Manufacturer).getDelimitedStringValuesOrEmptyString();
		if(!manufacturer.isEmpty()){adCriteria.put(Tag.Manufacturer, manufacturer);}
		String manufacturerModelName = criteria.get(TagFromName.ManufacturerModelName).getDelimitedStringValuesOrEmptyString();
		if(!manufacturerModelName.isEmpty()){adCriteria.put(Tag.ManufacturerModelName, manufacturerModelName);}
		String stationName = criteria.get(TagFromName.StationName).getDelimitedStringValuesOrEmptyString();
		if(!stationName.isEmpty()){adCriteria.put(Tag.StationName, stationName);}		
		String institutionName = criteria.get(TagFromName.InstitutionName).getDelimitedStringValuesOrEmptyString();
		if(!institutionName.isEmpty()){adCriteria.put(Tag.InstitutionName, institutionName);}
		String institutionalDepartmentName = criteria.get(TagFromName.InstitutionalDepartmentName).getDelimitedStringValuesOrEmptyString();
		if(!institutionalDepartmentName.isEmpty()){adCriteria.put(Tag.InstitutionalDepartmentName, institutionalDepartmentName);}
		String instanceNumber = criteria.get(TagFromName.InstanceNumber).getDelimitedStringValuesOrEmptyString();
		if(!instanceNumber.isEmpty()){adCriteria.put(Tag.InstanceNumber, instanceNumber);}
		String imageComments = criteria.get(TagFromName.ImageComments).getDelimitedStringValuesOrEmptyString();
		if(!imageComments.isEmpty()){adCriteria.put(Tag.ImageComments, imageComments);}
		String contentDate = criteria.get(TagFromName.ContentDate).getDelimitedStringValuesOrEmptyString();
		if(!contentDate.isEmpty()){adCriteria.put(Tag.ContentDate, contentDate);}
		String contentTime = criteria.get(TagFromName.ContentTime).getDelimitedStringValuesOrEmptyString();
		if(!contentTime.isEmpty()){adCriteria.put(Tag.ContentTime, contentTime);}
		String imageType = criteria.get(TagFromName.ImageType).getDelimitedStringValuesOrEmptyString();
		if(!imageType.isEmpty()){adCriteria.put(Tag.ImageType, imageType);}
		String acquisitionNumber = criteria.get(TagFromName.AcquisitionNumber).getDelimitedStringValuesOrEmptyString();
		if(!acquisitionNumber.isEmpty()){adCriteria.put(Tag.AcquisitionNumber, acquisitionNumber);}
		String acquisitionDate = criteria.get(TagFromName.AcquisitionDate).getDelimitedStringValuesOrEmptyString();
		if(!acquisitionDate.isEmpty()){adCriteria.put(Tag.AcquisitionDate, acquisitionDate);}
		String acquisitionTime = criteria.get(TagFromName.AcquisitionTime).getDelimitedStringValuesOrEmptyString();
		if(!acquisitionTime.isEmpty()){adCriteria.put(Tag.AcquisitionTime, acquisitionTime);}
		String acquisitionDateTime = criteria.get(TagFromName.AcquisitionDateTime).getDelimitedStringValuesOrEmptyString();
		if(!acquisitionDateTime.isEmpty()){adCriteria.put(Tag.AcquisitionDateTime, acquisitionDateTime);}		
		String derivationDescription = criteria.get(TagFromName.DerivationDescription).getDelimitedStringValuesOrEmptyString();
		if(!derivationDescription.isEmpty()){adCriteria.put(Tag.DerivationDescription, derivationDescription);}
		String qualityControlImage = criteria.get(TagFromName.QualityControlImage).getDelimitedStringValuesOrEmptyString();
		if(!qualityControlImage.isEmpty()){adCriteria.put(Tag.QualityControlImage, qualityControlImage);}
		String burnedInAnnotation = criteria.get(TagFromName.BurnedInAnnotation).getDelimitedStringValuesOrEmptyString();
		if(!burnedInAnnotation.isEmpty()){adCriteria.put(Tag.BurnedInAnnotation, burnedInAnnotation);}
		String lossyImageCompression = criteria.get(TagFromName.LossyImageCompression).getDelimitedStringValuesOrEmptyString();
		if(!lossyImageCompression.isEmpty()){adCriteria.put(Tag.LossyImageCompression, lossyImageCompression);}
		String lossyImageCompressionRatio = criteria.get(TagFromName.LossyImageCompressionRatio).getDelimitedStringValuesOrEmptyString();
		if(!lossyImageCompressionRatio.isEmpty()){adCriteria.put(Tag.LossyImageCompressionRatio, lossyImageCompressionRatio);}
		String lossyImageCompressionMethod = criteria.get(TagFromName.LossyImageCompressionMethod).getDelimitedStringValuesOrEmptyString();
		if(!lossyImageCompressionMethod.isEmpty()){adCriteria.put(Tag.LossyImageCompressionMethod, lossyImageCompressionMethod);}
		String contrastBolusAgent = criteria.get(TagFromName.ContrastBolusAgent).getDelimitedStringValuesOrEmptyString();
		if(!contrastBolusAgent.isEmpty()){adCriteria.put(Tag.ContrastBolusAgent, contrastBolusAgent);}
		String numberOfFrames = criteria.get(TagFromName.NumberOfFrames).getDelimitedStringValuesOrEmptyString();
		if(!numberOfFrames.isEmpty()){adCriteria.put(Tag.NumberOfFrames, numberOfFrames);}
		String studyInstanceUID = criteria.get(TagFromName.StudyInstanceUID).getDelimitedStringValuesOrEmptyString();
		if(!studyInstanceUID.isEmpty()){adCriteria.put(Tag.StudyInstanceUID, studyInstanceUID);}
		String seriesInstanceUID = criteria.get(TagFromName.SeriesInstanceUID).getDelimitedStringValuesOrEmptyString();
		if(!seriesInstanceUID.isEmpty()){adCriteria.put(Tag.SeriesInstanceUID, seriesInstanceUID);}
		String SOPInstanceUID = criteria.get(TagFromName.SOPInstanceUID).getDelimitedStringValuesOrEmptyString();
		if(!SOPInstanceUID.isEmpty()){adCriteria.put(Tag.SOPInstanceUID, SOPInstanceUID);}
		String SOPClassUID = criteria.get(TagFromName.SOPClassUID).getDelimitedStringValuesOrEmptyString();
		if(!SOPClassUID.isEmpty()){adCriteria.put(Tag.SOPClassUID, SOPClassUID);}	    		
		String exposure = criteria.get(TagFromName.Exposure).getDelimitedStringValuesOrEmptyString();
		if(!exposure.isEmpty()){adCriteria.put(Tag.Exposure, exposure);}
		String  spiralPitchFactor = criteria.get(TagFromName.SpiralPitchFactor).getDelimitedStringValuesOrEmptyString();
		if(!spiralPitchFactor.isEmpty()){adCriteria.put(Tag.SpiralPitchFactor, spiralPitchFactor);}		
		String  singleCollimationWidth = criteria.get(TagFromName.SingleCollimationWidth).getDelimitedStringValuesOrEmptyString();
		if(!singleCollimationWidth.isEmpty()){adCriteria.put(Tag.SingleCollimationWidth, singleCollimationWidth);}		
		String  totalCollimationWidth = criteria.get(TagFromName.TotalCollimationWidth).getDelimitedStringValuesOrEmptyString();
		if(!totalCollimationWidth.isEmpty()){adCriteria.put(Tag.TotalCollimationWidth, totalCollimationWidth);}		
		String  sliceThickness = criteria.get(TagFromName.SliceThickness).getDelimitedStringValuesOrEmptyString();
		if(!sliceThickness.isEmpty()){adCriteria.put(Tag.SliceThickness, sliceThickness);}		
		String  convolutionKernel = criteria.get(TagFromName.ConvolutionKernel).getDelimitedStringValuesOrEmptyString();
		if(!convolutionKernel.isEmpty()){adCriteria.put(Tag.ConvolutionKernel, convolutionKernel);}
		return adCriteria;
	}
	
	
	
}
