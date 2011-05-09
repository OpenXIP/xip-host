/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomManagerFactory {
	private static DicomManager dicomMgr = new DicomManagerImpl();
	
	private DicomManagerFactory(){}
	
	public static DicomManager getInstance(){
		return dicomMgr;
	}
}
