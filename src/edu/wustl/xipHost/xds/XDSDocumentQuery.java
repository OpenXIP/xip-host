/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSDocumentQuery implements Runnable{
	XDSManager xdsMgr;
	String [] patientID;
	
	public XDSDocumentQuery(String [] patientID){
		this.patientID = patientID;
		xdsMgr = XDSManagerFactory.getInstance();
	}
	
	XDSQueryResponseType xsdQueryResponse;
	public void run() {			
		xsdQueryResponse = xdsMgr.queryDocuments(patientID);				
		fireUpdateUI();
	}

	public XDSQueryResponseType getxsdQueryResponse(){
		return xsdQueryResponse;
	}
	
	XDSSearchListener listener;
    public void addXDSSearchListener(XDSSearchListener l) {        
        listener = l;          
    }
	    
    void fireUpdateUI(){
		XDSSearchEvent event = new XDSSearchEvent(this);         		
        listener.searchResultAvailable(event);
	}		
}
