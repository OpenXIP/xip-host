/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import java.util.EventListener;

/**
* @author Jaroslaw Krych
*
*/
public interface TargetIteratorListener extends EventListener {
	public void targetElementAvailable(IteratorElementEvent e);
	public void fullIteratorAvailable(IteratorEvent e);
}
