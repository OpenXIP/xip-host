/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationTerminationEvent extends EventObject {

	public ApplicationTerminationEvent(Application source) {
		super(source);
	}

}
