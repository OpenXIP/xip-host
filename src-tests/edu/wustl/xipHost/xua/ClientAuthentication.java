package edu.wustl.xipHost.xua;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.xipHost.hostControl.HostConfigurator;
import sun.misc.BASE64Encoder;

/**
 * This test code uses HttpClient to execute an HTTP request against
 * a target site that requires user authentication.
 */
public class ClientAuthentication {
	final static Logger logger = Logger.getLogger(HostConfigurator.class);
    public static void main(String[] args) throws Exception {
    	System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");	
    	System.setProperty("javax.net.ssl.trustStore" , "truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword" , "123456");
    	HttpClient httpclient = new DefaultHttpClient();
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
	    	/*FileUtils.writeStringToFile(new File("samlSTS.xml"), xmlResponse);
	    	
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
	    			InputStream isSaml = new ByteArrayInputStream(saml.getBytes());
	    			//GlobusCredential globusCred = new GlobusCredential(isSaml);
	    			System.out.println(saml);
	    		}
	    	}*/
	    	InputStream is2 = new ByteArrayInputStream(xmlResponse.getBytes());
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is2);
			doc.getDocumentElement().normalize();
			org.w3c.dom.Element docElement = doc.getDocumentElement();//to be returned to XUA to build XUAAssertion
	    	System.out.println(docElement.getNodeName());
	    	System.out.println(docElement.hasChildNodes());
	    	NodeList childNodes = docElement.getChildNodes();
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
	    						NodeList nodes2 = node.getChildNodes();
	    						System.out.println(nodes2.item(0).getNodeName());
	    						String saml = nodes2.item(0).getTextContent();
	    						System.out.println(saml);
	    						InputStream inputStreamSaml = new ByteArrayInputStream(saml.getBytes());
	    						System.out.println(nodes2.item(0).getTextContent());
	    						try {
	    							byte[] bytes  = Base64.decode(saml);
	    							ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
	    							GlobusCredential  credential = new GlobusCredential(stream);
	    							//GlobusCredential globusCred = new GlobusCredential(inputStreamSaml);
	    						} catch (GlobusCredentialException e){
	    							System.err.print(e.getErrorCode());
	    							System.err.print(e.getMessage());
	    							e.printStackTrace();
	    						}
	    						
	    					}
	    				}
	    				
	    			}
	    		}
	    	}
	    	is2.close();
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
