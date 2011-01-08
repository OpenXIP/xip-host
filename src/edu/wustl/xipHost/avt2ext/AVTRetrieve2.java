/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
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
import edu.wustl.xipHost.iterator.SubElement;
import edu.wustl.xipHost.iterator.TargetElement;

public class AVTRetrieve2 implements Runnable{
	final static Logger logger = Logger.getLogger(AVTRetrieve2.class);
	ADFacade adService = AVTFactory.getADServiceInstance();
	TargetElement targetElement;
	ADRetrieveTarget retrieveTarget;
	
	public AVTRetrieve2(TargetElement targetElement, ADRetrieveTarget retrieveTarget) throws IOException{
		this.targetElement = targetElement;
		this.retrieveTarget = retrieveTarget;
	}
	
	public void run() {
		try {
			logger.info("Executing AVT retrieve.");
			retrieve(targetElement, retrieveTarget);
			fireResultsAvailable(targetElement.getId());
		} catch (IOException e) {
			logger.error(e, e);
			return;
		}
	}
	
	SAXBuilder builder = new SAXBuilder();
	Document document;
	XMLOutputter outToXMLFile = new XMLOutputter();
	@SuppressWarnings("unchecked")
	void retrieve(TargetElement targetElement, ADRetrieveTarget retrieveTarget) throws IOException {		
		List<SubElement> subElements = targetElement.getSubElements();
		Map<Integer, Object> dicomCriteria = null;
		Map<String, Object> adAimCriteria = null;
		File importDir = null;
		for(SubElement subElement : subElements){
			dicomCriteria = subElement.getCriteria().getDICOMCriteria();
			adAimCriteria = subElement.getCriteria().getAIMCriteria();
			importDir = new File(subElement.getPath());
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
			//TODO: provide logging for DICOM criteria
			logger.debug("Import location: " + importDir.getCanonicalPath());
			logger.debug("Retrieve target: " + retrieveTarget.toString());
				
			File dirPath = importDir.getAbsoluteFile();
			if(retrieveTarget == ADRetrieveTarget.DICOM_AND_AIM){
				//Retrieve DICOM
				List<DicomObject> retrievedDICOM = adService.retrieveDicomObjs(dicomCriteria, adAimCriteria);
				for(int i = 0; i < retrievedDICOM.size(); i++){
					DicomObject dicom = retrievedDICOM.get(i);
					String filePrefix = dicom.getString(Tag.SOPInstanceUID);
					String fileName = null;
					IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
					Iterator<File> tmpFiles = FileUtils.iterateFiles(importDir, fileFilter, null);
					while(tmpFiles.hasNext()){
						File tmpFile = tmpFiles.next();
						if(tmpFile.getName().startsWith(filePrefix)){
							fileName = tmpFile.getAbsolutePath();
						}
					}
					FileOutputStream fos = new FileOutputStream(fileName);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					DicomOutputStream dout = new DicomOutputStream(bos);
					dout.writeDicomFile(dicom);
					dout.close();			
				}
				//Retrieve AIM		
				List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, adAimCriteria);
				Set<String> uniqueAnnotUIDs = new HashSet<String>(annotationUIDs);
				Iterator<String> iter = uniqueAnnotUIDs.iterator();
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
					}	
					//Ensure dirPath is correctly assign. There are references below of this variable
					File outFile = new File(dirPath + File.separator + uid);
					FileOutputStream outStream = new FileOutputStream(outFile);			
					outToXMLFile.output(document, outStream);
			    	outStream.flush();
			    	outStream.close();
			    	//Retrieve DICOM SEG
			    	//temporarily voided. AVTQuery needs to be modified to query for DICOM SEG objects
			    	//
			    	Set<String> dicomSegSOPInstanceUIDs = new HashSet<String>();
			    	List<DicomObject> segObjects = adService.retrieveSegmentationObjects(uid);
			    	for(int i = 0; i < segObjects.size(); i++){
			    		DicomObject dicom = segObjects.get(i);
			    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
			    		//Check if DICOM SEG was not serialized in reference to another AIM
			    		if(!dicomSegSOPInstanceUIDs.contains(sopInstanceUID)){
			    			dicomSegSOPInstanceUIDs.add(sopInstanceUID);
			    			DicomObject dicomSeg = adService.getDicomObject(sopInstanceUID);
			    			String message = "DICOM SEG " + sopInstanceUID + " cannot be loaded from file system!";
			    			if(dicomSeg == null){			    				
			    				throw new FileNotFoundException(message);
			    			} else {
			    				String filePrefix = sopInstanceUID;
			    				
			    				IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
			    				Iterator<File> tmpFiles = FileUtils.iterateFiles(importDir, fileFilter, null);
			    				//DICOM SEG tmp file not found e.g. DICOM SEG belongs to not specified Study for which TargetIteratorRunner was not requested
			    				boolean dicomSegFound = false;
			    				while(tmpFiles.hasNext()){
			    					File tmpFile = tmpFiles.next();
			    					if(tmpFile.getName().startsWith(filePrefix)){
			    						dicomSegFound = true;
			    					}
			    				}
				    			if(dicomSegFound == true){
			    					File outDicomSegFile = new File(dirPath + File.separator + sopInstanceUID);
		    						FileOutputStream fos = new FileOutputStream(outDicomSegFile);
		    						BufferedOutputStream bos = new BufferedOutputStream(fos);
		    						DicomOutputStream dout = new DicomOutputStream(bos);
		    						dout.writeDicomFile(dicomSeg);
		    						dout.close();
				    			} else if(dicomSegFound == false){
			    					//There wouldn't be UUIDs for this case since it was not found with the TargetIteratorRunner
				    				//TODO: build notification, add to the MultiValueMap etc.
				    				//Eliminate duplicate code with dicomSegFound = true
			    					File outDicomSegFile = new File(dirPath + File.separator + sopInstanceUID);
		    						FileOutputStream fos = new FileOutputStream(outDicomSegFile);
		    						BufferedOutputStream bos = new BufferedOutputStream(fos);
		    						DicomOutputStream dout = new DicomOutputStream(bos);
		    						dout.writeDicomFile(dicomSeg);
		    						dout.close();
			    				}				
			    			}
			    		}
			    	}	//	  
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
					}				
					File outFile = new File(dirPath + uid + ".xml");
					FileOutputStream outStream = new FileOutputStream(outFile);			
					outToXMLFile.output(document, outStream);
			    	outStream.flush();
			    	outStream.close();			    	
			    	
