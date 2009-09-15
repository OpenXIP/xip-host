/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <font  face="Tahoma" size="2">
 * Object that represents grid location.<br></br>
 * <br></br>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class GridLocation {
	String address;
	Type type;
	String protocolVersion;
	String shortName;
	
	public GridLocation(String address, Type type, String protocolVersion, String shortName){
		//verify address is a valid address - string can be converted to URL		
		if(address != null){
			try {
				new URL(address);
			} catch (MalformedURLException e) {							
				throw new IllegalArgumentException("Invalid Grid address: " + address);		
			}			
		}
		//Verify that parameters except address are not missing, are valid, are not empty strings or do not start from white space
		if(type != null && shortName != null && !shortName.isEmpty() && shortName.trim().length() != 0){
			this.address = address;
			this.type = type;
			this.protocolVersion  = protocolVersion;
			this.shortName = shortName;
		} else{			
			throw new IllegalArgumentException("GridLocation address: " + address + " is not valid.");			
		}				
	}

	public String getAddress(){
		return this.address;
	}	
	public Type getType(){
		return type;
	}
	public String getProtocolVersion(){
		return this.protocolVersion;
	}
	public String getShortName(){
		return this.shortName;
	}
	public enum Type {
		DICOM, AIM;
	}
	
	public boolean validate() {
		return true;	
	}	
}
