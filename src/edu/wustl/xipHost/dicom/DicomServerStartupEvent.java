/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.util.EventObject;

/**
 * @author Jarek Krych
 *
 */
public class DicomServerStartupEvent extends EventObject {

	public DicomServerStartupEvent(DicomServerStartup source){
		super(source);
	}
}
