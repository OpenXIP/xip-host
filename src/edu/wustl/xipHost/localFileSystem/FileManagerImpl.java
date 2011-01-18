/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.awt.Cursor;
import java.io.File;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.gui.InputDialog;

/**
 * @author Jaroslaw Krych
 *
 */
//public class FileManagerImpl implements FileManager, DicomParseListener {
public class FileManagerImpl implements FileManager {
	final static Logger logger = Logger.getLogger(FileManagerImpl.class);
	InputDialog inputDialog = new InputDialog();
	//int numThreads = 3;
	//ExecutorService exeService = Executors.newFixedThreadPool(numThreads);			
	
	public FileManagerImpl(){

	}
	
	public void run(File[] items){				
		displayItems(items);
		inputDialog.display();
	}	
	
	void displayItems(File[] items){
		this.items = items;		
		inputDialog.setItems(items);
		inputDialog.updateUI();
	}
	
	
	
	File[] items;	
	File[] getSelectedItems(){
		return items;
	}		
	
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
}
