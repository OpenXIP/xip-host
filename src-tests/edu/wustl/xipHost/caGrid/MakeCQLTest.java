/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package edu.wustl.xipHost.caGrid;

import java.io.FileInputStream;

import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import junit.framework.TestCase;

public class MakeCQLTest extends TestCase {
	GridUtil gridUtil;
	
	protected void setUp() throws Exception {
		super.setUp();
		gridUtil = new GridUtil();
		FileInputStream fis = new FileInputStream("resources/modelmap/NCIAModelMap.properties");
		gridUtil.loadNCIAModelMap(fis);
	}

	//MakeCQLTest 1A basic flow. 
	//Creating CQLQuery with custom XIPHost menthod
	public void testMakeCQLFromAttributeList1A() {	
		AttributeList attList = new AttributeList();
		try {
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);			
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.0019"); attList.put(t,a); }
			{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("1.3.6.1.4.1.9328.50.1.8858"); attList.put(t,a); }
			{ AttributeTag t = TagFromName.SpecificCharacterSet; Attribute a = new CodeStringAttribute(t); a.addValue(characterSets[0]); attList.put(t,a); }			
		}
		catch (Exception e) {
			e.printStackTrace(System.err);			
		}
		CQLQuery cql = gridUtil.convertToCQLStatement(attList, CQLTargetName.SERIES);
		try {
			System.err.println(ObjectSerializer.toString(cql, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e) {			
			e.printStackTrace();
		}	
		assertNotNull("AttributeList is valid but system unble to make CQL.", cql);		
	}		
}
