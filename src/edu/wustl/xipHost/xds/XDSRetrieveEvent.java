/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSRetrieveEvent extends EventObject {
	public XDSRetrieveEvent(XDSDocumentRetrieve source){
		super(source);
	}
}
