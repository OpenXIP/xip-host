/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.ObjectLocator;

import edu.wustl.xipHost.wg23.WG23DataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class WG23DataModelFileSystemImpl implements WG23DataModel{
	AvailableData availableData;
	ObjectLocator[] objLocators;
	public AvailableData getAvailableData() {		
		return availableData;
	}
	public ObjectLocator[] getObjectLocators() {
		return objLocators;
	}

	public void setAvailableData(AvailableData availableData){
		this.availableData = availableData;
	}
	
	public void setObjectLocators(ObjectLocator[] objLocators){
		this.objLocators = objLocators;
	}	
}
