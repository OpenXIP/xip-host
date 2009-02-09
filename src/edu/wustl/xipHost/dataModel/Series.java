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
public class Series {
	String seriesNumber;
	String modality;	
	String seriesDesc;
	String seriesInstanceUID;	
	List<Item> items = new ArrayList<Item>();
	
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
		return new String("Series:" + this.seriesNumber + " " + this.modality);
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
