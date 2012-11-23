/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import java.util.EventObject;
import java.util.Map;

import org.nema.dicom.PS3_19.ObjectLocator;
/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveEvent extends EventObject {
	//TODO to be removed
	public RetrieveEvent(String targetElementID){	
		super(targetElementID);
	}
	
	public RetrieveEvent(Map<String, ObjectLocator> objectLocators){
		super(objectLocators);
	}
}
