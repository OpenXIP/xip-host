/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.pdq;

import java.util.List;

import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Lawrence Tarbox, from code originally created by Jaroslaw Krych
 *
 */
public class PDQPatientIDQuery implements Runnable{
	PDQManager pdqMgr;
	AttributeList queryKeys = null;
	PDQLocation pdqSupplier = null;
	
	public PDQPatientIDQuery(AttributeList queryKeysInput, PDQLocation pdqSupplierInput){		
		pdqMgr = PDQManagerFactory.getInstance();
		queryKeys = queryKeysInput;
		pdqSupplier = pdqSupplierInput;
	}
	
	List<PDQPatientIDResponse> patientIDs;
	public void run() {
		patientIDs = pdqMgr.queryPatientIDs(queryKeys, pdqSupplier);		
		notifyPatientIDs(patientIDs);				
	}
		
	PDQSearchListener listener;
    public void addPDQSearchListener(PDQSearchListener l) {        
        listener = l;          
    }
	
    void notifyPatientIDs(List<PDQPatientIDResponse> patientIDs2){
    	listener.patientIDsAvailable(patientIDs2);
    }
}
