/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ArrayOfObjectDescriptor;
import org.nema.dicom.wg23.ArrayOfPatient;
import org.nema.dicom.wg23.ArrayOfSeries;
import org.nema.dicom.wg23.ArrayOfStudy;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;

import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.localFileSystem.WG23DataModelFileSystemImpl;
import edu.wustl.xipHost.wg23.WG23DataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class IteratorUtil {
	final static Logger logger = Logger.getLogger(IteratorUtil.class);
	
	@SuppressWarnings("unchecked")
	public static synchronized WG23DataModel getWG23DataModel(TargetElement targetElement){
		if(targetElement == null){return null;}										
		AvailableData availableData = new AvailableData();		
		ArrayOfPatient arrayOfPatient = new ArrayOfPatient();
		List<org.nema.dicom.wg23.Patient> listPatients = arrayOfPatient.getPatient();
		org.nema.dicom.wg23.Patient patient = new org.nema.dicom.wg23.Patient();
		List<SubElement> subElements = targetElement.getSubElements();
		//patientName is the same for all subElements, therefore it is sufficient to get it for the first element at index 0.
		Map<Integer, Object> dicomCriteria = subElements.get(0).getCriteria().getDICOMCriteria();
		String patientName = dicomCriteria.get(new Integer(1048592)).toString();	//patientName
		patient.setName(patientName);
		ArrayOfObjectDescriptor arrayOfObjectDescPatient = new ArrayOfObjectDescriptor();
		patient.setObjectDescriptors(arrayOfObjectDescPatient);
		ArrayOfStudy arrayOfStudy = new ArrayOfStudy();
		List<org.nema.dicom.wg23.Study> listOfStudies = arrayOfStudy.getStudy();
		List<ObjectLocator> objLocators = new ArrayList<ObjectLocator>();	
		if(targetElement.getTarget().equals(IterationTarget.PATIENT)){
			String currentStudyInstanceUID = null;
			ArrayOfSeries arrayOfSeries = null;
			for(SubElement subElement : subElements){
				String studyInstanceUID = subElement.getCriteria().getDICOMCriteria().get(new Integer(2097165)).toString(); //studyInstanceUID
				String path = subElement.getPath();
				Iterator<File> files;
				IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
				files = FileUtils.iterateFiles(new File(path), fileFilter, null);
				if(currentStudyInstanceUID == null || !currentStudyInstanceUID.equalsIgnoreCase(studyInstanceUID)){
					currentStudyInstanceUID = studyInstanceUID;
					org.nema.dicom.wg23.Study study = new org.nema.dicom.wg23.Study();
					study.setStudyUID(studyInstanceUID);
					ArrayOfObjectDescriptor arrayOfObjectDescStudy = new ArrayOfObjectDescriptor();					 					
					study.setObjectDescriptors(arrayOfObjectDescStudy);
					arrayOfSeries = new ArrayOfSeries();
					List<org.nema.dicom.wg23.Series> listOfSeries = arrayOfSeries.getSeries();
					org.nema.dicom.wg23.Series series = new org.nema.dicom.wg23.Series();
					String seriesInstanceUID = subElement.getCriteria().getDICOMCriteria().get(new Integer(2097166)).toString(); //seriesInstanceUID
					series.setSeriesUID(seriesInstanceUID);
					ArrayOfObjectDescriptor arrayOfObjectDesc = new ArrayOfObjectDescriptor();
					List<ObjectDescriptor> listObjectDescs = arrayOfObjectDesc.getObjectDescriptor();	
					//create list of objDescs and add them to each series
					while(files.hasNext()){
						File file = files.next();
						ObjectDescriptor objDesc = new ObjectDescriptor();					
						Uuid objDescUUID = new Uuid();
						objDescUUID.setUuid(UUID.randomUUID().toString());
						objDesc.setUuid(objDescUUID);													
						//check mime type
						String mimeType = null;
						objDesc.setMimeType(mimeType);			
						Uid uid = new Uid();
						String classUID = "";
						uid.setUid(classUID);
						objDesc.setClassUID(uid);
						String modCode = "";						
						Modality modality = new Modality();
						modality.setModality(modCode);
						objDesc.setModality(modality);	
						listObjectDescs.add(objDesc);
						
						ObjectLocator objLoc = new ObjectLocator();				
						objLoc.setUuid(objDescUUID);				
						try {
							objLoc.setUri(file.toURI().toURL().toExternalForm()); //getURI from the iterator
						} catch (MalformedURLException e) {
							logger.error(e, e);
						} 
						objLocators.add(objLoc);
					}
					series.setObjectDescriptors(arrayOfObjectDesc);
					listOfSeries.add(series);
					study.setSeries(arrayOfSeries);
					listOfStudies.add(study);
				} else {
					List<org.nema.dicom.wg23.Series> listOfSeries = arrayOfSeries.getSeries();
					org.nema.dicom.wg23.Series series = new org.nema.dicom.wg23.Series();
					String seriesInstanceUID = subElement.getCriteria().getDICOMCriteria().get(new Integer(2097166)).toString(); //seriesInstanceUID
					series.setSeriesUID(seriesInstanceUID);
					ArrayOfObjectDescriptor arrayOfObjectDesc = new ArrayOfObjectDescriptor();
					List<ObjectDescriptor> listObjectDescs = arrayOfObjectDesc.getObjectDescriptor();	
					//create list of objDescs and add them to each series
					while(files.hasNext()){
						File file = files.next();
						ObjectDescriptor objDesc = new ObjectDescriptor();					
						Uuid objDescUUID = new Uuid();
						objDescUUID.setUuid(UUID.randomUUID().toString());
						objDesc.setUuid(objDescUUID);													
						//check mime type
						objDesc.setMimeType("application/dicom");			
						Uid uid = new Uid();
						String classUID = "";
						uid.setUid(classUID);
						objDesc.setClassUID(uid);
						String modCode = "";						
						Modality modality = new Modality();
						modality.setModality(modCode);
						objDesc.setModality(modality);	
						listObjectDescs.add(objDesc);
						
						ObjectLocator objLoc = new ObjectLocator();				
						objLoc.setUuid(objDescUUID);				
						try {
							objLoc.setUri(file.toURI().toURL().toExternalForm()); //getURI from the iterator
						} catch (MalformedURLException e) {
							logger.error(e, e);
						} 
						objLocators.add(objLoc);
					}
					series.setObjectDescriptors(arrayOfObjectDesc);
					listOfSeries.add(series);
				}
			}
			patient.setStudies(arrayOfStudy);
			listPatients.add(patient);
			availableData.setPatients(arrayOfPatient);
			ArrayOfObjectDescriptor arrayOfObjectDescTopLevel = new ArrayOfObjectDescriptor();					 					
			availableData.setObjectDescriptors(arrayOfObjectDescTopLevel);
		} else if(targetElement.getTarget().equals(IterationTarget.STUDY)) {
			String studyInstanceUID = subElements.get(0).getCriteria().getDICOMCriteria().get(new Integer(2097165)).toString(); //studyInstanceUID
			org.nema.dicom.wg23.Study study = new org.nema.dicom.wg23.Study();
			study.setStudyUID(studyInstanceUID);
			ArrayOfObjectDescriptor arrayOfObjectDescStudy = new ArrayOfObjectDescriptor();					 					
			study.setObjectDescriptors(arrayOfObjectDescStudy);		
			ArrayOfSeries arrayOfSeries = new ArrayOfSeries();
			List<org.nema.dicom.wg23.Series> listOfSeries = arrayOfSeries.getSeries();
			for(SubElement subElement : subElements){	
				String seriesInstanceUID = subElement.getCriteria().getDICOMCriteria().get(new Integer(2097166)).toString(); //seriesInstanceUID
				String path = subElement.getPath();
				IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
				Iterator<File> files = FileUtils.iterateFiles(new File(path), fileFilter, null);
				org.nema.dicom.wg23.Series series = new org.nema.dicom.wg23.Series();
				series.setSeriesUID(seriesInstanceUID);
				ArrayOfObjectDescriptor arrayOfObjectDesc = new ArrayOfObjectDescriptor();
				List<ObjectDescriptor> listObjectDescs = arrayOfObjectDesc.getObjectDescriptor();	
				//create list of objDescs and add them to each series
				while(files.hasNext()){
					File file = files.next();
					ObjectDescriptor objDesc = new ObjectDescriptor();					
					Uuid objDescUUID = new Uuid();
					objDescUUID.setUuid(UUID.randomUUID().toString());
					objDesc.setUuid(objDescUUID);													
					//check mime type
					objDesc.setMimeType("application/dicom");			
					Uid uid = new Uid();
					String classUID = "";
					uid.setUid(classUID);
					objDesc.setClassUID(uid);
					String modCode = "";						
					Modality modality = new Modality();
					modality.setModality(modCode);
					objDesc.setModality(modality);	
					listObjectDescs.add(objDesc);
						
					ObjectLocator objLoc = new ObjectLocator();				
					objLoc.setUuid(objDescUUID);				
					try {
						objLoc.setUri(file.toURI().toURL().toExternalForm()); //getURI from the iterator
					} catch (MalformedURLException e) {
						logger.error(e, e);
					} 
					objLocators.add(objLoc);
				}	
				series.setObjectDescriptors(arrayOfObjectDesc);
				listOfSeries.add(series);
			}
			study.setSeries(arrayOfSeries);
			listOfStudies.add(study);
			
			patient.setStudies(arrayOfStudy);
			listPatients.add(patient);
			availableData.setPatients(arrayOfPatient);
			ArrayOfObjectDescriptor arrayOfObjectDescTopLevel = new ArrayOfObjectDescriptor();					 					
			availableData.setObjectDescriptors(arrayOfObjectDescTopLevel);
			
		} else if(targetElement.getTarget().equals(IterationTarget.SERIES)) {
			String studyInstanceUID = subElements.get(0).getCriteria().getDICOMCriteria().get(new Integer(2097165)).toString(); //studyInstanceUID
			String path = subElements.get(0).getPath();
			IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
			Iterator<File> files = FileUtils.iterateFiles(new File(path), fileFilter, null);
			org.nema.dicom.wg23.Study study = new org.nema.dicom.wg23.Study();
			study.setStudyUID(studyInstanceUID);
			ArrayOfObjectDescriptor arrayOfObjectDescStudy = new ArrayOfObjectDescriptor();					 					
			study.setObjectDescriptors(arrayOfObjectDescStudy);		
			ArrayOfSeries arrayOfSeries = new ArrayOfSeries();
			List<org.nema.dicom.wg23.Series> listOfSeries = arrayOfSeries.getSeries();
			org.nema.dicom.wg23.Series series = new org.nema.dicom.wg23.Series();
			String seriesInstanceUID = subElements.get(0).getCriteria().getDICOMCriteria().get(new Integer(2097166)).toString(); //seriesInstanceUID
			series.setSeriesUID(seriesInstanceUID);
			ArrayOfObjectDescriptor arrayOfObjectDesc = new ArrayOfObjectDescriptor();
			List<ObjectDescriptor> listObjectDescs = arrayOfObjectDesc.getObjectDescriptor();	
			//create list of objDescs and add them to each series
			while(files.hasNext()){
				File file = files.next();
				ObjectDescriptor objDesc = new ObjectDescriptor();					
				Uuid objDescUUID = new Uuid();
				objDescUUID.setUuid(UUID.randomUUID().toString());
				objDesc.setUuid(objDescUUID);													
				//check mime type
				objDesc.setMimeType("application/dicom");			
				Uid uid = new Uid();
				String classUID = "";
				uid.setUid(classUID);
				objDesc.setClassUID(uid);
				String modCode = "";						
				Modality modality = new Modality();
				modality.setModality(modCode);
				objDesc.setModality(modality);	
				listObjectDescs.add(objDesc);				
				ObjectLocator objLoc = new ObjectLocator();				
				objLoc.setUuid(objDescUUID);				
				try {
					objLoc.setUri(file.toURI().toURL().toExternalForm()); //getURI from the iterator
				} catch (MalformedURLException e) {
					logger.error(e, e);
				} 
				objLocators.add(objLoc);	
			}	
			series.setObjectDescriptors(arrayOfObjectDesc);
			listOfSeries.add(series);
			study.setSeries(arrayOfSeries);
			listOfStudies.add(study);
			
			patient.setStudies(arrayOfStudy);
			listPatients.add(patient);
			availableData.setPatients(arrayOfPatient);
			ArrayOfObjectDescriptor arrayOfObjectDescTopLevel = new ArrayOfObjectDescriptor();					 					
			availableData.setObjectDescriptors(arrayOfObjectDescTopLevel);
		}
		WG23DataModelFileSystemImpl dataModel = new WG23DataModelFileSystemImpl();
		dataModel.setAvailableData(availableData);
		ObjectLocator[] objLocs = new ObjectLocator[objLocators.size()];
		objLocators.toArray(objLocs);
		dataModel.setObjectLocators(objLocs);
		WG23DataModel wg23DataModel = dataModel;		
		return wg23DataModel;
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
