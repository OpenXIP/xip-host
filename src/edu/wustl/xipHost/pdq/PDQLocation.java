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

package edu.wustl.xipHost.pdq;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Object that represents a PDQ supplier location.<br></br>
 * @version	Janaury 2008
 * @author Lawrence Tarbox
 *
 */
public class PDQLocation {
	String pdqSupplierURL;
	String receivingApplication;
	String recievingFacility;
	String shortName;		
	URI	pdqSupplierURI;
	
	public PDQLocation(String pdqSupplierURLIn, String receivingApplicationIn, String recievingFacilityIn, String shortNameIn) throws URISyntaxException {		
		pdqSupplierURL = pdqSupplierURLIn;
		pdqSupplierURI = new URI(pdqSupplierURL);
		receivingApplication = receivingApplicationIn;
		recievingFacility = recievingFacilityIn;
		shortName = shortNameIn;
	}
	
	public String getPDQSupplierURL() {
		return pdqSupplierURL;
	}
	public URI getPDQSupplierURI() {
		return pdqSupplierURI;
	}
	public String getReceivingApplication(){
		return receivingApplication;
	}
	public String getRecievingFacility(){
		return recievingFacility;
	}
	public String getShortName(){
		return shortName;
	}
	
	//Move validate() to the view layer (to validate user input) 
	public boolean validate(){
		return true;	
	}
	
	
	/*class PDQLocationCreationException extends Exception {		
		private static final long serialVersionUID = 1L;
		String error;		
		
		public PDQLocationCreationException(String err) {
		    super(err);             // call superclass constructor
		    error = err;
		}		
		public String getError(){
			return error;
		}
	}*/
	
	public String toString(){
		return "PDQ Supplier URL: " + pdqSupplierURL + "Receiving Application: " + receivingApplication + "Receiving Facility: " + recievingFacility + "Short name: " + shortName;		
	}
	
	public static void main (String args []){
		PDQLocation loc = null;
		try {
			loc = new PDQLocation("test://localhost:2011", "TestApplication", "TestFacility", "TestLocation");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(loc == null);
	}

}
