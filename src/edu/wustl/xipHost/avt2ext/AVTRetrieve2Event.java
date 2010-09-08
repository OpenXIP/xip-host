/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTRetrieve2Event extends EventObject {
	public AVTRetrieve2Event(String targetElementID){	
		super(targetElementID);
	}
}
