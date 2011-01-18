/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ObjectLocator;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.iterator.RetrieveTarget;
import edu.wustl.xipHost.iterator.TargetElement;

/**
 * @author Jaroslaw Krych
 *
 */
public class LocalFileSystemRetrieve implements Retrieve {
	final static Logger logger = Logger.getLogger(LocalFileSystemRetrieve.class);	
	SearchResult selectedDataSearchResult;
	TargetElement targetElement;
	RetrieveTarget retrieveTarget;
	/**
	 * 
	 */
	public LocalFileSystemRetrieve(SearchResult selectedDataSearchResult) {
		this.selectedDataSearchResult = selectedDataSearchResult;
	}
	
	@Override
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) {
		this.targetElement = targetElement;
		this.retrieveTarget = retrieveTarget;		
	}

	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;		
	}

	@Override
	public void run() {
		objectLocators = new HashMap<String, ObjectLocator>();
		List<Patient> patients = selectedDataSearchResult.getPatients();
		for(Patient patient : patients){
			List<Study> studies = patient.getStudies();
			for(Study study : studies){
				List<Series> series = study.getSeries();
				for(Series oneSeries : series){
					List<Item> items = oneSeries.getItems();
					for(Item item : items){
						String uuid = item.getObjectLocator().getUuid().getUuid();
						ObjectLocator objLoc = item.getObjectLocator();
						objectLocators.put(uuid, objLoc);
					}
				}
			}
		}
		fireResultsAvailable(targetElement.getId());		
	}
	
	void fireResultsAvailable(String targetElementID){
		RetrieveEvent event = new RetrieveEvent(targetElementID);         		
        listener.retrieveResultsAvailable(event);
	}

	Map<String, ObjectLocator> objectLocators;
	public Map<String, ObjectLocator> getObjectLocators() {		
		return objectLocators;
	}
}
