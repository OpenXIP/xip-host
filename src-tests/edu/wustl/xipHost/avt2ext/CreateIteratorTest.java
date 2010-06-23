/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
		while(iter.hasNext()){
			TargetElement element = iter.next();
			numberOfElements ++;
			String id = element.getId();
			boolean blnId1 = false;
			if(id.equalsIgnoreCase("111")){
				blnId1 = true;
				element.getSubElements();
				List<SubElement> subElements = element.getSubElements();
				boolean blnNumOfSubElements = (subElements.size() == 2);
				SubElement subElement1 = subElements.get(1);	
				Criteria criteria1 = subElement1.getCriteria();
				Map<Integer, Object> dicomCriteria1 = criteria1.getDICOMCriteria();
				//original criteria size = 1
				//Plus plus studyInstanceUID plus seriesInstanceUID
				//total size = 3
				boolean blnDicomCriteriaSize1 = (dicomCriteria1.size() == 3);
				Object value1 = dicomCriteria1.get(new Integer(10001));	//patientName
				Object value2 = dicomCriteria1.get(new Integer(2097165));	//studyInstanceUID
				Object value3 = dicomCriteria1.get(new Integer(2097166));	//seriesInstanceUID
				String path1 = subElement1.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue1 = value1.toString().equalsIgnoreCase("*");
				boolean blnValue2 = value2.toString().equalsIgnoreCase("101.101");
				boolean blnValue3 = value3.toString().equalsIgnoreCase("101.101.1");
				
				Map<String, Object> aimCriteria1 = criteria1.getAIMCriteria();
				boolean blnAimCriteriaSize1 = (aimCriteria1.size() == 0);
				
				
				SubElement subElement2 = subElements.get(1);
				Criteria criteria2 = subElement2.getCriteria();
				Map<Integer, Object> dicomCriteria2 = criteria2.getDICOMCriteria();
				//original criteria size = 1
				//Plus plus studyInstanceUID plus seriesInstanceUID
				//total size = 3
				boolean blnDicomCriteriaSize2 = (dicomCriteria2.size() == 3);
				Object value4 = dicomCriteria2.get(new Integer(10001));	//patientName
				Object value5 = dicomCriteria2.get(new Integer(2097165));	//studyInstanceUID
				Object value6 = dicomCriteria2.get(new Integer(2097166));	//seriesInstanceUID
				String path2 = subElement2.getPath();
				//check if path exists
				//getPaths last directory equating to seriesINstanceUID
				boolean blnValue4 = value4.toString().equalsIgnoreCase("*");
				boolean blnValue5 = value5.toString().equalsIgnoreCase("202.202");
				boolean blnValue6 = value6.toString().equalsIgnoreCase("202.202.1");
				
				Map<String, Object> aimCriteria2 = criteria2.getAIMCriteria();
				boolean blnAimCriteriaSize2 = (aimCriteria2.size() == 0);
				
				IterationTarget target = element.getTarget();
				boolean blnTarget = target.toString().equalsIgnoreCase("PATIENT");
				
			} else if(id.equalsIgnoreCase("222") ){

				
			
			} else if (id.equalsIgnoreCase("333")){
			
			} else {
				
			}
			boolean blnAllIds = (blnId1);
			
			
		}
		
		//Assert iterator
		//Number of iterator's elements = 3
		boolean blnNumberOfElements = (numberOfElements == 3);
		assertTrue("Expected number of elements is 3, but actual number is " + numberOfElements, blnNumberOfElements);
		
		
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.STUDY
	public void testCreateIterator_1B(){
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.STUDY);
		//assert iter
		while(iter.hasNext()){
			TargetElement iterElement = iter.next();
			
			
		}
		
	}
	
	//AVUtil - createIterator. Basic flow
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//No subqueries needed. Connection ON.
	//IterationTarget.SERIES
	public void testCreateIterator_1C(){
		 Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.SERIES);
		//assert iter
		while(iter.hasNext()){
			TargetElement iterElement = iter.next();
			
			
			
		}
		
	}
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	public void testCreateIterator_2A(){
		
	}

	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are null.
	//Subqueries needed. Connection ON.
	public void testCreateIterator_3A(){
		 Iterator<TargetElement> iter = util.createIterator(null, null);
		
	}
	
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection lost.
	public void testCreateIterator_4A(){
		
	}
	
}
