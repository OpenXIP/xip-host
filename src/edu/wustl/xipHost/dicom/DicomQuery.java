/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.util.Map;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomQuery implements Query {
	DicomManager dicomMgr;
	AttributeList criteriaList;
	PacsLocation pacsLoc;	 
	
	public DicomQuery(){
		
	}
	
	public DicomQuery(AttributeList criteriaList, PacsLocation location){
		this.criteriaList = criteriaList;
		this.pacsLoc = location;
		dicomMgr = DicomManagerFactory.getInstance();
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		// TODO Auto-generated method stub
		
	}
	
	SearchResult result;
	public void run(){						
			if(criteriaList == null){return;}					
			if(criteriaList != null && pacsLoc != null){																
				//send request only once
				int i = 0;
				while(!stop && i < 1){
					result = dicomMgr.query(criteriaList, pacsLoc);
					isCompleted = true;
					i++;
				}
				if(stop){					
					result = null;
					fireQueryResultsAvailable();
					return;
				}else{
					fireQueryResultsAvailable();					
				}				
			}else{									
				return;
			}		
	}
	
	boolean stop = false;
	public void requestStop(){
		stop = true;
	}
	boolean isCompleted = false;
	public boolean isQueryCompleted(){
		return isCompleted;
	}
	
	public String getLocationName(){
		return pacsLoc.getShortName();
	}
	
	public SearchResult getSearchResult(){
		return result;
	}
	
	void fireQueryResultsAvailable(){
		QueryEvent event = new QueryEvent(this);
		listener.queryResultsAvailable(event);
	}

	DataAccessListener listener; 
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		this.listener = l;
	}
}
