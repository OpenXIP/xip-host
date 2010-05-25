/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfUUID;
import edu.wustl.xipHost.wg23.ClientToApplication;

/**
 * @author Jaroslaw Krych
 *
 */
public class ClientToApplicationStub extends ClientToApplication {

	/**
	 * 
	 */
	public ClientToApplicationStub() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayOfObjectLocator getDataAsFile(ArrayOfUUID uuids, boolean includeBulkData) {		
		return arrayObjLocs;
	}

	ArrayOfObjectLocator arrayObjLocs;
	public void setObjectLocators(ArrayOfObjectLocator arrayObjLocs){
		this.arrayObjLocs = arrayObjLocs;
	}
}
