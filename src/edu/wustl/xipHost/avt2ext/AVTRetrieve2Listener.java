/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface AVTRetrieve2Listener extends EventListener{
	public void retriveCompleted(AVTRetrieve2Event e);
}
