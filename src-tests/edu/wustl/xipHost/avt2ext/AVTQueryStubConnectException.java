package edu.wustl.xipHost.avt2ext;

import java.util.Map;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.SearchResult;

public class AVTQueryStubConnectException implements Query {
	final static Logger logger = Logger.getLogger(AVTQueryStubConnectException.class);
	SearchResultSetup resultSetup = new SearchResultSetup();
	SearchResult fullSearchResult;
	Map<Integer, Object> adDicomCriteria;
	Map<String, Object> adAimCriteria;
	QueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;
	
	public AVTQueryStubConnectException (Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		fullSearchResult = resultSetup.getSearchResult();
	}
	
	SearchResult result;
	public void run(){
		try{
			throw new java.net.ConnectException("Unable to connect");
		} catch (Exception e){
			notifyException(e.getMessage());
		}
	}
	
	
	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;
		
	}

	@Override
	public void setQuery(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.adDicomCriteria = adDicomCriteria; 
		this.adAimCriteria = adAimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject; 
	}
	
	void fireResultsAvailable(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}
	
	void notifyException(String message){         		
        listener.notifyException(message);
	}

	@Override
	public SearchResult getSearchResult() {
		return result;
	}

}
