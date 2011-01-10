/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.net.URI;
import java.util.List;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

import edu.wustl.xipHost.dicom.server.Workstation2;
import edu.wustl.xipHost.dicom.server.Workstation3;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveFromPacsLocationTest extends TestCase {
	AttributeList criteria;	
	PacsLocation calling;
	PacsLocation called;
	DicomManager dicomMgr;
	AttributeList retrieveCriteria;
	protected void setUp(){
		called = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "WashU WS2");		
		calling = new PacsLocation("127.0.0.1", 3003, "WORKSTATION3", "WashU WS3");
		retrieveCriteria = new AttributeList();	
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
	
//	DicomManagerImpl 1A - basic flow. AttributeList, PacsLocation are valid and network is on.
	public void testRetrieveFromPacsLocation1A() {	
		System.out.println(retrieveCriteria.toString());
		/*
		List<URI> uris = retrieve(retrieveCriteria, called, calling);
		for(int i = 0; i < uris.size(); i++){
			System.out.println(uris.get(i));
		}		
		assertTrue(uris.size() > 0);*/
	}
}
