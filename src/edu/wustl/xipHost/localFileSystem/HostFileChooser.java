/*
 * Copyright (c) 20078 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

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
		/*
		int result = showOpenDialog(new JFrame());	      	    
	    switch (result) {
	    case JFileChooser.APPROVE_OPTION:	    	
    		selectedFiles = this.getSelectedFiles();	    		
    		break;
	    case JFileChooser.CANCEL_OPTION:
	    	break;
	    case JFileChooser.ERROR_OPTION:
	    	break;	    
		}*/
	}
	
	public File[] getSelectedItems(){
		return selectedFiles;
	}
		
	public static void main (String args[]){
		new HostFileChooser(true, new File("C:/WUSTL/Tmp/RECIST"));
					
	}
}
