/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.dataModel;

import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;

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
