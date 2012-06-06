/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostLogin;

import org.globus.gsi.GlobusCredential;
import org.w3c.dom.Element;

/**
 * @author Jarek Krych
 *
 */
public class XUALogin implements Login {

	@Override
	public boolean login(String username, String password) {
		// TODO Auto-generated method stub
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

}
