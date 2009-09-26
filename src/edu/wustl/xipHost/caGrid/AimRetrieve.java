/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import edu.emory.cci.aim.stubs.service.AIMTCGADataService;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.helper.AIMDataServiceHelper;
import gov.nih.nci.ivi.helper.AIMTCGADataServiceHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;

/**
 * @author Jaroslaw Krych
 *
 */
public class AimRetrieve implements Runnable{

	CQLQuery cqlQuery;
	GridLocation gridLoc;
	File importDir;
	public AimRetrieve(CQLQuery cql, GridLocation gridLocation, File importLocation)throws IOException{
		cqlQuery = cql;
		gridLoc = gridLocation;
		if(importLocation == null){
			throw new NullPointerException();
		}else if(importLocation.exists() == false){
			throw new IOException();
		}else{
			importDir = importLocation;
		}	
	}
	
	
	List<File> retrievedAIMs;
	public void run() {
		try {
			retrievedAIMs = retrieve();
			fireUpdateUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<File> getRetrievedFiles(){
		return retrievedAIMs;
	}
			
	public List<File> retrieve() throws IOException{		
		File inputDir = File.createTempFile("AIM-XIPHOST", null, importDir);			
		importDir = inputDir;		
		inputDir.delete();
		if(importDir.exists() == false){
			importDir.mkdir();
		}
		if(cqlQuery == null){return null;}				
		List<File> files = new ArrayList<File>();
		//AIMDataServiceHelper helper = new AIMDataServiceHelper();
		AIMTCGADataServiceHelper aimHelper = new AIMTCGADataServiceHelper();
		try {			
			/*System.err.println(ObjectSerializer.toString(cqlQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));	*/
			//helper.retrieveAnnotations(cqlQuery, gridLoc.getAddress(), importDir.getCanonicalPath());
			aimHelper.retrieveAnnotations(cqlQuery, gridLoc.getAddress(), importDir.getCanonicalPath());
			File[] aims = importDir.listFiles();
			for(int i = 0; i < aims.length; i++){
				files.add(aims[i]);
			}			
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}								
		return files;		
	}		
	
	
	GridRetrieveListener listener;
    public void addGridRetrieveListener(GridRetrieveListener l) {        
        listener = l;          
    }
	void fireUpdateUI(){
		GridRetrieveEvent event = new GridRetrieveEvent(this);         		
        listener.importedFilesAvailable(event);
	}
}
