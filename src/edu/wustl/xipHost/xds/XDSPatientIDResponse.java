package edu.wustl.xipHost.xds;

public class XDSPatientIDResponse {
	String patIDRspString;
	String [] patID;

	public XDSPatientIDResponse(final String [] patIDIn, final String patIDRspIn){
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
