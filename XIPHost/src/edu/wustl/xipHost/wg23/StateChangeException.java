package edu.wustl.xipHost.wg23;

public class StateChangeException extends Exception {	
	String error;			
	public StateChangeException(String err) {
	    super(err);             // call superclass constructor
	    error = err;
	}		
	public String getError(){
		return error;
	}
}
