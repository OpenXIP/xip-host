/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import edu.wustl.xipHost.dataAccess.DataSource;

/**
 * <font  face="Tahoma" size="2">
 * Object that represents data location.<br></br>
 * Data can be viewed as DICOM, AIM (annotation objects).
 * Data can be stored within PACS systems or grid enabled repositories.
 * <br></br>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class PacsLocation implements DataSource {
	String hostAddress;
	int hostPort;
	String hostAETitle;
	String hostShortName;		
	
	public PacsLocation(String address, int port, String AETitle, String shortName) {		
		//verify address is a valid address (with regular expression) 
		if(address != null){
			try {
				new URL(address);
			} catch (MalformedURLException e) {					
				if(!isValidIP(address)){
					throw new IllegalArgumentException("Invalid PACS address: " + address);		
				}
			}
		}
		//Verify other that 'address' parameters 
		//if not missing, are valid and are not empty strings or do not start from white space
		if((port > 0 && port < 65535 && AETitle != null && shortName != null) 
				&& (!AETitle.isEmpty() && !shortName.isEmpty()) 
				&& (AETitle.trim().length() != 0 && shortName.trim().length() != 0)){
			hostAddress = address;
			hostPort = port;
			hostAETitle = AETitle;
			hostShortName = shortName;
		} else{			
			throw new IllegalArgumentException("Check PACS address: " + address + " " + port + " " + AETitle);			
		}		
	}
	
	public static boolean isValidIP(String ip){
		String two_five_five = "(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))";
        Pattern IPPattern = Pattern.compile("^(?:"+two_five_five+"\\.){3}"+two_five_five+"$");             
        boolean matches = IPPattern.matcher(ip).matches();
        return matches;
    }
	
	public String getAddress() {
		return hostAddress;
	}
	public int getPort(){
		return hostPort;
	}
	public String getAETitle(){
		return hostAETitle;
	}
	public String getShortName(){
		return hostShortName;
	}
	
	//Move validate() to the view layer (to validate user input) 
	public boolean validate(){
		return true;	
	}
	
	
	/*class PacsLocationCreationException extends Exception {		
		private static final long serialVersionUID = 1L;
		String error;		
		
		public PacsLocationCreationException(String err) {
		    super(err);             // call superclass constructor
		    error = err;
		}		
		public String getError(){
			return error;
		}
	}*/
	
	public String toString(){
		return "Address: " + hostAddress + " Port: " + hostPort + " AETitle: " + hostAETitle + " Short name: " + hostShortName;		
	}
	
	public static void main (String args []){
		PacsLocation loc = new PacsLocation("10.252.175.60", 3001, " ", "TestLocation");
		System.out.println(loc == null);
	}

	@Override
	public String getDataSourceId() {
		return null;
	}
	
	String id;
	@Override
	public void setDataSourceId(String id) {
		this.id = id;
	}
}
