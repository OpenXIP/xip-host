/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;
import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridSearchEvent extends EventObject {			
		public GridSearchEvent(GridQuery source){	
			super(source);
		}
}