package edu.wustl.xipHost.caGrid;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.ncia.util.SecureClientUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.globus.gsi.GlobusCredential;

/**
 * <font  face="Tahoma" size="2" color="Black">
 * Helper class, used to perform grid security authentication <b></b>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class GridLogin {

	
	public boolean login(String userName, String password) {				
		//args[0] = "wustl";
		//args[1] = "erlERL3r()";
		//args[0] = "rater1";
		//args[1] = "Rsn@1Rsn@1";
		try{
			if (userName == null || password == null) {
				System.out.println("usage: <name> <password>");				
				System.exit(-1);
			}
			//System.out.println(userName + " " + password);
			
			// using the currentGrid
			File f = new File("./resources/service_urls.properties");
			System.out.println("properties file is " + f.getCanonicalPath());
	
			Properties prop = new Properties();
			prop.load(new FileInputStream(f));
			
			//String url = prop.getProperty("cagrid.master.dorian.service.url");
			//System.out.println("url is " + url);
			String dorianURL = prop.getProperty("cagrid.master.dorian.service.url");
			String authUrl = prop.getProperty("cagrid.master.authentication.service.url");

			GlobusCredential globusCred = SecureClientUtil.generateGlobusCredential(userName,
					password,
                    dorianURL,
                    authUrl);
			
			System.out.println("Globus credential is " + globusCred.toString());
			
			/***********************/
			
			Credential credential = new Credential();
			BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
			bac.setUserId(userName);
			bac.setPassword(password);
			credential.setBasicAuthenticationCredential(bac);
			/* Commented JK on February 22nd, 2012
			*/
			/*AuthenticationClient client = new AuthenticationClient(url, credential);
			SAMLAssertion saml;
			saml = client.authenticate();
			IFSUserClient c2 = new IFSUserClient(url);
			
			ProxyLifetime lifetime = new ProxyLifetime();
			lifetime.setHours(12);
			lifetime.setMinutes(0);
			lifetime.setSeconds(0);
			int delegation = 1;
			
			GlobusCredential cred = c2.createProxy(saml, lifetime, delegation);
			ProxyUtil.saveProxyAsDefault(cred);
			System.out.println("logged in with identity: " + cred.getIdentity() + ", saved proxy as default");
				*/
			return new Boolean(true);
		}catch(Exception e){
			System.out.print("Exception JK : "+ e);
			return new Boolean(false);
		}
		
	}
	
	/*public static GlobusCredential authenticate(String dorianURL, String authenticationServiceURL, String userId,
	        String password) throws Exception {
	        // Create credential

	        BasicAuthentication auth = new BasicAuthentication();
	        auth.setUserId(userId);
	        auth.setPassword(password);

	        // Authenticate to the IdP (DorianIdP) using credential

	        AuthenticationClient authClient = new AuthenticationClient(authenticationServiceURL);
	        SAMLAssertion saml = authClient.authenticate(auth);

	        // Requested Grid Credential lifetime (12 hours)

	        CertificateLifetime lifetime = new CertificateLifetime();
	        lifetime.setHours(12);

	        // Request PKI/Grid Credential
	        GridUserClient dorian = new GridUserClient(dorianURL);
	        GlobusCredential credential = dorian.requestUserCertificate(saml, lifetime);
	        return credential;
	    }*/
	
	public void getAssertionCertificate(String userName, String password) {
		//TODO get assertion certificate logic
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {		
		GridLogin login = new GridLogin();
		//System.out.println(login.login("wustl", "erlERL3r()"));
		//login.authenticate("https://dorian.cagrid.org:6443/wsrf/services/cagrid/Dorian", "http://training03.cagrid.org:6080/wsrf/services/DefaultIndexService", "wustl", "erlERL3r()");
		
	}

}
