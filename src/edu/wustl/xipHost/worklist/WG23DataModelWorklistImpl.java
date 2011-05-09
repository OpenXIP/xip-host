/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectLocator;

import edu.wustl.xipHost.wg23.WG23DataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class WG23DataModelWorklistImpl implements WG23DataModel{
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
