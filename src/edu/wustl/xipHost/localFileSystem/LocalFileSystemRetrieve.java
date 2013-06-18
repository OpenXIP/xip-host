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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
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
	public LocalFileSystemRetrieve(){
		
	}
	/*
	public LocalFileSystemRetrieve(SearchResult selectedDataSearchResult) {
		this.selectedDataSearchResult = selectedDataSearchResult;
		//TODO to be removed
	}*/
	
	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria) {
		
	}

	@Override
	public void setCriteria(Object criteria) {
		this.selectedDataSearchResult = (SearchResult) criteria;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
				
	}

	@Override
	public void setImportDir(File importDir) {
		
	}

	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		
	}

	@Override
	public void run() {
		Map<String, ObjectLocator> objectLocs = new HashMap<String, ObjectLocator>();
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
						objectLocs.put(uuid, objLoc);
					}
				}
			}
		}
		
		fireResultsAvailable(objectLocs);		
	}
	
	void fireResultsAvailable(Map<String, ObjectLocator> objectLocs){
		RetrieveEvent event = new RetrieveEvent(objectLocs);         		
        listener.retrieveResultsAvailable(event);
	}

	
	//Map<String, ObjectLocator> objectLocators;
	public Map<String, ObjectLocator> getObjectLocators() {		
		//return objectLocators;
		return null;
		//TODO to be removed
	}

	RetrieveListener listener;
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		listener = l;
	}
}
