/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class IteratorEvent extends EventObject {

	public IteratorEvent(TargetIterator source) {
		super(source);
	}

}
