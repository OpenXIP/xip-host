/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.hostLogin;

import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import org.openhealthtools.ihe.atna.auditor.IHEAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import org.openhealthtools.ihe.xua.XUAAssertion;
import org.openhealthtools.ihe.xua.context.XUAModuleContext;
import org.w3c.dom.Element;

/**
 * @author Jarek Krych
 *
 */
public class XUALogin extends STSLogin implements Login {
	final static Logger logger = Logger.getLogger(XUALogin.class);
	String username;
	boolean isConnectionSecured = false;
	XUAModuleContext context; 
	
	public XUALogin(String serviceURL, String trustStoreFile, String trustStorePswd){
		super(serviceURL, trustStoreFile, trustStorePswd);
		context = XUAModuleContext.getContext();
		//context XUAEnabled set to false by default (e.g. for Guest User)
		context.setXUAEnabled(false); 
	}
	
	@Override
	public boolean login(String username, String password) {
		this.username = username;
		if(username.equals("Guest")) {
			return true;
		} else {
			super.login(username, password);
			if(super.isConnectionSecured()) {
				logger.debug("User: " + username + " successfuly authenticated to XUA Service via STS Service");
				//Send successful login audit message
				IHEAuditor.getAuditor().getConfig().setSystemUserId(username);
				IHEAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.SUCCESS, true, "XIP", "192.168.1.10","");
				context.setXUAEnabled(true); 
				try {
					samlAssertionElement = super.getSamlAssertion();
					XUAAssertion assertion = new XUAAssertion(samlAssertionElement);
					context.cacheAssertion(assertion);
					context.setActiveAssertion(assertion);
					isConnectionSecured = true;
					context.setXUAEnabled(true);
					return true;
				} catch (Exception e) {
					logger.error(e,  e);
					isConnectionSecured = false;
					return false;
				}
			} else {
				logger.debug("User: " + username + " denied access to XUA Service via STS Service");
				//Send login failure audit message
				IHEAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.MINOR_FAILURE, true, "XIP", "192.168.1.10", "");
				//TODO: notify user of failure
				isConnectionSecured = false;
				return false;
			}
		}		
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isConnectionSecured() {
		return isConnectionSecured;
	}

	GlobusCredential  globusCred;
	@Override
	public GlobusCredential getGlobusCredential() {
		globusCred = null;
		return globusCred;
	}

	Element samlAssertionElement;
	@Override
	public Element getSamlAssertion() {
		return samlAssertionElement;
	}

	@Override
	public void invalidateSecuredConnection() {
		globusCred = null;
		samlAssertionElement = null;
		isConnectionSecured = false;
	}
}
