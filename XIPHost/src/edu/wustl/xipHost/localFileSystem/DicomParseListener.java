/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.util.EventListener;
/**
 * @author Jaroslaw Krych
 *
 */
public interface DicomParseListener extends EventListener {
		public void dicomAvailable(DicomParseEvent e);
		public void nondicomAvailable(DicomParseEvent e);
}
