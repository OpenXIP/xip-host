/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.util.EventObject;

/**
 * @author Jarek Krych
 *
 */
public class StartupEvent extends EventObject {

	public StartupEvent(StartupRunner source){
		super(source);
	}
}
