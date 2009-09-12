/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class SearchResult {
	String datasoureDescription;
	List<Patient> patients = new ArrayList<Patient>();
	List<Item> items = new ArrayList<Item>();
	
	public SearchResult(String datasoureDescription){
		this.datasoureDescription = datasoureDescription;
	}
	
	public String toString(){
		return new String("Search Result:" + this.datasoureDescription);
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
}
