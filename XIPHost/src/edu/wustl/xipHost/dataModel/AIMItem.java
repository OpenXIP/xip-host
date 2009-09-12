/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

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
}
