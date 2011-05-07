package edu.wustl.xipHost.caGrid;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
//import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
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
			
			String url = prop.getProperty("cagrid.master.dorian.service.url");
			System.out.println("url is " + url);
			
			
			/***********************/
			
			Credential credential = new Credential();
			BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
			bac.setUserId(userName);
			bac.setPassword(password);
			credential.setBasicAuthenticationCredential(bac);
			AuthenticationClient client = new AuthenticationClient(url, credential);
			SAMLAssertion saml;
			saml = client.authenticate();
			IFSUserClient c2 = new IFSUserClient(url);
			/* commented on May 7th, 2011
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
			//System.out.print("Exception JK : "+ e);
			return new Boolean(false);
		}
		
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {		
		GridLogin login = new GridLogin();
		System.out.println(login.login("wustl", "erlERL3r()"));
		
	}

}
