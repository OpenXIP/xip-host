/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xua;

import org.openhealthtools.ihe.xua.XUAAssertion;
import org.openhealthtools.ihe.xua.context.XUAModuleContext;

/**
 * Helper class, used to perform XUA security authentication used by IHE WS transactions
 * @version	January 2011
 * @author Lawrence Tarbox
 */
public class XUALogin {

	XUAAssertion atnaUsername = null;

	public boolean Login(String userName, String password) {	
		XUAModuleContext context = XUAModuleContext.getContext(); 
		context.setXUAEnabled(true); 
		try {
			atnaUsername = context.getLoginHandler().login("https://ibm2:8443/XUATools/IBM_STS", // stsProviderUrl
			//atnaUsername = context.getLoginHandler().login("https://spirit1:8443/SpiritIdentityProvider4Tivoli/services/SpiritIdentityProvider4Tivoli", // stsProviderUrl
					"http://ihe.connecthaton.XUA/X-ServiceProvider-IHE-Connectathon", // audience
					userName, //"user_valid@ihe.net", // "user_valid@ihe.net","user_unsigned@ihe.net" // user
					password); //"passw0rd");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} // password

		if (atnaUsername == null) { 
			// XUA request was unsuccessful 
		    System.out.println("Unable to authenticate user");
			context.setXUAEnabled(false); 
			return false;
		}
		// XUA request was successful 
		return true;
	}
}
