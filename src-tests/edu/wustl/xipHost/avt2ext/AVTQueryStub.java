package edu.wustl.xipHost.avt2ext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.avt2ext.iterator.Criteria;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class AVTQueryStub implements Query, Runnable {
	final static Logger logger = Logger.getLogger(AVTQueryStub.class);
	SearchResultSetup resultSetup = new SearchResultSetup();
	SearchResult fullSearchResult;
	Map<Integer, Object> adDicomCriteria;
	Map<String, Object> adAimCriteria;
	ADQueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;
	
	public AVTQueryStub(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, ADQueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		fullSearchResult = resultSetup.getSearchResult();
	}
	
	SearchResult result;
	public void run(){
		System.out.println("Testing AVTQueryStub");
		switch (target) {
    	case PATIENT:
    		//List of patients would be given at least        		
    		break;
    	case STUDY:
    		List<Study> studies = null;
    		try{
    			//ignore adAimCriteria
    			//parse and get adDicomCriteria
    			//Find studies based on patientID
    			String value = adDicomCriteria.get(new Integer(1048608)).toString();	//patientId
    			List<Patient> patients = fullSearchResult.getPatients();
				for(Patient patient : patients){
					String patientId = patient.getPatientID();
					if (patientId.equalsIgnoreCase(value)){
						studies = patient.getStudies();
					}
				}
    			result = convertToSearchResult(studies, previousSearchResult, queriedObject);
    		} catch (Exception e){
    			studies = new ArrayList<Study>();
    			logger.error(e, e);
    			notifyException(e.getMessage());
    			return;
    		}
    		break;
		case SERIES: 
			List<Series> series = null;
			try{
				String value1 = adDicomCriteria.get(new Integer(1048608)).toString();	//patientId
				String value2 = adDicomCriteria.get(new Integer(2097165)).toString();	//studyInstanceUID
    			List<Patient> patients = fullSearchResult.getPatients();
				for(Patient patient : patients){
					String patientId = patient.getPatientID();
					if (patientId.equalsIgnoreCase(value1)){
						studies = patient.getStudies();
						for(Study study : studies){
							String studyInstanceUID = study.getStudyInstanceUID();
							if (studyInstanceUID.equalsIgnoreCase(value2)){
								series = study.getSeries();
							}
						}
					}
				}
				result = convertToSearchResult(series, previousSearchResult, queriedObject);
			} catch (Exception e){
				series = new ArrayList<Series>();
    			logger.error(e, e);
    			notifyException(e.getMessage());
    			return;
			}
			break;
		case ITEM: 
					
			break;
			default: logger.warn("Unidentified ADQueryTarget");break;
		}		
	
		//Set original criteria on SearchResult.
		if(previousSearchResult == null){
			Criteria originalCriteria = new Criteria(adDicomCriteria, adAimCriteria);
			result.setOriginalCriteria(originalCriteria);
		}
		fireResultsAvailable(result);
	}
	
	
	public SearchResult convertToSearchResult(Object object, SearchResult initialSearchResult, Object selectedObject){
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
			//selectedObject == null means it is a first query in a progressive query process 
			if(selectedObject == null){	
				if(obj instanceof Patient){
					Patient patientAD = Patient.class.cast(obj);			
					String patientName = patientAD.getPatientName(); if(patientName == null){patientName = "";}
					String patientID = patientAD.getPatientID(); if(patientID == null){patientID = "";}												
					String strPatientBirthDate = patientAD.getPatientBirthDate();	
					if(strPatientBirthDate == null){strPatientBirthDate = "";}
					if(resultAD.contains(patientID) == false){
						patientFromAD = new Patient(patientName, patientID, strPatientBirthDate);
						resultAD.addPatient(patientFromAD);
					}
				}else{
					
				}
			} else if(selectedObject instanceof Patient){
				patientFromAD = Patient.class.cast(selectedObject);
				Study studyAD = Study.class.cast(obj);						
				String studyDate = studyAD.getStudyDate();
				if(studyDate == null){studyDate = "";}
				String studyID = studyAD.getStudyID();if(studyID == null){studyID = "";}	
				String studyDesc = studyAD.getStudyDesc();if(studyDesc == null){studyDesc = "";}
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
				Series seriesAD = Series.class.cast(obj);
				String seriesNumber = seriesAD.getSeriesNumber();				
				if(seriesNumber == null){seriesNumber = "";} 
				String modality = seriesAD.getModality();if(modality == null){modality = "";}
				String seriesDesc = seriesAD.getSeriesDesc();if(seriesDesc == null){seriesDesc = "";}						
				String seriesInstanceUID = seriesAD.getSeriesInstanceUID();if(seriesInstanceUID == null){seriesInstanceUID = "";}				
				if(studyFromAD.contains(seriesInstanceUID) == false){
					seriesFromAD = new Series(seriesNumber.toString(), modality, seriesDesc, seriesInstanceUID);	
					studyFromAD.addSeries(seriesFromAD);
				}
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				studyFromAD.setLastUpdated(lastUpdated);
			} else if(selectedObject instanceof Series){
				seriesFromAD = Series.class.cast(selectedObject);
				if(obj instanceof Item){
					Item itemAD = Item.class.cast(obj);
					String itemSOPInstanceUID = itemAD.getItemID();				
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

	AVTListener listener;
	@Override
	public void addAVTListener(AVTListener l) {
		listener = l;
		
	}

	@Override
	public void setAVTQuery(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, ADQueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.adDicomCriteria = adDicomCriteria; 
		this.adAimCriteria = adAimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject; 
	}
	
	void fireResultsAvailable(SearchResult searchResult){
		AVTSearchEvent event = new AVTSearchEvent(searchResult);         		
        listener.searchResultsAvailable(event);
	}
	
	void notifyException(String message){         		
        listener.notifyException(message);
	}

}
