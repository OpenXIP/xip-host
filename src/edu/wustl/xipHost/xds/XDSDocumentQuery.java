/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.Map;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSDocumentQuery implements Query {
	XDSManager xdsMgr;
	String [] patientID;
	XDSRegistryLocation xdsRegistry;

	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	QueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;

	public XDSDocumentQuery(){
		xdsMgr = XDSManagerFactory.getInstance();
	}
	
	public XDSDocumentQuery(String [] patientID, XDSRegistryLocation xdsRegistry){
		this.patientID = patientID;
		this.xdsRegistry = xdsRegistry;
		xdsMgr = XDSManagerFactory.getInstance();
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.dicomCriteria = dicomCriteria; 
		this.aimCriteria = aimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject; 
		
		if (queriedObject instanceof Patient){
			Patient patient = (Patient) queriedObject;
			patientID[0] = patient.getPatientID();
		}
	}
	
	SearchResult searchResult;
	public void run() {			
		searchResult = xdsMgr.queryDocuments(patientID, xdsRegistry);				
		fireUpdateUI();
	}

	public SearchResult getxsdQueryResponse(){
		return searchResult;
	}
	 
    void fireUpdateUI(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}

	QueryListener listener;
    @Override
	public void addQueryListener(QueryListener l) {
    	 listener = l;  
	}

	@Override
	public SearchResult getSearchResult() {
		return searchResult;
	}		
}
