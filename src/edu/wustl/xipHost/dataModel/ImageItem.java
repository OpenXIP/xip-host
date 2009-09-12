/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

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
}
