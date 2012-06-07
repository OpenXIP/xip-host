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
public interface Login {
	boolean login(String username, String password);
	String getUsername();
	boolean isConnectionSecured();
	GlobusCredential getGlobusCredential();
	Element getSamlAssertion();
	void invalidateNBIASecuredConnection();
}
