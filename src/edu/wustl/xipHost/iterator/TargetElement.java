/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class TargetElement {

	//id is patientId, studyInstanceUID or seriesInstanceUID
	String id;
	IterationTarget target;
	SearchResult subSearchResult;
	
	public TargetElement(String id, IterationTarget target, SearchResult subSearchResult) {
		this.id = id;
		this.target = target;
		this.subSearchResult = subSearchResult;
	}

	public String getId() {
		return id;
	}

	public IterationTarget getTarget() {
		return target;
	}

	public SearchResult getSubSearchResult() {
		return subSearchResult;
	}

}
