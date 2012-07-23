/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.pdq;

import java.util.EventListener;
import java.util.List;

/**
 * @author Lawrence Tarbox, derived from code originally written by Jaroslaw Krych
 *
 */
public interface PDQSearchListener extends EventListener{
	public void patientIDsAvailable(List<PDQPatientIDResponse> patientIDs2);
}
