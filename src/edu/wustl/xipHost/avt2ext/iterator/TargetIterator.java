/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import edu.wustl.xipHost.avt2ext.ADQueryTarget;
import edu.wustl.xipHost.avt2ext.AVTListener;
import edu.wustl.xipHost.avt2ext.AVTRetrieveEvent;
import edu.wustl.xipHost.avt2ext.AVTSearchEvent;
import edu.wustl.xipHost.avt2ext.Query;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;

/**
 * @author Matthew Kelsey
 *
 */
public class TargetIterator implements Iterator<TargetElement>, Runnable, AVTListener {
	final static Logger logger = Logger.getLogger(TargetIterator.class);
	SearchResult selectedDataSearchResult;
	IterationTarget target;
	Query query;
	Patient currentPatient = null;
	Iterator<Patient> patientIt = null;
	Study currentStudy;
	Iterator<Study> studyIt = null;
	Iterator<Series> seriesIt = null;
	
	List<TargetElement> targetElementList = new ArrayList<TargetElement>();
	Iterator<TargetElement> targetElementListIt = null; 
	
	String pathRoot;
		
	private TargetIterator(SearchResult selectedDataSearchResult, IterationTarget target, Query query, File pathTmpDir, TargetIteratorListener targetListener) throws NullPointerException {
		if(selectedDataSearchResult == null)
			throw new NullPointerException("Cannot initialize TargetIterator with null SearchResult pointer");
		if(target == null)
			throw new NullPointerException("Cannot initialize TargetIterator with null IterationTarget");
		if(query == null)
			throw new NullPointerException("Cannot initialize TargetIterator with null Query");
		if(pathTmpDir == null)
			throw new NullPointerException("Cannot initialize TargetIterator with null value of path Tmp directory");
		if(pathTmpDir.exists() == false){
				throw new IllegalArgumentException("Tmporary Directory: " + pathTmpDir.getAbsolutePath() + " doesn't exists");
		}
		this.selectedDataSearchResult = selectedDataSearchResult;
		this.target = target;
		this.query = query;
		this.listener = targetListener;
		try {
			pathRoot = pathTmpDir.getCanonicalPath();
			logger.debug("TargetIterator temp directory is: " + pathRoot );
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
	
	// Initialize and start process to build TargetIterator list
	// Return iterator to list under construction
	public static TargetIterator buildTargetElementIterator(SearchResult selectedDataSearchResult, IterationTarget target, Query query, File pathTmpDir, TargetIteratorListener targetListener){
		TargetIterator targetIter = new TargetIterator(selectedDataSearchResult, target, query, pathTmpDir, targetListener);
		try {
			Thread t = new Thread(targetIter);
			t.start();
		} catch(Exception e) {
			logger.error(e, e);
		}
		return targetIter;
	}
	
	// Run thread to fill TargetElement list from SearchResult elements
	@Override
	public void run() {
		// Set internal target iterators
		try {
			if(this.selectedDataSearchResult.getPatients() != null) {
				List<Patient> patients = this.selectedDataSearchResult.getPatients();
				patientIt = patients.iterator();
			}
		} catch(Exception e) {
			logger.error(e, e);
		}
		// Fill targetElementsList with target elements from searchResult
		while(hasNextSearchResult())
		{
			TargetElement element = loadNextSearchResult();
			targetElementList.add(element);
			notifyTargetIteratorElementAvailable(element);
		}
		targetElementListIt = targetElementList.iterator();
		notifyIteratorComplete();		
	}
	
	TargetIteratorListener listener;
	private void notifyTargetIteratorElementAvailable(TargetElement element){
		IteratorElementEvent event = new IteratorElementEvent(element);
		listener.targetElementAvailable(event);
	}
	
	private void notifyIteratorComplete(){
		IteratorEvent event = new IteratorEvent(this);
		listener.fullIteratorAvailable(event);
	}
	
	public boolean hasNext(){
		return targetElementListIt.hasNext();
	}

	public TargetElement next(){
		return targetElementListIt.next();
	}
	
	// ** If not up to date, query for study list in patient target ** //
	private boolean updatePatient(Patient patient) {
		if(patient.getLastUpdated() == null) {
			// Query for patient
			Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			dicomCriteria.put(Tag.PatientName, patient.getPatientName());
			dicomCriteria.put(Tag.PatientID, patient.getPatientID());
			query.setAVTQuery(dicomCriteria, aimCriteria, ADQueryTarget.STUDY, selectedDataSearchResult, patient);
			query.addAVTListener(this);
			Thread t = new Thread((Runnable) query);
			t.start();	
			try {
				t.join();
				Patient updatedPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
				if(updatedPatient != null){
					if(updatedPatient.getLastUpdated() != null){
						return true;
					}
				}
			} catch (InterruptedException e) {
				logger.error(e, e);
				return false;
			}
		}
		return true;
	}
	
	private boolean updateStudy(Study study) {
		if(study.getLastUpdated() == null) {
			// Query for Study
			String patientId = null;
			String patientName = null;
			for (Patient patient : selectedDataSearchResult.getPatients()){
				if(patient.contains(study.getStudyInstanceUID())){
					patientId = patient.getPatientID();
					patientName = patient.getPatientName();
				}
			}
			Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			dicomCriteria.put(Tag.PatientID, patientId);
			dicomCriteria.put(Tag.PatientName, patientName);
			dicomCriteria.put(Tag.StudyInstanceUID, study.getStudyInstanceUID());
			query.setAVTQuery(dicomCriteria, aimCriteria, ADQueryTarget.SERIES, selectedDataSearchResult, study);
			query.addAVTListener(this);
			Thread t = new Thread((Runnable) query);
			t.start();
			try {
				t.join();
				for(Patient patient : selectedDataSearchResult.getPatients()){
					Study updatedStudy = patient.getStudy(study.getStudyInstanceUID());
					if(updatedStudy != null){
						if(updatedStudy.getLastUpdated() != null){
							return true;
						}
					}
				}
			} catch (InterruptedException e) {
				logger.error(e, e);
				return false;
			}
		}
		return true;
	}

	private boolean updateSeries(Series series) {
		if(series.getLastUpdated() == null) {
			// Query for Series/Items
			String patientId = null;
			String patientName = null;
			String studyInstanceUID = null;
			for (Patient patient : selectedDataSearchResult.getPatients()){
				for(Study study : patient.getStudies()){
					if(study.contains(series.getSeriesInstanceUID())){
						patientId = patient.getPatientID();
						patientName = patient.getPatientName();
						studyInstanceUID = study.getStudyInstanceUID();
					}
				}
			}
			Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			dicomCriteria.put(Tag.PatientID, patientId);
			dicomCriteria.put(Tag.PatientName, patientName);
			dicomCriteria.put(Tag.StudyInstanceUID, studyInstanceUID);
			dicomCriteria.put(Tag.SeriesInstanceUID, series.getSeriesInstanceUID());
			//dicomCriteria.put(Tag.Modality, series.getModality());
			query.setAVTQuery(dicomCriteria, aimCriteria, ADQueryTarget.ITEM, selectedDataSearchResult, series);
			query.addAVTListener(this);
			Thread t = new Thread((Runnable) query);
			t.start();
			try {
				t.join();
				for(Patient patient : selectedDataSearchResult.getPatients()){
					for(Study study : patient.getStudies()){
						Series updatedSeries = study.getSeries(series.getSeriesInstanceUID());
						if(updatedSeries != null){
							if(updatedSeries.getLastUpdated() != null){
								return true;
							}
						}
					}
				}
			} catch (InterruptedException e) {
				logger.error(e, e);
				return false;
			}
		}
		return true;
	}
	
	private boolean hasNextSearchResult() {
		if(target == IterationTarget.PATIENT) {
			return this.patientIt.hasNext();
		
		} else if(target == IterationTarget.STUDY) {
			// study list defined and contains additional entries
			if(this.studyIt != null && this.studyIt.hasNext()) {
				return true;
			
			// end of study list for this patient, load list from next (or first) patient
			} else if(patientIt.hasNext() == true) {
				this.currentPatient = patientIt.next();
				updatePatient(this.currentPatient);
				studyIt = this.currentPatient.getStudies().iterator();
				return studyIt.hasNext();
			
			// no additional entries in study list, no further patients in results
			} else
				return false;
		
		} else if(target == IterationTarget.SERIES) {
			// series list defined and contains additional entries
			if(this.seriesIt != null && this.seriesIt.hasNext()) {
				return true;
			
			// end of series list for this study, load list from next (or first) study
			} else if(studyIt != null && studyIt.hasNext()) {
				this.currentStudy = studyIt.next();
				updateStudy(this.currentStudy);
				seriesIt = this.currentStudy.getSeries().iterator();
				return seriesIt.hasNext();
			
			// end of series list for this patient, load list from next (or first) patient/study
			} else if(patientIt.hasNext() == true) {
				this.currentPatient = patientIt.next();
				updatePatient(this.currentPatient);
				studyIt = this.currentPatient.getStudies().iterator();
				this.currentStudy = studyIt.next();
				updateStudy(this.currentStudy);
				seriesIt = this.currentStudy.getSeries().iterator();
				return seriesIt.hasNext();
				// TODO Need logic to support skipping empty series lists in intermediate Patient, etc.
				// TODO hasNext() currently returns false 
			
			// no additional entries in series list, no further patients/studies in results
			} else
				return false;
			
		}
			
		return false;
	}
		
		
		

	private TargetElement loadNextSearchResult() {
		TargetElement targetElement = null;
		if(targetElementList != null){
			
			// ** PATIENT TARGET ** //
			if(this.target == IterationTarget.PATIENT) {
				// Update all elements below current patient
				Patient patient = patientIt.next();
				updatePatient(patient);
				for(Study study : patient.getStudies()) {
					updateStudy(study);
					for(Series series : study.getSeries()) {
						updateSeries(series);
					}
				}
				// Build Criteria and path for patient
				Criteria patientCriteria = new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());
				String patientPath = pathRoot;
				patientCriteria.getDICOMCriteria().putAll(selectedDataSearchResult.getOriginalCriteria().getDICOMCriteria());
				patientCriteria.getAIMCriteria().putAll(selectedDataSearchResult.getOriginalCriteria().getAIMCriteria());
				if(patient.getPatientName() != null && !patient.getPatientName().isEmpty()) {
					patientCriteria.getDICOMCriteria().put(Tag.PatientName, patient.getPatientName());
				}
				if(patient.getPatientID() != null && !patient.getPatientID().isEmpty()) {
					patientCriteria.getDICOMCriteria().put(Tag.PatientID, patient.getPatientID());
					patientPath += File.separator + patient.getPatientID();
					new File(patientPath).mkdir();
				}
	
				// Build Criteria and path for each Patient/Study/Series SubElement
				// Plus create empty files for all items found in Series
				List<SubElement> subElements = new ArrayList<SubElement>();
				for(Study study : patient.getStudies()) {
					Criteria studyCriteria= new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());		
					String studyPath = patientPath;
					studyCriteria.getDICOMCriteria().putAll(patientCriteria.getDICOMCriteria());
					studyCriteria.getAIMCriteria().putAll(patientCriteria.getAIMCriteria());
					if(study.getStudyInstanceUID() != null && !study.getStudyInstanceUID().isEmpty()) {
						studyCriteria.getDICOMCriteria().put(Tag.StudyInstanceUID, study.getStudyInstanceUID());
						studyPath += File.separator + study.getStudyInstanceUID();
						new File(studyPath).mkdir();
					}
					for(Series series : study.getSeries()) {
						Criteria seriesCriteria = new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());
						String seriesPath = studyPath;
						seriesCriteria.getDICOMCriteria().putAll(studyCriteria.getDICOMCriteria());
						seriesCriteria.getAIMCriteria().putAll(studyCriteria.getAIMCriteria());
						if(series.getSeriesInstanceUID() != null && !series.getSeriesInstanceUID().isEmpty()) {
							seriesCriteria.getDICOMCriteria().put(Tag.SeriesInstanceUID, series.getSeriesInstanceUID());
							//seriesCriteria.getDICOMCriteria().put(Tag.Modality, series.getModality());
							seriesPath += File.separator + series.getSeriesInstanceUID();
							new File(seriesPath).mkdir();
							List<Item> items = series.getItems();
							for(Item item : items){
								try {
									File.createTempFile(item.getItemID() + "_", ".tmp", new File(seriesPath));
								} catch (IOException e) {
									logger.error(e, e);
								}
							}	
						}
						SubElement seriesSubElement = new SubElement(seriesCriteria, seriesPath);
						subElements.add(seriesSubElement);
					}
				}
				targetElement = new TargetElement(patient.getPatientID(), subElements, target);
				notifyTargetIteratorElementAvailable(targetElement);
					
			// ** STUDY TARGET ** //
			} else if(this.target == IterationTarget.STUDY) {
				// Update all elements below current study
				Study study = studyIt.next();
				updateStudy(study);
				for(Series series : study.getSeries()) {
					updateSeries(series);
				}
				// Build Criteria and path for Study/Series
				List<SubElement> subElements = new ArrayList<SubElement>();
				Criteria studyCriteria = new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());
				String studyPath = pathRoot;
				studyCriteria.getDICOMCriteria().putAll(selectedDataSearchResult.getOriginalCriteria().getDICOMCriteria());
				studyCriteria.getAIMCriteria().putAll(selectedDataSearchResult.getOriginalCriteria().getAIMCriteria());
				if(currentPatient.getPatientName() != null && !currentPatient.getPatientName().isEmpty()) {
					studyCriteria.getDICOMCriteria().put(Tag.PatientName, currentPatient.getPatientName());
				}
				if(currentPatient.getPatientID() != null && !currentPatient.getPatientID().isEmpty()) {
					studyCriteria.getDICOMCriteria().put(Tag.PatientID, currentPatient.getPatientID());
					studyPath += File.separator + currentPatient.getPatientID();
					new File(studyPath).mkdir();
				}
				if(study.getStudyInstanceUID() != null && !study.getStudyInstanceUID().isEmpty()) {
					studyCriteria.getDICOMCriteria().put(Tag.StudyInstanceUID, study.getStudyInstanceUID());
					studyPath += File.separator + study.getStudyInstanceUID();
					new File(studyPath).mkdir();
				}
				for(Series series : study.getSeries()) {
					Criteria seriesCriteria = new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());
					String seriesPath = studyPath;
					seriesCriteria.getDICOMCriteria().putAll(studyCriteria.getDICOMCriteria());
					seriesCriteria.getAIMCriteria().putAll(studyCriteria.getAIMCriteria());
					if(series.getSeriesInstanceUID() != null && !series.getSeriesInstanceUID().isEmpty()) {
						seriesCriteria.getDICOMCriteria().put(Tag.SeriesInstanceUID, series.getSeriesInstanceUID());
						//seriesCriteria.getDICOMCriteria().put(Tag.Modality, series.getModality());
						seriesPath += File.separator + series.getSeriesInstanceUID();
						new File(seriesPath).mkdir();
						List<Item> items = series.getItems();
						for(Item item : items){
							try {
								File.createTempFile(item.getItemID() + "_", ".tmp", new File(seriesPath));
							} catch (IOException e) {
								logger.error(e, e);
							}
						}	
					}
					SubElement seriesSubElement = new SubElement(seriesCriteria, seriesPath);
					subElements.add(seriesSubElement);
				}
				targetElement = new TargetElement(study.getStudyInstanceUID(), subElements, target);
				notifyTargetIteratorElementAvailable(targetElement);
			
