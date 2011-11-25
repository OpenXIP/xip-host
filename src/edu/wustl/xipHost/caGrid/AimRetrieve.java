/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
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
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;


/**
 * @author Jaroslaw Krych
 *
 */
public class AimRetrieve implements Retrieve{

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
			//fireResultsAvailable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<File> getRetrievedFiles(){
		return retrievedAIMs;
	}
	
	Map<String, ObjectLocator> objectLocators;
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
					//System.out.println("xml: " + xml);					
					//System.out.println("AIM result " + ++ii + ". ");
					System.out.println(aimFile.getName());
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
	
	
	void fireResultsAvailable(String targetElementID){
		RetrieveEvent event = new RetrieveEvent(targetElementID);         		        
		listener.retrieveResultsAvailable(event);
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
