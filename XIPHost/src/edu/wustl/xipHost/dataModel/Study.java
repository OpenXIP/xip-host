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
public class Study extends org.nema.dicom.wg23.Patient {
	String studyDate;
	String studyID;
	String studyDesc;
	String studyInstanceUID;
	List<Series> series = new ArrayList<Series>();
	List<Item> items = new ArrayList<Item>();
	
	public Study(String studyDate, String studyID, String studyDesc, String studyInstanceUID){
		this.studyDate = studyDate;
		this.studyID = studyID;
		this.studyDesc = studyDesc;
		this.studyInstanceUID = studyInstanceUID;
	}
	
	public String getStudyDate(){
		return this.studyDate;
	}
	
	public String getStudyID(){
		return this.studyID;
	}
	
	public String getStudyDesc(){
		return this.studyDesc;
	}
	
	public String getStudyInstanceUID(){
		return this.studyInstanceUID;
	}
	
	public String toString(){		
		return new String("Study:" + this.studyDate + " " + this.studyID + " " + this.studyDesc);
	}
	
	public void addSeries(Series series){
		this.series.add(series);
	}
	
	public List<Series> getSeries(){
		return series;
	}
	
	public boolean contains(String seriesInstanceUID){
		List<Series> series = getSeries();
		for(int i = 0; i < series.size(); i++){
			if(series.get(i).getSeriesInstanceUID().equalsIgnoreCase(seriesInstanceUID)){return true;}
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
