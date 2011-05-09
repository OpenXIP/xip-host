/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class Series {
	String seriesNumber;
	String modality;	
	String seriesDesc;
	String seriesInstanceUID;	
	List<Item> items = new ArrayList<Item>();
	boolean containsSubsetOfItems = false;
	Timestamp lastUpdated = null;
	
	public Series(String seriesNumber, String modality, String seriesDesc, String seriesInstanceUID){
		this.seriesNumber = seriesNumber;
		this.modality = modality;
		this.seriesDesc = seriesDesc;
		this.seriesInstanceUID = seriesInstanceUID;
	}
	public String getSeriesNumber(){
		return this.seriesNumber;
	}
	public String getModality(){
		return this.modality;
	}
	public String getSeriesDesc(){
		return this.seriesDesc;		
	}
	public String getSeriesInstanceUID(){
		return this.seriesInstanceUID;		
	}	
	public String toString(){
		String str = "";
		if(seriesNumber.isEmpty()){
			str = "Series:" + seriesInstanceUID + " " + modality; 
		}else{
			//str = "Series:" + seriesNumber + " " + modality; 
			str = "Series:" + seriesInstanceUID + " " + modality; 
		}			
		return new String(str);
	}
		
	public void addItem(Item item){
		this.items.add(item);
	}
	public List<Item> getItems(){
		return items;
	}
	
	public void removeItem(Item itemToRemove){
		items.remove(itemToRemove);
	}
	
	public void removeItems(){
		items.clear();
	}
	
	public Item getItem(String itemID){
		for(Item item : items){
			if(item.getItemID().equalsIgnoreCase(itemID)){
				return item;
			}
		}
		return null;
	}
	
	public boolean contains(String itemID){
		for(int i = 0; i < items.size(); i++){
			if(items.get(i).getItemID().equalsIgnoreCase(itemID)){return true;}
		}			
		return false;
	}
	
	public void sort(){
		Collections.sort(items, new SortByUID());
	}
	
	class SortByUID implements Comparator<Item> {
		@Override
		public int compare(Item o1, Item o2) {			
			return o1.getItemID().compareTo(o2.getItemID());
		}
		
	}

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public boolean containsSubsetOfItems(){
		return containsSubsetOfItems;
	}
	
	public void setContainsSubsetOfItems(boolean containsSubsetOfItems){
		this.containsSubsetOfItems = containsSubsetOfItems;
	}
	
	@Override
	public boolean equals(Object objectSeries){
		Series series = null;
		if(objectSeries instanceof Series){
			series = Series.class.cast(objectSeries);
			String seriesInstanceUID = series.getSeriesInstanceUID();
			if(seriesInstanceUID.equalsIgnoreCase(this.seriesInstanceUID)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.seriesInstanceUID.hashCode();
	}
}
