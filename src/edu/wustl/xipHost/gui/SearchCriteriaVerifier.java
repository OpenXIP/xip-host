/**
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import com.pixelmed.dicom.AttributeList;

/**
 * @author David Maffitt
 *
 */
public interface SearchCriteriaVerifier {	
	public boolean verifyCriteria(AttributeList list);											
}
