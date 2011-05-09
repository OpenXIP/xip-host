/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationManagerFactory {
	private static ApplicationManager appMgr = new ApplicationManagerImpl();
	
	private ApplicationManagerFactory(){}
	
	public static ApplicationManager getInstance(){
		return appMgr;
	}
	
}
