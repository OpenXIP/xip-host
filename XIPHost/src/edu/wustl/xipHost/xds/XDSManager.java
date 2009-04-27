/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;
import java.util.List;

import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public interface XDSManager {
	public abstract List<XDSPatientIDResponse> queryPatientIDs(AttributeList queryKeys);
	//public abstract XDSQueryResponseType queryDocuments(String [] patientID);
	public abstract SearchResult queryDocuments(String [] patientID);
	public abstract boolean retrieveDocuemnts();
	public abstract File retrieveDocument(DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId);
}
