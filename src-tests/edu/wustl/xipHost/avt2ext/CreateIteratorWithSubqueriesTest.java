/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.Iterator;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.avt2ext.AVTUtil;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.avt2ext.iterator.TargetElement;
import edu.wustl.xipHost.dataModel.SearchResult;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class CreateIteratorWithSubqueriesTest extends TestCase {
	final static Logger logger = Logger.getLogger(CreateIteratorWithSubqueriesTest.class);
	SearchResult selectedDataSearchResult;
	AVTUtil util;
	public CreateIteratorWithSubqueriesTest(String name){
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		SearchResultSetupSubqueries result = new SearchResultSetupSubqueries();
		selectedDataSearchResult = result.getSearchResult();
		util = new AVTUtil();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
		
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: PATIENT
	public void testCreateIterator_2A(){
		AVTQuery avtQuery = new AVTQueryStub(null, null, null, null, null);
		Iterator<TargetElement> iter = util.createIterator(selectedDataSearchResult, IterationTarget.PATIENT, avtQuery);
		//assert iter
		
		assertTrue(false);
		
	}

	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: STUDY
	public void testCreateIterator_2B(){
		assertTrue(false);
		
	}
	
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget are valid.
	//Subqueries needed. Connection ON.
	//IterationTarget: SERIES
	public void testCreateIterator_2C(){
		assertTrue(false);
		
	}
	
	//AVTUtil - createIterator. Alternative flow.
	//Parameters: selectedDataSearchResult, IterationTarget is null.
	//Subqueries needed. Connection ON.
	//IterationTarget: STUDY
	public void testCreateIterator_3A(){
		 Iterator<TargetElement> iter = util.createIterator(null, null, null);
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
	
	
	
	
}
