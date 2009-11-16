/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.util.EventListener;
/**
 * @author Jaroslaw Krych
 *
 */
public interface GridSearchListener extends EventListener {
	public void searchResultAvailable(GridSearchEvent e);
	public void notifyException(String message);
}
