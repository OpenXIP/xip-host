/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import java.util.EventObject;
import java.util.Map;

import org.nema.dicom.wg23.ObjectLocator;
/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveEvent extends EventObject {
	
	public RetrieveEvent(Map<String, ObjectLocator> objectLocators){
		super(objectLocators);
	}
}
