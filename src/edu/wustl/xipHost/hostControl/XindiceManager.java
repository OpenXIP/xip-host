/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import org.jdom.Document;
import org.nema.dicom.wg23.Uuid;
import org.xmldb.api.base.XMLDBException;

/**
 * @author Jaroslaw Krych
 *
 */
public interface XindiceManager {
	public boolean createCollection(String collectionName);
	public boolean deleteCollection(String collectionName);
	public boolean addDocument(Document doc, String collectionName, Uuid objUUID);
	public boolean deleteDocument(String id, String collectionName);
	public boolean deleteAllDocuments(String collectionName);
	public String[] getModelUUIDs(String collectionName);
	public String [] getCollectionNames();
	public String[] query(String collectionName, Uuid modelUUID, String xpath);	
	public boolean startup() throws XMLDBException;
	public void shutdown();
}
