/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
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
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.iterator.RetrieveTarget;
import edu.wustl.xipHost.iterator.SubElement;
import edu.wustl.xipHost.iterator.TargetElement;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomRetrieve implements Retrieve {
	final static Logger logger = Logger.getLogger(DicomRetrieve.class);	
	DicomManager dicomMgr;
	AttributeList criteria;
	PacsLocation called;
	PacsLocation calling;
	TargetElement targetElement;
	RetrieveTarget retrieveTarget;
	
	public DicomRetrieve(){
		
	}
	
	public DicomRetrieve(AttributeList criteria, PacsLocation called, PacsLocation calling){
		this.criteria = criteria;
		this.called = called;
		this.calling = calling;
		dicomMgr = DicomManagerFactory.getInstance();
	}
	
	@Override
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) {
		this.targetElement = targetElement;
		this.retrieveTarget = retrieveTarget; 
	}

	
	List<File> files = new ArrayList<File>();
	public void run() {
		logger.info("Executing DICOM retrieve.");				
		retrieve(targetElement, retrieveTarget);
		fireResultsAvailable(targetElement.getId());						
	}
	
	List<URI> retrievedFiles;
	/**
	 * 1. Method performed hierarchical move from remote location to calling location first.
	 * 2. To get retrieved files' URIs method performs query but on the local server (calling)
	 */
	public List<URI> retrieve(AttributeList criteria, PacsLocation called, PacsLocation calling) {		
		retrievedFiles = new ArrayList<URI>();	    	    		    	    		        
        try {
        	String hostName = called.getAddress();
        	logger.debug("Host name: " + hostName);
        	int port = called.getPort();
        	logger.debug("Port: " + port);
        	String calledAETitle = called.getAETitle();
        	logger.debug("CalledAETitle: " + calledAETitle);
        	String callingAETitle = calling.getAETitle();
        	logger.debug("CallingAETitle: " + callingAETitle);
        	StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);
        	logger.debug("DICOM retrieve criteria");
        	if(logger.isDebugEnabled()){
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
        	}
        	mModel.performHierarchicalMoveTo(criteria, calling.getAETitle());			
		} catch (IOException e1) {
			return null;
		} catch (DicomException e1) {
			return null;
		} catch (DicomNetworkException e1) {						
			return null;
		}        	        	    	    			
		
		try {						    		
			//System.out.println("Local server is: " + getDBFileName());
			StudySeriesInstanceModel mDatabase = new StudySeriesInstanceModel(dicomMgr.getDBFileName());			
    		//RetrieveResposeGeneratorFactory provides access to files URLs stored in hsqldb    		    	
    		RetrieveResponseGeneratorFactory mRetrieveResponseGeneratorFactory = mDatabase.getRetrieveResponseGeneratorFactory(0);
    		QueryResponseGeneratorFactory mQueryResponseGeneratorFactory = mDatabase.getQueryResponseGeneratorFactory(0);			
    		RetrieveResponseGenerator mRetrieveResponseGenerator = mRetrieveResponseGeneratorFactory.newInstance();
			QueryResponseGenerator mQueryResponseGenerator = mQueryResponseGeneratorFactory.newInstance();						
			mQueryResponseGenerator.performQuery("1.2.840.10008.5.1.4.1.2.2.1", criteria, true);	// Study Root						
			AttributeList localResults = mQueryResponseGenerator.next();			
			while(localResults != null) {							 					
				mRetrieveResponseGenerator.performRetrieve("1.2.840.10008.5.1.4.1.2.2.3", localResults, true);	// Study Root		
				SetOfDicomFiles dicomFiles = mRetrieveResponseGenerator.getDicomFiles();
				Iterator<?> it = dicomFiles.iterator();			  
				while (it.hasNext() ) {
					SetOfDicomFiles.DicomFile x  = (SetOfDicomFiles.DicomFile)it.next();
					System.out.println("Dicom file: " + x.getFileName());			    
					retrievedFiles.add(new File(x.getFileName()).toURI());
				}		
				localResults = mQueryResponseGenerator.next();
			}			
    	} catch (Exception e) {
    		logger.error(e, e);
    		return null;
    	}      	    	    	
    	return retrievedFiles;
	}
	
	void retrieve(TargetElement targetElement, RetrieveTarget retrieveTarget){		
		retrievedFiles = new ArrayList<URI>();
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
			}		
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
				}			    	    		    	    		        
		        try {	        	
		        	StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);	        		        	
		        	mModel.performHierarchicalMoveTo(criteria, calling.getAETitle());			
				} catch (IOException e) {
					logger.error(e, e);
				} catch (DicomException e) {
					logger.error(e, e);
				} catch (DicomNetworkException e) {						
					logger.error(e, e);
				}        	        	    	    						
				try {						    		
					//System.out.println("Local server is: " + getDBFileName());
					StudySeriesInstanceModel mDatabase = new StudySeriesInstanceModel(dicomMgr.getDBFileName());			
		    		//RetrieveResposeGeneratorFactory provides access to files URLs stored in hsqldb    		    	
		    		RetrieveResponseGeneratorFactory mRetrieveResponseGeneratorFactory = mDatabase.getRetrieveResponseGeneratorFactory(0);
		    		QueryResponseGeneratorFactory mQueryResponseGeneratorFactory = mDatabase.getQueryResponseGeneratorFactory(0);			
		    		RetrieveResponseGenerator mRetrieveResponseGenerator = mRetrieveResponseGeneratorFactory.newInstance();
					QueryResponseGenerator mQueryResponseGenerator = mQueryResponseGeneratorFactory.newInstance();						
					mQueryResponseGenerator.performQuery("1.2.840.10008.5.1.4.1.2.2.1", criteria, true);	// Study Root						
					AttributeList localResults = mQueryResponseGenerator.next();			
					while(localResults != null) {							 					
						mRetrieveResponseGenerator.performRetrieve("1.2.840.10008.5.1.4.1.2.2.3", localResults, true);	// Study Root		
						SetOfDicomFiles dicomFiles = mRetrieveResponseGenerator.getDicomFiles();
						Iterator<?> it = dicomFiles.iterator();			  						
						while (it.hasNext() ) {
							SetOfDicomFiles.DicomFile x  = (SetOfDicomFiles.DicomFile)it.next();
							logger.debug("Dicom file: " + x.getFileName());			    							
							retrievedFiles.add(new File(x.getFileName()).toURI());
						}		
						localResults = mQueryResponseGenerator.next();
					}			
		    	} catch (Exception e) {
		    		logger.error(e, e);
		    	} 
		    	subElement.setFileURIs(retrievedFiles);
		    	subElement.setPath(null);		    	
			}
		}		
	}
	
	public List<File> getRetrievedFiles(){
		return files;
	}
	
	void fireResultsAvailable(String targetElementID){
		RetrieveEvent event = new RetrieveEvent(targetElementID);
		listener.retrieveResultsAvailable(event);
	}

	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;
	}
}
