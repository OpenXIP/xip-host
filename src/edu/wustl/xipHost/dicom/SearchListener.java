/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface SearchListener extends EventListener {
	public void searchResultAvailable(SearchEvent e);
}
