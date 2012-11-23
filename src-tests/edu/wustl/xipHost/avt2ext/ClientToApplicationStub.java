/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import org.nema.dicom.PS3_19.ArrayOfObjectLocator;
import org.nema.dicom.PS3_19.ArrayOfUUID;
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
