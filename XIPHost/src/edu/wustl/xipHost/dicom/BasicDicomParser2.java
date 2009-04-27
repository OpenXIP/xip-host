/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import edu.wustl.xipHost.gui.ExceptionDialog;
/**
 * <font  face="Tahoma" size="2">
 * Parses DICOM file and retrieves commonly used attribues.<br></br>
 * @version	January 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class BasicDicomParser2 {		
	String patientName;	
	String patientID;
	String patientBirthDate;
	String studyDate;
	String studyID;	
	String studyDesc;
	String studyInstanceUID;
	
	String seriesNumber;
	String modality;	
	String seriesDesc;
	String seriesInstanceUID;
	
	String sopClassUID;
	String sopInstanceUID;
	String transferSyntaxUID;
	
	AttributeList list = new AttributeList();
	
	public AttributeList parse(File dicomFile){		
		try {			
			list.read(dicomFile.getAbsolutePath() , null, true, true);
			/*Iterator iter = list.getDictionary().getTagIterator();
			while(iter.hasNext()){
				AttributeTag attTag  = (AttributeTag)iter.next();
				String strAtt = attTag.toString();									
				String attValue = Attribute.getSingleStringValueOrEmptyString(list, attTag);
				System.out.println(strAtt + " " + attValue);
			}*/
		} catch (IOException e) {			
			new ExceptionDialog("DICOM Exception!", 
					dicomFile.getName() + " is not DICOM file!",
					"Data for this file will not be saved.");
			//e.printStackTrace();
		} catch (DicomException e) {		
			e.printStackTrace();
		}				
		return list;
	}
    
	public String getPatientName(){						
		patientName = new String();
		Attribute att = list.get(TagFromName.PatientName);
		if(att != null){
			patientName = att.getDelimitedStringValuesOrEmptyString();
		}
		return patientName;
	}
	public String getPatientID(){
		patientID = new String();
		Attribute att = list.get(TagFromName.PatientID);		
		if(att != null){
			patientID = att.getDelimitedStringValuesOrEmptyString();
		}		
		return patientID;
	}	
	public String getPatientBirthDate(){
		patientBirthDate = new String();
		Attribute att = list.get(TagFromName.PatientBirthDate);		
		if(att != null){
			patientBirthDate = att.getDelimitedStringValuesOrEmptyString();
		}		
		return patientID;
	}	
	public String getStudyDate(){	
		studyDate = new String();
		Attribute att = list.get(TagFromName.StudyDate);
		if(att != null){
			studyDate = att.getDelimitedStringValuesOrEmptyString();
		}		
		return studyDate;			
	}
	public String getStudyID(){
		studyID = new String();
		Attribute att = list.get(TagFromName.StudyID);
		if(att != null){
			studyID = att.getDelimitedStringValuesOrEmptyString();
		}		
		return studyID;
	}	
	public String getStudyDescription(){
		studyDesc = new String();
		Attribute att = list.get(TagFromName.StudyDescription);
		if(att != null){
			studyDesc = att.getDelimitedStringValuesOrEmptyString();
		}		
		return studyDesc;
	}
	public String getStudyInstanceUID(){		
		studyInstanceUID = new String();
		Attribute att = list.get(TagFromName.StudyInstanceUID);
		if(att != null){
			studyInstanceUID = att.getDelimitedStringValuesOrEmptyString();
		}		
		return studyInstanceUID;					
	}
	
	public String getSeriesNumer(){		
		seriesNumber = new String();
		Attribute att = list.get(TagFromName.SeriesNumber);
		if(att != null){
			seriesNumber = att.getDelimitedStringValuesOrEmptyString();
		}		
		return seriesNumber;	
	}
	public String getModality(){
		modality = new String();
		Attribute att = list.get(TagFromName.Modality);
		if(att != null){
			modality = att.getDelimitedStringValuesOrEmptyString();
		}		
		return modality;
	}
	public String getSeriesDescription(){
		seriesDesc = new String();
		Attribute att = list.get(TagFromName.SeriesDescription);
		if(att != null){
			seriesDesc = att.getDelimitedStringValuesOrEmptyString();
		}		
		return seriesDesc;
	}	
	public String getSeriesInstanceUID(){
		seriesInstanceUID = new String();
		Attribute att = list.get(TagFromName.SeriesInstanceUID);
		if(att != null){
			seriesInstanceUID = att.getDelimitedStringValuesOrEmptyString();
		}		
		return seriesInstanceUID;
	}
	public String getSOPClassUID(){
		sopClassUID = new String();
		Attribute att = list.get(TagFromName.SOPClassUID);
		if(att != null){
			sopClassUID = att.getDelimitedStringValuesOrEmptyString();
		}
		return sopClassUID;
	}
	public String getSOPInstanceUID(){
		sopInstanceUID = new String();
		Attribute att = list.get(TagFromName.SOPInstanceUID);
		if(att != null){
			sopInstanceUID = att.getDelimitedStringValuesOrEmptyString();
		}
		return sopInstanceUID;
	}
	public String getTransferSyntaxUID(){
		transferSyntaxUID = new String();
		Attribute att = list.get(TagFromName.TransferSyntaxUID);
		if(att != null){
			transferSyntaxUID = att.getDelimitedStringValuesOrEmptyString();
		}
		return transferSyntaxUID;
	}
	
	public String[][] getShortDicomHeader(URI item){		
		String[][] map = new String[15][2];
		map[0][0] = "Patient name";
		map[1][0] = "Patient ID";
		map[2][0] = "Birth date";
		map[3][0] = "Study date";
		map[4][0] = "Study ID";
		map[5][0] = "Study description";
		map[6][0] = "Series number";
		map[7][0] = "Modality";
		map[8][0] = "Series description";
		map[9][0] = "File location";
		map[10][0] = "StudyInstanceUID";
		map[11][0] = "SeriesInstanceUID";
		map[12][0] = "TransferSyntaxUID";
		map[13][0] = "SOPInstanceUID";
		map[14][0] = "SOPClassUID";
		map[0][1] = getPatientName();
		map[1][1] = getPatientID();
		map[2][1] = getPatientBirthDate();
		map[3][1] = getStudyDate();
		map[4][1] = getStudyID();
		map[5][1] = getStudyDescription();
		map[6][1] = getSeriesNumer();
		map[7][1] = getModality();
		map[8][1] = getSeriesDescription();
		try {
			map[9][1] = item.toURL().toExternalForm();
		} catch (MalformedURLException e) {
			map[9][1] = "";
		}
		map[10][1] = getStudyInstanceUID(); 
		map[11][1] = getSeriesInstanceUID();
		map[12][1] = getTransferSyntaxUID();
		map[13][1] = getSOPInstanceUID();
		map[14][1] = getSOPClassUID();
		return map;
	}	
	
	public static void main (String [] args){
		BasicDicomParser2 parser = new BasicDicomParser2();
		parser.parse(new File("C:/WUSTL/Tmp/IN000349"));				
		System.out.println(parser.getStudyInstanceUID());
		System.out.println(parser.getSeriesInstanceUID());
		
	}
}
