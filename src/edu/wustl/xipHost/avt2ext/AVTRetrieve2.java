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
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.UID;
import org.nema.dicom.PS3_19.UUID;

import edu.wustl.xipHost.wg23.Uuid;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.iterator.TargetElement;

public class AVTRetrieve2 implements Retrieve {
	final static Logger logger = Logger.getLogger(AVTRetrieve2.class);
	ADFacade adService = AVTFactory.getADServiceInstance();
	TargetElement targetElement;
	RetrieveTarget retrieveTarget;
	File importDir;
	
	public AVTRetrieve2(File importDir){
		this.importDir = importDir;
	}
	
	public AVTRetrieve2(TargetElement targetElement, RetrieveTarget retrieveTarget, File importDir) throws IOException{
		this.targetElement = targetElement;
		this.retrieveTarget = retrieveTarget;
		this.importDir = importDir;
	}
	
	@Override
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) {
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
	Map<String, ObjectLocator> objectLocators;
	XMLOutputter outToXMLFile = new XMLOutputter();
	void retrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) throws IOException {		
		objectLocators = new HashMap<String, ObjectLocator>();
		SearchResult subSearchResult = targetElement.getSubSearchResult();
		Criteria originalCriteria = subSearchResult.getOriginalCriteria();
		Map<Integer, Object> dicomCriteria = originalCriteria.getDICOMCriteria();
		Map<String, Object> aimCriteria = originalCriteria.getAIMCriteria();
		List<Patient> patients = subSearchResult.getPatients();
		for(Patient patient : patients){
			dicomCriteria.put(Tag.PatientName, patient.getPatientName());
			dicomCriteria.put(Tag.PatientID, patient.getPatientID());
			List<Study> studies = patient.getStudies();
			for(Study study : studies){
				dicomCriteria.put(Tag.StudyInstanceUID, study.getStudyInstanceUID());
				List<Series> series = study.getSeries();
				for(Series oneSeries : series){					
					dicomCriteria.put(Tag.SeriesInstanceUID, oneSeries.getSeriesInstanceUID());
					if(aimCriteria == null){
						logger.debug("AD AIM criteria: " + aimCriteria);
					}else{
						logger.debug("AD AIM retrieve criteria:");
						Set<String> keys = aimCriteria.keySet();
						Iterator<String> iter = keys.iterator();
						while(iter.hasNext()){
							String key = iter.next();
							String value = (String) aimCriteria.get(key);
							if(!value.isEmpty()){
								logger.debug("Key: " + key + " Value: " + value);
							}					
						}				
					}
					File dirPath = importDir.getAbsoluteFile();
					List<ObjectDescriptor> objectDescriptors = new ArrayList<ObjectDescriptor>();
					List<Item> items = oneSeries.getItems();
					for(Item item : items){
						objectDescriptors.add(item.getObjectDescriptor());
					}
					if(retrieveTarget == RetrieveTarget.DICOM_AND_AIM){
						//Retrieve DICOM						
						//If oneSeries contains subset of items, narrow dicomCriteria to individual SOPInstanceUIDs
						//Then retrieve data item by item
						if(oneSeries.containsSubsetOfItems()){
							for(Item item : items){
								String itemSOPInstanceUID = item.getItemID();
								dicomCriteria.put(Tag.SOPInstanceUID, itemSOPInstanceUID);
								List<DicomObject> retrievedDICOM = adService.retrieveDicomObjs(dicomCriteria, aimCriteria);											
								int i = 0;
								for(i = 0; i < retrievedDICOM.size(); i++){
									DicomObject dicom = retrievedDICOM.get(i);							
									String filePrefix = dicom.getString(Tag.SOPInstanceUID);
									try {
										File file = new File(importDir.getAbsolutePath() + File.separatorChar + filePrefix);
										if(!file.exists()){
											file.createNewFile();
										}
										FileOutputStream fos = new FileOutputStream(file);
										BufferedOutputStream bos = new BufferedOutputStream(fos);
										DicomOutputStream dout = new DicomOutputStream(bos);
										dout.writeDicomFile(dicom);
										dout.close();
										ObjectLocator objLoc = new ObjectLocator();				
										UUID itemUUID = item.getObjectDescriptor().getDescriptorUuid();
										objLoc.setLocator(itemUUID);				
										objLoc.setSource(itemUUID);
										objLoc.setLength(file.length());
										objLoc.setOffset(0L);
										UID tsUID = new UID();
										tsUID.setUid(dout.getTransferSyntax().uid());
										objLoc.setTransferSyntax(tsUID);
										objLoc.setURI(file.getAbsolutePath()); 
										item.setObjectLocator(objLoc);
										objectLocators.put(itemUUID.getUuid(), objLoc);
									} catch (IOException e) {
										logger.error(e, e);
									}									
								}
								//Retrieve AIM		
								List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, aimCriteria);
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
							    	//retrievedFiles.add(outFile);
							    	ObjectLocator objLoc = new ObjectLocator();				
									UUID itemUUID = item.getObjectDescriptor().getDescriptorUuid();
									objLoc.setLocator(itemUUID);				
									objLoc.setSource(itemUUID);
									objLoc.setLength(outFile.length());
									objLoc.setOffset(0L);
									objLoc.setURI(outFile.getAbsolutePath()); 
									item.setObjectLocator(objLoc);
									objectLocators.put(itemUUID.getUuid(), objLoc);
							    	//Retrieve DICOM SEG
							    	//temporarily voided. AVTQuery needs to be modified to query for DICOM SEG objects
							    	//
							    	Set<String> dicomSegSOPInstanceUIDs = new HashSet<String>();
							    	List<DicomObject> segObjects = adService.retrieveSegmentationObjects(uid);
							    	for(int j = 0; j < segObjects.size(); j++){
							    		DicomObject dicom = segObjects.get(j);
							    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
							    		//Check if DICOM SEG was not serialized in reference to another AIM
							    		if(!dicomSegSOPInstanceUIDs.contains(sopInstanceUID)){
							    			dicomSegSOPInstanceUIDs.add(sopInstanceUID);
							    			DicomObject dicomSeg = adService.getDicomObject(sopInstanceUID);
							    			String message = "DICOM SEG " + sopInstanceUID + " cannot be loaded from file system!";
							    			if(dicomSeg == null){			    				
							    				throw new FileNotFoundException(message);
							    			} else {					 
							    				//TODO DICOM SEG tmp file not found e.g. DICOM SEG belongs to not specified Study for which TargetIteratorRunner was not requested					 					    			
						    					File outDicomSegFile = new File(dirPath + File.separator + sopInstanceUID);
					    						FileOutputStream fos = new FileOutputStream(outDicomSegFile);
					    						BufferedOutputStream bos = new BufferedOutputStream(fos);
					    						DicomOutputStream dout = new DicomOutputStream(bos);
					    						dout.writeDicomFile(dicomSeg);
					    						dout.close();
					    						//retrievedFiles.add(outDicomSegFile);
					    						ObjectLocator dicomSegObjLoc = new ObjectLocator();				
												Item itemDicomSeg = items.get(i);
												UUID dicomSegItemUUID = itemDicomSeg.getObjectDescriptor().getDescriptorUuid();
												dicomSegObjLoc.setLocator(dicomSegItemUUID);				
												dicomSegObjLoc.setSource(dicomSegItemUUID);
												dicomSegObjLoc.setLength(outDicomSegFile.length());
												dicomSegObjLoc.setOffset(0L);
												dicomSegObjLoc.setTransferSyntax(itemDicomSeg.getObjectDescriptor().getTransferSyntaxUID());
												dicomSegObjLoc.setURI(outDicomSegFile.getAbsolutePath()); 
												item.setObjectLocator(dicomSegObjLoc);
												objectLocators.put(dicomSegItemUUID.getUuid(), dicomSegObjLoc);										
							    			}
							    		}
							    	}	  
								}		
							}
							//Reset value of SOPInstanceUID in dicomCriteria
							dicomCriteria.remove(Tag.SOPInstanceUID);
						} else {
							List<DicomObject> retrievedDICOM = adService.retrieveDicomObjs(dicomCriteria, aimCriteria);											
							int i = 0;
							for(i = 0; i < retrievedDICOM.size(); i++){
								DicomObject dicom = retrievedDICOM.get(i);							
								String filePrefix = dicom.getString(Tag.SOPInstanceUID);
								try {
									File file = new File(importDir.getAbsolutePath() + File.separatorChar + filePrefix);
									if(!file.exists()){
										file.createNewFile();
									}
									FileOutputStream fos = new FileOutputStream(file);
									BufferedOutputStream bos = new BufferedOutputStream(fos);
									DicomOutputStream dout = new DicomOutputStream(bos);
									dout.writeDicomFile(dicom);
									dout.close();
									ObjectLocator objLoc = new ObjectLocator();				
									Item item = items.get(i);
									UUID itemUUID = item.getObjectDescriptor().getDescriptorUuid();
									objLoc.setLocator(itemUUID);				
									objLoc.setSource(itemUUID);
									objLoc.setLength(file.length());
									objLoc.setOffset(0L);
									UID tsUID = new UID();
									tsUID.setUid(dout.getTransferSyntax().uid());
									objLoc.setTransferSyntax(tsUID);
									objLoc.setURI(file.getAbsolutePath()); 
									item.setObjectLocator(objLoc);
									objectLocators.put(itemUUID.getUuid(), objLoc);
								} catch (IOException e) {
									logger.error(e, e);
								}									
							}
							
							//Retrieve AIM		
							List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, aimCriteria);
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
						    	//retrievedFiles.add(outFile);
						    	ObjectLocator objLoc = new ObjectLocator();				
								Item item = items.get(i);
								Uuid itemUUID = (Uuid) item.getObjectDescriptor().getDescriptorUuid();
								objLoc.setSource(itemUUID);
								objLoc.setLocator(itemUUID); // TODO generate a new UUID?  Maybe not.		
								objLoc.setOffset(0L);
								objLoc.setLength(outFile.length());
								objLoc.setURI(outFile.getAbsolutePath()); 
								item.setObjectLocator(objLoc);
								objectLocators.put(itemUUID.getUuid(), objLoc);
						    	//Retrieve DICOM SEG
						    	//temporarily voided. AVTQuery needs to be modified to query for DICOM SEG objects
						    	//
						    	Set<String> dicomSegSOPInstanceUIDs = new HashSet<String>();
						    	List<DicomObject> segObjects = adService.retrieveSegmentationObjects(uid);
						    	for(int j = 0; j < segObjects.size(); j++){
						    		DicomObject dicom = segObjects.get(j);
						    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
						    		//Check if DICOM SEG was not serialized in reference to another AIM
						    		if(!dicomSegSOPInstanceUIDs.contains(sopInstanceUID)){
						    			dicomSegSOPInstanceUIDs.add(sopInstanceUID);
						    			DicomObject dicomSeg = adService.getDicomObject(sopInstanceUID);
						    			String message = "DICOM SEG " + sopInstanceUID + " cannot be loaded from file system!";
						    			if(dicomSeg == null){			    				
						    				throw new FileNotFoundException(message);
						    			} else {					 
						    				//TODO DICOM SEG tmp file not found e.g. DICOM SEG belongs to not specified Study for which TargetIteratorRunner was not requested	
						    				//TODO make sure the transfer syntax is on the requested transfer syntax list
					    					File outDicomSegFile = new File(dirPath + File.separator + sopInstanceUID);
				    						FileOutputStream fos = new FileOutputStream(outDicomSegFile);
				    						BufferedOutputStream bos = new BufferedOutputStream(fos);
				    						DicomOutputStream dout = new DicomOutputStream(bos);
				    						dout.writeDicomFile(dicomSeg);
				    						dout.close();
				    						//retrievedFiles.add(outDicomSegFile);
				    						ObjectLocator dicomSegObjLoc = new ObjectLocator();				
											Item itemDicomSeg = items.get(i);
											UUID dicomSegItemUUID = itemDicomSeg.getObjectDescriptor().getDescriptorUuid();
											dicomSegObjLoc.setLocator(itemUUID);				
											dicomSegObjLoc.setSource(itemUUID);
											dicomSegObjLoc.setLength(outDicomSegFile.length());
											dicomSegObjLoc.setOffset(0L);
											UID tsUID = new UID();
											tsUID.setUid(dout.getTransferSyntax().uid());
											objLoc.setTransferSyntax(tsUID);
											dicomSegObjLoc.setURI(outDicomSegFile.getAbsolutePath()); 
											item.setObjectLocator(dicomSegObjLoc);
											objectLocators.put(dicomSegItemUUID.getUuid(), dicomSegObjLoc);										
						    			}
						    		}
						    	}	  
							}	
						}
					}
					//Reset Series level dicomCriteria
					dicomCriteria.remove(Tag.SeriesInstanceUID);
				}
				//Reset Study level dicomCriteria
				dicomCriteria.remove(Tag.StudyInstanceUID);
			}
			//Reset Patient level dicomCriteria
			dicomCriteria.remove(Tag.PatientName);
			dicomCriteria.remove(Tag.PatientID);
		}
	}		
	

	void fireResultsAvailable(String targetElementID){
		RetrieveEvent event = new RetrieveEvent(targetElementID);         		        
		listener.retrieveResultsAvailable(event);
	}

	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;
	}	
	
	public Map<String, ObjectLocator> getObjectLocators(){
		return objectLocators;
	}

	RetrieveListener l; 
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		this.l = l;		
	}

	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria,
			Map<String, Object> aimCriteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Object criteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImportDir(File importDir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		// TODO Auto-generated method stub
		
	}
}
