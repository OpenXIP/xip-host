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
import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTStore implements Runnable {
	ADFacade adService = AVTFactory.getADServiceInstance();
	List<File> aimToStore;	
	List<File> attachmentsToStore;
	
	/**
	 * Constructor used in JUnit testing
	 * @param aimToStore
	 */
	public AVTStore(File[] aimToStore){
		this.aimToStore = new ArrayList<File>();
		for(int i = 0; i < aimToStore.length; i++){
			this.aimToStore.add(aimToStore[i]);
		}
	}	
	
	public AVTStore(List<ObjectLocator> objectLocs){
		attachmentsToStore = new ArrayList<File>();
		//1. Check if objecLocs are AIM (xml) or DICOM (dcm)
		//aimToStore = new File[objectLocs.size()];
		aimToStore = new ArrayList<File>();
		for(ObjectLocator objLoc : objectLocs){						
			try {
				URI uri = new URI(objLoc.getUri());
				File file = new File(uri);
				String mimeType;
				mimeType = DicomUtil.mimeType(file);
				if(mimeType.equalsIgnoreCase("text/xml")){
					aimToStore.add(file);	
				}else if(!mimeType.equalsIgnoreCase("text/xml")){
					attachmentsToStore.add(file);
				}
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}						
	}
	
	boolean bln;
	public void run() {						
		if(aimToStore != null && attachmentsToStore != null){
			File aimFile = aimToStore.get(0);
			File dicomFile = attachmentsToStore.get(0);			
			try {				
				ImageAnnotation imageAnnotation = AnnotationIO.loadAnnotationWithAttachment(aimFile, dicomFile);
				AnnotationIO.saveOrUpdateAnnotation(imageAnnotation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		notifyStoreResult(true);
	}

	public boolean getStoreResult(){
		return bln;
	}
	
	void notifyStoreResult(boolean bln){
		
	}
}