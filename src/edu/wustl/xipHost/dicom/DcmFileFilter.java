/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;



/**
 * @author Jaroslaw Krych
 *
 */
public class DcmFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		try {
			if(DicomUtil.mimeType(file).equalsIgnoreCase("application/dicom")){
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			if (file.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}
	}
}