/*
 * Copyright (c) 20078 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * Used to choose files from the local file system
 */
public class HostFileChooser extends JFileChooser {	
	File [] selectedFiles;
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	
	
	public HostFileChooser(Boolean multiSelection, File currentDir){		
		setBackground(xipColor);		
		setMultiSelectionEnabled(multiSelection);		
		setCurrentDirectory(currentDir);
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		addChoosableFileFilter(new DcmFileFilter());
	}
	
	class DcmFileFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			String filename = file.getName();
			if(filename.endsWith(".dcm")) {
				return true;
			} else if (filename.endsWith("")) {
				/*boolean isDicom = DicomUtil.isDICOM(file);
				if(isDicom){
					return true;
				}*/
				return false;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "*.dcm";
		}
		
	}
}
