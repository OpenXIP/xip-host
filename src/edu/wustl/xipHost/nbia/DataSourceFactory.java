/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.nbia;

/**
 * @author Jaroslaw Krych
 *
 */
public class DataSourceFactory {
	private static DataSource ds = new NBIADataSourceImpl();
	
	private DataSourceFactory(){}
	
	public static DataSource getDataSource(){
		return ds;
	}
}
