package edu.wustl.xipHost.pdq;

public class PDQPatientIDResponse {
	String patIDRspString;
	String [] patID;

	public PDQPatientIDResponse(final String [] patIDIn, final String patIDRspIn){
		patID = patIDIn;
		patIDRspString = patIDRspIn;
	}

	String [] getPatID() {
		return patID;
	}
	
	public String toString() {
		return patIDRspString;
	}
}
