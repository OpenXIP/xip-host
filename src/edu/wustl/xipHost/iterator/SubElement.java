/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

/**
 * @author Jaroslaw Krych
 *
 */
public class SubElement {
	Criteria criteria;
	String path;
	
	public SubElement(Criteria criteria, String path) {
		super();
		this.criteria = criteria;
		this.path = path;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public String getPath() {
		return path;
	}
}
