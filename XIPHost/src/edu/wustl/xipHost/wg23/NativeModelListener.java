/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.util.EventListener;

import org.jdom.Document;
import org.nema.dicom.wg23.Uuid;

/**
 * @author Jaroslaw Krych
 *
 */
public interface NativeModelListener extends EventListener {
	public void nativeModelAvailable(Document doc, Uuid objUUID);
	public void nativeModelAvailable(String xmlNativeModel);
}
