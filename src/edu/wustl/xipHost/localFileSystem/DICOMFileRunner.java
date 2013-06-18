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

import java.io.File;
import edu.wustl.xipHost.dicom.BasicDicomParser2;
import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class DICOMFileRunner implements Runnable {		
	BasicDicomParser2 parser = new BasicDicomParser2();
	File item;
	String[][] map = null;	
	
	public DICOMFileRunner(File item){
		this.item = item;		
	}	
	
	public void run() {						
		if(DicomUtil.isDICOM(item)){						
			parser.parse(item);
			map = parser.getShortDicomHeader(item.toURI());								
			notifyDicomParsed();			
		}else{
			notifyNonDICOMAvailable();
		}
	}
	
	DicomParseListener listener;
    public void addDicomParseListener(DicomParseListener l) {        
        listener = l;          
    }
	void notifyDicomParsed(){
		DicomParseEvent event = new DicomParseEvent(this);         		
        listener.dicomAvailable(event);
	}	
	
	void notifyNonDICOMAvailable(){
		DicomParseEvent event = new DicomParseEvent(this);         		
        listener.nondicomAvailable(event);
	}
		
	public File getItem(){
		return this.item;
	}
	public String[][] getParsingResult(){
		return map;
	}	
}
