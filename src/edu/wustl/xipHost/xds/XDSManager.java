/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.List;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import com.pixelmed.dicom.AttributeList;

/**
 * @author Jaroslaw Krych
 *
 */
public interface XDSManager {
	public abstract List<XDSPatientIDResponse> queryPatientIDs(AttributeList queryKeys);
	public abstract XDSQueryResponseType queryDocuments(String [] patientID);
	public abstract boolean retrieveDocuemnts();
}
