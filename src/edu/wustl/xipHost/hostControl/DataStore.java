/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.util.List;

import org.apache.log4j.Logger;
import org.nema.dicom.PS3_19.ArrayOfObjectLocator;
import org.nema.dicom.PS3_19.ArrayOfUUID;
import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.Patient;
import org.nema.dicom.PS3_19.Series;
import org.nema.dicom.PS3_19.Study;
import org.nema.dicom.PS3_19.UUID;

import edu.wustl.xipHost.wg23.Uuid;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt2ext.AVTStore;
import edu.wustl.xipHost.caGrid.AimStore;
import edu.wustl.xipHost.dicom.DICOMStore;

/**
 * @author Jaroslaw Krych
 *
 */
public class DataStore implements Runnable {
	final static Logger logger = Logger.getLogger(DataStore.class);
	AvailableData availableData;
	Application app;
	
	public DataStore(AvailableData availableData, Application app){
		this.availableData = availableData;
		this.app = app;
	}
	
	Thread t;
	/**
	 * getAVTStoreThread used in JUnit testing
	 */
	public Thread getAVTStoreThread(){
		return t;
	}
	
	public void run() {
		// Parse AvailableData for AIM and DICOM descriptors at all levels	
		ArrayOfUUID arrayUUIDs = new ArrayOfUUID();
		List<UUID> listUUIDs = arrayUUIDs.getUUID();		
		
		if(availableData.getObjectDescriptors() != null){
			List<ObjectDescriptor> descs = availableData.getObjectDescriptors().getObjectDescriptor();
			for(int i = 0; i < descs.size(); i++){
				ObjectDescriptor desc = availableData.getObjectDescriptors().getObjectDescriptor().get(i);		
				listUUIDs.add(desc.getDescriptorUuid());
			}
		}
		if (availableData.getPatients() != null){
			List<Patient> patients = availableData.getPatients().getPatient();
			for (int i =0 ; i < patients.size(); i++){
				Patient patient = patients.get(i);
				if(patient.getObjectDescriptors() != null){
					List<ObjectDescriptor> patientDescs = patient.getObjectDescriptors().getObjectDescriptor();
					for(int j = 0; j < patientDescs.size(); j++){
						ObjectDescriptor patientDesc = patientDescs.get(j);
						listUUIDs.add(patientDesc.getDescriptorUuid());
					}
				}
				if(patient.getStudies() != null){
					List<Study> studies = patient.getStudies().getStudy();
					for(int j = 0; j < studies.size(); j++){
						Study study = studies.get(j);
						if(study.getObjectDescriptors() != null){
							List<ObjectDescriptor> studyDescs = study.getObjectDescriptors().getObjectDescriptor();
							for(int k = 0; k < studyDescs.size(); k++){
								ObjectDescriptor studyDesc = studyDescs.get(k);
								listUUIDs.add(studyDesc.getDescriptorUuid());
							}
						}						
						if(study.getSeries() != null){
							List<Series> series = study.getSeries().getSeries();
							for(int k = 0; k < series.size(); k++){
								Series oneSeries = series.get(k);
								List<ObjectDescriptor> seriesDescs = oneSeries.getObjectDescriptors().getObjectDescriptor();
								for(int m = 0; m < seriesDescs.size(); m++){
									ObjectDescriptor seriesDesc = seriesDescs.get(m);
									listUUIDs.add(seriesDesc.getDescriptorUuid());
								}
							}
						}						
					}
				}	
			}
		}
		ArrayOfObjectLocator arrayOfObjectLocator = app.getClientToApplication().getDataAsFile(arrayUUIDs, false);
		if(arrayOfObjectLocator == null){
			logger.warn("Array of recieved ObjectLocators is null.");
			return;
		}else{
			List<ObjectLocator> objLocs = arrayOfObjectLocator.getObjectLocator();
			//TODO: Get the storage options from some config file, instead of being hardcoded
			boolean submitToAVT2EXT = true;
			if(submitToAVT2EXT){
				logger.info("Starting 'store to AD'");
				AVTStore avtStore = new AVTStore(objLocs);
				t = new Thread(avtStore);
				t.start();
			}
			boolean submitToAIME = false;
			if(submitToAIME){
				logger.info("Starting 'store to AIME'");
				AimStore aimStore = new AimStore(objLocs, null);
				Thread t2 = new Thread(aimStore);
				t2.start();
			}			
			boolean submitToDICOM = true;
			if(submitToDICOM){
				logger.info("Starting 'store to DICOM'");
				DICOMStore dicomStore = new DICOMStore(objLocs);
				Thread t2 = new Thread(dicomStore);
				t2.start();
			}			
		}		
	}
}
