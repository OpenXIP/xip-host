/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

/**
 * @author Jaroslaw Krych
 *
 */
public interface DataSource {
	public String getDataSourceId();
	public void setDataSourceId(String id);
	public String toString();
}
