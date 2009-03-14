/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.impl.DefaultADFacadeImpl;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTFactory {
	private static ADFacade avtMgr = new DefaultADFacadeImpl();
				
	private AVTFactory(){};
	
	public static ADFacade getADServiceInstance(){				
		return avtMgr;
	}
	
}
