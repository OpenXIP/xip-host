/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.net.URI;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class SubElement {
	Criteria criteria;
	String path;
	List<URI> dicomFileURIs;
	
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
	
	public void setPath(String strPath){
		path = strPath;
	}
	
	public List<URI> getFileURIs(){
		return dicomFileURIs;
	}
	
	public void setFileURIs(List<URI> dicomFileURIs){
		this.dicomFileURIs = dicomFileURIs;
	}
}
