package edu.wustl.xipHost.caGrid;

import static org.junit.Assert.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;

public class QueryNBIAwithGridQueryTest implements QueryListener {
	final static Logger logger = Logger.getLogger(QueryNBIAwithGridQueryTest.class);
	static GridUtil gridUtil;
	static GridLocation nbiaLocation;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		gridUtil = new GridUtil();
		FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");
		gridUtil.loadNCIAModelMap(fis);
		nbiaLocation = new GridLocation("http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService", Type.DICOM, "NBIA-5.0", "NBIA Production Server at NCI");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		result = null;
	}
	
	//Basic flow. Query NBIA for Patient. PatientId valid. Assumed NBIA production server contains specified patient.
	@Test
	public void testNBIAQuerywithGridQuery_1A() throws DicomException {
		CQLQuery cql;
		AttributeList criteria = DicomUtil.constructEmptyAttributeList();
		String[] characterSets = { "ISO_IR 100" };
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
		{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0009"); criteria.put(t,a); }
		cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.PATIENT);
		System.err.print(cql);
		
		Query gridQuery = new GridQuery(cql, criteria, nbiaLocation, null, null);				
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery); 					
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		//Expected number of Patients = 1. No other data.
		//Expected patientId = 1.3.6.1.4.1.9328.50.1.0009
		List<Patient> patients = result.getPatients();
		assertEquals("Expected number of patients is: 1 but actual is: " + patients.size(), patients.size(), 1);
		Patient patient = patients.get(0);
		String patientId = patient.getPatientID();
		assertEquals("Query returned patient but not with the expected Id.", patientId, "1.3.6.1.4.1.9328.50.1.0009");
	}

	//Basic flow. Query NBIA for Studies. PatientId valid. Assumed NBIA production server contains specified patient.
	@Test
	public void testNBIAQuerywithGridQuery_1B() throws DicomException {
		CQLQuery cql;
		AttributeList criteria = DicomUtil.constructEmptyAttributeList();
		String[] characterSets = { "ISO_IR 100" };
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
		{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0009"); criteria.put(t,a); }
		cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.STUDY);
		SearchResult previousSeachResult = new SearchResult("Test");
		Patient patient1 = new Patient("1.3.6.1.4.1.9328.50.1.0009", "1.3.6.1.4.1.9328.50.1.0009", "");
		previousSeachResult.addPatient(patient1);
		Query gridQuery = new GridQuery(cql, criteria, nbiaLocation, previousSeachResult, patient1);				
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery); 					
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		//Expected number of Studies = 2.
		List<Patient> patients = result.getPatients();
		Patient patient2 = patients.get(0);
		List<Study> studies = patient2.getStudies();
		assertEquals("Expected number of studies is: 2 but actual is: " + studies.size(), studies.size(), 2);
		List<String> studyInstanceUIDs = new ArrayList<String>();
		String studyInstanceUID1 = studies.get(0).getStudyInstanceUID();
		String studyInstanceUID2 = studies.get(1).getStudyInstanceUID();
		studyInstanceUIDs.add(studyInstanceUID1);
		studyInstanceUIDs.add(studyInstanceUID2);
		boolean studiesAsExpected = false;
		if(studyInstanceUIDs.contains("1.3.6.1.4.1.9328.50.1.4717") && studyInstanceUIDs.contains("1.3.6.1.4.1.9328.50.1.4893")){
			studiesAsExpected = true;
		}
		assertTrue("Query returned studies but not with the expected UIDs.", studiesAsExpected);
	}
	
	//Basic flow. Query NBIA for Series. PatientId valid. StudyInstanceUID valid. Assumed NBIA production server contains specified patient and study.
	@Test
	public void testNBIAQuerywithGridQuery_1C() throws DicomException {
		CQLQuery cql;
		AttributeList criteria = DicomUtil.constructEmptyAttributeList();
		String[] characterSets = { "ISO_IR 100" };
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
		{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0009"); criteria.put(t,a); }
		{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.4893"); criteria.put(t,a); }
		cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.SERIES);
		SearchResult previousSeachResult = new SearchResult("Test");
		Patient patient1 = new Patient("1.3.6.1.4.1.9328.50.1.0009", "1.3.6.1.4.1.9328.50.1.0009", "");
		Study study1 = new Study("", "", "", "1.3.6.1.4.1.9328.50.1.4893");
		patient1.addStudy(study1);
		previousSeachResult.addPatient(patient1);
		Query gridQuery = new GridQuery(cql, criteria, nbiaLocation, previousSeachResult, study1);				
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery); 					
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		//Expected number of Series = 3.
		List<Patient> patients = result.getPatients();
		Patient patient2 = patients.get(0);
		List<Study> studies = patient2.getStudies();
		Study study = studies.get(0);
		List<Series> series = study.getSeries();
		List<String> seriesInstanceUIDs = new ArrayList<String>();
		String seriesInstanceUID1 = series.get(0).getSeriesInstanceUID();
		String seriesInstanceUID2 = series.get(1).getSeriesInstanceUID();
		String seriesInstanceUID3 = series.get(2).getSeriesInstanceUID();
		seriesInstanceUIDs.add(seriesInstanceUID1);
		seriesInstanceUIDs.add(seriesInstanceUID2);
		seriesInstanceUIDs.add(seriesInstanceUID3);
		assertEquals("Expected number of series is: 3 but actual is: " + series.size(), series.size(), 3);
		boolean seriesAsExpected = false;
		if(seriesInstanceUIDs.contains("1.3.6.1.4.1.9328.50.1.4894") && seriesInstanceUIDs.contains("1.3.6.1.4.1.9328.50.1.4898") 
				&& seriesInstanceUIDs.contains("1.3.6.1.4.1.9328.50.1.4978")){
			seriesAsExpected = true;
		}
		assertTrue("Query returned series but not with the expected seriesInstanceUIDs.", seriesAsExpected);
	}
	
	//Basic flow. Query NBIA for number of items found in a given Series. PatientId, StudyInstanceUID and SeriesInstanceUID are valid. 
	//Assumed NBIA production server contains specified patient and study and series.
	@Test
	public void testNBIAQuerywithGridQuery_1d() throws DicomException {
		CQLQuery cql;
		AttributeList criteria = DicomUtil.constructEmptyAttributeList();
		String[] characterSets = { "ISO_IR 100" };
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
		{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0009"); criteria.put(t,a); }
		{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.4978"); criteria.put(t,a); }
		cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.SERIES);
		SearchResult previousSeachResult = new SearchResult("Test");
		Patient patient1 = new Patient("1.3.6.1.4.1.9328.50.1.0009", "1.3.6.1.4.1.9328.50.1.0009", "");
		Study study1 = new Study("", "", "", "1.3.6.1.4.1.9328.50.1.4893");
		Series series1 = new Series("", "CT", "", "1.3.6.1.4.1.9328.50.1.4978");
		study1.addSeries(series1);
		patient1.addStudy(study1);
		previousSeachResult.addPatient(patient1);
		Query gridQuery = new GridQuery(cql, criteria, nbiaLocation, previousSeachResult, series1);	
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		Map<String, Object> aimCriteria = new HashMap<String, Object>();
		dicomCriteria.put(Tag.PatientID, "1.3.6.1.4.1.9328.50.1.0009");
		dicomCriteria.put(Tag.PatientName, "1.3.6.1.4.1.9328.50.1.0009");
		dicomCriteria.put(Tag.StudyInstanceUID, "1.3.6.1.4.1.9328.50.1.4893");
		dicomCriteria.put(Tag.SeriesInstanceUID, "1.3.6.1.4.1.9328.50.1.4978");
		gridQuery.setQuery(dicomCriteria, aimCriteria, QueryTarget.ITEM, previousSeachResult, series1);
		gridQuery.addQueryListener(this);
		Thread t = new Thread(gridQuery); 					
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		//Expected number of Series = 3.
		List<Patient> patients = result.getPatients();
		Patient patient2 = patients.get(0);
		List<Study> studies = patient2.getStudies();
		Study study = studies.get(0);
		List<Series> series = study.getSeries();
		Series oneSeries = series.get(0);
		List<Item> items = oneSeries.getItems();
		assertEquals("Expected number of items is: 59 but actual is: " + items.size(), items.size(), 59);
	}
	
	SearchResult result;
	@Override
	public void queryResultsAvailable(QueryEvent e) {
		Query query = (Query) e.getSource();
		result = query.getSearchResult();
	}

	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}

}
