/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		if(patientID == null) {
			this.patientID = "xiphost-auto-" + UUID.randomUUID().toString();
		} else if(patientID.isEmpty()) {
			this.patientID = "xiphost-auto-" + UUID.randomUUID().toString();
		} else {
			this.patientID = patientID;
		}
		this.patientBirthDate = patientBirthDate;	
	}
	
	public String getPatientName(){
		return this.patientName;
	}
	
	public String getPatientID(){
		return patientID;
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
	
	public void removeStudy(Study study){
		studies.remove(study);
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

	public void removeItem(Item item){
		studies.remove(item);
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

	public Item getItem(String itemID){
		for(Item item : items){
			if(item.getItemID().equalsIgnoreCase(itemID)){
				return item;
			} 
		}
		return null;
	}
	
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	@Override
	public boolean equals(Object objectPatient){
		Patient patient = null;
		if(objectPatient instanceof Patient){
			patient = Patient.class.cast(objectPatient);
			String patientName = patient.getPatientName();	
			String patientID = patient.getPatientID();
			String patientBirthDate = patient.getPatientBirthDate();
			if(patientName.equalsIgnoreCase(this.patientName) && patientID.equalsIgnoreCase(this.patientID) 
					&& patientBirthDate.equalsIgnoreCase(this.patientBirthDate)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int i = this.patientName.hashCode();
		int j = this.patientID.hashCode();
		int k = this.patientBirthDate.hashCode();
		return i + j + k;
	}
	
}
