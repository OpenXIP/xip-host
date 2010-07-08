/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import java.util.List;
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
	
	Integer currentElementIndex = -1;
	List<TargetElement> targetElements = new ArrayList<TargetElement>();
	SearchResult selectedDataSearchResult;
	IterationTarget target;
	
	
	public TargetIterator(SearchResult selectedDataSearchResult, IterationTarget target) {
		this.selectedDataSearchResult = selectedDataSearchResult;
		this.target = target;
		
		try {
			// TODO Check for invalid selectedDataSearchResult
			
			
			// Fill elements vector with target based TargetElements
			if(target == IterationTarget.PATIENT) {
				for(Patient patient : selectedDataSearchResult.getPatients()) {
					if(patient.getLastUpdated() != null) {
						Criteria patientCriteria = new Criteria(selectedDataSearchResult.getOriginalCriteria().getDICOMCriteria(),
																selectedDataSearchResult.getOriginalCriteria().getAIMCriteria());
						patientCriteria.getDICOMCriteria().put(Tag.PatientID, patient.getPatientID());
						
						// Build Criteria for each Patient/Study/Series SubElement
						List<SubElement> subElements = new ArrayList<SubElement>();
						for(Study study : patient.getStudies()) {
							Criteria studyCriteria = new Criteria(patientCriteria.getDICOMCriteria(), patientCriteria.getAIMCriteria());
							studyCriteria.getDICOMCriteria().put(Tag.StudyInstanceUID, study.getStudyInstanceUID());
							for(Series series : study.getSeries()) {
								Criteria seriesCriteria = new Criteria(studyCriteria.getDICOMCriteria(), studyCriteria.getAIMCriteria());
								seriesCriteria.getDICOMCriteria().put(Tag.SeriesInstanceUID, series.getSeriesInstanceUID());
								SubElement studySubElement = new SubElement(seriesCriteria, null);
								subElements.add(studySubElement);
							}
						}
						TargetElement element = new TargetElement(patient.getPatientID(), subElements, target);
						targetElements.add(element);
					}
					else {
						// TODO Update patient target
					}
				}
	
			} else if (target == IterationTarget.STUDY) {
				// TODO
			} else if (target == IterationTarget.SERIES) {
				// TODO
			}
		} catch(Exception e) {
			currentElementIndex = -1;
			targetElements.clear();
			e.printStackTrace();
		}
	}
	
	public boolean hasNext() {
		if((currentElementIndex + 1) < targetElements.size() && targetElements.get(currentElementIndex + 1) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	public TargetElement next() {
		if(this.hasNext()) {
			currentElementIndex += 1;
			return targetElements.get(currentElementIndex);
		}
		else {
			throw new NoSuchElementException();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	
}
