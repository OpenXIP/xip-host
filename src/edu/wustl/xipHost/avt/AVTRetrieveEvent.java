/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.util.EventObject;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTRetrieveEvent extends EventObject {
	public AVTRetrieveEvent(List<File> source){	
		super(source);
	}
}
