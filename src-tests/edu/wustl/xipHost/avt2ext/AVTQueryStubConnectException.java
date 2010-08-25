package edu.wustl.xipHost.avt2ext;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class AVTQueryStubConnectException implements Query, Runnable {
	final static Logger logger = Logger.getLogger(AVTQueryStub.class);
	SearchResultSetup resultSetup = new SearchResultSetup();
	SearchResult fullSearchResult;
	Map<Integer, Object> adDicomCriteria;
	Map<String, Object> adAimCriteria;
	ADQueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;
	
	public AVTQueryStubConnectException (Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, ADQueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
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
	
	
	AVTListener listener;
	@Override
	public void addAVTListener(AVTListener l) {
		listener = l;
		
	}

	@Override
	public void setAVTQuery(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, ADQueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.adDicomCriteria = adDicomCriteria; 
		this.adAimCriteria = adAimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject; 
	}
	
	void fireResultsAvailable(SearchResult searchResult){
		AVTSearchEvent event = new AVTSearchEvent(searchResult);         		
        listener.searchResultsAvailable(event);
	}
	
	void notifyException(String message){         		
        listener.notifyException(message);
	}

}
