/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSSearchEvent extends EventObject{
	public XDSSearchEvent(XDSDocumentQuery source){
		super(source);
	}
	public XDSSearchEvent(XDSPatientIDQuery source){
		super(source);
	}
}
