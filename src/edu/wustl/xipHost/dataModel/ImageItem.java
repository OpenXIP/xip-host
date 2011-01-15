/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;

/**
 * @author Jaroslaw Krych
 *
 */
public class ImageItem implements Item{
	String imageNumber;
	
	public ImageItem(String imageNumber){
		this.imageNumber = imageNumber;			
	}
	
	public String getItemID() {	
		return imageNumber;
	}
	
	@Override
	public String toString(){
		return new String("Image:" + this.imageNumber);
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
