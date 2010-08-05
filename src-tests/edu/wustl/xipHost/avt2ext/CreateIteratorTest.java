/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.wustl.xipHost.avt2ext.AVTUtil;
import edu.wustl.xipHost.avt2ext.iterator.Criteria;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.avt2ext.iterator.SubElement;
import edu.wustl.xipHost.avt2ext.iterator.TargetElement;
import edu.wustl.xipHost.dataModel.SearchResult;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class CreateIteratorTest extends TestCase {
	final static Logger logger = Logger.getLogger(CreateIteratorTest.class);
	SearchResult selectedDataSearchResult;
	SearchResult selectedDataSearchResultForSubqueries;
	
	AVTUtil util;
	public CreateIteratorTest(String name){
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		SearchResultSetup result = new SearchResultSetup();
		selectedDataSearchResult = result.getSearchResult();
		SearchResultSetupSubqueries resultForSubqueries = new SearchResultSetupSubqueries();
		selectedDataSearchResultForSubqueries = resultForSubqueries.getSearchResult();
		util = new AVTUtil();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.PATIENT
	public void testCreateIterator_1A(){
		Query avtQuery = new AVTQuery(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.PATIENT, avtQuery);	
		assertTrue("", assertIteratorTargetPatient(iter));
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.STUDY
	public void testCreateIterator_1B(){
		Query avtQuery = new AVTQuery(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.STUDY, avtQuery);
		assertTrue("", assertIteratorTargetStudy(iter));	
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.SERIES
	public void testCreateIterator_1C(){
		Query avtQuery = new AVTQuery(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.SERIES, avtQuery);
		assertTrue("", assertIteratorTargetSeries(iter));
	}
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: PATIENT
	public void testCreateIterator_2A(){
		AVTQueryStub avtQuery = new AVTQueryStub(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResultForSubqueries, IterationTarget.PATIENT, avtQuery);
		assertTrue("", assertIteratorTargetPatient(iter));
	}
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: STUDY
	public void testCreateIterator_2B(){
		AVTQueryStub avtQuery = new AVTQueryStub(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResultForSubqueries, IterationTarget.STUDY, avtQuery);
		assertTrue("", assertIteratorTargetStudy(iter));
	}
	
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: SERIES
	public void testCreateIterator_2C(){
		AVTQueryStub avtQuery = new AVTQueryStub(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResultForSubqueries, IterationTarget.SERIES, avtQuery);
		assertTrue("", assertIteratorTargetSeries(iter));
		
	}
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget is null.
	//Subqueries needed. Connection ON.
	//IterationTarget: STUDY
	public void testCreateIterator_3A(){
		 //Iterator<TargetElement> iter = util.createIterator(null, null, null);
		 assertTrue(false);
			
	}
	
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget is valid.
	//Subqueries needed. Connection lost.
	//IterationTarget: SERIES
	public void testCreateIterator_4A(){
		assertTrue(false);
		
	}
	
	//no patient ID present
	
	
	
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
				//Check Id
				//Check SubElements
				//Check IterationTarget
				blnId1 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsPatient1 = (subElements.size() == 2);
				if(blnNumOfSubElementsPatient1 == false){
					logger.warn("Incorrect number of subelements in Patient1. Expected 2, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1
				//patientID and patientName (patientName is overwritten)
				//Plus plus studyInstanceUID plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient1, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("111");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek1");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("101.101");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("101.101.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientID: expected '111', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek1', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '101.101', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '101.101.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient1, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement2 = subElements.get(1);
				Criteria criteria2 = subElement2.getCriteria();
				Map<Integer, Object> dicomCriteria2 = criteria2.getDICOMCriteria();
				//original criteria size = 1
				//Plus plus studyInstanceUID plus seriesInstanceUID
				//total size = 3
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 4);
				if(blnDicomCriteriaSize2 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient1, subelement 2. Expected 4, actual " + dicomCriteria2.size());
				}
				Object value5 = dicomCriteria2.get(new Integer(1048608));	//patientId
				Object value6 = dicomCriteria2.get(new Integer(1048592));	//patientName
				Object value7 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value8 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				String path2 = subElement2.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue5 = value5.toString().equalsIgnoreCase("111");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("Jarek1");
				boolean blnValue7 = value7.toString().equalsIgnoreCase("202.202");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("202.202.1");
				if(blnValue5 == false || blnValue6 == false || blnValue7 == false || blnValue8 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '111', actual " + "'" + value5 + "'");
					logger.warn("PatientName: expected 'Jarek1', actual " + "'" + value6 + "'");
					logger.warn("StudyInstanceUID: expected '202.202', actual " + "'" + value7 + "'");
					logger.warn("SeriesInstanceUID: expected '202.202.1', actual " + "'" + value8 + "'");
				}
				
				Map<String, Object> aimCriteria2 = criteria2.getAIMCriteria();
				boolean blnAimCriteriaSize2 = (aimCriteria2.size() == 0);
				if(blnAimCriteriaSize2 == false){
					logger.warn("Invalid size of AIM criteria for Patient 1, subelement 2. Expected size 0, actual " + aimCriteria2.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected PATIENT, actual " + target.toString());
				}
				
				//assert number of elements
				
				blnPatient1Atts = (blnId1 == true && blnNumOfSubElementsPatient1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true &&
						blnDicomCriteriaSize2 == true && blnValue5 == true && blnValue6 == true && blnValue7 == true && blnValue8 == true && blnAimCriteriaSize2 == true && 
						blnTarget == true);
				
			} else if(id.equalsIgnoreCase("222") ){
				//Check Id
				//Check SubElements
				//Check IterationTarget
				blnId2 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsPatient2 = (subElements.size() == 3);
				if(blnNumOfSubElementsPatient2 == false){
					logger.warn("Incorrect number of subelements in Patient2. Expected 3, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("222");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("303.303");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("303.303.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '303.303.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient 2, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement2 = subElements.get(1);
				Criteria criteria2 = subElement2.getCriteria();
				Map<Integer, Object> dicomCriteria2 = criteria2.getDICOMCriteria();
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 4);
				if(blnDicomCriteriaSize2 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 2. Expected 4, actual " + dicomCriteria2.size());
				}
				Object value5 = dicomCriteria2.get(new Integer(1048608));	//patientName
				Object value6 = dicomCriteria2.get(new Integer(1048592));	//patientName
				Object value7 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value8 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue5 = value5.toString().equalsIgnoreCase("222");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue7 = value7.toString().equalsIgnoreCase("303.303");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("404.404.1");
				if(blnValue5 == false || blnValue6 == false || blnValue7 == false || blnValue8 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value5 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value6 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value7 + "'");
					logger.warn("SeriesInstanceUID: expected '404.404.1', actual " + "'" + value8 + "'");
				}
				
				Map<String, Object> aimCriteria2 = criteria2.getAIMCriteria();
				boolean blnAimCriteriaSize2 = (aimCriteria2.size() == 0);
				if(blnAimCriteriaSize2 == false){
					logger.warn("Invalid size of AIM criteria for Patient 2, subelement 2. Expected size 0, actual " + aimCriteria2.size());
				}
				
				SubElement subElement3 = subElements.get(2);
				Criteria criteria3 = subElement3.getCriteria();
				Map<Integer, Object> dicomCriteria3 = criteria3.getDICOMCriteria();
				boolean blnDicomCriteriaSize3 = (dicomCriteria3.size() == 4);
				if(blnDicomCriteriaSize3 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 3. Expected 4, actual " + dicomCriteria3.size());
				}
				Object value9 = dicomCriteria3.get(new Integer(1048608));	//patientId
				Object value10 = dicomCriteria3.get(new Integer(1048592));	//patientName
				Object value11 = dicomCriteria3.get(new Integer(2097165));	//studyInstanceUID
				Object value12 = dicomCriteria3.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue9 = value9.toString().equalsIgnoreCase("222");
				boolean blnValue10 = value10.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue11 = value11.toString().equalsIgnoreCase("303.303");
				boolean blnValue12 = value12.toString().equalsIgnoreCase("505.505.1");
				if(blnValue9 == false || blnValue10 == false || blnValue11 == false || blnValue12 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value9 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value10 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value11 + "'");
					logger.warn("SeriesInstanceUID: expected '505.505.1', actual " + "'" + value12 + "'");
				}
				
				Map<String, Object> aimCriteria3 = criteria3.getAIMCriteria();
				boolean blnAimCriteriaSize3 = (aimCriteria3.size() == 0);
				if(blnAimCriteriaSize3 == false){
					logger.warn("Invalid size of AIM criteria for Patient 2, subelement 3. Expected size 0, actual " + aimCriteria3.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected PATIENT, actual " + target.toString());
				}
				
				blnPatient2Atts = (blnId2 == true && blnNumOfSubElementsPatient2 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true &&
						blnDicomCriteriaSize2 == true && blnValue5 == true && blnValue6 == true && blnValue7 == true && blnValue8 == true && blnAimCriteriaSize2 == true && 
						blnDicomCriteriaSize3 == true && blnValue9 == true && blnValue10 == true && blnValue11 == true && blnValue12 == true && blnAimCriteriaSize3 == true && 
						blnTarget == true);
			
			} else if (id.equalsIgnoreCase("333")){
				blnId3 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsPatient3 = (subElements.size() == 9);
				if(blnNumOfSubElementsPatient3 == false){
					logger.warn("Incorrect number of subelements in Patient2. Expected 9, actual " + subElements.size());
				}
				//check only 3 of the subelements: 1, 6, 9
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("333");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("404.404");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("606.606.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '404.404', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '606.606.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient 3, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement6 = subElements.get(5);
				Criteria criteria6 = subElement6.getCriteria();
				Map<Integer, Object> dicomCriteria6 = criteria6.getDICOMCriteria();
				boolean blnDicomCriteriaSize6 = (dicomCriteria6.size() == 4);
				if(blnDicomCriteriaSize6 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 6. Expected 4, actual " + dicomCriteria6.size());
				}
				Object value5 = dicomCriteria6.get(new Integer(1048608));	//patientName
				Object value6 = dicomCriteria6.get(new Integer(1048592));	//patientName
				Object value7 = dicomCriteria6.get(new Integer(2097165));	//studyInstanceUID
				Object value8 = dicomCriteria6.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue5 = value5.toString().equalsIgnoreCase("333");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue7 = value7.toString().equalsIgnoreCase("505.505");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("11.11.1");
				if(blnValue5 == false || blnValue6 == false || blnValue7 == false || blnValue8 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value5 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value6 + "'");
					logger.warn("StudyInstanceUID: expected '505.505', actual " + "'" + value7 + "'");
					logger.warn("SeriesInstanceUID: expected '11.11.1', actual " + "'" + value8 + "'");
				}
				
				Map<String, Object> aimCriteria6 = criteria6.getAIMCriteria();
				boolean blnAimCriteriaSize6 = (aimCriteria6.size() == 0);
				if(blnAimCriteriaSize6 == false){
					logger.warn("Invalid size of AIM criteria for Patient 3, subelement 6. Expected size 0, actual " + aimCriteria6.size());
				}

				SubElement subElement9 = subElements.get(8);
				Criteria criteria9 = subElement9.getCriteria();
				Map<Integer, Object> dicomCriteria9 = criteria9.getDICOMCriteria();
				boolean blnDicomCriteriaSize9 = (dicomCriteria9.size() == 4);
				if(blnDicomCriteriaSize9 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 9. Expected 4, actual " + dicomCriteria9.size());
				}
				Object value9 = dicomCriteria9.get(new Integer(1048608));	//patientId
				Object value10 = dicomCriteria9.get(new Integer(1048592));	//patientName
				Object value11 = dicomCriteria9.get(new Integer(2097165));	//studyInstanceUID
				Object value12 = dicomCriteria9.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue9 = value9.toString().equalsIgnoreCase("333");
				boolean blnValue10 = value10.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue11 = value11.toString().equalsIgnoreCase("606.606");
				boolean blnValue12 = value12.toString().equalsIgnoreCase("14.14.1");
				if(blnValue9 == false || blnValue10 == false || blnValue11 == false || blnValue12 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value9 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value10 + "'");
					logger.warn("StudyInstanceUID: expected '606.606', actual " + "'" + value11 + "'");
					logger.warn("SeriesInstanceUID: expected '14.14.1', actual " + "'" + value12 + "'");
				}
				
				Map<String, Object> aimCriteria9 = criteria9.getAIMCriteria();
				boolean blnAimCriteriaSize9 = (aimCriteria9.size() == 0);
				if(blnAimCriteriaSize9 == false){
					logger.warn("Invalid size of AIM criteria for Patient 3, subelement 9. Expected size 0, actual " + aimCriteria9.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected PATIENT, actual " + target.toString());
				}
				
				blnPatient3Atts = (blnId3 == true && blnNumOfSubElementsPatient3 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true &&
						blnDicomCriteriaSize6 == true && blnValue5 == true && blnValue6 == true && blnValue7 == true && blnValue8 == true && blnAimCriteriaSize6 == true && 
						blnDicomCriteriaSize9 == true && blnValue9 == true && blnValue10 == true && blnValue11 == true && blnValue12 == true && blnAimCriteriaSize9 == true && 
						blnTarget == true);
				
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
				//Check Id
				//Check SubElements
				//Check IterationTarget
				blnId1 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsStudy1 = (subElements.size() == 1);
				if(blnNumOfSubElementsStudy1 == false){
					logger.warn("Incorrect number of subelements in Study1. Expected 1, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Study1, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studySeriesUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("111");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek1");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("101.101");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("101.101.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '111', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek1', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '101.101', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '101.101.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Study1, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("STUDY");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected STUDY, actual " + target.toString());
				}
				
				//assert number of elements
				blnStudy1Atts = (blnId1 == true && blnNumOfSubElementsStudy1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true && blnTarget == true);
				if(blnStudy1Atts == false){
					logger.warn("Invalid attributes in Study1.");
				}
			} else if(id.equalsIgnoreCase("303.303") ){
				//Check Id
				//Check SubElements
				//Check IterationTarget
				blnId3 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsStudy3 = (subElements.size() == 3);
				if(blnNumOfSubElementsStudy3 == false){
					logger.warn("Incorrect number of subelements in Study3. Expected 3, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Study3, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("222");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("303.303");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("303.303.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '303.303.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Study3, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement2 = subElements.get(1);	
				Criteria criteria2 = subElement2.getCriteria();
				Map<Integer, Object> dicomCriteria2 = criteria2.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 4);
				if(blnDicomCriteriaSize2 == false){
					logger.warn("Incorrect number of DICOM criteria for Study3, subelement 2. Expected 4, actual " + dicomCriteria2.size());
				}
				Object value5 = dicomCriteria2.get(new Integer(1048608));	//patientId
				Object value6 = dicomCriteria2.get(new Integer(1048592));	//patientName
				Object value7 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value8 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				
				String path2 = subElement2.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue5 = value5.toString().equalsIgnoreCase("222");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue7 = value7.toString().equalsIgnoreCase("303.303");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("404.404.1");
				if(blnValue5 == false || blnValue5 == false || blnValue7 == false || blnValue8 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value5 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value6 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value7 + "'");
					logger.warn("SeriesInstanceUID: expected '404.404.1', actual " + "'" + value8 + "'");
				}
				//last
				Map<String, Object> aimCriteria2 = criteria2.getAIMCriteria();
				boolean blnAimCriteriaSize2 = (aimCriteria2.size() == 0);
				if(blnAimCriteriaSize2 == false){
					logger.warn("Invalid size of AIM criteria for Study3, subelement 2. Expected size 0, actual " + aimCriteria2.size());
				}
				
				SubElement subElement3 = subElements.get(2);	
				Criteria criteria3 = subElement3.getCriteria();
				Map<Integer, Object> dicomCriteria3 = criteria3.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize3 = (dicomCriteria3.size() == 4);
				if(blnDicomCriteriaSize3 == false){
					logger.warn("Incorrect number of DICOM criteria for Study3, subelement 3. Expected 4, actual " + dicomCriteria3.size());
				}
				Object value9 = dicomCriteria3.get(new Integer(1048608));	//patientId
				Object value10 = dicomCriteria3.get(new Integer(1048592));	//patientName
				Object value11 = dicomCriteria3.get(new Integer(2097165));	//studyInstanceUID
				Object value12 = dicomCriteria3.get(new Integer(2097166));	//seriesInstanceUID
				
				String path3 = subElement3.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue9 = value9.toString().equalsIgnoreCase("222");
				boolean blnValue10 = value10.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue11 = value11.toString().equalsIgnoreCase("303.303");
				boolean blnValue12 = value12.toString().equalsIgnoreCase("505.505.1");
				if(blnValue9 == false || blnValue10 == false || blnValue11 == false || blnValue12 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value9 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value10 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value11 + "'");
					logger.warn("SeriesInstanceUID: expected '505.505.1', actual " + "'" + value12 + "'");
				}
				
				Map<String, Object> aimCriteria3 = criteria3.getAIMCriteria();
				boolean blnAimCriteriaSize3 = (aimCriteria3.size() == 0);
				if(blnAimCriteriaSize3 == false){
					logger.warn("Invalid size of AIM criteria for Study3, subelement 3. Expected size 0, actual " + aimCriteria3.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("STUDY");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected STUDY, actual " + target.toString());
				}
				
				//assert number of elements
				blnStudy3Atts = (blnId3 == true && blnNumOfSubElementsStudy3 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true && 
						blnDicomCriteriaSize2 == true && blnValue5 == true && blnValue6 == true && blnValue7 == true && blnValue8 == true && blnAimCriteriaSize2 == true &&
						blnDicomCriteriaSize3 == true && blnValue9 == true && blnValue10 == true && blnValue11 == true && blnValue12 == true && blnAimCriteriaSize3 == true &&
						blnTarget == true);
				if(blnStudy3Atts == false){
					logger.warn("Invalid attributes in Study3.");
				}
			} else if(id.equalsIgnoreCase("606.606") ){
				//Check Id
				//Check SubElements
				//Check IterationTarget
				blnId6 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsStudy6 = (subElements.size() == 3);
				if(blnNumOfSubElementsStudy6 == false){
					logger.warn("Incorrect number of subelements in Study6. Expected 3, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Study6, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("333");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("606.606");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("12.12.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '606.606', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '12.12.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Study6, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement2 = subElements.get(1);	
				Criteria criteria2 = subElement2.getCriteria();
				Map<Integer, Object> dicomCriteria2 = criteria2.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 4);
				if(blnDicomCriteriaSize2 == false){
					logger.warn("Incorrect number of DICOM criteria for Study6, subelement 2. Expected 4, actual " + dicomCriteria2.size());
				}
				Object value5 = dicomCriteria2.get(new Integer(1048608));	//patientId
				Object value6 = dicomCriteria2.get(new Integer(1048592));	//patientName
				Object value7 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value8 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				
				String path2 = subElement2.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue5 = value5.toString().equalsIgnoreCase("333");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue7 = value7.toString().equalsIgnoreCase("606.606");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("13.13.1");
				if(blnValue5 == false || blnValue6 == false || blnValue7 == false || blnValue8 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value5 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value6 + "'");
					logger.warn("StudyInstanceUID: expected '606.606', actual " + "'" + value7 + "'");
					logger.warn("SeriesInstanceUID: expected '13.13.1', actual " + "'" + value8 + "'");
				}
				
				Map<String, Object> aimCriteria2 = criteria2.getAIMCriteria();
				boolean blnAimCriteriaSize2 = (aimCriteria2.size() == 0);
				if(blnAimCriteriaSize2 == false){
					logger.warn("Invalid size of AIM criteria for Study6, subelement 2. Expected size 0, actual " + aimCriteria2.size());
				}
				
				SubElement subElement3 = subElements.get(2);	
				Criteria criteria3 = subElement3.getCriteria();
				Map<Integer, Object> dicomCriteria3 = criteria3.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize3 = (dicomCriteria3.size() == 4);
				if(blnDicomCriteriaSize3 == false){
					logger.warn("Incorrect number of DICOM criteria for Study6, subelement 3. Expected 4, actual " + dicomCriteria3.size());
				}
				Object value9 = dicomCriteria3.get(new Integer(1048608));	//patientId
				Object value10 = dicomCriteria3.get(new Integer(1048592));	//patientName
				Object value11 = dicomCriteria3.get(new Integer(2097165));	//studyInstanceUID
				Object value12 = dicomCriteria3.get(new Integer(2097166));	//seriesInstanceUID
				
				String path3 = subElement3.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue9 = value9.toString().equalsIgnoreCase("333");
				boolean blnValue10 = value10.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue11 = value11.toString().equalsIgnoreCase("606.606");
				boolean blnValue12 = value12.toString().equalsIgnoreCase("14.14.1");
				if(blnValue9 == false || blnValue10 == false || blnValue11 == false || blnValue12 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value9 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value10 + "'");
					logger.warn("StudyInstanceUID: expected '606.606', actual " + "'" + value11 + "'");
					logger.warn("SeriesInstanceUID: expected '14.14.1', actual " + "'" + value12 + "'");
				}
				
				Map<String, Object> aimCriteria3 = criteria3.getAIMCriteria();
				boolean blnAimCriteriaSize3 = (aimCriteria3.size() == 0);
				if(blnAimCriteriaSize3 == false){
					logger.warn("Invalid size of AIM criteria for Study6, subelement 3. Expected size 0, actual " + aimCriteria3.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("STUDY");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected STUDY, actual " + target.toString());
				}
				
				//assert number of elements
				blnStudy6Atts = (blnId6 == true && blnNumOfSubElementsStudy6 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true && 
						blnDicomCriteriaSize2 == true && blnValue5 == true && blnValue5 == true && blnValue7 == true && blnValue8 == true && blnAimCriteriaSize2 == true &&
						blnDicomCriteriaSize3 == true && blnValue9 == true && blnValue10 == true && blnValue11 == true && blnValue12 == true && blnAimCriteriaSize3 == true &&
						blnTarget == true);
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
				//Check Id
				//Check SubElements
				//Check IterationTarget
				blnId1 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsSeries1 = (subElements.size() == 1);
				if(blnNumOfSubElementsSeries1 == false){
					logger.warn("Incorrect number of subelements in Series1. Expected 1, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Series1, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("111");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek1");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("101.101");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("101.101.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '111', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek1', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '101.101', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '101.101.1', actual " + "'" + value4 + "'");
				}

				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Series1, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("SERIES");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected SERIES, actual " + target.toString());
				}
				
				//assert number of elements
				blnSeries1Atts = (blnId1 == true && blnNumOfSubElementsSeries1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true && blnTarget == true);
				if(blnSeries1Atts == false){
					logger.warn("Invalid attributes in Series1.");
				} 
			} if(id.equalsIgnoreCase("404.404.1")){
				blnId4 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsSeries4 = (subElements.size() == 1);
				if(blnNumOfSubElementsSeries4 == false){
					logger.warn("Incorrect number of subelements in Series4. Expected 1, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Series4, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("222");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek2");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("303.303");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("404.404.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false ){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '222', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek2', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '404.404.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Series4, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("SERIES");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected SERIES, actual " + target.toString());
				}
				
				//assert number of elements
				blnSeries4Atts = (blnId4 == true && blnNumOfSubElementsSeries4 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true && blnTarget == true);
				if(blnSeries1Atts == false){
					logger.warn("Invalid attributes in Series4.");
				} 
			} if(id.equalsIgnoreCase("13.13.1")){
				blnId13 = true;
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElementsSeries13 = (subElements.size() == 1);
				if(blnNumOfSubElementsSeries13 == false){
					logger.warn("Incorrect number of subelements in Series13. Expected 1, actual " + subElements.size());
				}
				SubElement subElement1 = subElements.get(0);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1 overwritten by patientName
				//plus patientId
				//plus studyInstanceUID
				//plus seriesInstanceUID
				//total size = 4
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 4);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Series13, subelement 1. Expected 4, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(1048608));	//patientId
				Object value2 = dicomCriteria1.get(new Integer(1048592));	//patientName
				Object value3 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value4 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("333");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("Jarek3");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("606.606");
				boolean blnValue4 = value4.toString().equalsIgnoreCase("13.13.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false || blnValue4 == false ){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientId: expected '333', actual " + "'" + value1 + "'");
					logger.warn("PatientName: expected 'Jarek3', actual " + "'" + value2 + "'");
					logger.warn("StudyInstanceUID: expected '606.606', actual " + "'" + value3 + "'");
					logger.warn("SeriesInstanceUID: expected '13.13.1', actual " + "'" + value4 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Series13, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("SERIES");
				if(blnTarget == false){
					logger.warn("Invalid IterationTarget. Expected SERIES, actual " + target.toString());
				}
				
				//assert number of elements
				blnSeries13Atts = (blnId13 == true && blnNumOfSubElementsSeries13 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnValue4 == true && blnAimCriteriaSize1 == true && blnTarget == true);
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
