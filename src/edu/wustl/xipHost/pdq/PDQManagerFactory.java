/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.pdq;

/**
 * @author Lawrence Tarbox, derived from code originally created by Jaroslaw Krych
 *
 */
public class PDQManagerFactory {
	private static PDQManager pdqMgr = new PDQManagerImpl();
	private PDQManagerFactory(){};
	
	public static PDQManager getInstance(){
		return pdqMgr;
	}
}
