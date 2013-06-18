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

package edu.wustl.xipHost.osu;

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
		if(type != null 
				&& protocolVersion != null && !protocolVersion.isEmpty() && protocolVersion.trim().length() != 0
				&& shortName != null && !shortName.isEmpty() && shortName.trim().length() != 0){
			this.address = address;
			this.type = type;
			this.protocolVersion  = protocolVersion;
			this.shortName = shortName;
		} else{			
			throw new IllegalArgumentException("GridLocation address: " + address + " is not valid.");			
		}				
	}

	public String getAddress(){
		return address;
	}	
	public Type getType(){
		return type;
	}
	public String getProtocolVersion(){
		return protocolVersion;
	}
	public String getShortName(){
		return shortName;
	}
	public enum Type {
		DICOM, AIM;
	}	
	public String toString(){
		return address + " " + type.toString() + " "+ protocolVersion + " " + shortName;
	}
}
