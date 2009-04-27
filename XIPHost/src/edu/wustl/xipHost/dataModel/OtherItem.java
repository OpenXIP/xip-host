/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class OtherItem implements Item{
	String itemID;
	String itemDescription;
	
	public OtherItem(String itemID, String itemDescription){
		this.itemID = itemID;
		this.itemDescription = itemDescription;
	}
	
	public String getItemID() {
		return itemID;
	}

	@Override
	public String toString(){
		return itemDescription;
		
	}
}
