/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui.checkboxTree;

import java.util.EventObject;
import edu.wustl.xipHost.dataModel.SearchResult;

/**
 * @author Jaroslaw Krych
 *
 */
public class DataSelectionEvent extends EventObject {

	/**
	 * 
	 */
	public DataSelectionEvent(SearchResult selectedDataSearchResult) {
		super(selectedDataSearchResult);
	}

}
