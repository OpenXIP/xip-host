/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

/**
 * @author Jaroslaw Krych
 *
 */
public class WorklistFactory {
	private static Worklist worklist = new XMLWorklistImpl();
	
	private WorklistFactory(){}
	
	public static Worklist getInstance(){
		return worklist;
	}	
}
