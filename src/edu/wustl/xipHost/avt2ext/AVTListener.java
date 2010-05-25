/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface AVTListener extends EventListener{
	public void searchResultsAvailable(AVTSearchEvent e);
	public void retriveResultsAvailable(AVTRetrieveEvent e);
	public void notifyException(String message);
}
