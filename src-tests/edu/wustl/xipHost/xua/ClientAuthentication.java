package edu.wustl.xipHost.xua;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import sun.misc.BASE64Encoder;

/**
 * This test code uses HttpClient to execute an HTTP request against
 * a target site that requires user authentication.
 */
public class ClientAuthentication {

    public static void main(String[] args) throws Exception {
    	System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");	
    	System.setProperty("javax.net.ssl.trustStore" , "truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword" , "<truststorepswd>");
    	DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            /*httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope("localhost", 443),
                    new UsernamePasswordCredentials("<username>", "<password>"));*/

            HttpGet httpget = new HttpGet("https://secure01.cci.emory.edu:8443/SecurityTokenServiceNCIProd/rest/STS/issueToken?targetService=http://services.testcorp.org/provider1");
            BASE64Encoder encoder = new BASE64Encoder();
            String encodedCredential = encoder.encode( ("<username>" + ":" + "<password>").getBytes() );
            httpget.addHeader("Authorization", "Basic " + encodedCredential);
            
            System.out.println("executing request" + httpget.getRequestLine());
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
	    	String xmlResponse = sb.toString();
	    	System.out.println(xmlResponse);
	    	//Write xmlResponse to samlSTS.xml file
	    	FileUtils.writeStringToFile(new File("samlSTS.xml"), xmlResponse);
	    	
	    	SAXBuilder saxBuilder = new SAXBuilder();
	    	File certFile = new File("samlSTS.xml");
	    	org.jdom.Document document = saxBuilder.build(certFile);
	    	Element root = document.getRootElement();
	    	Namespace ns = Namespace.getNamespace("saml","urn:oasis:names:tc:SAML:2.0:assertion");
	    	Element attStatement = root.getChild("AttributeStatement", ns);
	    	List<Element> children = attStatement.getChildren();
	    	for(int i = 0; i < children.size(); i++) {
	    		Element child = children.get(i);
	    		Attribute att = child.getAttribute("Name");
	    		String attValue = att.getValue();
	    		if(attValue.equals("GlobusCredential")){
	    			String saml = child.getChildText("AttributeValue", ns);
	    			System.out.println(saml);
	    		}
	    	}
	       
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                //System.out.println("Response content length: " + entity.getContentLength());
            }
            EntityUtils.consume(entity);
            
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
