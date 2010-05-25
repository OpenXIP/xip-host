/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomOutputStream;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;

public class AVTRetrieve implements Runnable{
	final static Logger logger = Logger.getLogger(AVTRetrieve.class);
	ADFacade adService = AVTFactory.getADServiceInstance();
	String studyInstanceUID;
	String seriesInstanceUID;
	Map<String, Object> adAimCriteria;
	String aimUID;
	File importDir;
	ADRetrieveTarget retrieveTarget;
	
	public AVTRetrieve(String studyInstanceUID, String seriesInstanceUID, Map<String, Object> adAimCriteria, File importLocation, ADRetrieveTarget retrieveTarget) throws IOException{
		this.studyInstanceUID = studyInstanceUID;
		this.seriesInstanceUID = seriesInstanceUID;
		this.adAimCriteria = adAimCriteria;
		if(importLocation == null){
			throw new NullPointerException();			
		}else if(importLocation.exists() == false){
			throw new IOException();
		}else{
			importDir = importLocation;
		}
		this.retrieveTarget = retrieveTarget;
		logger.debug("AD retrieve parameters:");
		logger.debug("StudyInstanceUID: " + studyInstanceUID);
		logger.debug("SeriesInstanceUID: " + seriesInstanceUID);
		if(adAimCriteria == null){
			logger.debug("AD AIM criteria: " + adAimCriteria);
		}else{
			logger.debug("AD AIM retrieve criteria:");
			Set<String> keys = adAimCriteria.keySet();
			Iterator<String> iter = keys.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				String value = (String) adAimCriteria.get(key);
				if(!value.isEmpty()){
					logger.debug("Key: " + key + " Value: " + value);
				}					
			}				
		}
		logger.debug("Import location: " + importLocation.getCanonicalPath());
		logger.debug("Retrieve target: " + retrieveTarget.toString());
	}
	
	public void run() {
		try {
			logger.info("Executing AVT retrieve.");
			List<File> retrievedAIMs = retrieve();
			fireResultsAvailable(retrievedAIMs);
		} catch (IOException e) {
			logger.error(e, e);
			notifyException(e.getMessage());
			return;
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
		if(retrieveTarget == ADRetrieveTarget.DICOM_AND_AIM){
			//Retrieve DICOM
			List<DicomObject> retrievedDICOM = adService.retrieveDicomObjs(dicomCriteria, adAimCriteria);
			for(int i = 0; i < retrievedDICOM.size(); i++){
				DicomObject dicom = retrievedDICOM.get(i);
				String fileName = dirPath + dicom.getString(Tag.SOPInstanceUID) + ".dcm";
				File file = new File(fileName);
				DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
				dout.writeDicomFile(dicom);
				dout.close();			
				files.add(file);
			}
			//Retrieve AIM		
			List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, adAimCriteria);		
			Set<String> uniqueAnnotUIDs = new HashSet<String>(annotationUIDs);
			Iterator<String> iter = uniqueAnnotUIDs.iterator();
			Set<String> segDicomInstances = new HashSet<String>();
			while(iter.hasNext()){
				String uid = iter.next();
				ImageAnnotation loadedAnnot = adService.getAnnotation(uid);			
				String strXML = loadedAnnot.getAIM();
				byte[] source = strXML.getBytes();
				InputStream is = new ByteArrayInputStream(source);
				try {
					document = builder.build(is);
				} catch (JDOMException e) {
					logger.error(e, e);
					notifyException(e.getMessage());
					return null;
				}	
				File outFile = new File(dirPath + uid + ".xml");
				FileOutputStream outStream = new FileOutputStream(outFile);			
				outToXMLFile.output(document, outStream);
		    	outStream.flush();
		    	outStream.close();
		    	files.add(outFile);
		    	//Retrieve DICOM SEG
		    	List<DicomObject> segObjects = adService.retrieveSegmentationObjects(uid);
		    	for(int i = 0; i < segObjects.size(); i++){
		    		DicomObject dicom = segObjects.get(i);
		    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
		    		//Check of segDicom was not serialized in reference to another AIM
		    		if(!segDicomInstances.contains(sopInstanceUID)){
		    			segDicomInstances.add(sopInstanceUID);
		    			DicomObject segDicom = adService.getDicomObject(sopInstanceUID);		    		
						String fileName = dirPath + sopInstanceUID + ".dcm";
						File file = new File(fileName);
						DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
						dout.writeDicomFile(segDicom);
						dout.close();
						files.add(file);
		    		}
		    	}		  
			}				
		}else if(retrieveTarget == ADRetrieveTarget.SERIES){
			//Retrieve DICOM, empty aim criteria, because it is expected that entire series will be retrieved
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			List<DicomObject> retrievedDICOM = adService.retrieveDicomObjs(dicomCriteria, aimCriteria);
			for(int i = 0; i < retrievedDICOM.size(); i++){
				DicomObject dicom = retrievedDICOM.get(i);
				String fileName = dirPath + dicom.getString(Tag.SOPInstanceUID) + ".dcm";
				File file = new File(fileName);
				DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
				dout.writeDicomFile(dicom);
				dout.close();			
				files.add(file);
			}
		}else if(retrieveTarget == ADRetrieveTarget.AIM_SEG){
			//Retrieve AIM		
			List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, adAimCriteria);		
			Set<String> uniqueAnnotUIDs = new HashSet<String>(annotationUIDs);
			Iterator<String> iter = uniqueAnnotUIDs.iterator();
			Set<String> segDicomInstances = new HashSet<String>();
			while(iter.hasNext()){				
				String uid = iter.next();
				ImageAnnotation loadedAnnot = adService.getAnnotation(uid);			
				String strXML = loadedAnnot.getAIM();
				byte[] source = strXML.getBytes();
				InputStream is = new ByteArrayInputStream(source);
				try {
					document = builder.build(is);
				} catch (JDOMException e) {
					logger.error(e, e);
					notifyException(e.getMessage());
					return null;
				}				
				File outFile = new File(dirPath + uid + ".xml");
				FileOutputStream outStream = new FileOutputStream(outFile);			
				outToXMLFile.output(document, outStream);
		    	outStream.flush();
		    	outStream.close();
		    	files.add(outFile);
		    	
		    	List<DicomObject> segObjects = adService.retrieveSegmentationObjects(uid);
		    	for(int i = 0; i < segObjects.size(); i++){
		    		DicomObject dicom = segObjects.get(i);
		    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
		    		//Check of segDicom was not serialized in reference to another AIM
		    		if(!segDicomInstances.contains(sopInstanceUID)){
		    			segDicomInstances.add(sopInstanceUID);
		    			DicomObject segDicom = adService.getDicomObject(sopInstanceUID);		    		
						String fileName = dirPath + sopInstanceUID + ".dcm";
						File file = new File(fileName);
						DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
						dout.writeDicomFile(segDicom);
						dout.close();
						files.add(file);
		    		}
		    	}		    		
			}
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
	void notifyException(String message){         		
        listener.notifyException(message);
	}
}
