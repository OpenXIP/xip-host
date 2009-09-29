/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.helper.AIMTCGADataServiceHelper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


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
		AIMTCGADataServiceHelper aimHelper = new AIMTCGADataServiceHelper();
		
		try {			
			System.err.println(ObjectSerializer.toString(cqlQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));	
			//aimHelper.retrieveAnnotations(cqlQuery, gridLoc.getAddress(), importDir.getCanonicalPath());
			try {
				Iterator iter2 = aimHelper.queryAnnotations(cqlQuery, gridLoc.getAddress());
				int ii = 0;
				while (iter2.hasNext()) {
					String xml = (String)iter2.next();
					File aimFile = File.createTempFile("AIM-RETRIEVED-AIME-", ".xml", importDir);
					FileOutputStream outStream = new FileOutputStream(aimFile);
					XMLOutputter outToXMLFile = new XMLOutputter();
					SAXBuilder builder = new SAXBuilder();
					Document document;
					InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
					document = builder.build(is);
					outToXMLFile.output(document, outStream);
			    	outStream.flush();
			    	outStream.close();   
					System.out.println("xml: " + xml);
					
					System.out.println("Result " + ++ii + ". ");
				}
				
			} catch (MalformedURIException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
