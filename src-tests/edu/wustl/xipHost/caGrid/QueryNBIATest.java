/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import static org.junit.Assert.*;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;

import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Series;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * @author Jaroslaw Krych
 *
 */
public class QueryNBIATest {
	final static Logger logger = Logger.getLogger(QueryNBIATest.class);
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
			queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());
			//queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Study.class.getCanonicalName());
			//queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Image.class.getCanonicalName());
			//queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Patient.class.getCanonicalName());
			queryHashMap.put("gov.nih.nci.ncia.domain.Patient.patientId", "1.3.6.1.4.1.9328.50.1.0009");
			queryHashMap.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", "1.3.6.1.4.1.9328.50.1.4717");
			//queryHashMap.put("gov.nih.nci.ncia.domain.Series.instanceUID", "1.3.6.1.4.1.9328.50.1.4435");
			//queryHashMap.put("gov.nih.nci.ncia.domain.Image.sopInstanceUID", "1.3.6.1.4.1.9328.50.1.4433");  
			queryHashMap.put("gov.nih.nci.ncia.domain.Image.acquisitionDatetime", "test");  
		} else {
 
		}
		HashmapToCQLQuery h2cql;
		try {
			h2cql = new HashmapToCQLQuery(new ModelMap(new File("./resources/NCIAModelMap.properties")));
			
			try {
				cqlQuery = h2cql.makeCQLQuery(queryHashMap);
				/*QueryModifier queryModifier = new QueryModifier();
				//String[] attributeNames = new String[1];
				//attributeNames[0] = "gov.nih.nci.ncia.domain.Image.sopInstanceUID";
				//queryModifier.setAttributeNames(attributeNames);
				queryModifier.setCountOnly(true);
				cqlQuery.setQueryModifier(queryModifier);
				System.err.println(ObjectSerializer.toString(cqlQuery, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));*/
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

	//INFO: Query is run against NBIA "http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService" production service
	//Basic flow (Series level query). Test run with NBIA implementation (as oppose to with GridQuery class). 
	SAXBuilder builder = new SAXBuilder();
	@Test
	public void testQueryNBIA_1A() throws JDOMException, IOException {
		String gridServiceUrl= "http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService";
		List<String> expectedSeriesInstanceUID = new ArrayList<String>(); 
		try {
			NCIACoreServiceClient nciaClient = new NCIACoreServiceClient(gridServiceUrl);
			CQLQueryResults result = nciaClient.query(cqlQuery);
			if(result != null)	{
			    CQLQueryResultsIterator iter = new CQLQueryResultsIterator(result, true);
			    while (iter.hasNext()) {
				    String xmlSeries = (String)iter.next();
				    logger.debug(xmlSeries);
				    Document document;
				    Element root;
				    InputStream is = new ByteArrayInputStream(xmlSeries.getBytes());
				    document = builder.build(is);
					root = document.getRootElement();
					String seriesInstanceUID = root.getAttribute("instanceUID").getValue().trim();
					expectedSeriesInstanceUID.add(seriesInstanceUID);
					String modality = root.getAttribute("modality").getValue().trim();
					logger.debug("Result series instance uid is " + seriesInstanceUID + " modality: " + modality );
			     }
			}else{
			    logger.warn("No result found");
			}
		} catch (RemoteException e) {
			logger.error(e, e);
		} catch (MalformedURIException e) {
			logger.error(e, e);
		}
		assertEquals("Number of found Series is different than expected.", expectedSeriesInstanceUID.size(), 3);
		boolean resultsAsExpected = false;
		if(expectedSeriesInstanceUID.contains("1.3.6.1.4.1.9328.50.1.4767") 
				&& expectedSeriesInstanceUID.contains("1.3.6.1.4.1.9328.50.1.4718") 
				&& expectedSeriesInstanceUID.contains("1.3.6.1.4.1.9328.50.1.4722")){
			resultsAsExpected = true;
		} 
		assertTrue("Returned results are not as expected. ", resultsAsExpected);
	}
}
