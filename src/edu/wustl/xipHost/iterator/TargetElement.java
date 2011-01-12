/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.util.List;

import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class TargetElement {

	//id is patientId, studyInstanceUID or seriesInstanceUID
	String id;
	List<SubElement> subElements;
	IterationTarget target;
	SearchResult subSearchResult;
	
	public TargetElement(String id, List<SubElement> subElements, IterationTarget target, SearchResult subSearchResult) {
		this.id = id;
		this.subElements = subElements;
		this.target = target;
		this.subSearchResult = subSearchResult;
	}

	public String getId() {
		return id;
	}

	public List<SubElement> getSubElements() {
		return subElements;
	}

	public IterationTarget getTarget() {
		return target;
	}

	public SearchResult getSubSearchResult() {
		return subSearchResult;
	}

}
