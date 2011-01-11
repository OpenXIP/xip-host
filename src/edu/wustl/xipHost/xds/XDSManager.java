/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Jaroslaw Krych
 *
 */
public interface XDSManager {
	public abstract List<XDSPatientIDResponse> queryPatientIDs(AttributeList queryKeys, PDQLocation pdqSupplier);
	//public abstract XDSQueryResponseType queryDocuments(String [] patientID);
	public abstract SearchResult queryDocuments(String [] patientID);
	public abstract boolean retrieveDocuemnts();
	public abstract File retrieveDocument(DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId);
	public abstract boolean loadPDQLocations(File file) throws IOException, JDOMException;
	public abstract boolean addPDQLocation(PDQLocation pdqLocation);
	public abstract boolean modifyPDQLocation(PDQLocation oldPDQLocation, PDQLocation newPDQLocation);
	public abstract boolean removePDQLocation(PDQLocation pdqLocation);
	public abstract boolean storePDQLocations(List<PDQLocation> locations, File file) throws FileNotFoundException;
	public abstract List<PDQLocation> getPDQLocations();	
	public abstract boolean runStartupSequence();
}
