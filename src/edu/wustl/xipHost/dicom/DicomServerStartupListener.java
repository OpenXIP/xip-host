/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.util.EventListener;

/**
 * @author Jarek Krych
 *
 */
public interface DicomServerStartupListener extends EventListener {
	void dicomServerOn(DicomServerStartupEvent event);
}