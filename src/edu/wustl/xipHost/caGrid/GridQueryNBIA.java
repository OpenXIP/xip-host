/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.net.ConnectException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import edu.wustl.xipHost.dataModel.SearchResult;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridQueryNBIA extends GridQuery implements Runnable {
	SearchResult initialSearchResult;
	Object selectedObject;
	
	public GridQueryNBIA(CQLQuery cql, GridLocation gridLocation, SearchResult initialSearchResult, Object selectedObject){
		super(cql, gridLocation);
		this.initialSearchResult = initialSearchResult;
		this.selectedObject = selectedObject;
	}
	
	SearchResult searchResult;
	public void run() {
		try {
			try {
				System.err.println(ObjectSerializer.toString(cqlQuery, 
						new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
			} catch (SerializationException e) {			
				e.printStackTrace();
			}		 
			searchResult = query(cqlQuery, gridLoc, initialSearchResult, selectedObject);
			fireUpdateUI();			
		} catch (MalformedURIException e) {
			searchResult = null;
			fireUpdateUI();
			return;
		} catch (RemoteException e) {
			searchResult = null;
			fireUpdateUI();
			return;
		} catch (ConnectException e) {
			searchResult = null;
			fireUpdateUI();
			return;
		}		
	}
	
	public SearchResult getSearchResult(){
		return searchResult;
	}
	
	public SearchResult query(CQLQuery query, GridLocation location, SearchResult initialSearchResult, Object selectedObject) throws MalformedURIException, RemoteException, ConnectException{		
		/*try {
			System.err.println(ObjectSerializer.toString(cqlQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e) {			
			e.printStackTrace();
		}*/	
		DataServiceClient dicomClient = null;
		NCIACoreServiceClient nciaClient = null;
		CQLQueryResultsIterator iter;		
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			dicomClient = new DataServiceClient(location.getAddress());			
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			nciaClient = new NCIACoreServiceClient(location.getAddress());
		}else{
			return null;
		}
		final CQLQuery fcqlq = query;		
		CQLQueryResults results = null;
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			results = dicomClient.query(fcqlq);
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			results = nciaClient.query(fcqlq);
		}						
        iter = new CQLQueryResultsIterator(results);        
        SearchResult result = GridUtil.convertNCIACQLQueryResultsIteratorToSearchResult(iter, location, initialSearchResult, selectedObject);
        return result;			
	}		
}
