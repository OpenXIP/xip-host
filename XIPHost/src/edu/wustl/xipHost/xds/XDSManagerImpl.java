/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.xds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.openhealthtools.ihe.atna.auditor.PDQConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.XDSConsumerAuditor;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;
import org.openhealthtools.ihe.common.ebxml._3._0.rim.ObjectRefType;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.common.hl7v2.message.PixPdqMessageException;
import org.openhealthtools.ihe.common.hl7v2.message.PixPdqMessageUtilities;
import org.openhealthtools.ihe.common.hl7v2.mllpclient.ClientException;
import org.openhealthtools.ihe.common.mllp.MLLPDestination;
import org.openhealthtools.ihe.pdq.consumer.PdqConsumer;
import org.openhealthtools.ihe.pdq.consumer.PdqConsumerDemographicQuery;
import org.openhealthtools.ihe.pdq.consumer.PdqConsumerException;
import org.openhealthtools.ihe.pdq.consumer.PdqConsumerResponse;
import org.openhealthtools.ihe.xds.consumer.AbstractConsumer;
import org.openhealthtools.ihe.xds.consumer.B_Consumer;
import org.openhealthtools.ihe.xds.consumer.Consumer;
import org.openhealthtools.ihe.xds.consumer.query.DateTimeRange;
import org.openhealthtools.ihe.xds.consumer.query.MalformedQueryException;
import org.openhealthtools.ihe.xds.consumer.retrieve.DocumentRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveDocumentSetRequestType;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.GetDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.MalformedStoredQueryException;
import org.openhealthtools.ihe.xds.document.Document;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.metadata.MetadataFactory;
import org.openhealthtools.ihe.xds.metadata.constants.DocumentEntryConstants;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xua.XUAAssertion;
import org.openhealthtools.ihe.xua.context.XUAModuleContext;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.TagFromName;

import edu.wustl.xipHost.caGrid.GridManager;
import edu.wustl.xipHost.caGrid.GridManagerFactory;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.XDSDocumentItem;

/**
 * @author Jaroslaw Krych (stubs, tree display of results), Lawrence Tarbox (OHT implementation)
 *
 */
public class XDSManagerImpl implements XDSManager{

	//That's where audit is going
	private String auditUser = "wustl"; // TODO Get user from login info, or better yet, globally configure the audit and security stuff.
	private String auditURL = "syslog://129.6.24.109:8087"; // NIST web
	//private String auditURL = "syslog://nist1.ihe.net:8087"; // NIST Connectathon
	//private String auditURL = "syslog://127.0.0.1:4000"; // MESA
	//private String auditURL = "syslog://axolotl1:514"; // axolotl1
	
