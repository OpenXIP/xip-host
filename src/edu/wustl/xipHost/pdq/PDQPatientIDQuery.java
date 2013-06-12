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
