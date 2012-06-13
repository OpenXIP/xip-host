/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostLogin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.misc.BASE64Encoder;

/**
 * @author Jarek Krych
 *
 */
public class STSLogin implements Login {
	final static Logger logger = Logger.getLogger(STSLogin.class);
	String serviceURL;
	String trustStoreFile; 
	String trustStorePswd;
	String username;
	boolean isConnectionSecured = false;
	
	public STSLogin(String serviceURL, String trustStoreFile, String trustStorePswd){
		this.serviceURL = serviceURL;
		this.trustStoreFile = trustStoreFile;
		this.trustStorePswd = trustStorePswd;
	}
	
	@Override
	public boolean login(String username, String password) {
		this.username = username;
		System.setProperty("javax.net.ssl.trustStore" , trustStoreFile);
		System.setProperty("javax.net.ssl.trustStorePassword" , trustStorePswd);
    	HttpClient httpclient = new DefaultHttpClient();
    	try {
    		//serviceURL = "https://secure01.cci.emory.edu:8443/SecurityTokenServiceNCIProd/rest/STS/issueToken?targetService=http://services.testcorp.org/provider1"
    		HttpGet httpget = new HttpGet(serviceURL);
            BASE64Encoder encoder = new BASE64Encoder();
            String encodedCredential = encoder.encode( (username + ":" + password).getBytes() );
            httpget.addHeader("Authorization", "Basic " + encodedCredential);
            logger.debug("Executing request" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            
            InputStream is = entity.getContent();	
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
   	    	StringBuilder sb = new StringBuilder();
   	    	String line;
   	    	while ((line = br.readLine()) != null) {
   	    		sb.append(line);
   	    	}
   	    	br.close();
   	    	String xmlSamlAssertion = sb.toString();
   	    	parseSamlAssertion(xmlSamlAssertion);
   	    	isConnectionSecured = true;
   	    	logger.debug("User: " + username + " successfuly authenticated to STS Service");
   	    	return true;
    	} catch (Exception e) {
			logger.error(e, e);
			isConnectionSecured = false;
			logger.debug("User: " + username + " denied access to STS Service");
			return false;
		}
    	finally {
            // When HttpClient instance is no longer needed, shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
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

	@Override
	public GlobusCredential getGlobusCredential() {
		return globusCred;
	}

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
	
	GlobusCredential  globusCred;
	Element samlAssertionElement;
	void parseSamlAssertion(String xmlSamlAssertion) throws ParserConfigurationException, SAXException, IOException{
		InputStream is = new ByteArrayInputStream(xmlSamlAssertion.getBytes());
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		samlAssertionElement = doc.getDocumentElement();		//docELement to be returned to XUA to build XUAAssertion
    	NodeList childNodes = samlAssertionElement.getChildNodes();
    	for(int i = 0; i < childNodes.getLength(); i++){
    		Node childNode = childNodes.item(i);
    		if(childNode.getNodeName().equals("saml:AttributeStatement")){
    			NodeList attStatementNodes = childNode.getChildNodes();
    			for(int j = 0; j < attStatementNodes.getLength(); j++){
    				Node node = attStatementNodes.item(j);
    				NamedNodeMap atts = node.getAttributes();
    				for(int k = 0; k < atts.getLength(); k++){
    					Node att = atts.item(k);
    					if(att.getNodeValue().endsWith("GlobusCredential")){
    						NodeList globusCredNodes = node.getChildNodes();
    						String saml = globusCredNodes.item(0).getTextContent();
    						try {
    							byte[] bytes  = Base64.decode(saml);
    							ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
    							globusCred = new GlobusCredential(stream);
    						} catch (GlobusCredentialException e){
    							globusCred = null;
    							logger.error(e, e);    							
    						}    						
    					}
    				}
    				
    			}
    		}
    	}
    	is.close();
	}
}
