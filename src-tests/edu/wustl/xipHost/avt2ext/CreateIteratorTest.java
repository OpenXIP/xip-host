/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.iterator.IteratorElementEvent;
import edu.wustl.xipHost.iterator.IteratorEvent;
import edu.wustl.xipHost.iterator.TargetElement;
import edu.wustl.xipHost.iterator.TargetIteratorRunner;
import edu.wustl.xipHost.iterator.TargetIteratorListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

/**
 * @author Jaroslaw Krych
 *
 */
public class CreateIteratorTest implements TargetIteratorListener {
	final static Logger logger = Logger.getLogger(CreateIteratorTest.class);
	static Query avtQuery;
	static SearchResult selectedDataSearchResult;
	static SearchResult selectedDataSearchResultForSubqueries;
	TargetElement targetElement;
	File tmpDir;
	
	@BeforeClass
	public static void setUp() throws Exception {
		avtQuery = new AVTQueryStub(null, null, null, null, null);
		SearchResultSetup result = new SearchResultSetup();
		selectedDataSearchResult = result.getSearchResult();
		SearchResultSetupSubqueries resultForSubqueries = new SearchResultSetupSubqueries();
		selectedDataSearchResultForSubqueries = resultForSubqueries.getSearchResult();
		DOMConfigurator.configure("log4j.xml");
	}

	@AfterClass
	public void tearDown() throws Exception {
		
	}
	
	//TargetIteratorRunner - basic flow.
	//Parameters: valid
	//IterationTarget.PATIEN
	//@Ignore
	@Test
	public void testCreateIterator_1A(){
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.PATIENT, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 3){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		assertTrue("All parameters were valid but TargetIteratorRunner " +
				"did not produce expected result for IterationTarget.PATIENT.", assertIteratorTargetPatient(iter));
	}

	//TargetIteratorRunner - basic flow.
	//Parameters: valid
	//IterationTarget.STUDY
	@Test
	public void testCreateIterator_1B(){
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.STUDY, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 6){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		assertTrue("All parameters were valid but TargetIteratorRunner " +
				"did not produce expected result for IterationTarget.STUDY.", assertIteratorTargetStudy(iter));
	}
	
	//TargetIteratorRunner - basic flow.
	//Parameters: valid
	//IterationTarget.SERIES
	@Test
	public void testCreateIterator_1C(){
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.SERIES, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 6){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		assertTrue("All parameters were valid but TargetIteratorRunner " +
				"did not produce expected result for IterationTarget.SERIES.", assertIteratorTargetSeries(iter));
	}
	
	
	//TargetIteratorRunner - alternative flow.
	//Parameters: valid
	//Subqueries needed. Connection ON.
	//IterationTarget: PATIENT
	@Test
	public void testCreateIterator_2A(){
		Query avtQuery = new AVTQueryStub(null, null, null, null, null);
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResultForSubqueries, IterationTarget.PATIENT, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 3){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		assertTrue("All parameters were valid but TargetIteratorRunner " +
				"did not produce expected result for IterationTarget.PATIENT.", assertIteratorTargetPatient(iter));
	}
	
