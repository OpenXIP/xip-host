/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.dicomdataservice.client.DICOMDataServiceClient;
import gov.nih.nci.ivi.helper.DICOMDataServiceHelper;


/**
 * @author Jaroslaw Krych
 *
 */
public class GridRetrieve implements Runnable {	
	CQLQuery cqlQuery;
	GridLocation gridLoc;
	File importDir;
	
	public GridRetrieve(CQLQuery cql, GridLocation gridLocation, File importLocation) throws IOException {
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
	
	List<File> files;
	public void run(){		
		try {
			files = retrieveDicomData(cqlQuery, gridLoc, importDir);
			fireUpdateUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}
	
	public List<File> getRetrievedFiles(){
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
	
	DICOMDataServiceHelper helper = new DICOMDataServiceHelper("./resources/NCIAModelMap.properties");					
	//DICOMDataServiceClient dicomClient;
	public List<File> retrieveDicomData(CQLQuery cqlQuery, GridLocation location, File importDir) throws IOException {						
		if(importDir == null){
			throw new NullPointerException();
		}		
		List<File> dicomFiles = new ArrayList<File>();		
		File inputDir = File.createTempFile("DICOM-XIPHOST", null, importDir);			
		importDir = inputDir;		
		inputDir.delete();
								
		/*try {
			System.err.println(ObjectSerializer.toString(cqlQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e) {			
			e.printStackTrace();
		}*/		
		try{						
			//dicomClient = new DICOMDataServiceClient(location.getAddress());
			//dicomClient.retrieveDICOMData(cqlQuery);
			helper.retrieveDICOMData(cqlQuery, location.getAddress(), importDir.getCanonicalPath());
		}catch(Exception e){									
			return dicomFiles;
		}
		/* Record what files were retrieved */
		File retFilesDir = importDir;
		String [] retrievedFiles = retFilesDir.list();
		for(int i = 0; i < retrievedFiles.length; i++){								
			dicomFiles.add(new File(importDir + File.separator + retrievedFiles[i]));
		}
		return dicomFiles;
	}
	
	
}
