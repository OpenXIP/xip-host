package edu.wustl.xipHost.caGrid;

import java.rmi.RemoteException;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.ncia.domain.Series;

import org.apache.axis.types.URI.MalformedURIException;
import org.junit.Test;

public class NBIA51Test {

	@Test
	public void test() throws MalformedURIException, RemoteException {
		String gridServiceUrl= "http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService";

		NCIACoreServiceClient nciaClient = new NCIACoreServiceClient(gridServiceUrl);
		CQLQuery cqlQuery = null;
		CQLQueryResults result = nciaClient.query(cqlQuery);

		if(result != null)	{
		     CQLQueryResultsIterator iter = new CQLQueryResultsIterator(result);
		     while (iter.hasNext()) {
		          Series obj = (Series)iter.next();
		          if (obj == null) {
			     System.out.println("something not right.  obj is null" );
			     continue;
		          } else {
		        	  //System.out.println("Result series instance uid is " + obj.getSeriesInstanceUID() + " modality: " + obj.getModality());
		          }
		     }
		}else{
		    //System.out.println("No result found for " + filename);
		}
	}

}
