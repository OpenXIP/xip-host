/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;
import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomParseEvent extends EventObject {			

		public DicomParseEvent(FileRunner source){	
				super(source);
		}
}