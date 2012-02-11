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
import gov.nih.nci.ivi.helper.NCIADataServiceHelper;
import junit.framework.TestCase;

public class MakeCQLTest extends TestCase {
	NCIADataServiceHelper nciaHelper;
	GridUtil gridUtil;
	
	protected void setUp() throws Exception {
		super.setUp();
		nciaHelper = new NCIADataServiceHelper();
		gridUtil = new GridUtil();
		FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");
		gridUtil.loadNCIAModelMap(fis);
	}

	//MakeCQLTest 1A basic flow. 
	//Creating CQLQuery with custom XIPHost menthod
	public void testMakeCQLFromAttributeList1A() {	
		AttributeList attList = new AttributeList();
		try {
			String[] characterSets = { "ISO_IR 100" };
			SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);			
			{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue("TCGA-08-0514"); attList.put(t,a); }
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
