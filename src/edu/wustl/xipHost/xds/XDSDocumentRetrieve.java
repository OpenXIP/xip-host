/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSDocumentRetrieve implements Runnable {
	XDSManager xdsMgr;
	DocumentEntryType docEntryDetails;
	CX patientId;
	String homeCommunityId = null;
	
	public XDSDocumentRetrieve(DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId){
		this.docEntryDetails = docEntryDetails;
		this.patientId = patientId;
		this.homeCommunityId = homeCommunityId;
		xdsMgr = XDSManagerFactory.getInstance();		
	}
	
	@Override
	public void run() {
		File xdsRetrievedFile = xdsMgr.retrieveDocument(docEntryDetails, patientId, homeCommunityId);				
		fireUpdateUI(xdsRetrievedFile);
		
	}

	XDSRetrieveListener listener;
    public void addXDSRetrieveListener(XDSRetrieveListener l) {        
        listener = l;          
    }
	    
    void fireUpdateUI(File xdsRetrievedFile){
		//XDSRetrieveEvent event = new XDSRetrieveEvent(this);         		
        listener.documentsAvailable(xdsRetrievedFile);
	}	
	
}
