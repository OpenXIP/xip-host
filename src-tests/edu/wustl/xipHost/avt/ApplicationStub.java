/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.util.List;

import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectLocator;

import edu.wustl.xipHost.application.Application;

/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationStub extends Application {

	/**
	 * 
	 */
	public ApplicationStub(String name, File exePath, String vendor, String version, File iconFile) {
		super(name, exePath, vendor, version, iconFile);
	}

	ClientToApplicationStub clientToApplication = new ClientToApplicationStub();
	public ClientToApplicationStub getClientToApplication(){
		return clientToApplication;
	}
	
	public void setObjectLocators(ArrayOfObjectLocator arrayObjLocs){
		clientToApplication.setObjectLocators(arrayObjLocs);
	}
	
}
