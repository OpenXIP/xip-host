/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.util.EventObject;

import edu.wustl.xipHost.dataModel.SearchResult;

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
