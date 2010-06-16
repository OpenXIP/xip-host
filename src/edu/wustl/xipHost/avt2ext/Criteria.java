/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.util.Map;

/**
 * @author Jaroslaw Krych
 *
 */
public class Criteria {
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	
	/**
	 * 
	 */
	public Criteria(Map<Integer, Object> dicomCriteria,Map<String, Object> aimCriteria) {
		this.dicomCriteria = dicomCriteria;
		this.aimCriteria = aimCriteria;
	}
	
	
	public Map<Integer, Object> getDICOMCriteria(){
		return dicomCriteria;
	}
	
	public Map<String, Object> getAIMCriteria(){
		return aimCriteria;
	}

}
