/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.Map;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSDocumentQuery implements Query {
	XDSManager xdsMgr;
	String [] patientID;
	
	public XDSDocumentQuery(){
		
	}
	
	public XDSDocumentQuery(String [] patientID){
		this.patientID = patientID;
		xdsMgr = XDSManagerFactory.getInstance();
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		
	}
	
	SearchResult searchResult;
	public void run() {			
		searchResult = xdsMgr.queryDocuments(patientID);				
		fireUpdateUI();
	}

	public SearchResult getxsdQueryResponse(){
		return searchResult;
	}
	 
    void fireUpdateUI(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}

	DataAccessListener listener;
    @Override
	public void addDataAccessListener(DataAccessListener l) {
    	 listener = l;  
	}

	@Override
	public SearchResult getSearchResult() {
		return searchResult;
	}		
}
