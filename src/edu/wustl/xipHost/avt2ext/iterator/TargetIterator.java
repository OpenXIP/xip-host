/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import org.dcm4che2.data.Tag;

/**
 * @author Matthew Kelsey
 *
 */
public class TargetIterator implements Iterator<TargetElement> {
	
	SearchResult selectedDataSearchResult;
	
	IterationTarget target;
	Iterator<Patient> patientIt = null;
	Iterator<Study> studyIt = null;
	Iterator<Series> seriesIt = null;
	
		
	public TargetIterator(SearchResult selectedDataSearchResult, IterationTarget target) throws NullPointerException {
		if(selectedDataSearchResult == null)
			throw new NullPointerException("Cannot initialize TargetIterator with null SearchResult pointer");
		if(target == null)
			throw new NullPointerException("Cannot initialize TargetIterator with null IterationTarget");
		
		this.selectedDataSearchResult = selectedDataSearchResult;
		this.target = target;
		
		try {
			if(this.selectedDataSearchResult.getPatients() != null) {
				List<Patient> patients = this.selectedDataSearchResult.getPatients();
				patientIt = patients.iterator();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// ** If not up to date, query for study list in patient target ** //
	private boolean UpdatePatient(Patient patient) {
		if(patient.getLastUpdated() == null) {
			// Query for patient
			// success &= Query()
		}
		return true;
	}
	
	private boolean UpdateStudy(Study study) {
		if(study.getLastUpdated() == null) {
			// Query for Study
			// success &= Query()
		}
		return true;
	}

	private boolean UpdateSeries(Series series) {
		if(series.getLastUpdated() == null) {
			// Query for Series/Items
			// success &= Query()
		}
		return true;
	}
	
	public boolean hasNext() {
		if(target == IterationTarget.PATIENT) {
			return this.patientIt.hasNext();
		}
		else if(target == IterationTarget.STUDY) {
			// study list defined and contains additional entries
			if(this.studyIt != null && this.studyIt.hasNext()) {
				return true;
			}
			// end of study list for this patient, load list from next (or first) patient
			else if(patientIt.hasNext() == true) {
				Patient currentPatient = patientIt.next();
				UpdatePatient(currentPatient);
				studyIt = currentPatient.getStudies().iterator();
				return studyIt.hasNext();
			}
			// no additional entries in study list, no further patients in results
			else
				return false;
		}
		else if(target == IterationTarget.SERIES) {
			// series list defined and contains additional entries
			if(this.seriesIt != null && this.seriesIt.hasNext()) {
				return true;
			}
			// end of series list for this study, load list from next (or first) study
			else if(studyIt != null && studyIt.hasNext()) {
				Study currentStudy = studyIt.next();
				UpdateStudy(currentStudy);
				seriesIt = currentStudy.getSeries().iterator();
				return seriesIt.hasNext();
			}
			// end of series list for this patient, load list from next (or first) patient/study
			else if(patientIt.hasNext() == true) {
				Patient currentPatient = patientIt.next();
				UpdatePatient(currentPatient);
				studyIt = currentPatient.getStudies().iterator();
				Study currentStudy = studyIt.next();
				UpdateStudy(currentStudy);
				seriesIt = currentStudy.getSeries().iterator();
				return seriesIt.hasNext();
				// TODO Need logic to support skipping empty series lists in intermediate Patient, etc.
				// TODO hasNext() currently returns false 
			}
			// no additional entries in series list, no further patients/studies in results
			else
				return false;
			
		}
			
		return false;
	}
		
		
		

	public TargetElement next() {
		TargetElement targetElement = null;
		if(this.hasNext() == true){
			
			// ** PATIENT TARGET ** //
			if(this.target == IterationTarget.PATIENT) {
				// Update all elements below current patient
				Patient patient = patientIt.next();
				UpdatePatient(patient);
				for(Study study : patient.getStudies()) {
					UpdateStudy(study);
					for(Series series : study.getSeries()) {
						UpdateSeries(series);
					}
				}
				// Build criteria for patient
				Criteria patientCriteria = new Criteria(selectedDataSearchResult.getOriginalCriteria().getDICOMCriteria(),
						selectedDataSearchResult.getOriginalCriteria().getAIMCriteria());
				if(patient.getPatientName() != null && !patient.getPatientName().isEmpty()) {
					patientCriteria.getDICOMCriteria().put(Tag.PatientName, patient.getPatientName());
				}
				if(patient.getPatientID() != null && !patient.getPatientID().isEmpty()) {
					patientCriteria.getDICOMCriteria().put(Tag.PatientID, patient.getPatientID());
				}
	
				// Build Criteria for each Patient/Study/Series SubElement
				List<SubElement> subElements = new ArrayList<SubElement>();
				for(Study study : patient.getStudies()) {
					Criteria studyCriteria= new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());					
					studyCriteria.getDICOMCriteria().putAll(patientCriteria.getDICOMCriteria());
					studyCriteria.getAIMCriteria().putAll(patientCriteria.getAIMCriteria());
					studyCriteria.getDICOMCriteria().put(Tag.StudyInstanceUID, study.getStudyInstanceUID());
					for(Series series : study.getSeries()) {
						Criteria seriesCriteria = new Criteria(new HashMap<Integer, Object>(), new HashMap<String, Object>());
						seriesCriteria.getDICOMCriteria().putAll(studyCriteria.getDICOMCriteria());
						seriesCriteria.getAIMCriteria().putAll(studyCriteria.getAIMCriteria());
						seriesCriteria.getDICOMCriteria().put(Tag.SeriesInstanceUID, series.getSeriesInstanceUID());
						SubElement seriesSubElement = new SubElement(seriesCriteria, null);
						subElements.add(seriesSubElement);
					}
				}
				targetElement = new TargetElement(patient.getPatientID(), subElements, target);
			}
			
			// ** STUDY TARGET ** //
			else if(this.target == IterationTarget.STUDY) {
				// Update all elements below current study
				Study study = studyIt.next();
				UpdateStudy(study);
				for(Series series : study.getSeries()) {
					UpdateSeries(series);
				}
				// Build criteria for study
				Criteria studyCriteria = new Criteria(selectedDataSearchResult.getOriginalCriteria().getDICOMCriteria(),
						selectedDataSearchResult.getOriginalCriteria().getAIMCriteria());
				// TODO Finish building criteria for study
			}
			
			// ** SERIES TARGET ** //
			else if(this.target == IterationTarget.SERIES) {
				// Finish series next() method
			}
			else
				throw new NoSuchElementException();
		}
		else {
			throw new NoSuchElementException();
		}
			return targetElement;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
