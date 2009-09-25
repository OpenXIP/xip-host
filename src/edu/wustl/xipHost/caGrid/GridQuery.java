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
import edu.northwestern.radiology.aim.Series;
import edu.northwestern.radiology.aim.Study;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.dataModel.SearchResult;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.ncia.domain.Patient;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridQuery implements Runnable {
	CQLQuery cqlQuery;
	GridLocation gridLoc;
	
	public GridQuery(CQLQuery cql, GridLocation gridLocation){
		cqlQuery = cql;
		gridLoc = gridLocation;
	}
		
	SearchResult searchResult;
	public void run() {
		try {
			try {
				System.err.println(ObjectSerializer.toString(cqlQuery, 
						new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
			} catch (SerializationException e) {			
				e.printStackTrace();
			}		 
			searchResult = query(cqlQuery, gridLoc);
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
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#query(gov.nih.nci.cagrid.cqlquery.CQLQuery, edu.wustl.xipHost.caGrid.GridLocation)
	 */
	public SearchResult query(CQLQuery query, GridLocation location) throws MalformedURIException, RemoteException, ConnectException{		
		DataServiceClient dicomClient = null;
		NCIACoreServiceClient nciaClient = null;
		CQLQueryResultsIterator iter;		
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			dicomClient = new DataServiceClient(location.getAddress());			
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			nciaClient = new NCIACoreServiceClient(location.getAddress());
		}else{
			return null;
		}
		final CQLQuery fcqlq = query;		
		CQLQueryResults results = null;
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			results = dicomClient.query(fcqlq);
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			results = nciaClient.query(fcqlq);
		}						
        iter = new CQLQueryResultsIterator(results);        
        /*
        int ii = 0;
		while (iter.hasNext()) {
			System.out.println(ii++);
			java.lang.Object obj = iter.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}				
			if(obj instanceof gov.nih.nci.ncia.domain.Patient){
				gov.nih.nci.ncia.domain.Patient patient = gov.nih.nci.ncia.domain.Patient.class.cast(obj);
				System.out.println("Patient Id: " + patient.getPatientId());							
				System.out.println("Patient name: " + patient.getPatientName());
			}else if(obj instanceof gov.nih.nci.ncia.domain.Study){
				//gov.nih.nci.ncia.domain.Study study = gov.nih.nci.ncia.domain.Study.class.cast(obj);
				//System.out.println("studyInstanceUID: " + study.getStudyInstanceUID());
				//System.out.println("Study description: " + study.getStudyDescription());
			}else if(obj instanceof gov.nih.nci.ncia.domain.Series){
				//gov.nih.nci.ncia.domain.Series series = gov.nih.nci.ncia.domain.Series.class.cast(obj);			
				//System.out.println("SeriesInstanceUID: " + series.getSeriesInstanceUID());				
			}			
		} 
		*/               
        //SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location);	        
        SearchResult result = GridUtil.convertNCIACQLQueryResultsIteratorToSearchResult(iter, location);
        return result;			
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
