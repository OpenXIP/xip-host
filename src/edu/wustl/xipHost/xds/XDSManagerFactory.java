/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSManagerFactory {
	private static XDSManager xdsMgr = new XDSManagerImpl();
	private XDSManagerFactory(){};
	
	public static XDSManager getInstance(){
		return xdsMgr;
	}
}
