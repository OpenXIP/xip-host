/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.XDSDocumentItem;
import edu.wustl.xipHost.iterator.RetrieveTarget;
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
		Uuid itemUUID = item.getObjectDescriptor().getUuid();
		objLoc.setUuid(itemUUID);				
		objLoc.setUri(xdsRetrievedFile.getAbsolutePath()); 
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
}
