/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ObjectLocator;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.server.Workstation2;
import edu.wustl.xipHost.dicom.server.Workstation3;
import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.iterator.IteratorElementEvent;
import edu.wustl.xipHost.iterator.IteratorEvent;
import edu.wustl.xipHost.iterator.RetrieveTarget;
import edu.wustl.xipHost.iterator.TargetElement;
import edu.wustl.xipHost.iterator.TargetIteratorListener;
import edu.wustl.xipHost.iterator.TargetIteratorRunner;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveFromPacsLocationTest extends TestCase implements DataAccessListener, TargetIteratorListener {
	final static Logger logger = Logger.getLogger(RetrieveFromPacsLocationTest.class);	
	PacsLocation calling;
	PacsLocation called;
	DicomManager dicomMgr;
	AttributeList retrieveCriteria;
	DicomRetrieve dicomRetrieve;
	protected void setUp(){
		called = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "WashU WS2");		
		calling = new PacsLocation("127.0.0.1", 3003, "WORKSTATION3", "WashU WS3");
		retrieveCriteria = DicomUtil.constructEmptyAttributeList();
		try{
			{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("2.16.840.1.113669.6.4.0.1152905158098.2006284135300993"); retrieveCriteria.put(t,a); }			
			{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("2.16.840.1.113662.4.2724971601059.1160591040.79352554742476"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("*"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); retrieveCriteria.put(t,a); }
		} catch (DicomException excep){
			
		}
		Workstation2.startHSQLDB();
		Workstation2.startPixelmedServer();
		Workstation3.startHSQLDB();
		Workstation3.startPixelmedServer();	
		dicomMgr = DicomManagerFactory.getInstance();
	}
	
	protected void tearDown(){
		Workstation2.stopHSQLDB();
		Workstation3.stopHSQLDB();
	}
	
	boolean retrieveResultAvailable = false;
	
//	DicomManagerImpl 1A - basic flow. AttributeList, PacsLocation are valid and network is on.
	public void testRetrieveFromPacsLocation1A() {	
		logger.debug(retrieveCriteria.toString());
		dicomRetrieve = new DicomRetrieve(retrieveCriteria, called, calling);
		SearchResult selectedDataSearchResult = new SearchResult();
		selectedDataSearchResult.setDataSourceDescription("Test data source");
		Map<Integer, Object> dicomCriteria = DicomUtil.convertToADDicomCriteria(retrieveCriteria);
		Map<String, Object> aimCriteria = new HashMap<String, Object>();
		Criteria originalCriteria = new Criteria(dicomCriteria, aimCriteria);
		selectedDataSearchResult.setOriginalCriteria(originalCriteria);
		Patient patient = new Patient("Clunie^David", "2037316", "19601109");
		Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		patient.setLastUpdated(lastUpdated);
		selectedDataSearchResult.addPatient(patient);
		Study study = new Study("20061011", "29206", "Brain", "2.16.840.1.113669.6.4.0.1152905158098.2006284135300993");
		study.setLastUpdated(lastUpdated);
		patient.addStudy(study);
		Series series = new Series("2.16.840.1.113662.4.2724971601059.1160591040.79352554742476", "MR", "", "2.16.840.1.113662.4.2724971601059.1160591040.79352554742476");
		//series.setLastUpdated(lastUpdated);
		study.addSeries(series);
		IterationTarget interTarget = IterationTarget.SERIES;
		Query query = new DicomQuery();
		TargetIteratorRunner iterRunner = new TargetIteratorRunner(selectedDataSearchResult, interTarget, query, this);
		try {
			Thread t = new Thread(iterRunner);
			t.start();
		} catch(Exception e) {
			logger.error(e, e);
		}
		
		RetrieveTarget retrieveTarget = RetrieveTarget.DICOM_AND_AIM;
		synchronized(targetElements){
			while(targetElements.size() == 0){
				try {
					targetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		//TODO create targetElement
		TargetElement targetElement = targetElements.get(0);
		dicomRetrieve.setRetrieve(targetElement, retrieveTarget);
		dicomRetrieve.addDataAccessListener(this);
		Thread t = new Thread(dicomRetrieve);
		t.start();
		synchronized(this){
			while(retrieveResultAvailable == false){
				try {
					this.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}
		for(ObjectLocator objLoc : objLocators){
			logger.debug(objLoc.getUri());
		}		
		assertTrue(objLocators.size() > 0);
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryResultsAvailable(QueryEvent e) {
		// TODO Auto-generated method stub
		
	}

	List<ObjectLocator> objLocators = null;
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		retrieveResultAvailable = true;				
		objLocators = (List<ObjectLocator>) dicomRetrieve.getObjectLocators();
		this.notify();
	}

	@Override
	public void fullIteratorAvailable(IteratorEvent e) {
		// TODO Auto-generated method stub
		
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
}
