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
	
	static boolean isConnectionSecured;
	public static boolean isConnectionSecured(){
		return isConnectionSecured;
	}
	
	static GlobusCredential globusCred;
	public static GlobusCredential getGlobusCredential(){
		return globusCred;
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
		login.loadNBIAConnectionProperties();
		String userName = login.getUserName();
		String password = login.getEncyptedPassword();
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
	
	public void loadNBIAConnectionProperties(){
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("./resources/nbiaConnection.properties"));
			userName = prop.getProperty("nbia.connection.username");
			encryptedPassword = prop.getProperty("nbia.connection.password");
		} catch (FileNotFoundException e) {
			logger.error(e, e);
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
	
	 public void storePassword() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
			String password = "";

			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey sk = keyGen.generateKey();
			String hexSkey = byteArrayToHexString(sk.getEncoded());
			//hexSkey should be stored somewhere
			
		    byte[] keyValue = hexStringToByteArray(hexSkey);
			SecretKeySpec sks = new SecretKeySpec(keyValue, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			try {
				cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] encrypted = cipher.doFinal(password.getBytes());
			String passwordEncrypted =  byteArrayToHexString(encrypted);
		}
		
		private static String byteArrayToHexString(byte[] b){
		    StringBuffer sb = new StringBuffer(b.length * 2);
		    for (int i = 0; i < b.length; i++){
		      int v = b[i] & 0xff;
		      if (v < 16) {
		        sb.append('0');
		      }
		      sb.append(Integer.toHexString(v));
		    }
		    return sb.toString().toUpperCase();
		}
		
		private static byte[] hexStringToByteArray(String s) {
		    byte[] b = new byte[s.length() / 2];
		    for (int i = 0; i < b.length; i++){
		      int index = i * 2;
		      int v = Integer.parseInt(s.substring(index, index + 2), 16);
		      b[i] = (byte)v;
		    }
		    return b;
		}
		
		public static String getPassword() {
			String hexSkey = ""; //hexSkey needs to be retrieve from somewhere
		    byte[] keyValue = hexStringToByteArray(hexSkey);
			SecretKeySpec sks = new SecretKeySpec(keyValue, "AES");
			String password = null;
			try {
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, sks);
				 
		       
				String encryptedPassword = ""; //taken from properties file
				byte[] encrypted = hexStringToByteArray(encryptedPassword);;
		       
		        byte[] pwd = cipher.doFinal(encrypted);
		        password = new String(pwd);
			} catch (NoSuchAlgorithmException e) {
				logger.error(e.getMessage(), e);
				password = null;
			} catch (NoSuchPaddingException e) {
				logger.error(e.getMessage(), e);
				password = null;
			} catch (InvalidKeyException e) {
				logger.error(e.getMessage(), e);
				password = null;
			} catch (IllegalBlockSizeException e) {
				logger.error(e.getMessage(), e);
				password = null;
			} catch (BadPaddingException e) {
				logger.error(e.getMessage(), e);
				password = null;
			}
	        return password;
		}
	

}
