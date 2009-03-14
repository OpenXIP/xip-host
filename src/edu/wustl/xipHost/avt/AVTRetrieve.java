package edu.wustl.xipHost.avt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.io.DicomIO;

public class AVTRetrieve implements Runnable{
	ADFacade adService = AVTFactory.getADServiceInstance();
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
	XMLOutputter outToXMLFile = new XMLOutputter();
	List<File> retrieve() throws IOException {		
		File inputDir = File.createTempFile("AVT-XIPHOST", null, importDir);
		importDir = inputDir;
		inputDir.delete();
		if(!importDir.exists()){
			importDir.mkdir();
		}		
		String dirPath = "";
		if(importDir.getCanonicalPath().endsWith(File.separator)){
			dirPath = importDir.getCanonicalPath();
		}else{
			dirPath = importDir.getCanonicalPath() + File.separator;
		}
		List<File> files = new ArrayList<File>();
		HashMap<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		dicomCriteria.put(Tag.StudyInstanceUID, studyInstanceUID);
		dicomCriteria.put(Tag.SeriesInstanceUID, seriesInstanceUID);		
		//Retrieve DICOM
		List<String> dicomUIDs = adService.findDicomObjs(dicomCriteria, null);										
		for(int i = 0; i < dicomUIDs.size(); i++){			
			try {				
				String fileName = dirPath + dicomUIDs.get(i) + ".dcm";
				File file = new File(fileName);
				DicomIO.dumpDicom2File(dicomUIDs.get(i), fileName);
				files.add(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}			
		//Retrieve AIM		
		List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, null);		
		for(int i = 0; i < annotationUIDs.size(); i++){						
			ImageAnnotation loadedAnnot = adService.getAnnotation(annotationUIDs.get(i));			
			String strXML = loadedAnnot.getAIM();
			byte[] source = strXML.getBytes();
			InputStream is = new ByteArrayInputStream(source);
			try {
				document = builder.build(is);
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			File outFile = new File(dirPath + UUID.randomUUID().toString() + ".xml");
			FileOutputStream outStream = new FileOutputStream(outFile);			
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
