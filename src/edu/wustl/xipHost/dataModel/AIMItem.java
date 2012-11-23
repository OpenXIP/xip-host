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
public class AIMItem implements Item{
	String imageAnnotationType;
	String dateTime;
	String authorName;
	String aimUID;
	
	public AIMItem(String imageAnnotationType, String dateTime, String authorName, String aimUID){
		this.imageAnnotationType = imageAnnotationType;
		this.dateTime = dateTime;
		this.authorName = authorName;
		this.aimUID = aimUID;
	}
	
	public String getItemID(){
		return aimUID;
	}
	@Override
	public String toString(){
		String str = "AIM:";
		if(imageAnnotationType.isEmpty() && dateTime.isEmpty()){
			str = str + aimUID;
		}else{
			str = str + imageAnnotationType + " " + dateTime;
		}
		if(!authorName.isEmpty()){
			str = str + " Rater " + authorName;
		}		
		return str;
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