	//TargetIteratorRunner - alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: STUDY
	@Test
	public void testCreateIterator_2B(){
		Query avtQuery = new AVTQueryStub(null, null, null, null, null);
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResultForSubqueries, IterationTarget.STUDY, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 6){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		assertTrue("All parameters were valid but TargetIteratorRunner " +
				"did not produce expected result for IterationTarget.STUDY.", assertIteratorTargetStudy(iter));
	}
	
	
	//TargetIteratorRunner - alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: SERIES
	@Test
	public void testCreateIterator_2C(){
		Query avtQuery = new AVTQueryStub(null, null, null, null, null);
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResultForSubqueries, IterationTarget.SERIES, avtQuery, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			logger.error(e, e);
		}
		synchronized(targetElements){
			while(targetElements.size() < 6){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		assertTrue("All parameters were valid but TargetIteratorRunner " +
				"did not produce expected result for IterationTarget.SERIES.", assertIteratorTargetSeries(iter));
	}

	Iterator<TargetElement> iter;
	@SuppressWarnings("unchecked")
	@Override
	public void fullIteratorAvailable(IteratorEvent e) {		
		iter = (Iterator<TargetElement>) e.getSource();
	}

	List<TargetElement> targetElements = new ArrayList<TargetElement>();
	@Override
	public void targetElementAvailable(IteratorElementEvent e) {
		synchronized(targetElements){
			TargetElement element = (TargetElement) e.getSource();
			logger.debug("TargetElement available. ID: " + element.getId() + " at time " + System.currentTimeMillis());
			targetElements.add(element);
			targetElements.notify();
		}
	}
	
	
	private boolean assertIteratorTargetPatient(Iterator<TargetElement> iter){
		int numberOfElements = 0;
		boolean blnPatient1Atts = false;
		boolean blnPatient2Atts = false;
		boolean blnPatient3Atts = false;
		while(iter.hasNext()){
			TargetElement element = iter.next();
			numberOfElements ++;
			String id = element.getId();
			boolean blnId1 = false;
			boolean blnId2 = false;
			boolean blnId3 = false;
			if(id.equalsIgnoreCase("111")){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId1 = true;
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient1, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}				
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient1, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected PATIENT, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					boolean study1Assert = false;
					boolean study2Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 2);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("101.101")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("101.101.1")){
										study1Assert = true;
									}
								}
							} else if (study.getStudyInstanceUID().equalsIgnoreCase("202.202")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("202.202.1")){
										study2Assert = true;
									}
								}
							}
						}							 
					}
					if(study1Assert == true && study2Assert == true){
						studiesAssert = true;
					}
				}				
				blnPatient1Atts = (blnId1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && studiesAssert == true);
				
			} else if(id.equalsIgnoreCase("222") ){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId2 = true;	
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}				
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");					
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient 2, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected PATIENT, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					boolean series3Assert = false;
					boolean series4Assert = false;
					boolean series5Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("303.303")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("303.303.1")){
										series3Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("404.404.1")){
										series4Assert = true;
									} else if (seriesInstanceUID.equalsIgnoreCase("505.505.1")){
										series5Assert = true;
									}
								}
							} 
						}							 
					}
					if(numbStudies == true && series3Assert == true && series4Assert == true && series5Assert == true){
						studiesAssert = true;
					}
				}
				
				blnPatient2Atts = (blnId2 == true && blnDicomCriteriaSize1 == true && 
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && studiesAssert == true);
			
			} else if (id.equalsIgnoreCase("333")){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId3 = true;
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient 3, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
								
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected PATIENT, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					boolean series6Assert = false;
					boolean series7Assert = false;
					boolean series8Assert = false;
					boolean series9Assert = false;
					boolean series10Assert = false;
					boolean series11Assert = false;
					boolean series12Assert = false;
					boolean series13Assert = false;
					boolean series14Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 3);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("404.404")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("606.606.1")){
										series6Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("707.707.1")){
										series7Assert = true;
									} else if (seriesInstanceUID.equalsIgnoreCase("808.808.1")){
										series8Assert = true;
									}
								}
							} else if (study.getStudyInstanceUID().equalsIgnoreCase("505.505")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("909.909.1")){
										series9Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("10.10.1")){
										series10Assert = true;
									} else if (seriesInstanceUID.equalsIgnoreCase("11.11.1")){
										series11Assert = true;
									}
								}
							} else if (study.getStudyInstanceUID().equalsIgnoreCase("606.606")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("12.12.1")){
										series12Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("13.13.1")){
										series13Assert = true;
									} else if (seriesInstanceUID.equalsIgnoreCase("14.14.1")){
										series14Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series6Assert == true && series7Assert == true && series8Assert == true &&
							series9Assert == true && series10Assert == true && series11Assert == true &&
							series12Assert == true && series13Assert == true && series14Assert == true){
							studiesAssert = true;
					}
				}
				
				blnPatient3Atts = (blnId3 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && studiesAssert == true);				
			}
		}
		//Assert iterator
		//Number of iterator's elements = 3
		boolean blnNumberOfElements = (numberOfElements == 3);
		assertTrue("Expected number of elements is 3, but actual number is " + numberOfElements, blnNumberOfElements);
		assertTrue ("", blnPatient1Atts == true && blnPatient2Atts == true && blnPatient3Atts == true); 
		if (blnNumberOfElements && (blnPatient1Atts == true && blnPatient2Atts == true && blnPatient3Atts == true)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean assertIteratorTargetStudy(Iterator<TargetElement> iter){
		boolean blnStudy1Atts = false;
		boolean blnStudy3Atts = false;
		boolean blnStudy6Atts = false;
		int numberOfElements = 0;
		while(iter.hasNext()){
			TargetElement element = iter.next();
			numberOfElements ++;
			String id = element.getId();
			boolean blnId1 = false;
			boolean blnId3 = false;
			boolean blnId6 = false;
			if(id.equalsIgnoreCase("101.101")){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId1 = true;				
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();				
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Study1, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Study1, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("STUDY");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected STUDY, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean patientAssert = false;
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					if(patient.getPatientID().equalsIgnoreCase("111")){
						patientAssert = true;
					}
					boolean series1Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("101.101")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("101.101.1")){
										series1Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series1Assert == true){
							studiesAssert = true;
					}
				}
				
				blnStudy1Atts = (blnId1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && patientAssert == true && studiesAssert == true);
				
				if(blnStudy1Atts == false){
					logger.warn("Invalid attributes in Study1.");
				}
			} else if(id.equalsIgnoreCase("303.303") ){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId3 = true;				
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();				
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Study3, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Study3, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("STUDY");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected STUDY, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean patientAssert = false;
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					if(patient.getPatientID().equalsIgnoreCase("222")){
						patientAssert = true;
					}
					boolean series3Assert = false;
					boolean series4Assert = false;
					boolean series5Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("303.303")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("303.303.1")){
										series3Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("404.404.1")){
										series4Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("505.505.1")){
										series5Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series3Assert == true && series4Assert == true && series5Assert == true){
							studiesAssert = true;
					}
				}
				
				blnStudy3Atts = (blnId3 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && patientAssert == true && studiesAssert == true);
				
				if(blnStudy3Atts == false){
					logger.warn("Invalid attributes in Study3.");
				}
			} else if(id.equalsIgnoreCase("606.606") ){				
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId6 = true;
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Study6, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue2 = value2.toString().equalsIgnoreCase("*");
				if(blnValue2 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value2 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Study6, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("STUDY");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected STUDY, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean patientAssert = false;
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					if(patient.getPatientID().equalsIgnoreCase("333")){
						patientAssert = true;
					}
					boolean series12Assert = false;
					boolean series13Assert = false;
					boolean series14Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("606.606")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("12.12.1")){
										series12Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("13.13.1")){
										series13Assert = true;
									} else if(seriesInstanceUID.equalsIgnoreCase("14.14.1")){
										series14Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series12Assert == true && series13Assert == true && series14Assert == true){
							studiesAssert = true;
					}
				}
				
				blnStudy6Atts = (blnId6 == true && blnDicomCriteriaSize1 == true &&
						blnValue2 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && patientAssert == true && studiesAssert == true);
				
				
				if(blnStudy6Atts == false){
					logger.warn("Invalid attributes in Study6.");
				}
			}
		}
		//Assert iterator
		//Number of iterator's elements = 6
		boolean blnNumberOfElements = (numberOfElements == 6);
		assertTrue("Expected number of elements is 6, but actual number is " + numberOfElements, blnNumberOfElements);
		assertTrue ("", blnStudy1Atts == true && blnStudy3Atts == true && blnStudy6Atts == true);
		if (blnNumberOfElements && (blnStudy1Atts == true && blnStudy3Atts == true && blnStudy6Atts == true)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean assertIteratorTargetSeries(Iterator<TargetElement> iter){
		boolean blnSeries1Atts = false;
		boolean blnSeries4Atts = false;
		boolean blnSeries13Atts = false;
		int numberOfElements = 0;
		while(iter.hasNext()){
			TargetElement element = iter.next();
			numberOfElements ++;
			String id = element.getId();
			boolean blnId1 = false;
			boolean blnId4 = false;
			boolean blnId13 = false;
			if(id.equalsIgnoreCase("101.101.1")){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId1 = true;
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Series1, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}

				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Series1, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("SERIES");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected SERIES, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean patientAssert = false;
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					if(patient.getPatientID().equalsIgnoreCase("111")){
						patientAssert = true;
					}
					boolean series1Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("101.101")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("101.101.1")){
										series1Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series1Assert == true){
							studiesAssert = true;
					}
				}
				
				blnSeries1Atts = (blnId1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && patientAssert == true && studiesAssert == true);
				
				if(blnSeries1Atts == false){
					logger.warn("Invalid attributes in Series1.");
				} 
			} if(id.equalsIgnoreCase("404.404.1")){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId4 = true;
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Series4, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Series4, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("SERIES");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected SERIES, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean patientAssert = false;
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					if(patient.getPatientID().equalsIgnoreCase("222")){
						patientAssert = true;
					}
					boolean series4Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("303.303")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("404.404.1")){
										series4Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series4Assert == true){
							studiesAssert = true;
					}
				}
				
				blnSeries4Atts = (blnId4 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && patientAssert == true && studiesAssert == true);
				
				if(blnSeries1Atts == false){
					logger.warn("Invalid attributes in Series4.");
				} 
			} if(id.equalsIgnoreCase("13.13.1")){
				//Assert original criteria
				//Assert IterationTarget
				//Assert subSearchResult
				blnId13 = true;
				Map<Integer, Object> dicomCriteria1 = element.getSubSearchResult().getOriginalCriteria().getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 1);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Series13, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048592));	//patientName
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				if(blnValue1 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
				}
				
				Map<String, Object> aimCriteria1 = element.getSubSearchResult().getOriginalCriteria().getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Series13, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("SERIES");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected SERIES, actual " + target.toString());
				}
				
				//assert subSearchResult
				SearchResult subSearchResult = element.getSubSearchResult();
				List<Patient> patients = subSearchResult.getPatients();
				boolean numbPatients = (patients.size() == 1);
				boolean patientAssert = false;
				boolean numbStudies = false;
				boolean studiesAssert = false;
				for(Patient patient : patients){
					if(patient.getPatientID().equalsIgnoreCase("333")){
						patientAssert = true;
					}
					boolean series13Assert = false;
					List<Study> studies = patient.getStudies();
					numbStudies = (studies.size() == 1);
					if(numbStudies){
						for(Study study : studies){
							List<Series> series = study.getSeries();
							if(study.getStudyInstanceUID().equalsIgnoreCase("606.606")){
								for(Series oneSeries : series){
									String seriesInstanceUID = oneSeries.getSeriesInstanceUID();
									if(seriesInstanceUID.equalsIgnoreCase("13.13.1")){
										series13Assert = true;
									}
								}
							}
						}							 
					}
					if(numbStudies == true && series13Assert == true){
							studiesAssert = true;
					}
				}
				
				blnSeries13Atts = (blnId13 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnAimCriteriaSize1 == true && blnTarget == true &&
						numbPatients == true && patientAssert == true && studiesAssert == true);
				
				if(blnSeries13Atts == false){
					logger.warn("Invalid attributes in Series13.");
				} 
			}
		}
		//Assert iterator
		//Number of iterator's elements = 6
		boolean blnNumberOfElements = (numberOfElements == 14);
		assertTrue("Expected number of elements is 6, but actual number is " + numberOfElements, blnNumberOfElements);
		assertTrue ("", blnSeries1Atts == true && blnSeries4Atts == true && blnSeries13Atts == true);
		if (blnNumberOfElements && (blnSeries1Atts == true && blnSeries4Atts == true && blnSeries13Atts == true)) {
			return true;
		} else {
			return false;
		}
	}
	
}
