/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.pdq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Lawrence Tarbox, derived from code originally created by Jaroslaw Krych
 *
 */
public interface PDQManager {
	public abstract List<PDQPatientIDResponse> queryPatientIDs(AttributeList queryKeys, PDQLocation pdqSupplier);
	//public abstract XDSQueryResponseType queryDocuments(String [] patientID);
	public abstract boolean loadPDQLocations(File file) throws IOException, JDOMException;
	public abstract boolean addPDQLocation(PDQLocation pdqLocation);
	public abstract boolean modifyPDQLocation(PDQLocation oldPDQLocation, PDQLocation newPDQLocation);
	public abstract boolean removePDQLocation(PDQLocation pdqLocation);
	public abstract boolean storePDQLocations(List<PDQLocation> locations, File file) throws FileNotFoundException;
	public abstract List<PDQLocation> getPDQLocations();	
	public abstract boolean runStartupSequence();
}
