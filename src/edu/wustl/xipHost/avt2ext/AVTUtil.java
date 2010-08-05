/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.avt2ext.iterator.TargetElement;
import edu.wustl.xipHost.avt2ext.iterator.TargetIterator;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTUtil {
	final static Logger logger = Logger.getLogger(AVTUtil.class);
	//Object could be list of patients, studies or series
	public static SearchResult convertToSearchResult(Object object, SearchResult initialSearchResult, Object selectedObject){
		SearchResult resultAD = null;
		if(initialSearchResult == null){
			resultAD = new SearchResult("AD Database");
		}else{
			resultAD = initialSearchResult;
		}	
		Patient patientFromAD = null;
		Study studyFromAD = null;
		Series seriesFromAD = null; 
		Item itemFromAD = null;
		List<?> listOfObjects = (List<?>)object;
		Iterator<?> iter = listOfObjects.iterator();
		while (iter.hasNext()) {
			java.lang.Object obj = iter.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
			//selectedObject == null means it is a first query in a progressive query process 
			if(selectedObject == null){	
				if(obj instanceof com.siemens.scr.avt.ad.dicom.Patient){
					com.siemens.scr.avt.ad.dicom.Patient patientAD = com.siemens.scr.avt.ad.dicom.Patient.class.cast(obj);			
					String patientName = patientAD.getPatientName(); if(patientName == null){patientName = "";}
					String patientID = patientAD.getPatientID(); if(patientID == null){patientID = "";}
					Date patientADBirthDate = patientAD.getPatientBirthDate();				
					Calendar patientBirthDate = Calendar.getInstance();										
					String strPatientBirthDate = null;
					if(patientADBirthDate != null){
						patientBirthDate.setTime(patientADBirthDate);						
						strPatientBirthDate = sdf.format(patientBirthDate.getTime());
						if(strPatientBirthDate == null){strPatientBirthDate = "";}
			        }else{
			        	strPatientBirthDate = "";
			        }
					if(resultAD.contains(patientID) == false){
						patientFromAD = new Patient(patientName, patientID, strPatientBirthDate);
						resultAD.addPatient(patientFromAD);
					}
				}else{
					
				}
			} else if(selectedObject instanceof Patient){
				patientFromAD = Patient.class.cast(selectedObject);
				com.siemens.scr.avt.ad.dicom.GeneralStudy studyAD = com.siemens.scr.avt.ad.dicom.GeneralStudy.class.cast(obj);				
				Date studyDateTime = studyAD.getStudyDateTime();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(studyDateTime); 			
				String studyDate = null;
				if(calendar != null){
		        	studyDate = sdf.format(calendar.getTime());if(studyDate == null){studyDate = "";}
		        }else{
		        	studyDate = "";
		        }
				String studyID = studyAD.getStudyID();if(studyID == null){studyID = "";}	
				String studyDesc = studyAD.getStudyDescription();if(studyDesc == null){studyDesc = "";}
				String studyInstanceUID = studyAD.getStudyInstanceUID();if(studyInstanceUID == null){studyInstanceUID = "";}				
				studyFromAD = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				if(patientFromAD.contains(studyInstanceUID) == false){
					studyFromAD = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
					patientFromAD.addStudy(studyFromAD);
				}
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				patientFromAD.setLastUpdated(lastUpdated);
			} else if(selectedObject instanceof Study){
				studyFromAD = Study.class.cast(selectedObject);
				com.siemens.scr.avt.ad.dicom.GeneralSeries seriesAD = com.siemens.scr.avt.ad.dicom.GeneralSeries.class.cast(obj);
				String seriesNumber = seriesAD.getSeriesNumber();				
				if(seriesNumber == null){seriesNumber = "";} 
				String modality = seriesAD.getModality();if(modality == null){modality = "";}
				String seriesDesc = seriesAD.getSeriesDescription();if(seriesDesc == null){seriesDesc = "";}						
				String seriesInstanceUID = seriesAD.getSeriesInstanceUID();if(seriesInstanceUID == null){seriesInstanceUID = "";}				
				if(studyFromAD.contains(seriesInstanceUID) == false){
					seriesFromAD = new Series(seriesNumber.toString(), modality, seriesDesc, seriesInstanceUID);	
					studyFromAD.addSeries(seriesFromAD);
				}
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				studyFromAD.setLastUpdated(lastUpdated);
			} else if(selectedObject instanceof Series){
				seriesFromAD = Series.class.cast(selectedObject);
				if(obj instanceof com.siemens.scr.avt.ad.dicom.GeneralImage){
					com.siemens.scr.avt.ad.dicom.GeneralImage itemAD = com.siemens.scr.avt.ad.dicom.GeneralImage.class.cast(obj);
					String itemSOPInstanceUID = itemAD.getSOPInstanceUID();				
					if(itemSOPInstanceUID == null){itemSOPInstanceUID = "";} 				
					if(seriesFromAD.containsItem(itemSOPInstanceUID) == false){
						itemFromAD = new ImageItem(itemSOPInstanceUID);							
					}
				}else if(obj instanceof String){
					String imageAnnotationType = "";
					String dateTime = "";
					String authorName = "";
					String aimUID = String.class.cast(obj);
					itemFromAD = new AIMItem(imageAnnotationType, dateTime, authorName, aimUID);
				}
				seriesFromAD.addItem(itemFromAD);
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				seriesFromAD.setLastUpdated(lastUpdated);
			}
		}	
		if(logger.isDebugEnabled()){
			 Iterator<Patient> patients = resultAD.getPatients().iterator();
			 while(patients.hasNext()){
				 Patient patient = patients.next();
				 Timestamp patientLastUpdated = patient.getLastUpdated();
				 String strPatientLastUpdated = null;
				 if(patientLastUpdated != null){
					 strPatientLastUpdated = patientLastUpdated.toString();
				 }
				 logger.debug(patient.toString() + " Last updated: " + strPatientLastUpdated);
				 Iterator<Study> studies = patient.getStudies().iterator();
				 while(studies.hasNext()){
					 Study study = studies.next();
					 Timestamp studyLastUpdated = study.getLastUpdated();
					 String strStudyLastUpdated = null;
					 if(studyLastUpdated != null){
						 strStudyLastUpdated = studyLastUpdated.toString();
					 }
					 logger.debug(study.toString() + " Last updated: " + strStudyLastUpdated);
					 Iterator<Series> series = study.getSeries().iterator();
					 while(series.hasNext()){
						 Series oneSeries = series.next();
						 Timestamp seriesLastUpdated = oneSeries.getLastUpdated();
						 String strSeriesLastUpdated = null;
						 if(seriesLastUpdated != null){
							 strSeriesLastUpdated = seriesLastUpdated.toString();
						 }
						 logger.debug(oneSeries.toString() + " Last updated: " + strSeriesLastUpdated);
					 }
				 }
			 }
		}
		return resultAD;
	}
	

	public Iterator<TargetElement> createIterator(SearchResult selectedDataSearchResult, IterationTarget target, Query avtQuery){
		Iterator<TargetElement> iter = new TargetIterator(selectedDataSearchResult, target, avtQuery);
		return iter;
	}
	
}
