/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.xindice.client.xmldb.services.CollectionManager;
import org.apache.xindice.client.xmldb.services.DatabaseInstanceManager;
import org.apache.xindice.tools.command.Command;
import org.apache.xindice.util.XindiceException;
import org.apache.xindice.xml.dom.DOMParser;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.Uuid;

/**
 * @author Jaroslaw Krych
 *
 */
public class XindiceManagerImpl implements XindiceManager{
		
	static String uri = "xmldb:xindice-embed:///db/";
	//Collaction names
	static List<String> collNames = new ArrayList<String>();
	
	/**
	 * 
	 * @param collectionName - valid collectionName is a sequence of characters a-z and A-Z 
	 * @return
	 */
	public boolean createCollection(String collectionName){
		if(collectionName == null){return false;}		
		String str = "[a-zA-Z]+";		
        Pattern colNamePattern = Pattern.compile(str);             
        boolean matches = colNamePattern.matcher(collectionName).matches();
        if(!matches){
        	//return false;
        }		
		//String uri = "xmldb:xindice://localhost:8080/db/mycollection";		    	      
        Collection col = null;
        try {
	    	col = DatabaseManager.getCollection(uri);
			//service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			CollectionManager service = (CollectionManager) col.getService("CollectionManager", "1.0");	    
		    String collectionConfig = "<collection compressed=\"true\" " +
			"            name=\"" + collectionName + "\">" +
			"   <filer class=\"org.apache.xindice.core.filer.BTreeFiler\"/>" +
			"</collection>";		    
		    service.createCollection(collectionName, DOMParser.toDocument(collectionConfig));		    
		    //System.out.println("Collection " + collectionName + " created.");
		    collNames.add(collectionName);
		    return true;
		} catch (XMLDBException e) {
			return false;
		} catch (XindiceException e) {
			return false;
		}			    
		finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e) {
					return false;
				}
		    }
		}
	}
	
	public boolean deleteCollection(String collectionName){		
		if(collectionName == null){return false;}	
		Collection col = null;
		try {
			col = DatabaseManager.getCollection(uri);
			//service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		    CollectionManager service = (CollectionManager) col.getService("CollectionManager", "1.0");	
			service.dropCollection(collectionName);
			return true;
		} catch (XMLDBException e) {
			return false;
		}
		finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e) {
					return false;
				}
		    }
		}
	}
	
	
	//TODO
	/**
	 * UUID of the added document has a following form:
	 * "wg23NM-" + UUID of the ObjectLocator
	 */
	public boolean addDocument(Document doc, String collectionName, Uuid objUUID){		
		Collection col = null;
		try {
			String data = new XMLOutputter().outputString(doc);				
			col = DatabaseManager.getCollection("xmldb:xindice-embed:///db/" + collectionName);						
			String nmUUID = "wg23NM-" + objUUID.getUuid();
			XMLResource document = (XMLResource) col.createResource(nmUUID, "XMLResource");			
			document.setContent(data);
			col.storeResource(document);
			//System.out.println("Document " + xmlFile.toURI().toURL().toExternalForm() + " inserted.");			
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e) {
					return false;
				}
		    }
		}
		
	}
	
	
	public boolean deleteDocument(String id, String collectionName){
		Collection col = null;
		try {			
			col = DatabaseManager.getCollection("xmldb:xindice-embed:///db/" + collectionName);						
			Resource document = col.getResource(id);
			col.removeResource(document);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e) {
					return false;
				}
		    }
		}		
	}
	
	public boolean deleteAllDocuments(String collectionName){
		String[] uuids = getModelUUIDs(collectionName);
		if(uuids == null){
			return true;
		}else{
			for(int i = 0; i < uuids.length; i++){
				deleteDocument(uuids[i], collectionName);
			}
			return true;
		}
	}
		
	String readFileFromDisk(File file) throws Exception {		 
		  FileInputStream insr = new FileInputStream(file);
		  byte[] fileBuffer = new byte[(int)file.length()];		
		  insr.read(fileBuffer);
		  insr.close();		
		  return new String(fileBuffer);
	}	
	
	public String[] getModelUUIDs(String collectionName){
		Collection col = null;
		try {
			col = DatabaseManager.getCollection("xmldb:xindice-embed:///db/" + collectionName);
			if(col == null){return null;}
			String[] uuids = col.listResources();
			/*for(int i = 0; i < uuids.length; i++){
				System.out.println(uuids[i]);
			}*/
			return uuids;
		} catch (XMLDBException e) {
			//e.printStackTrace();
			return null;
		}
		finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e) {
					return null;
				}
		    }
		}	
	}
	public String [] getCollectionNames(){
		String[] names = new String[collNames.size()];
		return collNames.toArray(names);				
	}
		
	public String[] query(String collectionName, Uuid modelUUID, String xpath){		
		if(collectionName == null || modelUUID == null || xpath == null){
			return null;
		}
		Collection col = null;
		try {
			col = DatabaseManager.getCollection("xmldb:xindice-embed:///db/" + collectionName);
			if(col == null){return null;}
			XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			//The following line associates ns with a URI. It would be used for native model XML with namespace
			//association.
			//service.setNamespace("ns", "http://mycompany.com/dicom/metadata");
		    //ResourceSet resultSet = service.query(xpath);			    
			String name = modelUUID.getUuid();
			//System.out.println("Querying ..." + name + " " + xpath);
			ResourceSet resultSet = service.queryResource(name, xpath);
		    ResourceIterator results = resultSet.getIterator();		    
		    List<String> listResults = new ArrayList<String>(); 
		    while (results.hasMoreResources()) {
		    	Resource res = results.nextResource();			    	
		    	listResults.add((String) res.getContent());
		    	//System.out.println((String) res.getContent());
		    }
		    String[] queryResults = new String[listResults.size()];
		    listResults.toArray(queryResults);
		    return queryResults;
		} catch (XMLDBException e) {
			//e.printStackTrace();
			return null;
		}
		finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e) {
					return null;
				}
		    }
		}	
	}	
		
	public void shutdown(){		
		
		//String root = "xmldb:xindice://localhost:8080/db/";
		String root = "xmldb:xindice-embed:///db/";
	    Collection rootCollection;
		try {
			rootCollection = DatabaseManager.getCollection(root);			
			DatabaseInstanceManager mgr = (DatabaseInstanceManager) rootCollection.getService("DatabaseInstanceManager", 
		    		Command.XMLDBAPIVERSION);
		    mgr.shutdown();
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		    	   
	}
	
	public boolean startup() throws XMLDBException {
		try {
		    //String driver = "org.apache.xindice.client.xmldb.DatabaseImpl";
		    String driver = "org.apache.xindice.client.xmldb.embed.DatabaseImpl";
		    Class c = Class.forName(driver);		
		    Database database = (Database) c.newInstance();
		    DatabaseManager.registerDatabase(database);				    
		} catch (XMLDBException e) {
			//System.err.println("XML:DB Exception occured " + e.errorCode);
			throw new XMLDBException();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
			throw new XMLDBException();			
		} catch (InstantiationException e) {
			throw new XMLDBException();
		} catch (IllegalAccessException e) {
			throw new XMLDBException();
		}
		return true;
	}	
}
