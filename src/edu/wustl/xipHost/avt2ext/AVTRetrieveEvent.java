/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

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
