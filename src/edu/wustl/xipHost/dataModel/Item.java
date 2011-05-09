/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;

/**
 * @author Jaroslaw Krych
 *
 */
public interface Item {
	public String getItemID();
	public ObjectDescriptor getObjectDescriptor();
	public void setObjectDescriptor(ObjectDescriptor objDesc);
	public ObjectLocator getObjectLocator();
	public void setObjectLocator(ObjectLocator objLoc);
	public String toString();
}
