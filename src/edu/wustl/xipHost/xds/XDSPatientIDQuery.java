/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.List;

import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSPatientIDQuery implements Runnable{
	XDSManager xdsMgr;
	AttributeList queryKeys = null;
	PDQLocation pdqSupplier = null;
	
	public XDSPatientIDQuery(AttributeList queryKeysInput, PDQLocation pdqSupplierInput){		
		xdsMgr = XDSManagerFactory.getInstance();
		queryKeys = queryKeysInput;
		pdqSupplier = pdqSupplierInput;
	}
	
	List<XDSPatientIDResponse> patientIDs;
	public void run() {
		patientIDs = xdsMgr.queryPatientIDs(queryKeys, pdqSupplier);		
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
