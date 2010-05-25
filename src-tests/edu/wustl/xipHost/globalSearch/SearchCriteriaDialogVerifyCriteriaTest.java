/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.globalSearch;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.TagFromName;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class SearchCriteriaDialogVerifyCriteriaTest extends TestCase {
	SearchCriteriaPanel panel;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		panel = new SearchCriteriaPanel();		
	}

	//SearchCriteriaDialog 1A - basic flow. At least one criterium specified 
	public void testVerifyCriteria1A() throws DicomException  {						
		AttributeList attList = new AttributeList();
		Attribute a = new PersonNameAttribute(TagFromName.PatientName);        
		a.addValue("Krych");
        attList.put(TagFromName.PatientName, a);  
		Boolean blnVerify = panel.verifyCriteria(attList);
		assertTrue("Criteria were specified and are correct but system failed to validate criteria.", blnVerify);
	}
	
	//SearchCriteriaDialog 1B - alternative flow. No criteria specified except "SpecificCharacterSet"
	public void testVerifyCriteria1B() throws DicomException  {						
		AttributeList attList = new AttributeList();
		Attribute a = new PersonNameAttribute(TagFromName.PatientName);        
		Attribute a2 = new PersonNameAttribute(TagFromName.SpecificCharacterSet);
		a.addValue("");
		String[] characterSets = { "ISO_IR 100" };		
		a2.addValue(characterSets[0]);
        attList.put(TagFromName.PatientName, a);
        attList.put(TagFromName.SpecificCharacterSet, a2);
		Boolean blnVerify = panel.verifyCriteria(attList);
		assertFalse("No criteria specified but system succedded to validate criteria.", blnVerify);
	}
	
	//SearchCriteriaDialog 1C - alternative flow. Criteria specified have null values. 
	public void testVerifyCriteria1C() throws DicomException  {						
		AttributeList attList = new AttributeList();
		Attribute a = new PersonNameAttribute(TagFromName.PatientName);        
		a.addValue("null");
        attList.put(TagFromName.PatientName, a);  
		Boolean blnVerify = panel.verifyCriteria(attList);
		assertFalse("Criteria contain null values but system validated criteria.", blnVerify);
	}	
}
