/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.EventListener;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public interface XDSSearchListener extends EventListener{
	public void patientIDsAvailable(List<XDSPatientIDResponse> patientIDs2);
	public void documentsAvailable(XDSSearchEvent e);
}
