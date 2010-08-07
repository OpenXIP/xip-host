/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class Patient {	
	String patientName;	
	String patientID;
	String patientBirthDate;
	List<Study> studies = new ArrayList<Study>();
	List<Item> items = new ArrayList<Item>();
	Timestamp lastUpdated = null;
	
	public Patient(String patientName, String patientID, String patientBirthDate){
		this.patientName = patientName;
		this.patientID = patientID;
		this.patientBirthDate = patientBirthDate;	
	}
	
	public String getPatientName(){
		return this.patientName;
	}
	
	public String getPatientID(){
		return this.patientID;
	}
	
	public String getPatientBirthDate(){
		return this.patientBirthDate;
	}
		
	public String toString(){
		return new String("Patient:" + this.patientName + " " + this.patientID + " " + this.patientBirthDate);
	}
	
	public void addStudy(Study study){
		this.studies.add(study);
	}
	public List<Study> getStudies(){
		return studies;
	}
	public boolean contains(String studyInstanceUID){		
		for(int i = 0; i < studies.size(); i++){
			if(studies.get(i).getStudyInstanceUID().equalsIgnoreCase(studyInstanceUID)){return true;}
		}			
		return false;
	}
	
	public Study getStudy(String studyInstanceUID){
		for(Study study : studies){
			if(study.getStudyInstanceUID().equalsIgnoreCase(studyInstanceUID)){
				return study;
			} 
		}
		return null;
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

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
