/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
