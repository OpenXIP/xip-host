/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomQuery implements Runnable {
	DicomManager dicomMgr;
	AttributeList criteriaList;
	PacsLocation pacsLoc;	 
	
	public DicomQuery(AttributeList criteriaList, PacsLocation location){
		this.criteriaList = criteriaList;
		this.pacsLoc = location;
		dicomMgr = DicomManagerFactory.getInstance();
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
					fireUpdateUI();
					return;
				}else{
					fireUpdateUI();					
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
	
	SearchListener listener; 
	public void addSearchListener(SearchListener l){
		 this.listener = l;
	}
	
	void fireUpdateUI(){
		SearchEvent event = new SearchEvent(this);
		listener.searchResultAvailable(event);
	}
}
