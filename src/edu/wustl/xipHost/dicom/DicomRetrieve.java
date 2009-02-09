/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.pixelmed.dicom.AttributeList;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomRetrieve implements Runnable {
	DicomManager dicomMgr;
	AttributeList criteria;
	PacsLocation called;
	PacsLocation calling;
	
	public DicomRetrieve(AttributeList criteria, PacsLocation called, PacsLocation calling){
		this.criteria = criteria;
		this.called = called;
		this.calling = calling;
		dicomMgr = DicomManagerFactory.getInstance();
	}
	
	List<File> files = new ArrayList<File>();
	public void run() {
		if(criteria == null){return;}					
		if(criteria != null && called != null && calling != null){													
			List<URI> uris = dicomMgr.retrieve(criteria, called, calling);
			for(int i = 0; i < uris.size(); i++){
				File file = new File(uris.get(i));
				files.add(file);				
			}
			fireUpdateUI();			
		}else{
			return;
		}				
	}
	
	public List<File> getRetrievedFiles(){
		return files;
	}
	
	DicomRetrieveListener listener; 
	public void addDicomRetrieveListener(DicomRetrieveListener l){
		 this.listener = l;
	}
	
	void fireUpdateUI(){
		DicomRetrieveEvent event = new DicomRetrieveEvent(this);
		listener.retrieveResultAvailable(event);
	}

}
