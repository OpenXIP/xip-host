/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridManagerFactory {
	private static GridManager gridMgr = new GridManagerImpl();
	
	private GridManagerFactory(){}
	
	public static GridManager getInstance(){
		return gridMgr;
	}
}
