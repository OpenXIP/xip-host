/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryListener;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.hostLogin.Login;
import edu.wustl.xipHost.iterator.Criteria;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridQuery implements Query {
	final static Logger logger = Logger.getLogger(GridQuery.class);
	CQLQuery cql;
	AttributeList criteria;
	GridLocation gridLocation;
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	QueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;
	GridManager gridMgr = GridManagerFactory.getInstance();
	GridUtil gridUtil;
	
	
	//setQuery() method is used in connection with TargetIteratorRunner
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) { 
		this.dicomCriteria = dicomCriteria; 
		this.aimCriteria = aimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject;
		gridUtil = gridMgr.getGridUtil();
		cql = null;
		//Convert dicomCriteria to CQL
		AttributeList convertedCriteria = new AttributeList();
		String[] characterSets = { "ISO_IR 100" };
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
		Iterator<Integer> iter = dicomCriteria.keySet().iterator();
		while(iter.hasNext()){
			Integer dicomTag = iter.next();
			String value = (String)dicomCriteria.get(dicomTag);
			try {
				switch(dicomTag){
					case Tag.PatientID:
						{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue(value); convertedCriteria.put(t,a); }
						break;
					case Tag.PatientName:
						{ AttributeTag t = TagFromName.PatientName; Attribute a = new PersonNameAttribute(t,specificCharacterSet); a.addValue(value); convertedCriteria.put(t,a); }
						break;
					case Tag.StudyInstanceUID:
						{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(value); convertedCriteria.put(t,a); }
						break;
					case Tag.SeriesInstanceUID:
						{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(value); convertedCriteria.put(t,a); }
						break;
				}
			}	catch (Exception e) {
				logger.error(e,	e);			
			}
		}
		CQLTargetName cqlTargetName = null;
		if(this.target.equals(QueryTarget.STUDY)){
			cqlTargetName = CQLTargetName.STUDY;
		} else if(this.target.equals(QueryTarget.SERIES)){
			cqlTargetName = CQLTargetName.SERIES;
		} else if(this.target.equals(QueryTarget.ITEM)){
			cqlTargetName = CQLTargetName.IMAGE;
		}	
		cql = gridUtil.convertToCQLStatement(convertedCriteria, cqlTargetName);
		/*try {
			System.err.println(ObjectSerializer.toString(cql, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e) {
			logger.error(e,  e);
		}*/
	}
	
	
	
	/**
	 * @param cql - CQL query statement
	 * @param gridLocation - GRID location e.g. caGRID or NBIA 
	 * @param previousSearchResult - null with first query call 
	 * @param queriedObject - null with first query call
	 */
	public GridQuery(CQLQuery cql, AttributeList criteria, GridLocation gridLocation, SearchResult previousSearchResult, Object queriedObject){
		this.cql = cql;
		this.criteria = criteria;
		this.gridLocation = gridLocation;
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject;
		if(logger.isDebugEnabled()){
			String strCQL = "";
			try {
				strCQL = ObjectSerializer.toString(cql, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
			} catch (SerializationException e) {			
				logger.error(e, e);
			}
			logger.debug("CQL query statement: " + "\r\n" + "\r\n" + strCQL);
			logger.debug("Grid location: " + gridLocation.toString());
			if(previousSearchResult == null){
				logger.debug("Previous search result: " + previousSearchResult);
			}else{
				logger.debug("Previous search result: " + previousSearchResult.toString());
			}
			if(queriedObject == null){
				logger.debug("Queried object: " + queriedObject);
			}else if(queriedObject instanceof Patient){
				Patient patient = Patient.class.cast(queriedObject);
				logger.debug("Queried object: " + patient.toString());
			}else if(queriedObject instanceof Study){
				Study study = Study.class.cast(queriedObject);
				logger.debug("Queried object: " + study.toString());
			}
		}		
	}
		
	SearchResult searchResult;
	public void run() {
		logger.info("Executing GRID query.");
		try {		 
			searchResult = query(cql, gridLocation, previousSearchResult, queriedObject);
			//Set original criteria on SearchResult.
			if(previousSearchResult == null){
				Map<Integer, Object> dicomCriteria = DicomUtil.convertToADDicomCriteria(criteria);
				Map<String, Object> aimCriteria = new HashMap<String, Object>();
				Criteria originalCriteria = new Criteria(dicomCriteria, aimCriteria);
				searchResult.setOriginalCriteria(originalCriteria);
			}
			logger.info("GRID query finished.");
			fireResultsAvailable();			
		} catch (MalformedURIException e) {
			logger.error(e, e);
			searchResult = null;
			//fireUpdateUI();
			notifyException(e.getMessage());
			return;
		} catch (RemoteException e) {
			logger.error(e, e);
			searchResult = null;
			notifyException(e.getMessage());
			//fireUpdateUI();
			return;
		} catch (ConnectException e) {
			logger.error(e, e);
			searchResult = null;
			//fireUpdateUI();
			notifyException(e.getMessage());
			return;
		}		
	}
	
	DataServiceClient dicomClient = null;
	NCIACoreServiceClient nbiaClient = null;

	/**
	 * Method used to perform progressive GRID query. 
	 * @param cql - CQL query statement
	 * @param gridLocation - GRID location e.g. caGRID or NBIA 
	 * @param previousSearchResult - null with first query call 
	 * @param queriedObject - null with first query call
	 * @return SearchResult object, that becomes previousSearchResult in subsequent query calls.
	 */
	public SearchResult query(CQLQuery query, GridLocation location, SearchResult previousSearchResult, Object queriedObject) throws MalformedURIException, RemoteException, ConnectException{		
		CQLQueryResultsIterator iter;		
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			dicomClient = new DataServiceClient(location.getAddress());			
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-5.0")){
			nbiaClient = new NCIACoreServiceClient(location.getAddress());
			Login login = HostConfigurator.getLogin();
			boolean isConnSecured = login.isConnectionSecured();
			if (isConnSecured == false || login.getGlobusCredential() == null) {
				nbiaClient = new NCIACoreServiceClient(location.getAddress());
			} else {
				nbiaClient = new NCIACoreServiceClient(location.getAddress(), login.getGlobusCredential());
				nbiaClient.setAnonymousPrefered(false);
			}
		}else{
			return null;
		}
		final CQLQuery fcqlq = query;		
		CQLQueryResults results = null;
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			results = dicomClient.query(fcqlq);
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-5.0")){
			results = nbiaClient.query(fcqlq);
		}						
        iter = new CQLQueryResultsIterator(results, true);              
        SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location, previousSearchResult, queriedObject);
        return result;			
	}		
	
	
	public SearchResult getSearchResult(){
		return searchResult;
	}
	
	QueryListener listener;
	@Override
	public void addQueryListener(QueryListener l) {
		listener = l; 
		
	}
	
	void fireResultsAvailable(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}
	void notifyException(String message){         		
        listener.notifyException(message);
	}
	
	
}
