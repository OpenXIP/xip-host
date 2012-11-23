/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;

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
	
	ObjectDescriptor objDesc;
	@Override
	public ObjectDescriptor getObjectDescriptor() {
		return objDesc;
	}
	
	public void setObjectDescriptor(ObjectDescriptor objDesc){
		this.objDesc = objDesc;
	}

	ObjectLocator objLoc;
	@Override
	public ObjectLocator getObjectLocator() {
		return objLoc;
	}
	
	public void setObjectLocator(ObjectLocator objLoc){
		this.objLoc = objLoc;
	}
}
