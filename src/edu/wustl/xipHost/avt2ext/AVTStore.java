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

package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.jdom.JDOMException;
import org.nema.dicom.wg23.ObjectLocator;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.User;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.util.DicomParser;
import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTStore implements Runnable {
	final static Logger logger = Logger.getLogger(AVTStore.class);
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
		aimToStore = new ArrayList<File>();
		for(ObjectLocator objLoc : objectLocs){						
			if(logger.isDebugEnabled()){
				logger.debug(objLoc.getUuid().getUuid() + " " + objLoc.getUri());
			}
			try {
				URI uri = new URI(objLoc.getUri());
				File file = new File(uri);
				String mimeType;
				mimeType = DicomUtil.mimeType(file);
				if(mimeType.equalsIgnoreCase("text/xml")){
					aimToStore.add(file);	
				}else if(mimeType.equalsIgnoreCase("application/dicom")){
					attachmentsToStore.add(file);
				}
				else if(!mimeType.equalsIgnoreCase("text/xml")){
					logger.warn(file.getCanonicalPath() + " is not DICOM nor AIM XML file and cannot be stored in AD.");
				}
			} catch (URISyntaxException e) {
				logger.error(e, e);
			} catch (IOException e) {
				logger.error(e, e);
			}
		}						
	}
	
	public void run() {						
		if(attachmentsToStore != null){				
			Iterator<File> iter = attachmentsToStore.iterator();
			List<DicomObject> dicomSegObjs = new ArrayList<DicomObject>();
			while(iter.hasNext()){					
				try {
					File file = iter.next();
					DicomObject seg = DicomParser.read(file);
					dicomSegObjs.add(seg);												
				} catch (IOException e) {
					logger.error(e, e);
				}							
			}				
			adService.saveDicoms(dicomSegObjs);
		}		
		if(aimToStore != null){
			User user = new User();
			String username = System.getProperty("user.name");
			user.setUserName(username);
			user.setRoleInt(1);	
			String comment = "Stored " + new Date().toString();
			List<ImageAnnotation> annotationsToStore = new ArrayList<ImageAnnotation>();
			for(int i = 0; i < aimToStore.size(); i++){
				File aimFile = aimToStore.get(i);			
				try {				
					ImageAnnotation imageAnnotation = AnnotationIO.loadAnnotationFromFile(aimFile);
					annotationsToStore.add(imageAnnotation);										
				} catch (IOException e) {
					logger.error(e, e);
				} catch (JDOMException e) {
					logger.error(e, e);
				}
			}
			adService.saveAnnotations(annotationsToStore, user, null, comment);
		}	
		notifyStoreResult(true);
	}

	void notifyStoreResult(boolean bln){
		logger.info("Finished 'store to AD'");
	}
}
