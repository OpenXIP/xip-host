/*
 * Copyright (c) 20078 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * Used to choose files from the local file system
 */
public class HostFileChooser extends JFileChooser {	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	
	
	public HostFileChooser(Boolean multiSelection, File currentDir){		
		setBackground(xipColor);		
		setMultiSelectionEnabled(multiSelection);		
		setCurrentDirectory(currentDir);
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		addChoosableFileFilter(new DcmFileFilter());
	}
	
	class DcmFileFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			try {
				if(DicomUtil.mimeType(file).equalsIgnoreCase("application/dicom")){
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public String getDescription() {
			return "*.dcm";
		}
	}
}
