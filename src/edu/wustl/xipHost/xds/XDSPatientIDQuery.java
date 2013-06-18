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
