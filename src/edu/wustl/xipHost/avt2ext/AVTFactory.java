/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import org.apache.log4j.Logger;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.FacadeManager;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTFactory {
	final static Logger logger = Logger.getLogger(AVTFactory.class);
	private static ADFacade avtMgr = FacadeManager.getFacade();
	
	private AVTFactory(){
		try {
			Class.forName("com.siemens.scr.avt.ad.api.impl.DefaultADFacadeImpl");
		} catch (ClassNotFoundException e) {
			logger.error(e, e);
		}			
	};
	
	public static ADFacade getADServiceInstance(){				
		return avtMgr;
	}
	
}
