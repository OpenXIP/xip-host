package edu.wustl.xipHost.hostControl;

import org.openhealthtools.ihe.atna.auditor.IHEAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;

import edu.wustl.xipHost.caGrid.GridLogin;
import edu.wustl.xipHost.xua.XUALogin;


public class Login {
	
	static GridLogin gridLogin = new GridLogin();
	static XUALogin xuaLogin = new XUALogin();
	
	//establishSecureConnection
	
	public static boolean useXUA = false;
	public static boolean validateGridSecur = false;
	/**
	 * validateUser is a client to middleware authorization and authentication component
	 */		
	public static boolean validateUser(String user, String password){	
		boolean validUser = true;
		userName = user;
		
		// For simplicity in demos ...
		if (user.equalsIgnoreCase("rater1") && password.equals("123")) {
			password = "Rsn@1Rsn@1";
		}
		if (user.equalsIgnoreCase("wustl") && password.equals("123") ||user.equalsIgnoreCase("wustl") && password.equals("erl")){
			password = "erlERL3r()";
		}

		//set true if security enabled
		if (validateGridSecur){
			validUser = gridLogin.Login(user, password);
		}
		if (useXUA) {
			validUser = xuaLogin.Login(user, password);
		}
		
		if (validUser){
			// send successful login audit message
			IHEAuditor.getAuditor().getConfig().setSystemUserId(user);
			IHEAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.SUCCESS, true, "XIP", "192.168.1.10");
		} else {
			// send login failure audit message
			IHEAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.MINOR_FAILURE, true, "XIP", "192.168.1.10");
			// TODO: notify user of failure
		}
		
		return validUser;
	}
	
	static String userName;
	public String getUserName(){
		return userName;
	}	
	public static void setValidateGridSecur(boolean bln){
		validateGridSecur = bln;
	}
	
	public static void main (String args[]){
		new Login();
	}
}
