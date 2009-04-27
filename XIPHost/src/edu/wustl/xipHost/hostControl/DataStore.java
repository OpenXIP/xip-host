/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.util.List;

import org.nema.dicom.wg23.ArrayOfObjectDescriptor;
import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfUUID;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt.AVTStore;
import edu.wustl.xipHost.caGrid.AimStore;

/**
 * @author Jaroslaw Krych
 *
 */
public class DataStore implements Runnable {

	AvailableData availableData;
	Application app;
	
	public DataStore(AvailableData availableData, Application app){
		this.availableData = availableData;
		this.app = app;
	}
	
	public void run() {
		//1. It is excpected for IA application that AIM object will is added to the top AvailableData descriptors list.
		//2. DICOM attachemnt objects are excpected to be added to the series of the AvailableData
		//3. When storing AIM pairs of AIM plus DICOM degmentation objects are expected
		
		ObjectDescriptor aimDesc = availableData.getObjectDescriptors().getObjectDescriptor().get(0);		
		ObjectDescriptor dicomSegDesc = availableData.getPatients().getPatient().get(0).getStudies().getStudy().get(0).getSeries().getSeries().get(0).getObjectDescriptors().getObjectDescriptor().get(0);
		ArrayOfUUID arrayUUIDs = new ArrayOfUUID();
		List<Uuid> listUUIDs = arrayUUIDs.getUuid();		
		listUUIDs.add(aimDesc.getUuid());
		listUUIDs.add(dicomSegDesc.getUuid());
		ArrayOfObjectLocator arrayOfObjectLocator = app.getClientToApplication().getDataAsFile(arrayUUIDs, false);
		if(arrayOfObjectLocator == null){
			return;
		}else{
			List<ObjectLocator> objLocs = arrayOfObjectLocator.getObjectLocator();
			AVTStore avtStore = new AVTStore(objLocs);
			Thread t = new Thread(avtStore);
			t.start();
			boolean submitToOSU = false;
			if(submitToOSU){
				AimStore aimStore = new AimStore(objLocs, null);
				Thread t2 = new Thread(aimStore);
				t2.start();
			}			
		}		
	}
}
