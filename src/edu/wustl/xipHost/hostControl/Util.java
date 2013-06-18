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

package edu.wustl.xipHost.hostControl;

import java.io.File;
import java.io.IOException;

/**
 * <font  face="Tahoma" size="2" color="Black">
 * Allows for creating/deleting TmpXIP and OutXIP directories, MIMEType checking.<br></br>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class Util {		
	
	/**
     * Creates a temp directory in the System temp directory.
     * Host does not make use of this method
     * Added for the future use
     */    
    public static File create(String prefix, String suffix) throws IOException {
    	File tempFile = File.createTempFile(prefix, suffix);
    	if (!tempFile.delete()) throw new IOException();
        if (!tempFile.mkdir()) throw new IOException();
    	return tempFile;
    }
    /**
     * Creates a temp directory in a given directory.
     */    
    public static File create(String prefix, String suffix, File directory) throws IOException {               
    	File tempFile = File.createTempFile(prefix, suffix, directory);
        if (!tempFile.delete()) throw new IOException();
        if (!tempFile.mkdir()) throw new IOException();
        return tempFile;        
    }
	
    public static Boolean createOutputDir(String dirName){
		boolean exists = (new File(dirName)).exists();
	    if (exists) {
	        // File or directory exists	    	
	    	return new Boolean(true);
	    } else {
	        // File or directory does not exist
	    	File file = new File(dirName);
	    	//Delete on exit will not work when directory is not empty
	    	//file.deleteOnExit();	    	
	    	return file.mkdir();
	    }		
	}    		           
    
    public static Boolean deleteHostTmpFiles(File parentOfTmpDir){    	   	   	
    	if(parentOfTmpDir.exists() == false){return false;}
    	File[] filesToDelete = parentOfTmpDir.listFiles();
		for(int i = 0; i < filesToDelete.length; i++){
			if(validateHostTmpDir(filesToDelete[i])){
				File dir = filesToDelete[i];
				delete(dir);
			}													
		}
		return true;
    }    
    
   static boolean validateHostTmpDir(File tmpDir){
    	if(tmpDir == null){return false;}    	  	
    	if(!tmpDir.exists()){
    		return false;
    	}    	
		if(tmpDir.isDirectory() && tmpDir.getName().startsWith("TmpXIP") && tmpDir.getName().endsWith(".tmp")){
				return true;
		}		
    	return false;
    }
	
	public static boolean delete(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = delete(children[i]);
                if (!success) {
                    return false;
                }
            }
        }    
        // The directory is now empty so delete it
        return dir.delete();
    }
}
