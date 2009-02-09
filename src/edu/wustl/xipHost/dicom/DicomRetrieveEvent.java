/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomRetrieveEvent extends EventObject {
	public DicomRetrieveEvent(DicomRetrieve source){
		super(source);
	}
}
