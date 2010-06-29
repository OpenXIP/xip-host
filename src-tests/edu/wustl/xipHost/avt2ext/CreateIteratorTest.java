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
	AVTUtil util;
	public CreateIteratorTest(String name){
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		SearchResultSetup result = new SearchResultSetup();
		selectedDataSearchResult = result.getSearchResult();
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
		//AVTQuery query = new AVTQueryStub(null, null, null, null, null);
		//query.run();
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.PATIENT);
		//assert iter
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
				//Should we check for patientID or patient business key
				//Plus plus studyInstanceUID plus seriesInstanceUID
				//total size = 3
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 3);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient1, subelement 1. Expected 3, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(10001));	//patientName
				Object value2 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value3 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("101.101");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("101.101.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
					logger.warn("StudyInstanceUID: expected '101.101', actual " + "'" + value2 + "'");
					logger.warn("SeriesInstanceUID: expected '101.101.1', actual " + "'" + value3 + "'");
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
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 3);
				if(blnDicomCriteriaSize2 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient1, subelement 2. Expected 3, actual " + dicomCriteria2.size());
				}
				Object value4 = dicomCriteria2.get(new Integer(10001));	//patientName
				Object value5 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value6 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				String path2 = subElement2.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue4 = value4.toString().equalsIgnoreCase("*");
				boolean blnValue5 = value5.toString().equalsIgnoreCase("202.202");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("202.202.1");
				if(blnValue4 == false || blnValue5 == false || blnValue6 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value4 + "'");
					logger.warn("StudyInstanceUID: expected '101.101', actual " + "'" + value5 + "'");
					logger.warn("SeriesInstanceUID: expected '101.101.1', actual " + "'" + value6 + "'");
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
				
				blnPatient1Atts = (blnId1 == true && blnNumOfSubElementsPatient1 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnAimCriteriaSize1 == true &&
						blnDicomCriteriaSize2 == true && blnValue4 == true && blnValue5 == true && blnValue6 == true && blnAimCriteriaSize2 == true && 
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
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 3);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 1. Expected 3, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(10001));	//patientName
				Object value2 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value3 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("303.303");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("303.303.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value2 + "'");
					logger.warn("SeriesInstanceUID: expected '303.303.1', actual " + "'" + value3 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient 2, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement2 = subElements.get(1);
				Criteria criteria2 = subElement2.getCriteria();
				Map<Integer, Object> dicomCriteria2 = criteria2.getDICOMCriteria();
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 3);
				if(blnDicomCriteriaSize2 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 2. Expected 3, actual " + dicomCriteria2.size());
				}
				Object value4 = dicomCriteria2.get(new Integer(10001));	//patientName
				Object value5 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value6 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue4 = value4.toString().equalsIgnoreCase("*");
				boolean blnValue5 = value5.toString().equalsIgnoreCase("303.303");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("404.404.1");
				if(blnValue4 == false || blnValue5 == false || blnValue6 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value4 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value5 + "'");
					logger.warn("SeriesInstanceUID: expected '404.404.1', actual " + "'" + value6 + "'");
				}
				
				Map<String, Object> aimCriteria2 = criteria2.getAIMCriteria();
				boolean blnAimCriteriaSize2 = (aimCriteria2.size() == 0);
				if(blnAimCriteriaSize2 == false){
					logger.warn("Invalid size of AIM criteria for Patient 2, subelement 2. Expected size 0, actual " + aimCriteria2.size());
				}
				
				SubElement subElement3 = subElements.get(2);
				Criteria criteria3 = subElement3.getCriteria();
				Map<Integer, Object> dicomCriteria3 = criteria3.getDICOMCriteria();
				boolean blnDicomCriteriaSize3 = (dicomCriteria3.size() == 3);
				if(blnDicomCriteriaSize3 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient2, subelement 3. Expected 3, actual " + dicomCriteria3.size());
				}
				Object value7 = dicomCriteria3.get(new Integer(10001));	//patientName
				Object value8 = dicomCriteria3.get(new Integer(2097165));	//studyInstanceUID
				Object value9 = dicomCriteria3.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue7 = value7.toString().equalsIgnoreCase("*");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("303.303");
				boolean blnValue9 = value9.toString().equalsIgnoreCase("505.505.1");
				if(blnValue7 == false || blnValue8 == false || blnValue9 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value7 + "'");
					logger.warn("StudyInstanceUID: expected '303.303', actual " + "'" + value8 + "'");
					logger.warn("SeriesInstanceUID: expected '505.505.1', actual " + "'" + value9 + "'");
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
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnAimCriteriaSize1 == true &&
						blnDicomCriteriaSize2 == true && blnValue4 == true && blnValue5 == true && blnValue6 == true && blnAimCriteriaSize2 == true && 
						blnDicomCriteriaSize3 == true && blnValue7 == true && blnValue8 == true && blnValue9 == true && blnAimCriteriaSize3 == true && 
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
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 3);
				if(blnDicomCriteriaSize1 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 1. Expected 3, actual " + dicomCriteria1.size());
				}
				Object value1 = dicomCriteria1.get(new Integer(10001));	//patientName
				Object value2 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value3 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("404.404");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("606.606.1");
				if(blnValue1 == false || blnValue2 == false || blnValue3 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value1 + "'");
					logger.warn("StudyInstanceUID: expected '404.404', actual " + "'" + value2 + "'");
					logger.warn("SeriesInstanceUID: expected '606.606.1', actual " + "'" + value3 + "'");
				}
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				if(blnAimCriteriaSize1 == false){
					logger.warn("Invalid size of AIM criteria for Patient 3, subelement 1. Expected size 0, actual " + aimCriteria1.size());
				}
				
				SubElement subElement6 = subElements.get(5);
				Criteria criteria6 = subElement6.getCriteria();
				Map<Integer, Object> dicomCriteria6 = criteria6.getDICOMCriteria();
				boolean blnDicomCriteriaSize6 = (dicomCriteria6.size() == 3);
				if(blnDicomCriteriaSize6 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 6. Expected 3, actual " + dicomCriteria6.size());
				}
				Object value4 = dicomCriteria6.get(new Integer(10001));	//patientName
				Object value5 = dicomCriteria6.get(new Integer(2097165));	//studyInstanceUID
				Object value6 = dicomCriteria6.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue4 = value4.toString().equalsIgnoreCase("*");
				boolean blnValue5 = value5.toString().equalsIgnoreCase("505.505");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("11.11.1");
				if(blnValue4 == false || blnValue5 == false || blnValue6 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value4 + "'");
					logger.warn("StudyInstanceUID: expected '505.505', actual " + "'" + value5 + "'");
					logger.warn("SeriesInstanceUID: expected '11.11.1', actual " + "'" + value6 + "'");
				}
				
				Map<String, Object> aimCriteria6 = criteria6.getAIMCriteria();
				boolean blnAimCriteriaSize6 = (aimCriteria6.size() == 0);
				if(blnAimCriteriaSize6 == false){
					logger.warn("Invalid size of AIM criteria for Patient 3, subelement 6. Expected size 0, actual " + aimCriteria6.size());
				}

				SubElement subElement9 = subElements.get(8);
				Criteria criteria9 = subElement9.getCriteria();
				Map<Integer, Object> dicomCriteria9 = criteria9.getDICOMCriteria();
				boolean blnDicomCriteriaSize9 = (dicomCriteria9.size() == 3);
				if(blnDicomCriteriaSize9 == false){
					logger.warn("Incorrect number of DICOM criteria for Patient3, subelement 9. Expected 3, actual " + dicomCriteria9.size());
				}
				Object value7 = dicomCriteria9.get(new Integer(10001));	//patientName
				Object value8 = dicomCriteria9.get(new Integer(2097165));	//studyInstanceUID
				Object value9 = dicomCriteria9.get(new Integer(2097166));	//seriesInstanceUID
				boolean blnValue7 = value7.toString().equalsIgnoreCase("*");
				boolean blnValue8 = value8.toString().equalsIgnoreCase("606.606");
				boolean blnValue9 = value9.toString().equalsIgnoreCase("14.14.1");
				if(blnValue7 == false || blnValue8 == false || blnValue9 == false){
					logger.warn("Incorrect criteria values");
					logger.warn("PatientName: expected '*', actual " + "'" + value7 + "'");
					logger.warn("StudyInstanceUID: expected '606.606', actual " + "'" + value8 + "'");
					logger.warn("SeriesInstanceUID: expected '14.14.1', actual " + "'" + value9 + "'");
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
				
				blnPatient2Atts = (blnId3 == true && blnNumOfSubElementsPatient3 == true && blnDicomCriteriaSize1 == true &&
						blnValue1 == true && blnValue2 == true && blnValue3 == true && blnAimCriteriaSize1 == true &&
						blnDicomCriteriaSize6 == true && blnValue4 == true && blnValue5 == true && blnValue6 == true && blnAimCriteriaSize6 == true && 
						blnDicomCriteriaSize9 == true && blnValue7 == true && blnValue8 == true && blnValue9 == true && blnAimCriteriaSize9 == true && 
						blnTarget == true);
				
			} else {
				
			}
			
			
			
		}
		
		//Assert iterator
		//Number of iterator's elements = 3
		boolean blnNumberOfElements = (numberOfElements == 3);
		assertTrue("Expected number of elements is 3, but actual number is " + numberOfElements, blnNumberOfElements);
		assertTrue ("", blnPatient1Atts == true && blnPatient2Atts == true && blnPatient3Atts == true);
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.STUDY
	public void testCreateIterator_1B(){
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.STUDY);
		assertTrue(false);
		
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.SERIES
	public void testCreateIterator_1C(){
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.SERIES);
		
		assertTrue(false);
	}
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	public void testCreateIterator_2A(){
		assertTrue(false);
		
	}

	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are null.
	//Subqueries needed. Connection ON.
	public void testCreateIterator_3A(){
		 Iterator<TargetElement> iter = util.createIterator(null, null);
		 assertTrue(false);
			
	}
	
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection lost.
	public void testCreateIterator_4A(){
		assertTrue(false);
		
	}
	
	
	
	
}
