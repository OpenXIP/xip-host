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

package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.net.URI;
import java.util.List;
import org.nema.dicom.wg23.ObjectLocator;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import gov.nih.nci.ivi.helper.AIMTCGADataServiceHelper;

/**
 * @author Jaroslaw Krych
 *
 */
public class AimStore implements Runnable{
	//AIMDataServiceHelper helper = new AIMDataServiceHelper();	
	AIMTCGADataServiceHelper tcgaAIMHelper = new AIMTCGADataServiceHelper();
	String aimServiceURL;	
	List<ObjectLocator> aimObjectLocs;
	
	public AimStore(List<ObjectLocator> aimObjectLocs, GridLocation gridLocation){		
		this.aimObjectLocs = aimObjectLocs;		
		if(gridLocation == null){			
			gridLocation = new GridLocation("http://node01.cci.emory.edu:8080/wsrf/services/cagrid/AIMTCGADataService", Type.AIM, "AIM-TCGA", "TCGA AIM Server at Emory");
		}
		this.aimServiceURL = gridLocation.getAddress();				
	}
	
	public void run() {				
		try {
			for(int i = 0; i < aimObjectLocs.size(); i++){								
				URI uri = new URI(aimObjectLocs.get(i).getUri());
				File file = new File(uri);
				//helper.submitAnnotations(file.getCanonicalPath(), aimServiceURL);
				tcgaAIMHelper.submitAnnotations(file.getCanonicalPath(), aimServiceURL);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}
