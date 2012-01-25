/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.helper.DICOMDataServiceHelper;


/**
 * @author Jaroslaw Krych
 *
 */
public class GridRetrieve implements Retrieve {	
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
			retrieveDicomData(cqlQuery, gridLoc, importDir);
			
			//fireResultsAvailable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}
	
	public List<File> getRetrievedFiles(){
		return files;
	}
	
	
	void fireResultsAvailable(String targetElementID){
		RetrieveEvent event = new RetrieveEvent(targetElementID);         		        
		listener.retrieveResultsAvailable(event);
	}
	
	DICOMDataServiceHelper dicomHelper = new DICOMDataServiceHelper();					
	//NCIADataServiceHelper nciaHelper = new NCIADataServiceHelper();
	Map<String, ObjectLocator> objectLocators;
	public List<File> retrieveDicomData(CQLQuery cqlQuery, GridLocation location, File importDir) throws IOException {						
		if(importDir == null){
			throw new NullPointerException();
		}		
		List<File> dicomFiles = new ArrayList<File>();		
		File inputDir = File.createTempFile("DICOM-XIPHOST", null, importDir);			
		importDir = inputDir;		
		inputDir.delete();
		if(importDir.exists() == false){
			importDir.mkdir();
		}
		try {
			System.err.println(ObjectSerializer.toString(cqlQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e) {			
			e.printStackTrace();
		}		
		try{						
			if(location.getProtocolVersion().equalsIgnoreCase("DICOM")){
				dicomHelper.retrieveDICOMData(cqlQuery, location.getAddress(), inputDir.getCanonicalPath());				
			}else if(location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
				//nciaHelper does not retrieve data as of 10/08/2009
				//nciaHelper.retrieveDICOMData(cqlQuery, location.getAddress(), importDir.getCanonicalPath());
			}else{
				
			}			
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

	DataAccessListener listener;	
	
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria,
			Map<String, Object> aimCriteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Object criteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImportDir(File importDir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		// TODO Auto-generated method stub
		
	}
}
