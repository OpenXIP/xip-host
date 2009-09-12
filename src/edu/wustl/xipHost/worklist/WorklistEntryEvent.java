/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class WorklistEntryEvent extends EventObject {
	public WorklistEntryEvent(WorklistEntry source){	
		super(source);
	}
}
