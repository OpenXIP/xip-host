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

package edu.wustl.xipHost.gui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Used to choose files from the local file system
 */
public class FileChooser {	
	protected File [] selectedFiles;
	String strTmp = new String();
	JFileChooser fc = new JFileChooser();;
	JFrame frame = new JFrame();
	Boolean multiSelection;
	
	public FileChooser(Boolean multiSelection){
		this.multiSelection = multiSelection;
		fc.setMultiSelectionEnabled(multiSelection);
		fc.setCurrentDirectory(new File(new File("C:/WUSTL/Tmp/").getAbsolutePath()));
	}
	public void displayFileChooser () {		
	    // Show open dialog; this method does not return until the dialog is closed
	    
	    int result = fc.showOpenDialog(frame);	    
	    //selectedFiles = fc.getSelectedFiles();
	   
	    
	    switch (result) {
	    case JFileChooser.APPROVE_OPTION:
	    	if(multiSelection){
	    		selectedFiles = fc.getSelectedFiles();
	    	}else{
	    		selectedFiles = new File[1]; 
	    		selectedFiles[0] = fc.getSelectedFile();
	    	}
	    		    	
	      break;
	    case JFileChooser.CANCEL_OPTION:
	      break;
	    case JFileChooser.ERROR_OPTION:
	      break;	    
		}
	}
	
	public File[] getSelectedFiles(){
		return selectedFiles;
	}	
	
	public void setDefaultTmpDir(String strTmpDir){
		strTmp = strTmpDir;
	}
	
	public static void main (String args[]){
		FileChooser fileChooser = new FileChooser(false);
		fileChooser.displayFileChooser();
		File [] selectedItems = fileChooser.getSelectedFiles();
		System.out.println("File Chooser " + selectedItems.length);
	}
}
