/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.nema.dicom.wg23.ObjectLocator;

import edu.wustl.xipHost.caGrid.GridLocation.Type;

import gov.nih.nci.ivi.helper.AIMDataServiceHelper;

/**
 * @author Jaroslaw Krych
 *
 */
public class AimStore implements Runnable{
	AIMDataServiceHelper helper = new AIMDataServiceHelper();	
	String aimServiceURL;	
	List<ObjectLocator> aimObjectLocs;
	
	public AimStore(List<ObjectLocator> aimObjectLocs, GridLocation gridLocation){		
		this.aimObjectLocs = aimObjectLocs;		
		if(gridLocation == null){			
			gridLocation = new GridLocation("http://ividemo.bmi.ohio-state.edu:8081/wsrf/services/cagrid/AIMDataService", Type.AIM, "AIM Server Ohio State University AIM_1_rv_1.9");
		}
		this.aimServiceURL = gridLocation.getAddress();				
	}
	
	public void run() {				
		try {
			for(int i = 0; i < aimObjectLocs.size(); i++){								
				URI uri = new URI(aimObjectLocs.get(i).getUri());
				File file = new File(uri);
				helper.submitAnnotations(file.getCanonicalPath(), aimServiceURL);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}