			    	List<DicomObject> segObjects = adService.retrieveSegmentationObjects(uid);
			    	for(int i = 0; i < segObjects.size(); i++){
			    		DicomObject dicom = segObjects.get(i);
			    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
			    		//Check of segDicom was not serialized in reference to another AIM
			    		if(!segDicomInstances.contains(sopInstanceUID)){
			    			segDicomInstances.add(sopInstanceUID);
			    			DicomObject segDicom = adService.getDicomObject(sopInstanceUID);
			    			if(segDicom == null){
			    				String message = "DICOM SEG " + sopInstanceUID + " cannot be loaded from file system!";
			    				throw new FileNotFoundException(message);
			    			} else {
			    				String filePrefix = sopInstanceUID;
			    				String fileName = null;
			    				IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
			    				Iterator<File> tmpFiles = FileUtils.iterateFiles(importDir, fileFilter, null);
			    				while(tmpFiles.hasNext()){
			    					File tmpFile = tmpFiles.next();
			    					if(tmpFile.getName().startsWith(filePrefix)){
			    						fileName = tmpFile.getName();
			    					}
			    				}
								DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
								dout.writeDicomFile(segDicom);
								dout.close();								
			    			}
			    		}
			    	}		    		
				}
			}	
		}
	}		
	

	AVTRetrieve2Listener listener;
    public void addAVTListener(AVTRetrieve2Listener l) {        
        listener = l;          
    }
	void fireResultsAvailable(String targetElementID){
		//instead of passing this, pass targetElement ID
		AVTRetrieve2Event event = new AVTRetrieve2Event(targetElementID);         		
        listener.retriveCompleted(event);
	}
	
}
