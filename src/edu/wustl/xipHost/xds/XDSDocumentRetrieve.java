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

package edu.wustl.xipHost.xds;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
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
		//FIXME
    	//RetrieveEvent event = new RetrieveEvent(targetElementID);
    	RetrieveEvent event = null;
        listener.retrieveResultsAvailable(event);
	}

    RetrieveListener listener;
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		listener = l;
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
