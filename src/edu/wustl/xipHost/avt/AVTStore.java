/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

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
import com.siemens.scr.avt.ad.connector.api.AIMDataService;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTStore implements Runnable {
	AIMDataService aimDataService = AVTFactory.getAIMDataServiceInstance();
	String[] aimToStore;		
	
	public AVTStore(String[] aimToStore){
		this.aimToStore = aimToStore;
	}
	
	public AVTStore(List<ObjectLocator> aimObjectLocs){
		aimToStore = new String[aimObjectLocs.size()];
		for(int i = 0; i < aimObjectLocs.size(); i++){
			String aimStrURI = aimObjectLocs.get(i).getUri();
			URI uri;
			try {
				uri = new URI(aimStrURI);
				File file = new File(uri);			    
				FileInputStream fs = new FileInputStream(file);			    
			    DataInputStream in = new DataInputStream(fs);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String strLine;
			    String aimXML = new String();			    
			    while ((strLine = br.readLine()) != null){			     
			      aimXML = aimXML + strLine;			 
			    }
			    in.close();
			    aimToStore[i] = aimXML;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}		
	}
	
	boolean bln;
	public void run() {
		bln = aimDataService.storeImageAnnotations(aimToStore);
		notifyStoreResult(bln);
	}

	public boolean getStoreResult(){
		return bln;
	}
	
	void notifyStoreResult(boolean bln){
		
	}
}
