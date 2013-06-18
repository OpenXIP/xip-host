/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
