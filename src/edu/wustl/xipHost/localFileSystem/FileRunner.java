/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.io.File;
import edu.wustl.xipHost.dicom.BasicDicomParser2;
import edu.wustl.xipHost.dicom.DicomUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class FileRunner implements Runnable {		
	BasicDicomParser2 parser = new BasicDicomParser2();
	File item;
	String[][] map = null;	
	
	public FileRunner(File item){
		this.item = item;		
	}	
	
	public void run() {						
		if(DicomUtil.isDICOM(item)){						
			parser.parse(item);
			map = parser.getShortDicomHeader(item.toURI());				
			/*for(int i = 0; i < map.length; i++){				
			   System.out.println(map[i][0] + " " + map[i][1]);
			}*/			
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
