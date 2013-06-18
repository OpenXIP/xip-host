/*
Copyright (c) 2013, Washington University in St.Louis
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
package edu.wustl.xipHost.xua;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.wustl.xipHost.hostLogin.Login;
import edu.wustl.xipHost.hostLogin.STSLogin;

/**
 * This test code uses HttpClient to execute an HTTP request against STS service and requires user authentication.
 */
public class STSClientAuthenticationTest {
	final static Logger logger = Logger.getLogger(STSClientAuthenticationTest.class);
	static Login login;
	static boolean blnLogin = false;
	static String strURL = "https://secure01.cci.emory.edu:8443/SecurityTokenServiceNCIProd/rest/STS/issueToken?targetService=http://services.testcorp.org/provider1";
	static String trustStoreLoc = "./config/truststore.jks";
	static String trustStorePswd = "123456";
	String username = "<JIRAusername>";
	String password = "<JIRApassword>";
    
	@BeforeClass
	public static void setUpBeforeClass() {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		login = new STSLogin(strURL, trustStoreLoc, trustStorePswd);
    }
	
	@Test
	public void testGetSAMLAssertionFromSTS(){
		login.login(username, password);
		boolean blnSecured = login.isConnectionSecured();
		assertTrue("Unable to establish secured connection with STS service. Check username and password.", blnSecured);
		assertNotNull("SAML assertion value is null.", login.getSamlAssertion());
		assertNotNull("GlobusCredential value is null.", login.getGlobusCredential());
	}
}
