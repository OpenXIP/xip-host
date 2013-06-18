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
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class PathChooser {

	protected File selectedPath;	
	JFileChooser fc = new JFileChooser();
	String strPath = new String();
	//String strOutOK = new String();
	//String strTmpCancel = new String();
		
	public void displayPathChooser (String path){				
		strPath = path;
		fc.setApproveButtonText("Select");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//fc.setMultiSelectionEnabled(false);
		try {
			//fc.setCurrentDirectory(new File(new File(".").getCanonicalPath()));
			fc.setCurrentDirectory(new File(new File(path).getCanonicalPath()));
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	    JFrame frame = new JFrame();    
	    int result = fc.showOpenDialog(frame); 
	        
	    switch (result) {
	    case JFileChooser.APPROVE_OPTION:	    	 	    	
	    	//selectedPath = fc.getCurrentDirectory().getPath();
	    	selectedPath = fc.getSelectedFile();
	    	strPath = selectedPath.getAbsolutePath();
	      break;
	    case JFileChooser.CANCEL_OPTION:
	    	//strPath = strPath;
	    	selectedPath = new File(strPath);
	      break;
	    case JFileChooser.ERROR_OPTION:
	      // The selection process did not complete successfully
	      break;	    
		}
	}
	
	public File getSelectedPath(){
		return selectedPath;
	}	
		
}
