/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.nci;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class NCIARetrieveEvent extends EventObject{
	public NCIARetrieveEvent(NCIARetrieve source){	
		super(source);
	}	
}
