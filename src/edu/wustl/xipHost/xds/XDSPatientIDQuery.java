/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.List;

import com.pixelmed.dicom.AttributeList;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSPatientIDQuery implements Runnable{
	XDSManager xdsMgr;
	AttributeList queryKeys = null;
	
	public XDSPatientIDQuery(AttributeList queryKeysInput){		
		xdsMgr = XDSManagerFactory.getInstance();
		queryKeys = queryKeysInput;
	}
	
	List<XDSPatientIDResponse> patientIDs;
	public void run() {
		patientIDs = xdsMgr.queryPatientIDs(queryKeys);		
		notifyPatientIDs(patientIDs);				
	}
		
	XDSSearchListener listener;
    public void addXDSSearchListener(XDSSearchListener l) {        
        listener = l;          
    }
	
    void notifyPatientIDs(List<XDSPatientIDResponse> patientIDs2){
    	listener.patientIDsAvailable(patientIDs2);
    }
}
