/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.util.List;
import org.apache.log4j.Logger;
import org.nema.dicom.PS3_19.ArrayOfObjectDescriptor;
import org.nema.dicom.PS3_19.ArrayOfPatient;
import org.nema.dicom.PS3_19.ArrayOfSeries;
import org.nema.dicom.PS3_19.ArrayOfStudy;
import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.MimeType;
import org.nema.dicom.PS3_19.Modality;
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.UID;
import edu.wustl.xipHost.wg23.Uuid;

import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

/**
 * @author Jaroslaw Krych
 *
 */
public class IteratorUtil {
	final static Logger logger = Logger.getLogger(IteratorUtil.class);
	
	public static synchronized AvailableData getAvailableData(TargetElement targetElement){
		SearchResult subSearchResult = targetElement.getSubSearchResult();
		AvailableData availableData = new AvailableData();		
		ArrayOfPatient arrayOfPatient = new ArrayOfPatient();
		List<org.nema.dicom.PS3_19.Patient> listOfPatients = arrayOfPatient.getPatient();
		List<Patient> searchResultPatients = subSearchResult.getPatients();
		for(int i = 0; i < searchResultPatients.size(); i++){
			Patient searchResultPatient = searchResultPatients.get(i);
			String patientName = searchResultPatient.getPatientName();			
			org.nema.dicom.PS3_19.Patient patient = new org.nema.dicom.PS3_19.Patient();
			patient.setName(patientName);
			ArrayOfObjectDescriptor arrayOfObjectDescPatient = new ArrayOfObjectDescriptor();
			List<ObjectDescriptor> listObjectDescsPatient = arrayOfObjectDescPatient.getObjectDescriptor();
			List<Item> patientItems = searchResultPatient.getItems();
			for(Item item : patientItems){
				ObjectDescriptor objDesc = item.getObjectDescriptor();
				listObjectDescsPatient.add(objDesc);
			}
			patient.setObjectDescriptors(arrayOfObjectDescPatient);
			listOfPatients.add(patient);
			List<Study> searchResultStudies = searchResultPatient.getStudies();
			ArrayOfStudy arrayOfStudy = new ArrayOfStudy();
			List<org.nema.dicom.PS3_19.Study> listOfStudies = arrayOfStudy.getStudy();
			for(int j = 0; j < searchResultStudies.size(); j++){
				Study searchResultStudy = searchResultStudies.get(j);							
				String studyInstanceUID = searchResultStudy.getStudyInstanceUID();
				org.nema.dicom.PS3_19.Study study = new org.nema.dicom.PS3_19.Study();
				UID studyUID = new UID();
				studyUID.setUid(studyInstanceUID);
				study.setStudyUID(studyUID);
				ArrayOfObjectDescriptor arrayOfObjectDescStudy = new ArrayOfObjectDescriptor();
				List<ObjectDescriptor> listObjectDescsStudy = arrayOfObjectDescStudy.getObjectDescriptor();
				List<Item> studyItems = searchResultStudy.getItems();
				for(Item item : studyItems){
					ObjectDescriptor objDesc = item.getObjectDescriptor();
					listObjectDescsStudy.add(objDesc);
				}
				study.setObjectDescriptors(arrayOfObjectDescStudy);
				listOfStudies.add(study);
				patient.setStudies(arrayOfStudy);
				List<Series> searchResultSeries = searchResultStudy.getSeries();
				ArrayOfSeries arrayOfSeries = new ArrayOfSeries();
				List<org.nema.dicom.PS3_19.Series> listOfSeries = arrayOfSeries.getSeries();
				for(int k = 0; k < searchResultSeries.size(); k++){
					Series oneSeries = searchResultSeries.get(k);
					String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
					org.nema.dicom.PS3_19.Series series = new org.nema.dicom.PS3_19.Series();
					UID seriesUID = new UID();
					seriesUID.setUid(seriesInstanceUID);
					series.setSeriesUID(seriesUID);
					ArrayOfObjectDescriptor arrayOfObjectDescSeries = new ArrayOfObjectDescriptor();
					List<ObjectDescriptor> listObjectDescsSeries = arrayOfObjectDescSeries.getObjectDescriptor();
					List<Item> seriesItems = oneSeries.getItems();
					for(Item item : seriesItems){
						ObjectDescriptor objDesc = item.getObjectDescriptor();
						listObjectDescsSeries.add(objDesc);
					}
					series.setObjectDescriptors(arrayOfObjectDescSeries);
					listOfSeries.add(series);
					study.setSeries(arrayOfSeries);
				}
			}
			availableData.setPatients(arrayOfPatient);
			ArrayOfObjectDescriptor arrayOfObjectDescTopLevel = new ArrayOfObjectDescriptor();					 					
			availableData.setObjectDescriptors(arrayOfObjectDescTopLevel);
		}
		return availableData;
	}
	
	public static int getTotalNumberOfItems(SearchResult subSearchResult){
		int numTopItems = subSearchResult.getItems().size();
		int numPatientItems = 0;
		int numStudyItems = 0;
		int numSeriesItems = 0;
		List<Patient> searchResultPatients = subSearchResult.getPatients();
		for(int i = 0; i < searchResultPatients.size(); i++){
			Patient searchresultPatient = searchResultPatients.get(i);
			numPatientItems = numPatientItems + searchresultPatient.getItems().size();
			List<Study> searchResultStudies = searchresultPatient.getStudies();
			for(int j = 0; j < searchResultStudies.size(); j++){
				Study searchResultStudy = searchResultStudies.get(j);							
				numStudyItems = numStudyItems + searchResultStudy.getItems().size();
				List<Series> searchResultSeries = searchResultStudy.getSeries();
				for(int k = 0; k < searchResultSeries.size(); k++){
					Series oneSeries = searchResultSeries.get(k);
					numSeriesItems = numSeriesItems + oneSeries.getItems().size();
				}
			}
		}
		return numTopItems + numPatientItems + numStudyItems +  numSeriesItems;
	}
}
