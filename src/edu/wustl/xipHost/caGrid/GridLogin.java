package edu.wustl.xipHost.caGrid;

import gov.nih.nci.cagrid.ncia.util.SecureClientUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * <font  face="Tahoma" size="2" color="Black">
 * Helper class, used to perform grid security authentication <b></b>
 * @version	Janaury 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class GridLogin {
	final static Logger logger = Logger.getLogger(GridLogin.class);
	
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
			// using the currentGrid
			File f = new File("./resources/service_urls.properties");
			System.out.println("properties file is " + f.getCanonicalPath());
	
			Properties prop = new Properties();
			prop.load(new FileInputStream(f));
			
			//String url = prop.getProperty("cagrid.master.dorian.service.url");
			//System.out.println("url is " + url);
			String dorianURL = prop.getProperty("cagrid.master.dorian.service.url");
			String authUrl = prop.getProperty("cagrid.master.authentication.service.url");

			globusCred = SecureClientUtil.generateGlobusCredential(userName,
					password,
                    dorianURL,
                    authUrl);
			
			System.out.println("Globus credential is " + globusCred.toString());
			return new Boolean(true);
		}catch(Exception e){
			globusCred = null;
			System.out.print("Exception JK : "+ e);
			return new Boolean(false);
		}
		
	}
	
	static boolean isConnectionSecured = false;
	public static boolean isConnectionSecured(){
		return isConnectionSecured;
	}
	
	static GlobusCredential globusCred;
	public static GlobusCredential getGlobusCredential(){
		return globusCred;
	}
	
	public static void invalidateNBIASecuredConnection(){
		isConnectionSecured = false;
		globusCred = null;
	}
	
	public GlobusCredential acquireGlobusCredential(String userName, String password){
		try{			
			if(userName == null) {
				logger.warn("UserName is null");
				return null;
			}
			if(password == null){
				logger.warn("Password is null");
				return null;
			}
			File f = new File("./resources/service_urls.properties");
	
			Properties prop = new Properties();
			prop.load(new FileInputStream(f));
			String dorianURL = prop.getProperty("cagrid.master.dorian.service.url");
			String authUrl = prop.getProperty("cagrid.master.authentication.service.url");
			
			globusCred = SecureClientUtil.generateGlobusCredential(userName,
					password,
                    dorianURL,
                    authUrl);
			isConnectionSecured = true;
			return globusCred;
		}catch(Exception e){
			globusCred = null;
			isConnectionSecured = false;
			logger.error(e,  e);
			return globusCred;
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {		
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		GridLogin login = new GridLogin();
		String userName = "";
		String password = "";
		GlobusCredential globusCred = login.acquireGlobusCredential(userName, password);
		System.out.println("Globus credential is " + globusCred.toString());
	}
	
	String userName;
	public String getUserName(){
		return userName;
	}
	
	String encryptedPassword;
	public String getEncyptedPassword(){
		return encryptedPassword;
	}
}
