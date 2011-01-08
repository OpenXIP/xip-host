/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveEvent extends EventObject {
	public RetrieveEvent(String targetElementID){	
		super(targetElementID);
	}
}
