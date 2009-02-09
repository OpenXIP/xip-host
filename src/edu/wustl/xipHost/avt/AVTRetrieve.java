package edu.wustl.xipHost.avt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import com.siemens.scr.avt.ad.connector.api.AIMDataService;

public class AVTRetrieve implements Runnable{
	AIMDataService aimDataService = AVTFactory.getAIMDataServiceInstance();
	String studyInstanceUID;
	String seriesInstanceUID;
	String aimUID;
	File importDir;
	
	public AVTRetrieve(String studyInstanceUID, String seriesInstanceUID, File importLocation) throws IOException{
		this.studyInstanceUID = studyInstanceUID;
		this.seriesInstanceUID = seriesInstanceUID;
		if(importLocation == null){
			throw new NullPointerException();
		}else if(importLocation.exists() == false){
			throw new IOException();
		}else{
			importDir = importLocation;
		}	
	}
	public AVTRetrieve(String aimUID, File importLocation) throws IOException{
		this.aimUID = aimUID;
		if(importLocation == null){
			throw new NullPointerException();
		}else if(importLocation.exists() == false){
			throw new IOException();
		}else{
			importDir = importLocation;
		}	
	}
	
	
	public void run() {
		try {
			List<File> retrievedAIMs = retrieve();
			fireResultsAvailable(retrievedAIMs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}
	
	SAXBuilder builder = new SAXBuilder();
	Document document;
	List<File> retrieve() throws IOException {		
		String[] retrievedAIM;
		List<File> files = new ArrayList<File>();
		if(aimUID != null){
			retrievedAIM = new String[1];
			String strXML = aimDataService.getImageAnnotation(aimUID);
			retrievedAIM[0] = strXML;			
		}else{
			retrievedAIM = aimDataService.retrieveImageAnnotations(studyInstanceUID, seriesInstanceUID);						
		}	
		File inputDir = File.createTempFile("AIM-XIPHOST", null, importDir);
		importDir = inputDir;
		inputDir.delete();
		if(!importDir.exists()){
			importDir.mkdir();
		}
		for(int i = 0; i < retrievedAIM.length; i++){									
			String fileName = null;
			try {
				byte[] source = retrievedAIM[i].getBytes();
				InputStream is = new ByteArrayInputStream(source);
				document = builder.build(is);				
				fileName = document.getRootElement().getAttribute("uid").getValue() + ".xml";
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			//File outFile = new File(this.importDir + File.separator + fileName);
			File outFile = new File(this.importDir + File.separator + UUID.randomUUID().toString() + ".xml");
			FileOutputStream outStream = new FileOutputStream(outFile);
			XMLOutputter outToXMLFile = new XMLOutputter();
			outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();
	    	files.add(outFile);
		}										
		return files;		
	}		
	

	AVTListener listener;
    public void addAVTListener(AVTListener l) {        
        listener = l;          
    }
	void fireResultsAvailable(List<File> retrievedAIM){
		AVTRetrieveEvent event = new AVTRetrieveEvent(retrievedAIM);         		
        listener.retriveResultsAvailable(event);
	}	
}
