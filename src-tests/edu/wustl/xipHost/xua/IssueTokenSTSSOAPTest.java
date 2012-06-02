package edu.wustl.xipHost.xua;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient.SecurityInfo;
import org.picketlink.identity.federation.core.wstrust.SamlCredential;
import org.picketlink.identity.federation.core.wstrust.WSTrustException;
import org.w3c.dom.Element;


public class IssueTokenSTSSOAPTest {
	public String stsUrl = "https://secure01.cci.emory.edu:8443/SecurityTokenServicePF";
	public String requesterUsername = "<username>";
	public String requesterPassword = "<password>";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		// Specify username and password for the training account
		System.setProperty("javax.net.ssl.trustStore" , "truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword" , "<truststorepswd>");

				try {
					// create a WSTrustClient instance.
					WSTrustClient client = new WSTrustClient(
							"PicketLinkSTS",
							"PicketLinkSTSPort",
							stsUrl,
							new SecurityInfo(requesterUsername, requesterPassword));

					// issue a SAML assertion using the client API.
					Element assertion = null;
					System.out
							.println("\nInvoking token service to get SAML assertion for "
									+ requesterUsername);

					// specify the type of token you want to use. In this case its SAML2
					assertion = client.issueTokenForEndpoint("http://services.testcorp.org/provider1");
					System.out.println("SAML assertion for " + requesterUsername
							+ " successfully obtained!");
					SamlCredential credential = new SamlCredential(assertion);
					System.out.println("Token Issued : " + credential);
					assertNotNull(credential);
					
				
					
				
				} catch (WSTrustException wse) {
					System.out
							.println("Unable to issue assertion: " + wse.getMessage());
					fail(wse.toString());
				} catch (Exception e) {
					fail(e.toString());
				}

	}

}
