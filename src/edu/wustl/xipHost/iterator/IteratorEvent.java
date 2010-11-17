/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.util.EventObject;
import java.util.Iterator;

/**
 * @author Jaroslaw Krych
 *
 */
public class IteratorEvent extends EventObject {

	public IteratorEvent(Iterator<TargetElement> source) {
		super(source);
	}

}
