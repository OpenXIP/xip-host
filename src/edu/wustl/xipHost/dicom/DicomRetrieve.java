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

import com.pixelmed.database.StudySeriesInstanceModel;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SetOfDicomFiles;
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
import edu.wustl.xipHost.iterator.TargetElement;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomRetrieve implements Retrieve {
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
		if(criteria == null){return;}					
		if(criteria != null && called != null && calling != null){													
			List<URI> uris = retrieve(criteria, called, calling);
			for(int i = 0; i < uris.size(); i++){
				File file = new File(uris.get(i));
				files.add(file);				
			}

			fireResultsAvailable(targetElement.getId());		
		}else{
			return;
		}				
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
        	int port = called.getPort();
        	String calledAETitle = called.getAETitle(); 
        	String callingAETitle = calling.getAETitle();
        	StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);
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
					//System.out.println("Dicom file: " + x.getFileName());			    
					retrievedFiles.add(new File(x.getFileName()).toURI());
				}		
				localResults = mQueryResponseGenerator.next();
			}			
    	} catch (Exception e) {
    		return null;
    	}      	    	    	
    	return retrievedFiles;
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
