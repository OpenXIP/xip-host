/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
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
