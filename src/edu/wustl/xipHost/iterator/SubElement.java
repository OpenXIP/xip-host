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
	
	public SubElement(Criteria criteria) {
		super();
		this.criteria = criteria;
	}

	public Criteria getCriteria() {
		return criteria;
	}
}
