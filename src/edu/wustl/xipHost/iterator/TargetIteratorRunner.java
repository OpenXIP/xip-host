/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;

/**
 * @author Matthew Kelsey & Jarek Krych
 *
 */
public class TargetIteratorRunner implements Runnable, DataAccessListener {
	final static Logger logger = Logger.getLogger(TargetIteratorRunner.class);
	SearchResult selectedDataSearchResult;
	IterationTarget target;
	Query query;
	Patient currentPatient = null;
	Iterator<Patient> patientIt = null;
	Study currentStudy;
	Iterator<Study> studyIt = null;
	Iterator<Series> seriesIt = null;
	
	List<TargetElement> targetElementList = new ArrayList<TargetElement>();
	Iterator<TargetElement> targetIterator = null; 
	
	String pathRoot;
		
	public TargetIteratorRunner(SearchResult selectedDataSearchResult, IterationTarget target, Query query, File pathTmpDir, TargetIteratorListener targetListener) throws NullPointerException {
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
		if(logger.isDebugEnabled()){
			List<Patient> patients = selectedDataSearchResult.getPatients();
			logger.debug("Value of selectedDataSearchresult as passed to TargetIteratorRunner constructor: ");
			for(Patient logPatient : patients){
				logger.debug(logPatient.toString());
				List<Study> studies = logPatient.getStudies();
				for(Study logStudy : studies){
					logger.debug("   " + logStudy.toString());
					List<Series> series = logStudy.getSeries();
					for(Series logSeries : series){
						logger.debug("      " + logSeries.toString());
					}
				}
			}
		}
		this.target = target;
		logger.debug("Iteration target: " + target.toString());
		this.query = query;
		logger.debug("Query class: " + query.getClass().getName());
		this.listener = targetListener;
		try {
			pathRoot = pathTmpDir.getCanonicalPath();
			logger.debug("TargetIterator temp directory is: " + pathRoot );
		} catch (IOException e) {
			logger.error(e, e);
		}
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
			notifyException(e.getMessage());
			logger.error(e, e);
		}
		// Fill targetElementsList with target elements from searchResult
		while(hasNextSearchResult())
		{
			TargetElement element = loadNextSearchResult();
			targetElementList.add(element);
			notifyTargetIteratorElementAvailable(element);
		}
		targetIterator = targetElementList.iterator();
		notifyIteratorComplete(targetIterator);		
	}
	
	TargetIteratorListener listener;
	private void notifyTargetIteratorElementAvailable(TargetElement element){
		IteratorElementEvent event = new IteratorElementEvent(element);
		listener.targetElementAvailable(event);
	}
	
	private void notifyIteratorComplete(Iterator<TargetElement> iter){
		IteratorEvent event = new IteratorEvent(iter);
		listener.fullIteratorAvailable(event);
	}
	
	// ** If not up to date, query for study list in patient target ** //
	private boolean updatePatient(Patient patient) {
		//FIXME: dicom and aim criteria should include original criteria plus PatientName and PatientID
		if(patient.getLastUpdated() == null) {
			// Query for patient
			Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			dicomCriteria.put(Tag.PatientName, patient.getPatientName());
			dicomCriteria.put(Tag.PatientID, patient.getPatientID());
			query.setQuery(dicomCriteria, aimCriteria, QueryTarget.STUDY, selectedDataSearchResult, patient);
			query.addDataAccessListener(this);
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
				notifyException(e.getMessage());
				logger.error(e, e);
				return false;
			}
		}
		return true;
	}
	
	private boolean updateStudy(Study study) {
		//FIXME: dicom and aim criteria should include original criteria plus PatientName and PatientID plus StudyInstanceUID
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
			query.setQuery(dicomCriteria, aimCriteria, QueryTarget.SERIES, selectedDataSearchResult, study);
			query.addDataAccessListener(this);
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
				notifyException(e.getMessage());
				logger.error(e, e);
				return false;
			}
		}
		return true;
	}

	private boolean updateSeries(Series series) {
		//FIXME: dicom and aim criteria should include original criteria plus PatientName and PatientID plus Study and Series InstanceUID
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
			query.setQuery(dicomCriteria, aimCriteria, QueryTarget.ITEM, selectedDataSearchResult, series);
			query.addDataAccessListener(this);
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
				notifyException(e.getMessage());
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
									File file = new File(seriesPath + File.separatorChar + item.getItemID());
									if(!file.exists()){
										file.createNewFile();
									}
								} catch (IOException e) {
									logger.error(e, e);
								}
							}	
						}
						SubElement seriesSubElement = new SubElement(seriesCriteria, seriesPath);
						subElements.add(seriesSubElement);
					}
				}
				// Create a pruned subtree of the selectedDataSearchResult, including just the Items covered
				// by this TargetElement.  Do include the upper level Items, leading up to this TargetElement, 
				// as they may be needed for evaluation of this TargetElement.
				SearchResult prunedSearchResult = new SearchResult(selectedDataSearchResult.getDataSourceDescription());
				prunedSearchResult.setOriginalCriteria(selectedDataSearchResult.getOriginalCriteria());
				prunedSearchResult.addPatient(patient);
				List<Item> itemsList = selectedDataSearchResult.getItems();
				for (Item item : itemsList) {
					prunedSearchResult.addItem(item);
				}
				targetElement = new TargetElement(patient.getPatientID(), subElements, target, prunedSearchResult);
			
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
								File file = new File(seriesPath + File.separatorChar + item.getItemID());
								if(!file.exists()){
									file.createNewFile();
								}
							} catch (IOException e) {
								logger.error(e, e);
							}
						}	
					}
					SubElement seriesSubElement = new SubElement(seriesCriteria, seriesPath);
					subElements.add(seriesSubElement);
				}
				// Create a pruned subtree of the selectedDataSearchResult, including just the Items covered
				// by this TargetElement.  Do include the upper level Items, leading up to this TargetElement, 
				// as they may be needed for evaluation of this TargetElement.
				Patient prunedCurrentPatient = new Patient(currentPatient.getPatientName(), currentPatient.getPatientID(), 
						currentPatient.getPatientBirthDate());
				prunedCurrentPatient.setLastUpdated(currentPatient.getLastUpdated());
				prunedCurrentPatient.addStudy(study);
				List<Item> patientItemsList = currentPatient.getItems();
				for (Item item : patientItemsList) {
					prunedCurrentPatient.addItem(item);
				}
				SearchResult prunedSearchResult = new SearchResult(selectedDataSearchResult.getDataSourceDescription());
				prunedSearchResult.setOriginalCriteria(selectedDataSearchResult.getOriginalCriteria());
				prunedSearchResult.addPatient(prunedCurrentPatient);
				List<Item> itemsList = selectedDataSearchResult.getItems();
				for (Item item : itemsList) {
					prunedSearchResult.addItem(item);
				}
				targetElement = new TargetElement(study.getStudyInstanceUID(), subElements, target, selectedDataSearchResult);
			
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
						File file = new File(seriesPath + File.separatorChar + item.getItemID());
						if(!file.exists()){
							file.createNewFile();
						}
					} catch (IOException e) {
						logger.error(e, e);
					}
				}	
				SubElement seriesSubElement = new SubElement(seriesCriteria, seriesPath);
				subElements.add(seriesSubElement);
				// Create a pruned subtree of the selectedDataSearchResult, including just the Items covered
				// by this TargetElement.  Do include the upper level Items, leading up to this TargetElement, 
				// as they may be needed for evaluation of this TargetElement.
				Study prunedCurrentStudy = new Study(currentStudy.getStudyDate(), 
						currentStudy.getStudyID(), 
						currentStudy.getStudyDesc(),
						currentStudy.getStudyInstanceUID());
				prunedCurrentStudy.setLastUpdated(currentStudy.getLastUpdated());
				prunedCurrentStudy.addSeries(series);
				List<Item> studyItemsList = currentStudy.getItems();
				for (Item item : studyItemsList) {
					prunedCurrentStudy.addItem(item);
				}
				Patient prunedCurrentPatient = new Patient(currentPatient.getPatientName(), 
						currentPatient.getPatientID(), 
						currentPatient.getPatientBirthDate());
				prunedCurrentPatient.setLastUpdated(currentPatient.getLastUpdated());
				prunedCurrentPatient.addStudy(prunedCurrentStudy);
				List<Item> patientItemsList = currentPatient.getItems();
				for (Item item : patientItemsList) {
					prunedCurrentPatient.addItem(item);
				}
				SearchResult prunedSearchResult = new SearchResult(selectedDataSearchResult.getDataSourceDescription());
				prunedSearchResult.setOriginalCriteria(selectedDataSearchResult.getOriginalCriteria());
				prunedSearchResult.addPatient(prunedCurrentPatient);
				List<Item> itemsList = selectedDataSearchResult.getItems();
				for (Item item : itemsList) {
					prunedSearchResult.addItem(item);
				}
				targetElement = new TargetElement(series.getSeriesInstanceUID(), subElements, target, prunedSearchResult);
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
		
	}

	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	synchronized public void queryResultsAvailable(QueryEvent e) {
		Query source = (Query)e.getSource();
		selectedDataSearchResult = source.getSearchResult();
	}
}
