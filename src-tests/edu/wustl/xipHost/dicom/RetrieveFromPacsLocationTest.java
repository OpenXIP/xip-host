/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.nema.dicom.PS3_19.ObjectLocator;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dicom.server.Workstation2;
import edu.wustl.xipHost.dicom.server.Workstation3;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveFromPacsLocationTest extends TestCase implements RetrieveListener {
	final static Logger logger = Logger.getLogger(RetrieveFromPacsLocationTest.class);	
	PacsLocation calling;
	PacsLocation called;
	AttributeList retrieveCriteria;
	DicomRetrieve dicomRetrieve;
	String databaseFileName;
	protected void setUp(){
		called = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "WashU WS2");		
		calling = new PacsLocation("127.0.0.1", 3003, "WORKSTATION3", "WashU WS3");
		retrieveCriteria = DicomUtil.constructEmptyAttributeList();
		try{
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			{ AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0022"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0022"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.9328.50.1.10607"); retrieveCriteria.put(t,a); }			
			{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("1.3.6.1.4.1.9328.50.1.10697"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("*"); retrieveCriteria.put(t,a); }
			{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); retrieveCriteria.put(t,a); }
		} catch (DicomException excep){
			
		}
		Workstation2.startHSQLDB();
		Workstation2.startPixelmedServer();
		Workstation3.startHSQLDB();
		Workstation3.startPixelmedServer();
		databaseFileName = Workstation3.getServerConfig().getProperty("Application.DatabaseFileName");
	}
	
	protected void tearDown(){
		Workstation2.stopHSQLDB();
		Workstation3.stopHSQLDB();
	}
	
	boolean retrieveResultAvailable = false;
	
	//DicomRetrieve 1A - basic flow. AttributeList, PacsLocation (calling and called) are valid and network is on.
	public void testRetrieveFromPacsLocation1A() {	
		logger.debug(retrieveCriteria.toString());
		Map<Integer, Object> dicomCriteria = DicomUtil.convertToADDicomCriteria(retrieveCriteria);
		Map<String, Object> aimCriteria = new HashMap<String, Object>();
		File importDir = new File("./test-content/WORKSTATION2");
		RetrieveTarget retrieveTarget = RetrieveTarget.DICOM_AND_AIM;
		DataSource dataSource = called;
		dicomRetrieve = new DicomRetrieve(dicomCriteria, aimCriteria, importDir, retrieveTarget, dataSource);
		dicomRetrieve.setDefaultCallingPacsLocation(calling);
		dicomRetrieve.setDatabaseFileName(databaseFileName);
		dicomRetrieve.addRetrieveListener(this);
		Thread t = new Thread(dicomRetrieve);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
		if(objectLocs != null){
			assertTrue("Number of retrieved objects should be 2, but actual is " + objectLocs.size(), objectLocs.size() == 2);
		} else {
			fail("Null value of ObjectLocators");
		}
		
	}

	Map<String, ObjectLocator> objectLocs;
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveResultsAvailable(RetrieveEvent e) {
		objectLocs = (Map<String, ObjectLocator>) e.getSource();
	}

}
