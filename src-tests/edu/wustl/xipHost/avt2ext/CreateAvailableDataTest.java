/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.util.Iterator;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.iterator.IteratorElementEvent;
import edu.wustl.xipHost.iterator.IteratorEvent;
import edu.wustl.xipHost.iterator.IteratorUtil;
import edu.wustl.xipHost.iterator.TargetElement;
import edu.wustl.xipHost.iterator.TargetIteratorRunner;
import edu.wustl.xipHost.iterator.TargetIteratorListener;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.hostControl.Util;
import edu.wustl.xipHost.wg23.WG23DataModel;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class CreateAvailableDataTest extends TestCase implements TargetIteratorListener {
	TargetElement targetElement;
	File tmpDir;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		tmpDir = new File("./test-content", "TmpAVTTest");	
		if(tmpDir.exists() == false){
			tmpDir.mkdir();
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Util.delete(tmpDir);
	}
	
	//AVUtil - getWG23DataModel. Basic flow
	//Parameters: valid
	//IterationTarget.PATIENT
	public void testCreateIterator_1A(){
		AVTQueryStub avtQuery = new AVTQueryStub(null, null, null, null, null);
		SearchResultSetupAvailableData resultForAvailableData = new SearchResultSetupAvailableData();
		SearchResult selectedDataSearchResult = resultForAvailableData.getSearchResult();
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.PATIENT, avtQuery, tmpDir, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			//logger.error(e, e);
		}
		targetElement = iter.next();
		WG23DataModel wg23data = IteratorUtil.getWG23DataModel(targetElement);
		
	}

	//AVUtil - getWG23DataModel. Basic flow
	//Parameters: valid
	//IterationTarget.STUDY
	public void testCreateIterator_1B(){
		AVTQueryStub avtQuery = new AVTQueryStub(null, null, null, null, null);
		SearchResultSetupAvailableData resultForAvailableData = new SearchResultSetupAvailableData();
		SearchResult selectedDataSearchResult = resultForAvailableData.getSearchResult();
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.STUDY, avtQuery, tmpDir, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			//logger.error(e, e);
		}
		targetElement = iter.next();
		WG23DataModel wg23data = IteratorUtil.getWG23DataModel(targetElement);
		
	}
	
	//AVUtil - getWG23DataModel. Basic flow
	//Parameters: valid
	//IterationTarget.SERIES
	public void testCreateIterator_1C(){
		AVTQueryStub avtQuery = new AVTQueryStub(null, null, null, null, null);
		SearchResultSetupAvailableData resultForAvailableData = new SearchResultSetupAvailableData();
		SearchResult selectedDataSearchResult = resultForAvailableData.getSearchResult();
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, IterationTarget.SERIES, avtQuery, tmpDir, this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
			t.join();
		} catch(Exception e) {
			//logger.error(e, e);
		}
		targetElement = iter.next();
		WG23DataModel wg23data = IteratorUtil.getWG23DataModel(targetElement);
		
	}

	Iterator<TargetElement> iter;
	@Override
	public void fullIteratorAvailable(IteratorEvent e) {
		iter = (Iterator<TargetElement>) e.getSource();
	}

	@Override
	public void targetElementAvailable(IteratorElementEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