			// ** SERIES TARGET ** //
			} else if(this.target == IterationTarget.SERIES) {
				// Update Item list in series
				Series series = seriesIt.next();
				updateSeries(series);
				
				// Build Criteria for Series
				List<SubElement> subElements = new ArrayList<SubElement>();
				Criteria seriesCriteria = new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());
				String seriesPath = pathRoot;
				seriesCriteria.getDICOMCriteria().putAll(selectedDataSearchResult.getOriginalCriteria().getDICOMCriteria());
				seriesCriteria.getAIMCriteria().putAll(selectedDataSearchResult.getOriginalCriteria().getAIMCriteria());
				if(this.currentPatient.getPatientName() != null && !this.currentPatient.getPatientName().isEmpty()) {
					seriesCriteria.getDICOMCriteria().put(Tag.PatientName, this.currentPatient.getPatientName());
				}
				if(this.currentPatient.getPatientID() != null && !this.currentPatient.getPatientID().isEmpty()) {
					seriesCriteria.getDICOMCriteria().put(Tag.PatientID, this.currentPatient.getPatientID());
					seriesPath += File.separator + currentPatient.getPatientID();
					new File(seriesPath).mkdir();
				}
				if(this.currentStudy.getStudyInstanceUID() != null && !this.currentStudy.getStudyInstanceUID().isEmpty()) {
					seriesCriteria.getDICOMCriteria().put(Tag.StudyInstanceUID, this.currentStudy.getStudyInstanceUID());
					//seriesCriteria.getDICOMCriteria().put(Tag.Modality, series.getModality());
					seriesPath += File.separator + currentStudy.getStudyInstanceUID();
					new File(seriesPath).mkdir();
				}
				if(series.getSeriesInstanceUID() != null && !series.getSeriesInstanceUID().isEmpty()) {
					seriesCriteria.getDICOMCriteria().put(Tag.SeriesInstanceUID, series.getSeriesInstanceUID());
					//seriesCriteria.getDICOMCriteria().put(Tag.Modality, series.getModality());
					seriesPath += File.separator + series.getSeriesInstanceUID();
					new File(seriesPath).mkdir();
				}
				List<Item> items = series.getItems();
				for(Item item : items){
					try {
						File.createTempFile(item.getItemID() + "_", ".tmp", new File(seriesPath));
					} catch (IOException e) {
						logger.error(e, e);
					}
				}	
				SubElement seriesSubElement = new SubElement(seriesCriteria, seriesPath);
				subElements.add(seriesSubElement);
				targetElement = new TargetElement(series.getSeriesInstanceUID(), subElements, target);
				notifyTargetIteratorElementAvailable(targetElement);
			} else
				throw new NoSuchElementException();
		} else {
			throw new NoSuchElementException();
		}
			return targetElement;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retriveResultsAvailable(AVTRetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	synchronized public void searchResultsAvailable(AVTSearchEvent e) {
		selectedDataSearchResult = (SearchResult) e.getSource();
		//SearchResult updatedDataSearchResult = (SearchResult) e.getSource();
		//mergeUpdate(updatedDataSearchResult);
	}
	
	synchronized void mergeUpdate(SearchResult updatedDataSearchResult){
		List<Patient> initialPatients = selectedDataSearchResult.getPatients();
		List<Patient> updatedPatients = updatedDataSearchResult.getPatients();
		System.out.println(initialPatients.equals(updatedPatients));
		if(initialPatients.size() != updatedPatients.size()){
			for(Patient patient : updatedPatients){
				if(!selectedDataSearchResult.contains(patient.getPatientID())){
					selectedDataSearchResult.addPatient(patient);
				}
			}
		} else if (initialPatients.size() == updatedPatients.size() ){
			for(Patient patient : updatedPatients){
				Patient initialPatient = selectedDataSearchResult.getPatient(patient.getPatientID());
				List<Study> initialStudies = initialPatient.getStudies();
				List<Study> updatedStudies = patient.getStudies();
				if(initialStudies.size() != updatedStudies.size()){
					for(Study study : updatedStudies){
						if(!initialPatient.contains(study.getStudyInstanceUID())){
							initialPatient.addStudy(study);
						}
					}
				} else if (initialStudies.size() == updatedStudies.size()){
					for(Study study : updatedStudies){
						Study initialStudy = selectedDataSearchResult.getPatient(patient.getPatientID()).getStudy(study.getStudyInstanceUID());
						List<Series> initialSeries = initialStudy.getSeries();
						List<Series> updatedSeries = study.getSeries();
						if(initialSeries.size() != updatedSeries.size()){
							for(Series series : updatedSeries){
								if(!initialStudy.contains(series.getSeriesInstanceUID())){
									initialStudy.addSeries(series);
								}
							}
						}
					}
				}
			}
		}
	}

}
