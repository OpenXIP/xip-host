/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import edu.wustl.xipHost.wg23.Uuid;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.XDSDocumentItem;
import edu.wustl.xipHost.iterator.TargetElement;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSDocumentRetrieve implements Retrieve {
	XDSManager xdsMgr;
	DocumentEntryType docEntryDetails;
	CX patientId;
	String homeCommunityId = null;
	TargetElement targetElement;
	RetrieveTarget retrieveTarget;
	
	
	public XDSDocumentRetrieve(){
		
	}
	
	public XDSDocumentRetrieve(DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId){
		this.docEntryDetails = docEntryDetails;
		this.patientId = patientId;
		this.homeCommunityId = homeCommunityId;
		xdsMgr = XDSManagerFactory.getInstance();		
	}
	
	@Override
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) {
		this.targetElement = targetElement;
		this.retrieveTarget = retrieveTarget; 
		xdsMgr = XDSManagerFactory.getInstance();		
	}	
	
	File xdsRetrievedFile;
	Map<String, ObjectLocator> objectLocators;
	@Override
	public void run() {
		objectLocators = new HashMap<String, ObjectLocator>();
		SearchResult subSearchResult = targetElement.getSubSearchResult();
		List<Patient> patients = subSearchResult.getPatients();
		Patient patient = patients.get(0);
		XDSDocumentItem item = (XDSDocumentItem)patient.getItems().get(0);
		docEntryDetails = item.getDocumentType();
		patientId = item.getPatientId();
		homeCommunityId = item.getHomeCommunityId();
		xdsRetrievedFile = xdsMgr.retrieveDocument(docEntryDetails, patientId, homeCommunityId);				
		ObjectLocator objLoc = new ObjectLocator();				
		Uuid itemUUID = (Uuid) item.getObjectDescriptor().getDescriptorUuid();
		objLoc.setSource(itemUUID);				
		objLoc.setURI(xdsRetrievedFile.getAbsolutePath()); 
		item.setObjectLocator(objLoc);
		objectLocators.put(itemUUID.getUuid(), objLoc);
		fireResultsAvailable(targetElement.getId());
		
	}
 
	public File getRetrievedXSDFile(){
		return xdsRetrievedFile;
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

	@Override
	public Map<String, ObjectLocator> getObjectLocators() {
		return objectLocators;
	}

	@Override
	public void addRetrieveListener(RetrieveListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria,
			Map<String, Object> aimCriteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Object criteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImportDir(File importDir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		// TODO Auto-generated method stub
		
	}
}
