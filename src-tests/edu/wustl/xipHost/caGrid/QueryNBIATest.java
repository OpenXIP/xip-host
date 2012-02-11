/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import static org.junit.Assert.*;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Image;
import gov.nih.nci.ncia.domain.Series;
import gov.nih.nci.ncia.domain.Study;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
/**
 * @author Jaroslaw Krych
 *
 */
public class QueryNBIATest {
	CQLQuery cqlQuery;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		HashMap<String, String> queryHashMap = new HashMap<String, String>();																		
		if (queryHashMap.isEmpty()) {			
			//queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());
			//queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Study.class.getCanonicalName());
			queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Image.class.getCanonicalName());
			//queryHashMap.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", "1.3.6.1.4.1.9328.50.1.4717");
			//queryHashMap.put("gov.nih.nci.ncia.domain.Series.instanceUID", "1.3.6.1.4.1.9328.50.1.4718");
			queryHashMap.put("gov.nih.nci.ncia.domain.Patient.patientId", "1.3.6.1.4.1.9328.50.1.0008");
			queryHashMap.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", "1.3.6.1.4.1.9328.50.1.4434");
			queryHashMap.put("gov.nih.nci.ncia.domain.Series.instanceUID", "1.3.6.1.4.1.9328.50.1.4435");
			//queryHashMap.put("gov.nih.nci.ncia.domain.Image.sopInstanceUID", "1.3.6.1.4.1.9328.50.1.4433");  
		}else{
 
		}
		HashmapToCQLQuery h2cql;
		try {
			h2cql = new HashmapToCQLQuery(new ModelMap());
			
			try {
				cqlQuery = h2cql.makeCQLQuery(queryHashMap);
				QueryModifier queryModifier = new QueryModifier();
				//String[] attributeNames = new String[1];
				//attributeNames[0] = "gov.nih.nci.ncia.domain.Image.sopInstanceUID";
				//queryModifier.setAttributeNames(attributeNames);
				queryModifier.setCountOnly(true);
				cqlQuery.setQueryModifier(queryModifier);
				System.err.println(ObjectSerializer.toString(cqlQuery, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ModelMapException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();			
		} catch (ClassNotFoundException e1) {
			
		}
			
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	SAXBuilder builder = new SAXBuilder();
	@Test
	public void test() throws JDOMException, IOException {
		String gridServiceUrl= "http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService";
		try {
			NCIACoreServiceClient nciaClient = new NCIACoreServiceClient(gridServiceUrl);
			CQLQueryResults result = nciaClient.query(cqlQuery);
			if(result != null)	{
			    CQLQueryResultsIterator iter = new CQLQueryResultsIterator(result, true);
			    while (iter.hasNext()) {
				    String xmlSeries = (String)iter.next();
				    System.out.println(xmlSeries);
				    Document document;
				    Element root;
				    InputStream is = new ByteArrayInputStream(xmlSeries.getBytes());
				    document = builder.build(is);
					root = document.getRootElement();
					String objName = root.getName();
					String nameSpace = root.getNamespacePrefix();
					System.out.println(nameSpace);
					//path for the parent of TMP directory
					String seriesInstanceUID = root.getAttribute("instanceUID").getValue().trim();
					String modality = root.getAttribute("modality").getValue().trim();
					System.out.println("Result series instance uid is " + seriesInstanceUID + " modality: " + modality );
			     }
			}else{
			    System.out.println("No result found for ");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}
}
