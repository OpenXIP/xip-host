/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface AVTListener extends EventListener{
	public void searchResultsAvailable(AVTSearchEvent e);
	public void retriveResultsAvailable(AVTRetrieveEvent e);
}
