/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectLocator;

/**
 * @author Jaroslaw Krych
 *
 */
public interface WG23DataModel {
	public AvailableData getAvailableData();
	public ObjectLocator[] getObjectLocators();	
}
