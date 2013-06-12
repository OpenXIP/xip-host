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

package edu.wustl.xipHost.dicom;

import java.util.Map;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
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

	QueryListener listener; 
	@Override
	public void addQueryListener(QueryListener l) {
		this.listener = l;
	}
}
