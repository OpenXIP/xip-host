/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import com.pixelmed.database.StudySeriesInstanceModel;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.query.QueryResponseGenerator;
import com.pixelmed.query.QueryResponseGeneratorFactory;
import com.pixelmed.query.RetrieveResponseGenerator;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;
import com.pixelmed.query.StudyRootQueryInformationModel;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.iterator.TargetElement;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomRetrieve implements Retrieve {
	final static Logger logger = Logger.getLogger(DicomRetrieve.class);	
	DicomManager dicomMgr;
	PacsLocation called;
	PacsLocation calling;
	//TargetElement targetElement;
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	List<ObjectDescriptor> objectDescriptors;
	File importDir;
	RetrieveTarget retrieveTarget;
	//DataSource dataSource;
	
	public DicomRetrieve(){
		
	}
	
	public DicomRetrieve(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, File importDir, RetrieveTarget retrieveTarget, DataSource dataSource){
		this.dicomCriteria = dicomCriteria;
		this.aimCriteria = aimCriteria;
		this.importDir = importDir;
		this.retrieveTarget = retrieveTarget;
		this.called = (PacsLocation) dataSource;
		dicomMgr = DicomManagerFactory.getInstance();
		calling = dicomMgr.getDefaultCallingPacsLocation();
	}
	
	/*
	public DicomRetrieve(AttributeList criteria, PacsLocation called, PacsLocation calling){
		this.criteria = criteria;
		this.called = called;
		this.calling = calling;
		dicomMgr = DicomManagerFactory.getInstance();
	}*/
	
	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria) {
		this.dicomCriteria = dicomCriteria;
		this.aimCriteria = aimCriteria;
		dicomMgr = DicomManagerFactory.getInstance();
		calling = dicomMgr.getDefaultCallingPacsLocation();
	}

	@Override
	public void setCriteria(Object criteria) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		this.objectDescriptors = objectDescriptors;		
	}
	
	@Override
	public void setImportDir(File importDir) {
		this.importDir = importDir;	
	}
	
	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		this.retrieveTarget = retrieveTarget;		
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.called = (PacsLocation) dataSource;
	}
	
	@Override
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) {
		//this.targetElement = targetElement;
		this.retrieveTarget = retrieveTarget;
		//TODO to be removed
	}

	
	public void run() {
		logger.info("Executing DICOM retrieve.");				
		Map<String, ObjectLocator> objectLocs = retrieve(dicomCriteria);
		fireResultsAvailable(objectLocs);						
	}
	
	// TODO Move crietria inside the retrieve() method after all retrieve() removed
	AttributeList criteria;
	Map<String, ObjectLocator> retrieve(Map<Integer, Object> dicomCriteria){
		String hostName = called.getAddress();
		int port = called.getPort();
		String calledAETitle = called.getAETitle();
		String callingAETitle = calling.getAETitle();
		if(called != null && calling != null){	
			if(logger.isDebugEnabled()){			
		    	logger.debug("Host name: " + hostName);	    	
		    	logger.debug("Port: " + port);	    
		    	logger.debug("CalledAETitle: " + calledAETitle);	    	
		    	logger.debug("CallingAETitle: " + callingAETitle);
		    	logger.debug("DBFileName: " + dicomMgr.getDBFileName());
			}
			objectLocators = new HashMap<String, ObjectLocator>();	
			if(retrieveTarget == RetrieveTarget.DICOM_AND_AIM){
				criteria = DicomUtil.convertToPixelmedDicomCriteria(dicomCriteria);			    	   
				logger.debug("DICOM retrieve criteria:");
				DicomDictionary dictionary = AttributeList.getDictionary();
			    Iterator<?> iter = dictionary.getTagIterator();        
			    String strAtt = null;
			    String attValue = null;
			    while(iter.hasNext()){
			    	AttributeTag attTag  = (AttributeTag)iter.next();					    	
			    	strAtt = attTag.toString();									
					attValue = Attribute.getSingleStringValueOrEmptyString(criteria, attTag);			
					if(!attValue.isEmpty()){
						logger.debug(strAtt + " " + attValue);				
					}
			    }	    
			    try {
			    	StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);
					mModel.performHierarchicalMoveTo(criteria, calling.getAETitle());										        	
		        	logger.debug("Local server is: " + dicomMgr.getDBFileName());
					StudySeriesInstanceModel mDatabase = new StudySeriesInstanceModel(dicomMgr.getDBFileName());			
		    		//RetrieveResposeGeneratorFactory provides access to files URLs stored in hsqldb    		    	
		    		RetrieveResponseGeneratorFactory mRetrieveResponseGeneratorFactory = mDatabase.getRetrieveResponseGeneratorFactory(0);
		    		QueryResponseGeneratorFactory mQueryResponseGeneratorFactory = mDatabase.getQueryResponseGeneratorFactory(0);			
		    		RetrieveResponseGenerator mRetrieveResponseGenerator = mRetrieveResponseGeneratorFactory.newInstance();
					QueryResponseGenerator mQueryResponseGenerator = mQueryResponseGeneratorFactory.newInstance();						
					mQueryResponseGenerator.performQuery("1.2.840.10008.5.1.4.1.2.2.1", criteria, true);	// Study Root						
					AttributeList localResults = mQueryResponseGenerator.next();			
					int i = 0;
					while(localResults != null) {							 					
						mRetrieveResponseGenerator.performRetrieve("1.2.840.10008.5.1.4.1.2.2.3", localResults, true);	// Study Root		
						SetOfDicomFiles dicomFiles = mRetrieveResponseGenerator.getDicomFiles();
						Iterator<?> it = dicomFiles.iterator();							
						while (it.hasNext() ) {
							SetOfDicomFiles.DicomFile x  = (SetOfDicomFiles.DicomFile)it.next();
							logger.debug("Dicom file: " + x.getFileName());			    														
							String fileURI = (new File(x.getFileName()).toURI()).toURL().toExternalForm();
							ObjectLocator objLoc = new ObjectLocator();														
							Uuid itemUUID = objectDescriptors.get(i).getUuid();
							objLoc.setUuid(itemUUID);				
							objLoc.setUri(fileURI); 
							objectLocators.put(itemUUID.getUuid(), objLoc);
							i++;
						}		
						localResults = mQueryResponseGenerator.next();
					}
			    } catch (IOException e) {
					logger.error(e, e);
					return null;
				} catch (DicomException e) {
					logger.error(e, e);
					return null;
				} catch (DicomNetworkException e) {
					logger.error(e, e);
					return null;
				}	
			}
		}	
		return objectLocators;
	}
	
	
	/**
	 * 1. Method performed hierarchical move from remote location to calling location first.
	 * 2. To get retrieved files' URIs method performs query but on the local server (calling)
	 */
	Map<String, ObjectLocator> objectLocators;
	void retrieve(TargetElement targetElement){		
		objectLocators = new HashMap<String, ObjectLocator>();
		String hostName = called.getAddress();
		int port = called.getPort();
		String calledAETitle = called.getAETitle();
		String callingAETitle = calling.getAETitle();
		if(called != null && calling != null){	
			if(logger.isDebugEnabled()){			
		    	logger.debug("Host name: " + hostName);	    	
		    	logger.debug("Port: " + port);	    
		    	logger.debug("CalledAETitle: " + calledAETitle);	    	
		    	logger.debug("CallingAETitle: " + callingAETitle);
		    	logger.debug("DBFileName: " + dicomMgr.getDBFileName());
			}
			SearchResult subSearchResult = targetElement.getSubSearchResult();
			List<Patient> patients = subSearchResult.getPatients();
			for(Patient patient : patients){
				try{
					List<Study> studies = patient.getStudies();
					for(Study study : studies){						
						{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(study.getStudyInstanceUID()); criteria.put(t,a); }
						{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); criteria.put(t,a); }
						List<Series> series = study.getSeries();
						for(Series oneSeries : series){												
							{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(oneSeries.getSeriesInstanceUID()); criteria.put(t,a); }							
							List<ObjectDescriptor> objectDescriptors = new ArrayList<ObjectDescriptor>();
							List<Item> items = oneSeries.getItems();
							  try {	   	
								if(oneSeries.containsSubsetOfItems()){
									for(Item item : items){
										objectDescriptors.add(item.getObjectDescriptor());
										String itemSOPInstanceUID = item.getItemID();
										{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(itemSOPInstanceUID); criteria.put(t,a); }			
										logger.debug("DICOM retrieve criteria:");
										DicomDictionary dictionary = AttributeList.getDictionary();
									    Iterator<?> iter = dictionary.getTagIterator();        
									    String strAtt = null;
									    String attValue = null;
									    while(iter.hasNext()){
									    	AttributeTag attTag  = (AttributeTag)iter.next();					    	
									    	strAtt = attTag.toString();									
											attValue = Attribute.getSingleStringValueOrEmptyString(criteria, attTag);			
											if(!attValue.isEmpty()){
												logger.debug(strAtt + " " + attValue);				
											}
									    }	    
							        	StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);	        		        								        	
							        	mModel.performHierarchicalMoveTo(criteria, calling.getAETitle());							        	
							        	//System.out.println("Local server is: " + getDBFileName());
							        	logger.debug("Local server is: " + dicomMgr.getDBFileName());
										StudySeriesInstanceModel mDatabase = new StudySeriesInstanceModel(dicomMgr.getDBFileName());			
							    		//RetrieveResposeGeneratorFactory provides access to files URLs stored in hsqldb    		    	
							    		RetrieveResponseGeneratorFactory mRetrieveResponseGeneratorFactory = mDatabase.getRetrieveResponseGeneratorFactory(0);
							    		QueryResponseGeneratorFactory mQueryResponseGeneratorFactory = mDatabase.getQueryResponseGeneratorFactory(0);			
							    		RetrieveResponseGenerator mRetrieveResponseGenerator = mRetrieveResponseGeneratorFactory.newInstance();
										QueryResponseGenerator mQueryResponseGenerator = mQueryResponseGeneratorFactory.newInstance();						
										mQueryResponseGenerator.performQuery("1.2.840.10008.5.1.4.1.2.2.1", criteria, true);	// Study Root						
										AttributeList localResults = mQueryResponseGenerator.next();			
										List<String> retrievedFilesURIs = new ArrayList<String>();
										while(localResults != null) {							 					
											mRetrieveResponseGenerator.performRetrieve("1.2.840.10008.5.1.4.1.2.2.3", localResults, true);	// Study Root		
											SetOfDicomFiles dicomFiles = mRetrieveResponseGenerator.getDicomFiles();
											Iterator<?> it = dicomFiles.iterator();			  						
											while (it.hasNext() ) {
												SetOfDicomFiles.DicomFile x  = (SetOfDicomFiles.DicomFile)it.next();
												logger.debug("Dicom file: " + x.getFileName());			    														
												String fileURI = (new File(x.getFileName()).toURI()).toURL().toExternalForm();
												ObjectLocator objLoc = new ObjectLocator();														
												Uuid itemUUID = item.getObjectDescriptor().getUuid();
												objLoc.setUuid(itemUUID);				
												objLoc.setUri(fileURI); 
												item.setObjectLocator(objLoc);
												objectLocators.put(itemUUID.getUuid(), objLoc);
												retrievedFilesURIs.add(fileURI);							
											}		
											localResults = mQueryResponseGenerator.next();
										}
										//Reset value of SOPInstanceUID in criteria
										{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.removeValues(); criteria.put(t,a); }
									}
								} else {
									StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);	        		        	
						        	mModel.performHierarchicalMoveTo(criteria, calling.getAETitle());						        	
						        	//System.out.println("Local server is: " + getDBFileName());
									StudySeriesInstanceModel mDatabase = new StudySeriesInstanceModel(dicomMgr.getDBFileName());			
						    		//RetrieveResposeGeneratorFactory provides access to files URLs stored in hsqldb    		    	
						    		RetrieveResponseGeneratorFactory mRetrieveResponseGeneratorFactory = mDatabase.getRetrieveResponseGeneratorFactory(0);
						    		QueryResponseGeneratorFactory mQueryResponseGeneratorFactory = mDatabase.getQueryResponseGeneratorFactory(0);			
						    		RetrieveResponseGenerator mRetrieveResponseGenerator = mRetrieveResponseGeneratorFactory.newInstance();
									QueryResponseGenerator mQueryResponseGenerator = mQueryResponseGeneratorFactory.newInstance();						
									mQueryResponseGenerator.performQuery("1.2.840.10008.5.1.4.1.2.2.1", criteria, true);	// Study Root						
									AttributeList localResults = mQueryResponseGenerator.next();			
									List<String> retrievedFilesURIs = new ArrayList<String>();
									while(localResults != null) {							 					
										mRetrieveResponseGenerator.performRetrieve("1.2.840.10008.5.1.4.1.2.2.3", localResults, true);	// Study Root		
										SetOfDicomFiles dicomFiles = mRetrieveResponseGenerator.getDicomFiles();
										Iterator<?> it = dicomFiles.iterator();			  						
										while (it.hasNext() ) {
											SetOfDicomFiles.DicomFile x  = (SetOfDicomFiles.DicomFile)it.next();
											logger.debug("Dicom file: " + x.getFileName());			    														
											String fileURI = (new File(x.getFileName()).toURI()).toURL().toExternalForm();										
											retrievedFilesURIs.add(fileURI);							
										}		
										localResults = mQueryResponseGenerator.next();
									}									
									List<Patient> searchResultPatients = subSearchResult.getPatients();
									for(int i = 0; i < searchResultPatients.size(); i++){
										Patient searchResultPatient = searchResultPatients.get(i);						
										List<Study> searchResultStudies = searchResultPatient.getStudies();
										for(int j = 0; j < searchResultStudies.size(); j++){
											Study searchResultStudy = searchResultStudies.get(j);								
											List<Series> searchResultSeries = searchResultStudy.getSeries();
											for(int k = 0; k < searchResultSeries.size(); k++){
												Series updateSeries = searchResultSeries.get(k);
												List<Item> seriesItems = updateSeries.getItems();
												if(seriesItems.size() == retrievedFilesURIs.size()){
													for(int m = 0; m < seriesItems.size(); m++){
														Item updateItem = seriesItems.get(m);
														ObjectLocator objLoc = new ObjectLocator();														
														Uuid itemUUID = updateItem.getObjectDescriptor().getUuid();
														objLoc.setUuid(itemUUID);				
														objLoc.setUri(retrievedFilesURIs.get(m)); 
														updateItem.setObjectLocator(objLoc);
														objectLocators.put(itemUUID.getUuid(), objLoc);
													}
												} else {
													logger.warn("Number of expected DICOM objects: " + seriesItems.size() + ". Actual number: " + retrievedFilesURIs.size() + ".");									
												}
												
											}
										}
									}																								
								}								
						  	} catch (IOException e) {
								logger.error(e, e);
							} catch (DicomException e) {
								logger.error(e, e);
							} catch (DicomNetworkException e) {						
								logger.error(e, e);
							}
							//Reset DICOM SERIES level values in criteria
							{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.removeValues(); criteria.put(t,a); }
						}
						//Reset DICOM STUDY level values in criteria
						{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.removeValues(); criteria.put(t,a); }
						{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.removeValues(); criteria.put(t,a); }
					}
				} catch (Exception e) {
					logger.error(e, e);			
				}
			}
			
			/*
			List<SubElement> subElements = targetElement.getSubElements();
			for(SubElement subElement : subElements){
				Map<Integer, Object> dicomCriteria = subElement.getCriteria().getDICOMCriteria();
				Object valueStudyInstanceUID = dicomCriteria.get(new Integer(2097165));	//studyInstanceUID
				Object valueSeriesInstanceUID = dicomCriteria.get(new Integer(2097166));	//seriesInstanceUID
				try{
					{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(valueStudyInstanceUID.toString()); criteria.put(t,a); }
					{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(valueSeriesInstanceUID.toString()); criteria.put(t,a); }
					{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("*"); criteria.put(t,a); }
					{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); criteria.put(t,a); }
				} catch (Exception e) {
					e.printStackTrace(System.err);			
				}
				if(criteria == null){
					logger.warn("DICOM criteria NULL");
					return;
				}					
				Iterator<Integer> iter = dicomCriteria.keySet().iterator();
				logger.debug("DICOM retrieve criteria");
				while(iter.hasNext()){
					Integer key = iter.next();
					Object value = dicomCriteria.get(key);
					logger.debug("Key: " + key + " Value: " +  value);
				}*/			    	    		    	    		        	    	
		}		
	}
	
	void fireResultsAvailable(Map<String, ObjectLocator> objectLocators){
		RetrieveEvent event = new RetrieveEvent(objectLocators);         		        
		listener.retrieveResultsAvailable(event);
	}

	
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		
	}

	@Override
	public Map<String, ObjectLocator> getObjectLocators() {
		return objectLocators;
		//TODO to be removed
	}

	RetrieveListener listener;
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		listener = l;
	}
}
