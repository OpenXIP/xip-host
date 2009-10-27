/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.net.ConnectException;
import java.rmi.RemoteException;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.caGrid.GridLocation;
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
public class GridQuery implements Runnable {
	final static Logger logger = Logger.getLogger(GridQuery.class);
	CQLQuery cql;
	GridLocation gridLocation;
	SearchResult previousSearchResult;
	Object queriedObject;
	
	/**
	 * @param cql - CQL query statement
	 * @param gridLocation - GRID location e.g. caGRID or NBIA 
	 * @param previousSearchResult - null with first query call 
	 * @param queriedObject - null with first query call
	 */
	public GridQuery(CQLQuery cql, GridLocation gridLocation, SearchResult previousSearchResult, Object queriedObject){
		this.cql = cql;
		this.gridLocation = gridLocation;
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject;
	}
		
	SearchResult searchResult;
	public void run() {
		logger.info("Executing GRID query.");
		try {		 
			searchResult = query(cql, gridLocation, previousSearchResult, queriedObject);
			logger.info("GRID query finished.");
			fireUpdateUI();			
		} catch (MalformedURIException e) {
			logger.error(e, e);
			searchResult = null;
			fireUpdateUI();
			return;
		} catch (RemoteException e) {
			logger.error(e, e);
			searchResult = null;
			fireUpdateUI();
			return;
		} catch (ConnectException e) {
			logger.error(e, e);
			searchResult = null;
			fireUpdateUI();
			return;
		}		
	}
	
	DataServiceClient dicomClient = null;
	NCIACoreServiceClient nciaClient = null;

	/**
	 * Method used to perform progressive GRID query. 
	 * @param cql - CQL query statement
	 * @param gridLocation - GRID location e.g. caGRID or NBIA 
	 * @param previousSearchResult - null with first query call 
	 * @param queriedObject - null with first query call
	 * @return SearchResult object, that becomes previousSearchResult in subsequent query calls.
	 */
	public SearchResult query(CQLQuery query, GridLocation location, SearchResult previousSearchResult, Object queriedObject) throws MalformedURIException, RemoteException, ConnectException{		
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
        //SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location);	                
        SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location, previousSearchResult, queriedObject);
        return result;			
	}		
	
	
	public SearchResult getSearchResult(){
		return searchResult;
	}
	
	GridSearchListener listener;
    public void addGridSearchListener(GridSearchListener l) {        
        listener = l;          
    }
	void fireUpdateUI(){
		GridSearchEvent event = new GridSearchEvent(this);         		
        listener.searchResultAvailable(event);
	}	
}
