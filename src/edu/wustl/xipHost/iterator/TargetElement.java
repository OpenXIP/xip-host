/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.iterator;

import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class TargetElement {

	//id is patientId, studyInstanceUID or seriesInstanceUID
	String id;
	List<SubElement> subElements;
	IterationTarget target;
	
	public TargetElement(String id, List<SubElement> subElements, IterationTarget target) {
		this.id = id;
		this.subElements = subElements;
		this.target = target;
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

}
