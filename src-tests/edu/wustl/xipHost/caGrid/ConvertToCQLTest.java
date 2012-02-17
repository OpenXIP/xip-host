/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Series;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.globus.wsrf.encoding.SerializationException;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class ConvertToCQLTest extends TestCase {
	GridUtil util;
	Map<String, String> map;
	
	protected void setUp() throws Exception {
		 util = new GridUtil();
		 FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");
		 map = util.loadNCIAModelMap(fis);
	}
	
	//GridUtil 1A - basic flow. AttributeList is valid. 	
	public void testConvertToCQL1A() throws DicomException, FileNotFoundException, ModelMapException, IOException, ClassNotFoundException, MalformedQueryException, SerializationException  {						
		AttributeList attList = new AttributeList();
		Attribute a = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);        
		a.addValue("1.3.6.1.4.1.9328.50.1.9772");
        attList.put(TagFromName.SeriesInstanceUID, a);  
		
        HashMap<String, String> queryHashMap = new HashMap<String, String>();
		queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());
		queryHashMap.put("gov.nih.nci.ncia.domain.Series.instanceUID", "1.3.6.1.4.1.9328.50.1.9772");		
		/* Convert hash map to SQL */
		HashmapToCQLQuery h2cql = new HashmapToCQLQuery(new ModelMap());
		CQLQuery expectedCQL = h2cql.makeCQLQuery(queryHashMap); 
		/*System.err.println(ObjectSerializer.toString(expectedCQL, 
				new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));*/		
        assertEquals("Attribute list valid but unable to convert to CQL", expectedCQL, util.convertToCQLStatement(attList, CQLTargetName.SERIES));		
	}

	//GridUtil 1B - alternative flow. AttributeList is null. 	
	public void testConvertToCQL1B() throws DicomException, FileNotFoundException, ModelMapException, IOException, ClassNotFoundException, MalformedQueryException  {						
		AttributeList attList = null;		                       
        assertNull("Attribute list is null but convertToCQL did not return null.", util.convertToCQLStatement(attList, CQLTargetName.SERIES));		
	}

	
}