	public List<XDSPatientIDResponse> queryPatientIDs(AttributeList queryKeys) {		
		System.out.println("Finding Patient IDs.");
		List<XDSPatientIDResponse> patIDRspListOut = null;

		//TODO Move to the configuration file
		System.setProperty("javax.net.ssl.keyStore","/MESA/certificates/WS_XIP.jks");
		System.setProperty("javax.net.ssl.keyStorePassword","caBIG2009");
		//System.setProperty("javax.net.ssl.trustStore","/MESA/certificates/truststore.jks");
		//System.setProperty("javax.net.ssl.trustStorePassword","caBIG2009");
		System.setProperty("javax.net.ssl.trustStore","/XIP/connectathontrusts.na2009.jks");
		System.setProperty("javax.net.ssl.trustStorePassword","connectathon");
		//System.setProperty("javax.net.ssl.keyStore","/MESA/mesa_tests/rad/actors/secure_node/test_sys_1.jks");
		//System.setProperty("javax.net.ssl.keyStorePassword","secret");
		//System.setProperty("javax.net.ssl.trustStore","/MESA/mesa_tests/rad/actors/secure_node/mesatrusts.2009.jks");
		//System.setProperty("javax.net.ssl.trustStorePassword","mesa");

		// TODO Should we do all this setup and create pdq once, configure it, and leave it created?
		try {
			//PDQConsumerAuditor.getAuditor().getConfig().setAuditRepositoryURI(TConfig.ATNA_URI);
			PDQConsumerAuditor.getAuditor().getConfig().setAuditRepositoryUri(new URI(auditURL));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("URI to auditor improperly formed");
		}
		PDQConsumerAuditor.getAuditor().getConfig().setAuditSourceId("XIP");
		PDQConsumerAuditor.getAuditor().getConfig().setAuditorEnabled(true);
		PDQConsumerAuditor.getAuditor().getConfig().setAuditEnterpriseSiteId("IHE ERL");
		PDQConsumerAuditor.getAuditor().getConfig().setHumanRequestor("ltarbox");
		PDQConsumerAuditor.getAuditor().getConfig().setSystemUserId(auditUser);
		PDQConsumerAuditor.getAuditor().getConfig().setSystemUserName("Wash. Univ.");
		
		//TODO Move l. 78 to user login (execute after successful/failed login)
		XUAModuleContext context = XUAModuleContext.getContext(); 
		context.setXUAEnabled(true); 
		//String atnaUsername = context.getLoginHandler().login("https://ibm2:8443/XUATools/IBM_STS", // stsProviderUrl
		XUAAssertion atnaUsername = null;
		try {
			atnaUsername = context.getLoginHandler().login("https://ibm2:8443/XUATools/IBM_STS", // stsProviderUrl
			//atnaUsername = context.getLoginHandler().login("https://spirit1:8443/SpiritIdentityProvider4Tivoli/services/SpiritIdentityProvider4Tivoli", // stsProviderUrl
					"http://ihe.connecthaton.XUA/X-ServiceProvider-IHE-Connectathon", // audience
					"user_valid@ihe.net", // "user_valid@ihe.net","user_unsigned@ihe.net" // user
					"passw0rd");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} // password
		if (atnaUsername != null) { 
			// XUA request was successful 
			auditUser = atnaUsername.getAtnaUsername();
			PDQConsumerAuditor.getAuditor().getConfig().setSystemUserId(auditUser);
			PDQConsumerAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.SUCCESS, true, "XIP", "192.168.1.10");
		} 
		else 
		{ 
			// XUA request was unsuccessful 
			PDQConsumerAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.MINOR_FAILURE, true, "XIP", "192.168.1.10");
			// TODO: notify user of failure
		    System.out.println("Unable to authenticate user");
			return null;
		}
		//*/
		// If not using XUA, uncomment the following line of code
		//PDQConsumerAuditor.getAuditor().auditUserAuthenticationLoginEvent(RFC3881EventOutcomeCodes.SUCCESS, true, "XIP", "192.168.1.10");
		
		/*
		// Alternative of setting up a secure connection
		Properties props = new Properties();
		props.setProperty(SecurityDomain.JAVAX_NET_SSL_KEYSTORE, "/x.jks�);
		props.setProperty(SecurityDomain.JAVAX_NET_SSL_KEYSTORE_PASSWORD, "pswd");
		props.setProperty(SecurityDomain.JAVAX_NET_SSL_TRUSTSTORE, "/y.jks");
		props.setProperty(SecurityDomain.JAVAX_NET_SSL_TRUSTSTORE_PASSWORD, "pswd");
		SecurityDomain domain = new SecurityDomain("domainXY", props);
		ConfigurationManager.registerDefaultSecurityDomain(domain);
	    */ 
		// End possible alternatives
		
	    PdqConsumer pdq = null;
		InputStream cpStream = null;
		try {
			// TODO Add reading location of pdq config file from config file.  For now, hardcoded.
			//InputStream cpStream = new FileInputStream(TConfig.CPROFILE_PATH);
			cpStream = new FileInputStream("./config/pdqConfig.xml");
			pdq = new PdqConsumer(cpStream);
			cpStream.close();
		} catch (FileNotFoundException e) {
		    System.out.println("Unable to open pdqConfig.xml");
		} catch (ClientException e) {
		    System.out.println("Unable to set up connection with PDQ Manager using config file");
		} catch (IOException e) {
		    System.out.println("Unable to close pdqConfig.xml");
		} 
		
		try {
		    if (pdq == null) {
				pdq = new PdqConsumer();
		    }
			// TODO Add reading of pdq URI from config file.  For now, hardcoded

			// Hardcoded URI for PDQ server
		    // URI pdqSupplier = new URI("mllp", null, "hxti1", 3600, null, null, null);
		    // URI pdqSupplier = new URI("mllp", null, "initiate1", 3600, null, null, null);
		    //              For 2009 connectathon
		    //URI pdqSupplier = new URI("mllp", null, "hl7-proxy.ihe.net", 9327, null, null, null); // Allscripts
		    //URI pdqSupplier = new URI("mllp", null, "allscripts4", 3601, null, null, null); // Allscripts
		    //URI pdqSupplier = new URI("mllps", null, "allscripts4", 3710, null, null, null); // Allscripts
		    //URI pdqSupplier = new URI("mllp", null, "icw2", 3750, null, null, null); // ICW
		    //URI pdqSupplier = new URI("mllp", null, "initiate1", 3600, null, null, null); // Initiate
		    //URI pdqSupplier = new URI("mllps", null, "initiate1", 3610, null, null, null); // Initiate
		    //URI pdqSupplier = new URI("mllps", null, "initiate1", 3610, null, null, null); // Initiate
		    //URI pdqSupplier = new URI("mllps", null, "swpartners1", 3610, null, null, null); // swpartners
		    
		    //              For 2009 Internet Testing 
		    // URI pdqSupplier = new URI("mllp", null, "67.155.0.245", 3600, null, null, null); 	// Initiate - success
		    // URI pdqSupplier = new URI("mllp", null, "75.101.154.211", 3600, null, null, null); 	// ICW
		    // URI pdqSupplier = new URI("mllp", null, "195.23.85.214", 3600, null, null, null); 	// Alert 
		    // URI pdqSupplier = new URI("mllp", null, "72.214.26.5", 2200, null, null, null); 		// SW Parters - success
		    URI pdqSupplier = new URI("mllp", null, "office.tiani-spirit.com", 6667, null, null, null); // Tiani-Spirit - success
		    // URI pdqSupplier = new URI("mllp", null, "24.153.226.221", 5950, null, null, null); 		// EMDS
		    // URI pdqSupplier = new URI("mllp", null, "198.160.211.53", 3601, null, null, null); 	// MISYSPLC - success
		    //              For MESA testing
		    // URI pdqSupplier = new URI("mllp", null, "localhost", 3700, null, null, null); // for MESA testing
			// URI pdqSupplier = new URI("mllps", null, "localhost", 4100, null, null, null); // for MESA testing
			pdq.setMLLPDestination(new MLLPDestination(pdqSupplier));
	
		    // Possible alternative - Get PDQ server URI out of a config file
		    /*
		    MLLPDestination mllps = new MLLPDestination(TConfig.MLLPS_URI);
			MLLPDestination.setUseATNA(true);
			pdq.setMLLPDestination(mllps);
			*/
			
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("Unable to set up connection with PDQ Manager");
		    return null;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("URI to PDQ Manager improperly formed");
		    return null;
		}
	    
	    PdqConsumerDemographicQuery pdqQuery = null;
		try {
			pdqQuery = pdq.createDemographicQuery();
		    /*
		    pdqQuery.addQueryPatientNameFamilyName("Mickey");
		    pdqQuery.addQueryPatientNameGivenName("Mouse");
		    pdqQuery.addQueryPatientSex("M");
		    */
			String patientName = queryKeys.get(TagFromName.PatientName).getSingleStringValueOrNull();
			if (patientName != null){
				// TODO Parse name fields into constituent parts to feed into pdqQuery
				Vector <String> nameComponents = (Vector <String>) PersonNameAttribute.getNameComponents(patientName);
			    if ((nameComponents.size() > 0) && nameComponents.get(0) != "") {
			    	pdqQuery.addQueryPatientNameFamilyName(nameComponents.get(0));
			    }
			    if ((nameComponents.size() > 1) && nameComponents.get(1) != "") {
			    	pdqQuery.addQueryPatientNameGivenName(nameComponents.get(1));
			    }
			    if ((nameComponents.size() > 2) && nameComponents.get(2) != "") {
			    	pdqQuery.addQueryPatientNameOtherName(nameComponents.get(2));
			    }
			    if ((nameComponents.size() > 3) && nameComponents.get(3) != "") {
			    	pdqQuery.addQueryPatientNamePrefix(nameComponents.get(3));
			    }
			    if ((nameComponents.size() > 4) && nameComponents.get(4) != "") {
			    	pdqQuery.addQueryPatientNameSuffix(nameComponents.get(4));
			    }
			    if ((nameComponents.size() > 5) && nameComponents.get(5) != "") {
			    	pdqQuery.addQueryPatientNameDegree(nameComponents.get(5));
			    }
			}
			
			String patientID = queryKeys.get(TagFromName.PatientID).getSingleStringValueOrNull();
			if (patientID != null){
				String assigningAuthority = queryKeys.get(TagFromName.IssuerOfPatientID).getSingleStringValueOrEmptyString();
				//pdqQuery.addQueryPatientID(patientID, assigningAuthority, "", "");
				pdqQuery.addQueryPatientID(patientID, assigningAuthority, "1.3.6.1.4.1.21367.2009.1.2.300", "ISO");
			}
			
			String birthdate = queryKeys.get(TagFromName.PatientBirthDate).getSingleStringValueOrNull();
			if (birthdate != null){
				pdqQuery.addQueryPatientDateOfBirth(birthdate);
			}
			
			String sex = queryKeys.get(TagFromName.PatientSex).getSingleStringValueOrNull();
			if (sex != null){
				pdqQuery.addQueryPatientSex(sex);
			}
			
			// TODO Optionally parse Patient Address and add Patient Account fields
			String patientAddress = queryKeys.get(TagFromName.PatientAddress).getDelimitedStringValuesOrNull();
			if (patientAddress != null){
				pdqQuery.addQueryPatientAddressStreetAddress(patientAddress);
			}
			//pdqQuery.addQueryPatientAddressStreetAddress("10 PINETREE");

			String specificCharSet = queryKeys.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull();
			if (specificCharSet != null){
				try {
					pdqQuery.changeDefaultCharacterSet(specificCharSet);
				} catch (PixPdqMessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				    System.out.println("Specific character set not supported.");
				}
			}
			
			// per MESA docs
			//pdqQuery.changeDefaultMessageQueryName("QRY_PDQ_1001", "Query By Name", "IHEDEMO", "", "", "");
			//pdqQuery.changeDefaultWhatDomainsReturned("", "1.3.6.1.4.1.21367.2005.1.1", "ISO");
			
			// per IHE docs
			pdqQuery.changeDefaultMessageQueryName("IHE PDQ Query", "", "", "", "", "");
		    //pdqQuery.changeDefaultWhatDomainsReturned("IHENA", "1.3.6.1.4.1.21367.2009.1.2.300", "ISO"); // for MESA testing - in pdqConfig.xml file
		    //pdqQuery.changeDefaultWhatDomainsReturned("HIMSS2005", "1.3.6.1.4.1.21367.2005.1.1", "ISO"); // for MESA testing - in pdqConfig.xml file
		    //pdqQuery.addOptionalQuantityLimit(10);
			
			// Let's check out the message to see what we are sending
			System.out.println(PixPdqMessageUtilities.msgToString(pdqQuery));
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("PDQ query improperly formed");
			try {
				System.out.println(PixPdqMessageUtilities.msgToString(pdqQuery));
			} catch (PixPdqMessageException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    return null;
		} catch (PixPdqMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Problem encountered in printing PDQ query");
		}
	    
	    // TODO Return an array of pdqResponse structures with demographics and IDs to choose from
	    //PdqConsumerResponse pdqResponse = pdq.sendQuery(pdqQuery, false, "MIR CABIG");
	    PdqConsumerResponse pdqResponse = null;
		try {
			pdqResponse = pdq.sendDemographicQuery(pdqQuery, false);
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("PDQ Query failed");
		    PDQConsumerAuditor.getAuditor().auditNodeAuthenticationFailure(true, // Mitigated sucessfully?
		    		"PD Consumer", // Reporting actor
		    		"XIP_WS", // Reporting process
		    		"PD Supplier", // Failing actor
		    		pdq.getMLLPDestination().getURI().toString(), // Failing URI
		    		"Secure Channel Failed"); // Reason for failure
		    return null;
		}

		try {
		    System.out.println("Number of patients returned = " + pdqResponse.getPatientCount());
			patIDRspListOut = new ArrayList<XDSPatientIDResponse> (pdqResponse.getPatientCount());
			
		    for (int i=0; i < pdqResponse.getPatientCount(); i++) {
		    	String patID[] = pdqResponse.getPatientIdentifier(i, 0); // Only need one, only asked for one
		    	String patName[] = pdqResponse.getPatientName(i, 0); // We'll just look at the first name
		    	String patAddr[] = pdqResponse.getPatientAddress(i, 0); //We'll just look at the first address
		    	String patIDString = "ID: " + patID[0];
		    	patIDString = patIDString + " \nName: " + patName[0] + ", " + patName[1];
		    	for (int j=2; j < patName.length; j++) {
		    		if (patName[j] != "") {
		    			patIDString = patIDString + " " + patName[j];
		    		}
		    	}
		    	patIDString = patIDString + " \nAddress: " + patAddr[0];
		    	for (int j=1; j < patAddr.length; j++) {
		    		if (patAddr[j] != "") {
		    			patIDString = patIDString + ", " + patAddr[j];
		    		}
		    	}
		    	patIDString = patIDString + "\n";
		    	System.out.println(patIDString);
		    	
		    	patIDRspListOut.add(new XDSPatientIDResponse(patID, patIDString));
		    }
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("Parsing PDQ Query Response failed");
		    return null;
		}

		// TODO Add return of patient ID and demographic structures
	    return patIDRspListOut;
	    
	}
	
	private boolean useXdsB = true;

	AbstractConsumer c = null;
	
	//public XDSQueryResponseType queryDocuments(String [] patientIDin) {		
	public	SearchResult queryDocuments(String [] patientIDin) {
		String patIDString = "ID passed into query: " + patientIDin[0];
    	for (int i=1; i < patientIDin.length; i++) {
    		if (patientIDin [i] != "") {
    			patIDString = patIDString + " " + patientIDin[i];
    		}
    	}
    	System.out.println(patIDString);
		// TODO Query documents for a given patientID logic goes here
		
		// Consumer Use Case 1: Stored Query
		System.out.println("Define Query.");
	    
	    ////////////////////////////////////////////////////////////////////////////////
		//If an instance does not already exist, create an instance of the XDS Consumer
		//and provide the XDS Registry url and port.
		////////////////////////////////////////////////////////////////////////////////
		//String registryURL = "http://hxti1:8080/ihe/registry";
		//String registryURL = "http://hcxw2k1.nist.gov:8080/xdsServices2/registry/soap/portals/yr3a/storedquery";
		//String registryURL = "http://129.6.24.109:9080/axis2/services/xdsregistrya";
		//String registryURL = "http://ihexds.nist.gov:9080/tf5/services/xdsregistrya"; // 9085 for tls, swap a for b for XDS.b
		//String registryURL = "https://ihexds.nist.gov:9085/tf5/services/xdsregistrya";
		//String registryURL = "http://ihexds.nist.gov:9080/tf5/services/xdsregistryb";
		//String registryURL = "https://ihexds.nist.gov:9085/tf5/services/xdsregistryb";
		//String registryURL = "https://nist1.ihe.net:9085/tf5/services/xdsregistryb"; // NIST at connectathon
		//String registryURL = "https://127.0.0.1:4100/test";
		//String registryURL = "https://spirit1:8443/XDS/registry";
		//String registryURL = "https://ith-icoserve1:8243/Registry/services/RegistryService";
		String registryURL = "https://ibm3:9448/IBMXDSRegistry/XDSb/SOAP12/Registry";
		// TODO Get URI from a config file
		URI registryURI = null;
		try {
			registryURI = new URI(registryURL);
		} catch (URISyntaxException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		System.out.println("URI of the XDS Registry - " + registryURI.toString());
		
		if (!useXdsB)
		{
			// for XDS.a
			c = new Consumer(registryURI);
		}
		else
		{
		// for XDS.b
			c = new B_Consumer(registryURI);
			//((B_Consumer)c).setPrimaryRepositoryURI(primaryRepositoryURI); //only if repos supports consolidation
			//String NIST_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2008.1.2.701";
			//String XDS_B_REPOSITORY_UNIQUE_ID = "2.16.840.1.113662.2.1.53"; // Spirit
			String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2009.1.2.1030"; // IBM
			//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2008.2.5.102"; //ITH
			
			URI XDS_B_INITIATING_GATEWAY = null;
			URI XDS_B_REPOSITORY_URI = null;
			try {
				String IBM_INITIATING_GATEWAY = "https://ibm3:9448/XGatewayWS/InitiatingGatewayQuery";
				String ITH_INITIATING_GATEWAY = "https://ith-icoserve1:8143/XCommunityBridge/services/InitiatingGatewayService";
				//String IBM_INITIATING_GATEWAY = "http://ibm3:9085/XGatewayWS/InitiatingGatewayRetrieve";
				XDS_B_INITIATING_GATEWAY = new URI(IBM_INITIATING_GATEWAY);
				String NIST_B_STORED_QUERY_SECURED = "https://nist1.ihe.net:9085/tf5/services/xdsrepositoryb";
				//XDS_B_REPOSITORY_URI = new URI(NIST_B_STORED_QUERY_SECURED);
				//XDS_B_REPOSITORY_URI = new URI("https://spirit1:8443/XDS/repository");
				//XDS_B_REPOSITORY_URI = new URI("https://ith-icoserve1:8243/Repository/services/RepositoryService");
				XDS_B_REPOSITORY_URI = new URI("https://ibm3:9448/IBMXDSRepository/XDSb/SOAP12/Repository");
			} catch (URISyntaxException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			((B_Consumer)c).setInitiatingGatewayURI(XDS_B_INITIATING_GATEWAY); //needed to resolve home commmunity IDs
			((B_Consumer)c).getRepositoryMap().put(XDS_B_REPOSITORY_UNIQUE_ID, XDS_B_REPOSITORY_URI); // For XDS.b
		}
		
		//AtnaAgentFactory.getAtnaAgent().setDoAudit(false);
		//AtnaAgentFactory.getAtnaAgent().setAuditRepository(new URI(auditURL));
		try {
			XDSConsumerAuditor auditor = XDSConsumerAuditor.getAuditor();
			auditor.getConfig().setAuditRepositoryUri(new URI(auditURL));
			auditor.getConfig().setAuditorEnabled(true);
			auditor.getConfig().setAuditSourceId("XIP");
		} catch (URISyntaxException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		//////////////////////////////////////////  //////////////////////////////////////
		//Construct the parameters to our FindDocumentsQuery
		////////////////////////////////////////////////////////////////////////////////
		
		// Set up the patient ID: "JM19400814^^^&1.3.6.1.4.1.21367.2005.1.1&ISO"
		CX patientId = Hl7v2Factory.eINSTANCE.createCX();
		//patientId.setIdNumber("223344");
		//patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.1.2.200");
		//patientId.setIdNumber("a57c85bb8028428^^^");
		//patientId.setIdNumber("NIST-test-10");
		//patientId.setIdNumber("89765a87b^^^");
		//patientId.setIdNumber("270a59d7a8b145b^^^");
		patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2009.1.2.300"); // for 2009 connectathon 
		patientId.setAssigningAuthorityUniversalIdType("ISO");
		//patientId.setIdNumber("d74cde348faf4e3");
		//patientId.setIdNumber("5aaef86586ad4ae");
		//patientId.setIdNumber("PDQ113XX01");
		//patientId.setIdNumber("felipe_melo"); // for Sprit
		patientId.setIdNumber("TBDxxxxxxx"); // for ITH
		//patientId.setIdNumber("101"); //for IBM
		// TODO PIX lookup if assigning authority is not what we expect.  Also, read assigning authority from config file
		/*
		//our IDs would be incorporated here
		patientId.setIdNumber(patientIDin[0]);
		if ((patientIDin.length > 2) && (patientIDin[2] != "1.3.6.1.4.1.21367.2009.1.2.300") ) {
			patientId.setAssigningAuthorityUniversalId(patientIDin[2]);
		} else {
			patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2009.1.2.300");
		}
		if ((patientIDin.length > 3) && (patientIDin[3] != "ISO") ){
			patientId.setAssigningAuthorityUniversalIdType(patientIDin[3]);
		} else {
			patientId.setAssigningAuthorityUniversalIdType("ISO");
		}
		*/

		// Set up the date-time range for creationTime between Dec 25, 2003 and Jan 01,
		//2006
		//restricting document date range (from to only)
		DateTimeRange[] creationTimeRange = null;
		try {
			DateTimeRange[] timeRange = {new
					DateTimeRange(DocumentEntryConstants.CREATION_TIME, "20031225", "20080101")};
			creationTimeRange = timeRange;
		} catch (MalformedQueryException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//soecifying type of the document
		//Create a list of healthcare facility codes we want to search on. In this
		//example we only want documents where the healthcare facility is �Outpatient�
		CodedMetadataType[] hcfc1 = {MetadataFactory.eINSTANCE.createCodedMetadataType()};
		hcfc1[0].setCode("Outpatient");
		
		//Create a list of document status types we want to search on. In this example,
		//we only want �Approved� documents.
		AvailabilityStatusType[] status = {AvailabilityStatusType.APPROVED_LITERAL};
		
		////////////////////////////////////////////////////////////////////////////////
		//Construct our FindDocumentsQuery for patient
		////////////////////////////////////////////////////////////////////////////////
		FindDocumentsQuery query = null;
		try {
			query = 
			//new FindDocumentsQuery(
			//	patientId,
			//	null, // no classCodes
			//	creationTimeRange, //creationTimeRange,
			//	null, // no practiceSettingCodes
			//	hcfc1, //null, //new CodedMetadataType[]{hcfc1},
			//	null, // no eventCodes
			//	null, // no confidentialityCodes
			//	null, // no formatCodes
			//	null, // no author
			//	status);
			new FindDocumentsQuery(patientId, 
					status);
		} catch (MalformedStoredQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Query formation failed");
		} //new AvailabilityStatusType[]{AvailabilityStatusType.APPROVED_LITERAL});
		

		////////////////////////////////////////////////////////////////////////////////
		//Construct our GetDocumentQuery for document with uniqueID "1144362162012";
		//thus, the argument for the "isUUID" parameter is "false".
		////////////////////////////////////////////////////////////////////////////////
		//GetDocumentsQuery query = new GetDocumentsQuery(new String[]{"129.6.58.91.12407"},
		//false);

		////////////////////////////////////////////////////////////////////////////////
		//Execute the query.
		/////////////////////////////////////////////////////////////////////////////
		System.out.println("Execute Query.");
		// first fetch the complete list of UUIDs, which can be quite large
		XDSQueryResponseType responseList = null;
		try {
			responseList = c.invokeStoredQuery(query, true); // was .invokeStoredQuery(query, false, "MIR CABIG"); - true to just get UUIDs
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Find query execution failed");
		}
		if(responseList == null || responseList.getReferences().size() == 0){
			System.out.println("NO DOCUMENTS FOUND");
			return null;
		}
		
		// Now fetch details on the first n documents in the list
		// TODO fetch say 10 instead of 1
		//DocumentEntryType docEntrySimple = ((DocumentEntryResponseType)responseList.getDocumentEntryResponses().get(0)).getDocumentEntry();
		/*
		int docIndex = 0; //responseList.getReferences().size() - 1;
		ObjectRefType docRef = (ObjectRefType)responseList.getReferences().get(docIndex);
		//GetDocumentQuery accepts as an argument String[] with up to 10 elements (or n depends on the registry)
		GetDocumentsQuery queryDetails = null;
		try {
			//queryDetails = new GetDocumentsQuery(new String[] {docEntrySimple.getEntryUUID()}, false);
			queryDetails = new GetDocumentsQuery(new String[] {docRef.getId()}, true);
		} catch (MalformedStoredQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Get query creation failed");
			return null;
		}

		XDSQueryResponseType responseDetails = null;
		try {
			responseDetails = c.invokeStoredQuery(queryDetails, false);  
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Get query execution failed");
		}
		if(responseDetails == null || responseDetails.getDocumentEntryResponses().size() == 0){
			System.out.println("NO DOCUMENT DETAILS FOUND");
			return null;
		}
		
		//responseDetails should be used to display on the JTree

		//From this point on it is considered retrieve (except the return statement)
		// TEMPORARY retrieve the last document on the list
		DocumentEntryType docEntryDetails = ((DocumentEntryResponseType)responseDetails.getDocumentEntryResponses().get(0)).getDocumentEntry();
		if(docEntryDetails.getUri() == null){
			System.out.println("Malformed DocumentEntry.URI is null.");
			//throw new Exception("Malformed DocumentEntry.URI is null.");
			return null;
		}
		System.out.println("Getting document with URI: " + docEntryDetails.getUri());
		*/
		//JK
		int numOfDocs = responseList.getReferences().size();
		String[] docReferences = new String[numOfDocs];
		for(int i = 0; i < numOfDocs; i++){
			ObjectRefType docReference = (ObjectRefType)responseList.getReferences().get(i);
			
			docReferences[i] = docReference.getId();
		}
		XDSQueryResponseType response;
		try {
			GetDocumentsQuery docsQuery = new GetDocumentsQuery(docReferences, true);				
			response = c.invokeStoredQuery(docsQuery, false);
		} 	catch (MalformedStoredQueryException e1) {				
			e1.printStackTrace();				
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int numOfResponses = response.getDocumentEntryResponses().size();
		SearchResult searchResult = new SearchResult("XDS Repository: " + registryURI.toString());
		Patient patient = new Patient("", patIDString, "");
		searchResult.addPatient(patient);		
		for(int i = 0; i < numOfResponses; i++){
			DocumentEntryType docDetails = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getDocumentEntry();
			String id = docDetails.getUniqueId();
			String availability = docDetails.getAvailabilityStatus().getLiteral();
			String language = docDetails.getLanguageCode();
			String mime = docDetails.getMimeType();
			String homeCommunity = ((DocumentEntryResponseType)response.getDocumentEntryResponses().get(i)).getHomeCommunityId();
			String docDesc = id + " / " + availability + " / " + language + " / " + mime;
			System.out.println(docDesc);
			Item item = new XDSDocumentItem(id, availability, language, mime, docDetails, patientId, homeCommunity);
			//searchResult.addItem(item);
			patient.addItem(item);
		}
			
		return searchResult;
		
		/* 
		// for XDS.a
		InputStream document = null;
		try {
			document = c.retrieveDocument(docEntryDetails.getUri(), patientId, docEntryDetails.getUniqueId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Getting document with XDS.a failed.");
		}
		if(document == null){
			System.out.println("Document InputStream is null.");
			//throw new Exception("Document InputStream is null.");
			return null;
		}
		System.out.println("Document Stream: " + '\n' + document.toString());
		try {
			// TODO generate file name from the document UID and mime type.
			System.out.println("Mime type:  " + docEntryDetails.getMimeType());
			String dst = "tmp.pdf";
	        OutputStream out;
				out = new FileOutputStream(dst);
		    
	        // Transfer bytes from document to out
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = document.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        document.close();
	        out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*/ // end for XDS.a
		/*
		// for XDS.b
		public File retrieveDocument(DocumentEntryType docEntryDetails, CX patientId){
			// build the document request
		    RetrieveDocumentSetRequestType retrieveRequest = org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
		    DocumentRequestType documentRequest = org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory.eINSTANCE.createDocumentRequestType(); 
		    documentRequest.setRepositoryUniqueId(docEntryDetails.getRepositoryUniqueId());
		    documentRequest.setHomeCommunityId(((DocumentEntryResponseType)responseDetails.getDocumentEntryResponses().get(0)).getHomeCommunityId());
		    documentRequest.setDocumentUniqueId(docEntryDetails.getUniqueId());
		    
		    retrieveRequest.getDocumentRequest().add(documentRequest);
		    
			// execute retrieve
		    List documents = new ArrayList();
			XDSResponseType response = null;
			try {
				response = c.retrieveDocumentSet(retrieveRequest, documents, docEntryDetails.getPatientId());
			} catch (Exception e) {
				System.out.println(e.toString());
				//throw e;
				return null;
			}
			System.out.println("Response status: " + response.getStatus().getName());
			System.out.println("Returned " + documents.size() + " documents.");
			if(documents.size() > 0){
				Document document = (Document)documents.get(0);
				System.out.println("First document returned: " + document.toString());
			}
		}
		*/ // end for XDS.b
		
		// TODO Track the paging - we have two responses - one with all UUIDs, and one with just the info on the first 10.
		
		//return responseDetails;				
	}

	//XDS.a
	public File retrieveDocument(DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId){
		File destFile = null;
		if (!useXdsB) {
			InputStream document = null;
			try {
				document = ((Consumer)c).retrieveDocument(docEntryDetails.getUri(), patientId, docEntryDetails.getUniqueId()); // only XDS.a
			} catch (Exception e) {
				e.printStackTrace();			
				return null;
			}
			if(document == null){
				return null;
			}
			System.out.println("Document Stream: " + '\n' + document.toString());
			try {
				// TODO generate file name from the document UID and mime type.
				System.out.println("Mime type:  " + docEntryDetails.getMimeType());
				//GridManagaer is used only to get Tmp directory
				GridManager gridMgr = GridManagerFactory.getInstance();
				File importDir = gridMgr.getImportDirectory();
				File inputDir = File.createTempFile("XDS-XIPHOST", "pdf", importDir);			
				importDir = inputDir;		
				inputDir.delete();			
				destFile = importDir;
		        OutputStream out;
					out = new FileOutputStream(destFile);		    
		        // Transfer bytes from document to out
		        byte[] buf = new byte[1024];
		        int len;
		        while ((len = document.read(buf)) > 0) {
		            out.write(buf, 0, len);
		        }
		        document.close();
		        out.close();	        
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else { 
			// build the document request
		    RetrieveDocumentSetRequestType retrieveRequest = org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
		    DocumentRequestType documentRequest = org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory.eINSTANCE.createDocumentRequestType(); 
		    documentRequest.setRepositoryUniqueId(docEntryDetails.getRepositoryUniqueId());
		    documentRequest.setHomeCommunityId(homeCommunityId);
		    documentRequest.setDocumentUniqueId(docEntryDetails.getUniqueId());
		    
		    retrieveRequest.getDocumentRequest().add(documentRequest);
		    
			// execute retrieve
		    List documents = new ArrayList();
			XDSResponseType response = null;
			try {
				response = ((B_Consumer)c).retrieveDocumentSet(retrieveRequest, documents, docEntryDetails.getPatientId());
			} catch (Exception e) {
				System.out.println(e.toString());
				//throw e;
				return null;
			}
			System.out.println("Response status: " + response.getStatus().getName());
			System.out.println("Returned " + documents.size() + " documents.");
			Document document = null;
			if(documents.size() > 0){
				document = (Document)documents.get(0);
				System.out.println("First document returned: " + document.toString());
			} else {
				return null;
			}

			// TODO generate file name from the document UID and mime type.
			System.out.println("Mime type:  " + docEntryDetails.getMimeType());
			//GridManagaer is used only to get Tmp directory
			GridManager gridMgr = GridManagerFactory.getInstance();
			File importDir = gridMgr.getImportDirectory();
			File inputDir = null;
			try {
				inputDir = File.createTempFile("XDS-XIPHOST", "pdf", importDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			importDir = inputDir;		
			inputDir.delete();			
			destFile = importDir;
	        OutputStream out = null;
			try {
				out = new FileOutputStream(destFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream documentStream = document.getDocumentData();
	        // Transfer bytes from document to out
	        byte[] buf = new byte[1024];
	        int len;
	        try {
				while ((len = documentStream.read(buf)) > 0) {
				    out.write(buf, 0, len);
				}
		        documentStream.close();
		        out.close();	        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return destFile;
	}
		
	public boolean retrieveDocuemnts() {
		// TODO Retrieve docuemnts logic goes here
/*		
		// Consumer Use Case 5: Retrieve
		////////////////////////////////////////////////////////////////////////////////
		//We assume a Consumer c has already been appropriately constructed as in
		//Section 3.1.3.2. Additionally, we assume a DocumentEntryType docEntry filled
		//with XDS metadata for the desired document has already been obtained from
		//the issuing a query to the XDS Registry.
		////////////////////////////////////////////////////////////////////////////////
		// do the retrieve
		System.out.println("Retrieving Document.");
	    InputStream document = null;
		try{
			document = c.retrieveDocument(docEntry.getUri(), "MIR CABIG");
		} catch(Exception e){
			// if an exception happens, the retrieve has failed
			System.out.println("Error when attempting to retrieve from: " +
					docEntry.getUri()+ " -- " + e.toString());
			throw e;
		}
		if(document == null){
			// if null is returned, then the repository returned null, something else
			// is wrong.
			System.out.println("Document InputStream is null.");
			throw new Exception("Document InputStream is null.");
		}
		else
		{
		    logger.info("Store document into file.");
		    FileOutputStream out = null;
	        try {
	            out = new FileOutputStream("outagain.txt");
	            int b;
	
	            while ((b = document.read()) != -1) {
	                out.write(b);
	            }
	
	        } finally {
	            if (document != null) {
	                document.close();
	            }
	            if (out != null) {
	                out.close();
	            }
	        }
		}
*/
		return false;
	}

}
