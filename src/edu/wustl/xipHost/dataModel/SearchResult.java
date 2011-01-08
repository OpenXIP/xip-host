/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import java.util.ArrayList;
import java.util.List;
import edu.wustl.xipHost.iterator.Criteria;


/**
 * SearchResult is a data structure that holds results of a query.
 * Initially query result is received in a proprietary format and that is converted to SearchResult.
 * It is thought that SearchResult is gradually growing and new information is added to it. The SearchResult assembly reflects the concept of the progressive query.
 * Nodes are updated gradually depending on the JTree selection and SearchResult is updated as new subqueries result in nodes updates.
 * @author Jaroslaw Krych
 *
 */
public class SearchResult {
	String datasoureDescription;
	List<Patient> patients = new ArrayList<Patient>();
	List<Item> items = new ArrayList<Item>();
	Criteria originalCriteria;
	
	public SearchResult(){
		
	}
	
	public SearchResult(String datasoureDescription){
		this.datasoureDescription = datasoureDescription;
	}
	
	public String getDataSourceDescription(){
		return new String(this.datasoureDescription);
	}
	
	public void setDataSourceDescription(String sourceDescription){
		datasoureDescription = sourceDescription;
	}
	
	public String toString(){						
		return String.valueOf(patients.size()) + " patients and " + String.valueOf(items.size()) + " items.";
	}
	
	public void addPatient(Patient patient){
		this.patients.add(patient);
	}
	
	public List<Patient> getPatients(){
		return patients;
	}
	
	public boolean contains(String patientID){		
		for(int i = 0; i < patients.size(); i++){
			if(patients.get(i).getPatientID().equalsIgnoreCase(patientID)){return true;}
		}			
		return false;
	}
	
	public Patient getPatient(String patientId){
		for(Patient patient : patients){
			if(patient.getPatientID().equalsIgnoreCase(patientId)){
				return patient;
			} 
		}
		return null;
	}
	
	public void removePatient(Patient patient){
		patients.remove(patient);
	}
	
	public void addItem(Item item){
		this.items.add(item);
	}
	
	public List<Item> getItems(){
		return items;
	}
	
	public boolean containsItem(String itemID){
		for(int i = 0; i < items.size(); i++){
			if(items.get(i).getItemID().equalsIgnoreCase(itemID)){return true;}
		}			
		return false;
	}

	public Criteria getOriginalCriteria() {
		return originalCriteria;
	}

	public void setOriginalCriteria(Criteria originalCriteria) {
		this.originalCriteria = originalCriteria;
	}
}
