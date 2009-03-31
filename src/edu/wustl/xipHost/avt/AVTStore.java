/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.JDOMException;
import org.nema.dicom.wg23.ObjectLocator;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.io.AnnotationIO;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTStore implements Runnable {
	ADFacade adService = AVTFactory.getADServiceInstance();
	File[] aimToStore;		
	
	public AVTStore(File[] aimToStore){
		this.aimToStore = aimToStore;
	}
		
	public AVTStore(List<ObjectLocator> aimObjectLocs){
		aimToStore = new File[aimObjectLocs.size()];
		for(int i = 0; i < aimObjectLocs.size(); i++){
			String aimStrURI = aimObjectLocs.get(i).getUri();
			URI uri;
			try {
				uri = new URI(aimStrURI);
				File file = new File(uri);			    				
			    aimToStore[i] = file;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}		
	}
	
	boolean bln;
	public void run() {
		List<ImageAnnotation> imageAnnotations = new ArrayList<ImageAnnotation>();
		int i = aimToStore.length;
		for(int j = 0; j < i; j++){
			try {
				ImageAnnotation imageAnnotation = AnnotationIO.loadAnnotationFromFile(aimToStore[j]);
				imageAnnotations.add(imageAnnotation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		bln = adService.saveAnnotations(imageAnnotations, null, null);
		notifyStoreResult(bln);
	}

	public boolean getStoreResult(){
		return bln;
	}
	
	void notifyStoreResult(boolean bln){
		
	}
}
