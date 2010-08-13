/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.util.EventObject;
import edu.wustl.xipHost.gui.ApplicationBar.AppButton;

/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationEvent extends EventObject {
	
	public ApplicationEvent(AppButton source){
		super(source);
	}
}
