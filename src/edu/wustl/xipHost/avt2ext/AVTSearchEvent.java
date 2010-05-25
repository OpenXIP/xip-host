/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

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
