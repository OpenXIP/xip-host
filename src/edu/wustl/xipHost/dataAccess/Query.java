package edu.wustl.xipHost.dataAccess;

import java.util.Map;
import edu.wustl.xipHost.dataModel.SearchResult;

public interface Query extends Runnable {
	public void addQueryListener(QueryListener l);
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject);
	public SearchResult getSearchResult();
}