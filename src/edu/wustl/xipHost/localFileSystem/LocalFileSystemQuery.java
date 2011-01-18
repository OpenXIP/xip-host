/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

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
public class LocalFileSystemQuery implements Query {

	/**
	 * 
	 */
	public LocalFileSystemQuery(SearchResult selectedDataSearchResult) {
		result = selectedDataSearchResult;
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		
	}

	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;		
	}

	@Override
	public SearchResult getSearchResult() {
		return result;
	}

	SearchResult result;
	@Override
	public void run() {
		fireResultsAvailable();		
	}
	
	void fireResultsAvailable(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}

}
