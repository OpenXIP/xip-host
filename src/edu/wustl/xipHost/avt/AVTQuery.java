package edu.wustl.xipHost.avt;

import com.siemens.scr.avt.ad.connector.api.AIMDataService;
import com.siemens.scr.avt.ad.connector.api.ImageAnnotationDescriptor;

public class AVTQuery implements Runnable{
	AIMDataService aimDataService;
	String studyInstanceUID;
	String seriesInstanceUID; 
	
	public AVTQuery(String studyInstanceUID, String seriesInstanceUID){
		this.studyInstanceUID = studyInstanceUID;
		this.seriesInstanceUID = seriesInstanceUID;
		aimDataService = AVTFactory.getAIMDataServiceInstance();
		if(aimDataService == null){
			System.out.println("Connection problem");
		}
	}
	
	public void run() {
		ImageAnnotationDescriptor[] aimDescs = aimDataService.listImageAnnotations(studyInstanceUID, seriesInstanceUID);		
		if(aimDescs == null){
			aimDescs = new ImageAnnotationDescriptor[1];
		}
		fireResultsAvailable(aimDescs);		//returns array with empty string
	}
	

	AVTListener listener;
    public void addAVTListener(AVTListener l) {        
        listener = l;          
    }
	void fireResultsAvailable(ImageAnnotationDescriptor[] aimDescs){
		AVTSearchEvent event = new AVTSearchEvent(aimDescs);         		
        listener.searchResultsAvailable(event);
	}	
}
