/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.net.ConnectException;
import java.rmi.RemoteException;
import org.apache.axis.types.URI.MalformedURIException;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.dataModel.SearchResult;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridQuery implements Runnable {
	GridManager gridMgr;
	CQLQuery cqlQuery;
	GridLocation gridLoc;
	
	public GridQuery(CQLQuery cql, GridLocation gridLocation){
		cqlQuery = cql;
		gridLoc = gridLocation;
		gridMgr = GridManagerFactory.getInstance();
	}
		
	SearchResult searchResult;
	public void run() {
		try {
			/*try {
				System.err.println(ObjectSerializer.toString(cqlQuery, 
						new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
			} catch (SerializationException e) {			
				e.printStackTrace();
			}		
			System.out.println(gridLoc.getAddress()); */
			searchResult = gridMgr.query(cqlQuery, gridLoc);
			fireUpdateUI();
			//System.out.println("Grid Query number of studies: " + dicomSearchResult.getStudies().size());			
		} catch (MalformedURIException e) {
			searchResult = null;
			fireUpdateUI();
			return;
		} catch (RemoteException e) {
			searchResult = null;
			fireUpdateUI();
			return;
		} catch (ConnectException e) {
			searchResult = null;
			fireUpdateUI();
			return;
		}		
	}
	
	public SearchResult getSearchResult(){
		return searchResult;
	}
	
	GridSearchListener listener;
    public void addGridSearchListener(GridSearchListener l) {        
        listener = l;          
    }
	void fireUpdateUI(){
		GridSearchEvent event = new GridSearchEvent(this);         		
        listener.searchResultAvailable(event);
	}	

}
