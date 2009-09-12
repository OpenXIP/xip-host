/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;


/**
 * @author Jaroslaw Krych
 *
 */
public class FileManagerFactory {
	static FileManager fileMgr = new FileManagerImpl();
	
	private FileManagerFactory(){}
	
	public static FileManager getInstance(){
		return fileMgr;
	}
}
