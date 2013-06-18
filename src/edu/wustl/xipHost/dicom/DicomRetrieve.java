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

import java.io.File;
import java.io.IOException;
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
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.query.QueryResponseGenerator;
import com.pixelmed.query.QueryResponseGeneratorFactory;
import com.pixelmed.query.RetrieveResponseGenerator;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;
import com.pixelmed.query.StudyRootQueryInformationModel;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomRetrieve implements Retrieve {
	final static Logger logger = Logger.getLogger(DicomRetrieve.class);	
	DicomManager dicomMgr;
	PacsLocation called;	//request destination
	PacsLocation calling;	//initiating request
	//TargetElement targetElement;
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	List<ObjectDescriptor> objectDescriptors;
	File importDir;
	RetrieveTarget retrieveTarget;
	String databaseFileName;
	
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
		databaseFileName = dicomMgr.getDBFileName();
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
		databaseFileName = dicomMgr.getDBFileName();
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
	
	public void setDefaultCallingPacsLocation(DataSource defaultCallingPacsLocation){
		calling = (PacsLocation) defaultCallingPacsLocation;
	}
	
	public void setDatabaseFileName(String dbFileName){
		databaseFileName = dbFileName;
	}
	
	public void run() {
		logger.info("Executing DICOM retrieve.");				
		Map<String, ObjectLocator> objectLocs = retrieve(dicomCriteria);
		fireResultsAvailable(objectLocs);						
	}
	
	/**
	 * 1. Method performed hierarchical move from remote location (called) to calling location first.
	 * 2. To get retrieved files' URIs, method performs query on calling server
	 */
	AttributeList criteria;
	Map<String, ObjectLocator> objectLocators;
	Map<String, ObjectLocator> retrieve(Map<Integer, Object> dicomCriteria){
		if(called != null && calling != null){	
			String hostName = called.getAddress();
			int port = called.getPort();
			String calledAETitle = called.getAETitle();
			String callingAETitle = calling.getAETitle();
			if(logger.isDebugEnabled()){			
		    	logger.debug("Host name: " + hostName);
		    	logger.debug("Port: " + port);		    
		    	logger.debug("CalledAETitle: " + calledAETitle);	    	
		    	logger.debug("CallingAETitle: " + callingAETitle);
		    	if(databaseFileName != null){
		    		logger.debug("DBFileName: " + databaseFileName);
		    	} else {
		    		logger.warn("DBFileName value is null!");
		    	}
			}
			objectLocators = new HashMap<String, ObjectLocator>();
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
				StudySeriesInstanceModel mDatabase = null;
				if(databaseFileName != null){
					mDatabase = new StudySeriesInstanceModel(databaseFileName);
				} else {
					throw new DicomException("databaseFileName is NULL.");
				}			
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
		return objectLocators;
	}
	
	void fireResultsAvailable(Map<String, ObjectLocator> objectLocators){
		RetrieveEvent event = new RetrieveEvent(objectLocators);         		        
		listener.retrieveResultsAvailable(event);
	}

	RetrieveListener listener;
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		listener = l;
	}
}
