/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class IteratorElementEvent extends EventObject {

	public IteratorElementEvent(TargetElement source) {
		super(source);
	}

}
