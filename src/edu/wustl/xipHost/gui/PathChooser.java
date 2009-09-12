/*
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
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
