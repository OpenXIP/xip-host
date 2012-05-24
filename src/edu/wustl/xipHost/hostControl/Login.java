package edu.wustl.xipHost.hostControl;

import org.openhealthtools.ihe.atna.auditor.IHEAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import edu.wustl.xipHost.caGrid.GridLogin;
import edu.wustl.xipHost.xua.XUALogin;

public class Login {
	
	static GridLogin gridLogin = new GridLogin();
	static XUALogin xuaLogin = new XUALogin();
	public static boolean useXUA = false;
	public static boolean validateGridSecur = true;
	
	public static boolean validateUser(String user, String password){	
		boolean validUser = false;
		//set true if security enabled
		if (validateGridSecur){
			validUser = gridLogin.login(user, password);
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
	
	String userName;
	public String getUserName(){
		return userName;
	}	
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public static void setValidateGridSecur(boolean bln){
		validateGridSecur = bln;
	}
	
	public static void main (String args[]){
		new Login();
	}
}
