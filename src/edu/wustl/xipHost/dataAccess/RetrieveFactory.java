/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import org.apache.log4j.Logger;

public class RetrieveFactory {
	final static Logger logger = Logger.getLogger(RetrieveFactory.class);
	private RetrieveFactory() {}
	
	public static Retrieve getInstance(String classFullyQualifiedName){
		Retrieve retrieve = null;
		try {
			retrieve = (Retrieve) Class.forName(classFullyQualifiedName).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error(e, e);
			return null;
		} catch (InstantiationException e) {
			logger.error(e, e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e, e);
			return null;
		}
		return retrieve;
	}

}
