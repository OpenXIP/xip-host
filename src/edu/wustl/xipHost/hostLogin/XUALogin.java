/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostLogin;

import org.globus.gsi.GlobusCredential;
import org.openhealthtools.ihe.atna.auditor.IHEAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import org.w3c.dom.Element;

/**
 * @author Jarek Krych
 *
 */
public class XUALogin implements Login {

	@Override
	public boolean login(String username, String password) {
		String user = "";
		boolean validUser = false;
		if (validUser){
			// send successful login audit message
			IHEAuditor.getAuditor().getConfig().setSystemUserId(user);
			IHEAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.SUCCESS, true, "XIP", "192.168.1.10");
		} else {
			// send login failure audit message
			IHEAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.MINOR_FAILURE, true, "XIP", "192.168.1.10");
			// TODO: notify user of failure
		}
		
		return false;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnectionSecured() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GlobusCredential getGlobusCredential() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getSamlAssertion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidateNBIASecuredConnection() {
		// TODO Auto-generated method stub
		
	}

}
