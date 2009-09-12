/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.nci;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.utils.ZipEntryInputStream;
import gov.nih.nci.ncia.domain.Series;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class QueryNCIATest extends TestCase {
	
	CQLQuery cqlQuery = null;
	String serviceURL = "http://imaging-stage.nci.nih.gov/wsrf/services/cagrid/NCIACoreService";
	NCIACoreServiceClient client;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {		
		super.setUp();		
		client = new NCIACoreServiceClient(serviceURL); 
		/* Create CQL for grid query */
		HashMap<String, String> queryHashMap = new HashMap<String, String>();																		
		queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());				
		queryHashMap.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", "1.3.6.1.4.1.9328.50.1.11018");		
		HashmapToCQLQuery h2cql;
		try {
			h2cql = new HashmapToCQLQuery(new ModelMap());			
			try {
				cqlQuery = h2cql.makeCQLQuery(queryHashMap);
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
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// - basic flow. 
	//CQL statement and GridLocation are valid and network is on.
	public void testQueryNCIA1A() throws QueryProcessingExceptionType, MalformedQueryExceptionType, RemoteException  {										              				
		CQLQueryResults result = client.query(cqlQuery);
		CQLQueryResultsIterator iter = new CQLQueryResultsIterator(result);                
        int i = 0;
		while (iter.hasNext()) {
			System.out.println(i++);
			java.lang.Object obj = iter.next();
			if (obj == null) {				
				continue;
			}
			Series series = Series.class.cast(obj);						
			System.out.println("Series UID: " + series.getSeriesInstanceUID());
			System.out.println("Series UID: " + series.getSeriesDescription());

		}				
	}
	
	
	public void testRetrieveNCIA1A() throws Exception{
		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		String seriesInstanceUID = "1.3.6.1.4.1.9328.50.1.11018";
		//String localDownloadLocation = System.getProperty("java.io.tmpdir") + File.separator + "NCIAGridClientDownload";
		String localDownloadLocation = "C:/WUSTL/Tmp/TestNCIA";
		
		org.cagrid.transfer.context.stubs.types.TransferServiceContextReference tscr = client.retrieveDicomDataBySeriesUID(seriesInstanceUID);			
		tclient = new TransferServiceContextClient(tscr.getEndpointReference());
		istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
		if(istream == null){
			System.out.println("istrea is null");
			return;
		}
		ZipInputStream zis = new ZipInputStream(istream);
        ZipEntryInputStream zeis = null;
        BufferedInputStream bis = null;
        int ii = 1;
        while(true) {
              try {
                    zeis = new ZipEntryInputStream(zis);
              } catch (EOFException e) {
                    break;
              } catch (IOException e) {
				fail("IOException thrown when recieving the zip stream" + e);
            	  System.out.println("IOException " + e);
			}
              String unzzipedFile = localDownloadLocation;
              System.out.println(ii++ + " filename: " + zeis.getName());
              bis = new BufferedInputStream(zeis);
              byte[] data = new byte[8192];
              int bytesRead = 0;
              try {
            	  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile + File.separator + zeis.getName()));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0)  {
						bos.write(data, 0, bytesRead);
				}
				bos.flush();
		        bos.close();
			} catch (IOException e) {
				fail("IOException thrown when reading the zip stream " + e);
				System.out.println("IOException " + e);
			}
        }
        try {
			zis.close();
		} catch (IOException e) {
			fail("IOException thrown when closing the zip stream " + e);
			System.out.println("IOException " +e);
		}
        try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception thrown when closing the transer context " + e);
		}		
	}
	
	
}
