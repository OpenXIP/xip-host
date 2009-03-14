/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.util.EventObject;
import edu.wustl.xipHost.dataModel.SearchResult;


/**
 * @author Jaroslaw Krych
 *
 */
public class AVTSearchEvent extends EventObject {
	public AVTSearchEvent(SearchResult source){	
		super(source);
	}
}
