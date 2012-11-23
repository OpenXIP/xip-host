/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.ObjectLocator;

/**
 * @author Jaroslaw Krych
 *
 */
public interface WG23DataModel {
	public AvailableData getAvailableData();
	public ObjectLocator[] getObjectLocators();	
}
