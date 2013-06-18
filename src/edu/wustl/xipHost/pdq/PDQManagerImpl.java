/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.pdq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openhealthtools.ihe.atna.auditor.PDQConsumerAuditor;
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
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqConsumer;
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqConsumerQuery;
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqConsumerResponse;
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqContinuationQuery;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.TagFromName;

import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Lawrence Tarbox
 *
 */
public class PDQManagerImpl implements PDQManager{

	Document documentPdq;
	Element rootPdq;
	SAXBuilder builder = new SAXBuilder();
	List<PDQLocation> pdqLocations = new ArrayList<PDQLocation>();
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#loadPDQLocations(java.io.File)
	 */
	public boolean loadPDQLocations(File file) throws IOException, JDOMException {				
		documentPdq = builder.build(file);
		rootPdq = documentPdq.getRootElement();										
		List<?> children = rootPdq.getChildren("pdq_location");				
		for (int i = 0; i < children.size(); i++){
			String address = (((Element)children.get(i)).getChildText("PdqSupplierURL"));
			String rcvApplication = (((Element)children.get(i)).getChildText("ReceivingApplication"));
			String rvcFacility = (((Element)children.get(i)).getChildText("RecievingFacility"));
			String shortName = (((Element)children.get(i)).getChildText("ShortName"));
			try {
				PDQLocation loc = new PDQLocation(address, rcvApplication, rvcFacility, shortName);
				pdqLocations.add(loc);
			} catch (URISyntaxException e) {
				System.out.println("Unable to load: " + address + " " + rcvApplication + " " + rvcFacility + " " + shortName + " - invalid location.");				
			}																									
		}
		return true;							
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#storePDQLocations(java.util.List, java.io.File)
	 */
	public boolean storePDQLocations(List<PDQLocation> locations, File file) throws FileNotFoundException {					
		Element rootSave = new Element("locations");
		Document document = new Document();
		document.setRootElement(rootSave);		
		if(locations == null){return false;}
		for(int i = 0; i < locations.size(); i++){						
			Element pdqElem = new Element("pdq_location");
			Element supplierURL = new Element("PdqSupplierURL");
			Element rcvApplicationElem = new Element("ReceivingApplication");
			Element rcvFacilityElem = new Element("RecievingFacility");					
			Element shortNameElem = new Element("ShortName");
			pdqElem.addContent(supplierURL);
			pdqElem.addContent(rcvApplicationElem);
			pdqElem.addContent(rcvFacilityElem);
			pdqElem.addContent(shortNameElem);
			rootSave.addContent(pdqElem);
			supplierURL.addContent(locations.get(i).getPDQSupplierURL());
			rcvApplicationElem.addContent(String.valueOf(locations.get(i).getReceivingApplication()));
			rcvFacilityElem.addContent(locations.get(i).getRecievingFacility());
			shortNameElem.addContent(locations.get(i).getShortName());
		}
		try {
			FileOutputStream outStream = new FileOutputStream(file);
			XMLOutputter outToXMLFile = new XMLOutputter();
			outToXMLFile.setFormat(Format.getPrettyFormat());
	    	outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();                       
		} catch (IOException e) {
			return false;
		} 
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#addPDQLocation(edu.wustl.xipHost.globalSearch.PDQLocation)
	 */
	public boolean addPDQLocation(PDQLocation pdqLocation){		
		try {
			if(!pdqLocations.contains(pdqLocation)){
				return pdqLocations.add(pdqLocation);
			} else {
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}								
	}	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#modifyPDQLocation(edu.wustl.xipHost.globalSearch.PDQLocation, edu.wustl.xipHost.globalSearch.PDQLocation)
	 */
	public boolean modifyPDQLocation(PDQLocation oldPDQLocation, PDQLocation newPDQLocation) {
		//validate method is used to check if parameters are valid, are notmissing, 
		//do not contain empty strings or do not start from white spaces		
		try {
			int i = pdqLocations.indexOf(oldPDQLocation);
			if (i != -1){
				pdqLocations.set(i, newPDQLocation);
				return true;
			} else{
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}
	}	
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#removePDQLocation(edu.wustl.xipHost.globalSearch.PDQLocation)
	 */
	public boolean removePDQLocation(PDQLocation pdqLocation){
		//System.out.println(PDQLocations.indexOf(PDQLocation));
		try {
			return pdqLocations.remove(pdqLocation);
		} catch (IllegalArgumentException e){
			return false;
		}		
	}

	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#getPDQLocations()
	 */
	public List<PDQLocation> getPDQLocations(){
		return pdqLocations;
	}	

	public List<PDQPatientIDResponse> queryPatientIDs(AttributeList queryKeys, PDQLocation pdqSupplier) {	
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
		
		if (pdqSupplier.getPDQSupplierURI().getScheme().contains("mllp")) {
			return queryPatientIDsV2(queryKeys, pdqSupplier);
		} else if (pdqSupplier.getPDQSupplierURI().getScheme().contains("http")) {
			return queryPatientIDsV3(queryKeys, pdqSupplier);
		} else if (pdqSupplier.getPDQSupplierURI().getScheme().contains("test")) {
			// For some testing, there is no PDQ supplier to get Patient IDs from.  In this case, return test IDs to choose from.
			List<PDQPatientIDResponse> testIDs = new ArrayList<PDQPatientIDResponse>();

			String patientID = queryKeys.get(TagFromName.PatientID).getSingleStringValueOrNull();
			if (patientID != null){
				String assigningAuthority = queryKeys.get(TagFromName.IssuerOfPatientID).getSingleStringValueOrEmptyString();
				String patID[] = {patientID, "", assigningAuthority, "ISO"}; // NIST test ID
				String patIDString = "ID: " + patID[0] + " \nName: User Input\n";
				testIDs.add(new PDQPatientIDResponse(patID, patIDString));
			}

			String patIDa[] = {"5541138b47a445a", "", "1.3.6.1.4.1.21367.2005.3.7", "ISO"}; // NIST test ID
			String patIDStringa = "ID: " + patIDa[0] + " \nName: NIST Test Subject\n";
			testIDs.add(new PDQPatientIDResponse(patIDa, patIDStringa));

			String patIDb[] = {"b89182560b284fd", "", "1.3.6.1.4.1.21367.2005.3.7", "ISO"}; // NIST test ID
			String patIDStringb = "ID: " + patIDb[0] + " \nName: NIST Test Subject - one docs\n";
			testIDs.add(new PDQPatientIDResponse(patIDb, patIDStringb));

			String patIDc[] = {"5a6d285d57bf408", "", "1.3.6.1.4.1.21367.2005.3.7", "ISO"}; // NIST test ID
			String patIDStringc = "ID: " + patIDc[0] + " \nName: NIST Test Subject - two docs\n";
			testIDs.add(new PDQPatientIDResponse(patIDc, patIDStringc));
			
			//String patIDd[] = {"123", "", "1.3.6.1.4.1.21367.2010.1.2.300", "ISO"};
			String patIDd[] = {"101", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDStringd = "ID: " + patIDd[0] + " \nName: IBM\n";
			testIDs.add(new PDQPatientIDResponse(patIDd, patIDStringd));
			
			//String patIDe[] = {"4111", "", "1.3.6.1.4.1.21367.2010.1.2.300", "ISO"}; 
			//String patIDe[] = {"779911", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDe[] = {"20101210161154", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDStringe = "ID: " + patIDe[0] + " \nName: Oracle\n";
			testIDs.add(new PDQPatientIDResponse(patIDe, patIDStringe));
			
			String patIDf[] = {"TestPatient1", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDStringf = "ID: " + patIDf[0] + " \nName: CareEvolution\n";
			testIDs.add(new PDQPatientIDResponse(patIDf, patIDStringf));

			String patIDg[] = {"1", "", "1.3.6.1.4.1.21367.13.20.2000", "ISO"};
			String patIDStringg = "ID: " + patIDg[0] + " \nName: GE\n";
			testIDs.add(new PDQPatientIDResponse(patIDg, patIDStringg));

			String patIDh[] = {"161111", "", "1.3.6.1.4.1.21367.13.20.3000", "ISO"};
			String patIDStringh = "ID: " + patIDg[0] + " \nName: MOSS\n";
			testIDs.add(new PDQPatientIDResponse(patIDh, patIDStringh));

			String patIDi[] = {"20101215162537", "", "1.3.6.1.4.1.21367.13.20.3000", "ISO"}; 
			String patIDStringi = "ID: " + patIDi[0] + " \nName: eCW\n";
			testIDs.add(new PDQPatientIDResponse(patIDi, patIDStringi));

			// Set up the patient ID: "JM19400814^^^&1.3.6.1.4.1.21367.2005.1.1&ISO"
			//patientId.setIdNumber("223344");
			//patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2007.1.2.200");
			//patientId.setIdNumber("a57c85bb8028428^^^");
			//patientId.setIdNumber("NIST-test-10");
			//patientId.setIdNumber("89765a87b^^^");
			//patientId.setIdNumber("270a59d7a8b145b^^^");
			//patientId.setAssigningAuthorityName("");
			//patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2009.1.2.300"); // for 2009 connectathon 
			//patientId.setAssigningAuthorityUniversalId("1.3.6.1.4.1.21367.2010.1.2.300"); // for 2010 connectathon 
			//patientId.setAssigningAuthorityUniversalIdType("ISO");
			//patientId.setIdNumber("d74cde348faf4e3");
			//patientId.setIdNumber("5aaef86586ad4ae");
			//patientId.setIdNumber("PDQ113XX01");
			//patientId.setIdNumber("felipe_melo"); // for Sprit
			//patientId.setIdNumber("TBDxxxxxxx"); // for ITH
			//patientId.setIdNumber("101"); //for IBM
			return testIDs;
		} else {
		    System.out.println("URI to PDQ Manager improperly formed - scheme not recognized");
		    return null;
		}
	}

	public List<PDQPatientIDResponse> queryPatientIDsV2(AttributeList queryKeys, PDQLocation pdqSupplier) {		
		System.out.println("Finding Patient IDs V2.");
		List<PDQPatientIDResponse> patIDRspListOut = null;
		
	    PdqConsumer pdq = null;
		InputStream cpStream = null;
		try {
			// TODO Add reading location of pdq config file from config file.  May need to vary based on query location.  For now, hardcoded.
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
		    	// Reading the config file failed, so try creating the PdqConsumer without the config file
				pdq = new PdqConsumer();
		    }
			
			pdq.setMLLPDestination(new MLLPDestination(pdqSupplier.getPDQSupplierURI()));
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("Unable to set up connection with PDQ Manager");
		    return null;
		}
	    
	    PdqConsumerDemographicQuery pdqQuery = null;
		try {
			pdqQuery = pdq.createDemographicQuery();
			String patientName = queryKeys.get(TagFromName.PatientName).getSingleStringValueOrNull();
			if ((patientName != null) && ( ! patientName.isEmpty())){
				// TODO Parse name fields into constituent parts to feed into pdqQuery
				Vector <String> nameComponents = (Vector <String>) PersonNameAttribute.getNameComponents(patientName);
			    if ((nameComponents.size() > 0) && ( ! nameComponents.get(0).isEmpty())) {
			    	pdqQuery.addQueryPatientNameFamilyName(nameComponents.get(0));
			    }
			    if ((nameComponents.size() > 1) && ( ! nameComponents.get(1).isEmpty())) {
			    	pdqQuery.addQueryPatientNameGivenName(nameComponents.get(1));
			    }
			    if ((nameComponents.size() > 2) && ( ! nameComponents.get(2).isEmpty())) {
			    	pdqQuery.addQueryPatientNameOtherName(nameComponents.get(2));
			    }
			    if ((nameComponents.size() > 3) && ( ! nameComponents.get(3).isEmpty())) {
			    	pdqQuery.addQueryPatientNamePrefix(nameComponents.get(3));
			    }
			    if ((nameComponents.size() > 4) && ( ! nameComponents.get(4).isEmpty())) {
			    	pdqQuery.addQueryPatientNameSuffix(nameComponents.get(4));
			    }
			    if ((nameComponents.size() > 5) && ( ! nameComponents.get(5).isEmpty())) {
			    	pdqQuery.addQueryPatientNameDegree(nameComponents.get(5));
			    }
			}
			
			String patientID = queryKeys.get(TagFromName.PatientID).getSingleStringValueOrNull();
			if ((patientID != null) && ( ! patientID.isEmpty())){
				String assigningAuthority = queryKeys.get(TagFromName.IssuerOfPatientID).getSingleStringValueOrEmptyString();
				pdqQuery.addQueryPatientID(patientID, assigningAuthority, "", "");
				//pdqQuery.addQueryPatientID(patientID, assigningAuthority, "1.2.3.4.5.1000", "ISO"); // for NIST testing
				//pdqQuery.addQueryPatientID(patientID, assigningAuthority, "1.3.6.1.4.1.21367.2010.1.2.300", "ISO");
			}
			
			String birthdate = queryKeys.get(TagFromName.PatientBirthDate).getSingleStringValueOrNull();
			if ((birthdate != null) && ( ! birthdate.isEmpty())){
				pdqQuery.addQueryPatientDateOfBirth(birthdate);
			}
			
			String sex = queryKeys.get(TagFromName.PatientSex).getSingleStringValueOrNull();
			if ((sex != null) && ( ! sex.isEmpty())){
				pdqQuery.addQueryPatientSex(sex);
			}
			
			// TODO Optionally parse Patient Address and add Patient Account fields
			String patientAddress = queryKeys.get(TagFromName.PatientAddress).getDelimitedStringValuesOrNull();
			if ((patientAddress != null) && ( ! patientAddress.isEmpty())){
				pdqQuery.addQueryPatientAddressStreetAddress(patientAddress);
			}
			//pdqQuery.addQueryPatientAddressStreetAddress("10 PINETREE");
			//pdqQuery.addQueryPatientAddressStateOrProvince("MD"); // For NIST testing

			String patientMother = queryKeys.get(TagFromName.PatientMotherBirthName).getDelimitedStringValuesOrNull();
			if ((patientMother != null) && ( ! patientMother.isEmpty())){
				// TODO Parse name fields into constituent parts to feed into pdqQuery
				Vector <String> nameComponents = (Vector <String>) PersonNameAttribute.getNameComponents(patientMother);
			    if ((nameComponents.size() > 0) && ( ! nameComponents.get(0).isEmpty())) {
			    	pdqQuery.addQueryPatientMothersMaidenFamilyName(nameComponents.get(0));
			    }
			    if ((nameComponents.size() > 1) && ( ! nameComponents.get(1).isEmpty())) {
			    	pdqQuery.addQueryPatientMothersMaidenGivenName(nameComponents.get(1));
			    }
			    if ((nameComponents.size() > 2) && ( ! nameComponents.get(2).isEmpty())) {
			    	pdqQuery.addQueryPatientMothersMaidenOtherName(nameComponents.get(2));
			    }
			}

			String patientPhone = queryKeys.get(TagFromName.PatientTelephoneNumbers).getDelimitedStringValuesOrNull();
			if ((patientPhone != null) && ( ! patientPhone.isEmpty())){
				pdqQuery.addQueryPatientPhoneHomeUnformattedTelephoneNumber(patientPhone);
			}

			String specificCharSet = queryKeys.get(TagFromName.SpecificCharacterSet).getSingleStringValueOrNull();
			if ((specificCharSet != null) && ( ! specificCharSet.isEmpty())){
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
		    //pdqQuery.changeDefaultWhatDomainsReturned("NIST2010", "2.16.840.1.113883.3.72.5.9.1", "ISO"); // for NIST testing - in pdqConfig.xml file
		    //pdqQuery.changeDefaultWhatDomainsReturned("", "", "");		    //pdqQuery.changeDefaultWhatDomainsReturned("", "1.2.3.4.5.2000", "ISO"); // for NIST testing - in pdqConfig.xml file
		    //pdqQuery.changeDefaultWhatDomainsReturned("IHENA", "1.3.6.1.4.1.21367.2009.1.2.300", "ISO"); // for MESA testing - in pdqConfig.xml file
		    //pdqQuery.changeDefaultWhatDomainsReturned("HIMSS2005", "1.3.6.1.4.1.21367.2005.1.1", "ISO"); // for MESA testing - in pdqConfig.xml file
		    //pdqQuery.changeDefaultWhatDomainsReturned("", "1.3.6.1.4.1.21367.2010.1.2.300", "ISO"); // for MESA testing - in pdqConfig.xml file
		    //pdqQuery.changeDefaultWhatDomainsReturned("", "1.3.6.1.4.1.21367.13.20.1000", "ISO"); // Gazelle Red
			//pdqQuery.changeDefaultWhatDomainsReturned("", "1.3.6.1.4.1.21367.13.20.5155", "ISO"); //EMDS
		    //pdqQuery.addOptionalQuantityLimit(1);

		    pdqQuery.changeDefaultReceivingApplication(pdqSupplier.getReceivingApplication(), "", "");
		    pdqQuery.changeDefaultRecievingFacility(pdqSupplier.getRecievingFacility(), "", "");
		    
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
			patIDRspListOut = new ArrayList<PDQPatientIDResponse> (pdqResponse.getPatientCount());
			
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
		    	
		    	patIDRspListOut.add(new PDQPatientIDResponse(patID, patIDString));
		    }
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("Parsing PDQ Query Response failed");
		    return null;
		}
/*
		// Continuation testing
	    PdqConsumerResponse pdqResponse2 = null;
		try {
			if (pdqResponse.getContinuationPointer() != null) {
				pdqQuery.addOptionalContinuationPointer(pdqResponse);
			    pdqQuery.addOptionalQuantityLimit(10);
			    pdqResponse2 = pdq.sendDemographicQuery(pdqQuery, false);
			}
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("PDQ Continuation Query failed");
		}
			
		try {
		    System.out.println("Number of continuation patients returned = " + pdqResponse2.getPatientCount());
			
		    for (int i=0; i < pdqResponse2.getPatientCount(); i++) {
		    	String patID[] = pdqResponse2.getPatientIdentifier(i, 0); // Only need one, only asked for one
		    	String patName[] = pdqResponse2.getPatientName(i, 0); // We'll just look at the first name
		    	String patAddr[] = pdqResponse2.getPatientAddress(i, 0); //We'll just look at the first address
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
		    	
		    	patIDRspListOut.add(new PDQPatientIDResponse(patID, patIDString));
		    }
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("Parsing PDQ Query Response failed");
		    return null;
		}
*/
		
		// TODO Add return of patient ID and demographic structures
	    return patIDRspListOut;
	    
	}

	public List<PDQPatientIDResponse> queryPatientIDsV3(AttributeList queryKeys, PDQLocation pdqSupplier) {		
		System.out.println("Finding Patient IDs V3.");
		List<PDQPatientIDResponse> patIDRspListOut = null;

		// TODO Add reading location of pdq config file from config file.  May need to vary based on query location.  For now, hardcoded.
		V3PdqConsumer pdq = new V3PdqConsumer(pdqSupplier.getPDQSupplierURI()); 
		
		V3PdqConsumerQuery pdqQuery = new V3PdqConsumerQuery(
				HostConfigurator.getHostConfigurator().getpdqSendApplicationOID(),
				HostConfigurator.getHostConfigurator().getPDQSendFacilityOID(),
				pdqSupplier.getReceivingApplication(),
				pdqSupplier.getRecievingFacility());
		/*
		pdqQuery.addPatientName(false, "Mickey", "Mouse", "", "", "");
		pdqQuery.setPatientSex("M");
		*/
		String patientName = queryKeys.get(TagFromName.PatientName).getSingleStringValueOrNull();
		if ((patientName != null) && ( ! patientName.isEmpty())){
			String family = "";
			String given = "";
			String other = "";
			String prefix = "";
			String suffix = "";
			//String degree = "";
			
			Boolean wildcard = false;
			
			Vector <String> nameComponents = (Vector <String>) PersonNameAttribute.getNameComponents(patientName);
		    if ((nameComponents.size() > 0) && (!nameComponents.get(0).isEmpty())) {
		    	family =nameComponents.get(0);
		    	if (family.contains("*")) {
			    	wildcard |= true;
			    	family.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 1) && (!nameComponents.get(1).isEmpty())) {
		    	given = nameComponents.get(1);
		    	if (given.contains("*")) {
			    	wildcard |= true;
			    	given.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 2) && (!nameComponents.get(2).isEmpty())) {
		    	other = nameComponents.get(2);
		    	if (other.contains("*")) {
			    	wildcard |= true;
			    	other.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 3) && (!nameComponents.get(3).isEmpty())) {
		    	prefix = nameComponents.get(3);
		    	if (prefix.contains("*")) {
			    	wildcard |= true;
			    	prefix.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 4) && (!nameComponents.get(4).isEmpty())) {
		    	suffix = nameComponents.get(4);
		    	if (suffix.contains("*")) {
			    	wildcard |= true;
			    	suffix.replaceAll("*", "");
		    	}
		    }
		    //if ((nameComponents.size() > 5) && nameComponents.get(5) != "") {
		    //	degree = nameComponents.get(5);
			//	wildcard |= degree.contains("*");
		    //}
		    pdqQuery.addPatientName(wildcard, family, given, other, suffix, prefix);
		    //pdqQuery.addPatientName(true, family, given, other, suffix, prefix);
		}
		
		String patientID = queryKeys.get(TagFromName.PatientID).getSingleStringValueOrNull();
		if ((patientID != null) && ( ! patientID.isEmpty())){
			String assigningAuthority = queryKeys.get(TagFromName.IssuerOfPatientID).getSingleStringValueOrEmptyString();
			pdqQuery.addPatientID("", patientID, assigningAuthority);
			//pdqQuery.addPatientID( "1.2.3.4.5.1000", patientID, assigningAuthority); // for NIST testing
			//pdqQuery.addPatientID("1.3.6.1.4.1.21367.2010.1.2.300", patientID, assigningAuthority);
		}
		
		String birthdate = queryKeys.get(TagFromName.PatientBirthDate).getSingleStringValueOrNull();
		if ((birthdate != null) && ( ! birthdate.isEmpty())){
			pdqQuery.setPatientDateOfBirth(birthdate);
		}
		
		String sex = queryKeys.get(TagFromName.PatientSex).getSingleStringValueOrNull();
		if ((sex != null) && ( ! sex.isEmpty())){
			pdqQuery.setPatientSex(sex);
		}
		
		// TODO Optionally parse Patient Address and add Patient Account fields
		String patientAddress = queryKeys.get(TagFromName.PatientAddress).getDelimitedStringValuesOrNull();
		if ((patientAddress != null) && ( ! patientAddress.isEmpty())){
			//TODO:Divide into constituent parts:
			// streetAddress, city, county, state, country, zip, otherDesignation, type
			pdqQuery.addPatientAddress(patientAddress, "", "", "", "", "", "", "");
		}
		//pdqQuery.addPatientAddress("10 PINETREE", "", "", "", "", "", "");
		//pdqQuery.addPatientAddress(addressStreetAddress, addressCity, addressCounty, addressState, addressCountry, addressZip, addressOtherDesignation, addressType)
		//pdqQuery.addPatientAddress("1905 Romrog Way", "ROCK SPRINGS", "", "WY", "", "82901", "", ""); // For NIST testing

		String patientMother = queryKeys.get(TagFromName.PatientMotherBirthName).getDelimitedStringValuesOrNull();
		if ((patientMother != null) &&(patientMother.isEmpty())){
			String family = "";
			String given = "";
			String other = "";
			String prefix = "";
			String suffix = "";
			//String degree = "";
			
			Boolean wildcard = false;
			
			Vector <String> nameComponents = (Vector <String>) PersonNameAttribute.getNameComponents(patientMother);
		    if ((nameComponents.size() > 0) && (!nameComponents.get(0).isEmpty())) {
		    	family =nameComponents.get(0);
		    	if (family.contains("*")) {
			    	wildcard |= true;
			    	family.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 1) && (!nameComponents.get(1).isEmpty())) {
		    	given = nameComponents.get(1);
		    	if (given.contains("*")) {
			    	wildcard |= true;
			    	given.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 2) && (!nameComponents.get(2).isEmpty())) {
		    	other = nameComponents.get(2);
		    	if (other.contains("*")) {
			    	wildcard |= true;
			    	other.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 3) && (!nameComponents.get(3).isEmpty())) {
		    	prefix = nameComponents.get(3);
		    	if (prefix.contains("*")) {
			    	wildcard |= true;
			    	prefix.replaceAll("*", "");
		    	}
		    }
		    if ((nameComponents.size() > 4) && (!nameComponents.get(4).isEmpty())) {
		    	suffix = nameComponents.get(4);
		    	if (suffix.contains("*")) {
			    	wildcard |= true;
			    	suffix.replaceAll("*", "");
		    	}
		    }
		    //if ((nameComponents.size() > 5) && nameComponents.get(5) != "") {
		    //	degree = nameComponents.get(5);
			//	wildcard |= degree.contains("*");
		    //}
		    pdqQuery.addPatientMothersMaidenName(wildcard, family, given, other, suffix, prefix);
		    //pdqQuery.addPatientMothersMaidenName(true, family, given, other, suffix, prefix);
		}

		String patientPhone = queryKeys.get(TagFromName.PatientTelephoneNumbers).getDelimitedStringValuesOrNull();
		if ((patientPhone != null) && ( ! patientPhone.isEmpty())){
			pdqQuery.addPatientTelecom(patientPhone, "HP");
		}

		//TODO Set the return domain from a config file?
		// per MESA docs
		//pdqQuery.changeDefaultMessageQueryName("QRY_PDQ_1001", "Query By Name", "IHEDEMO", "", "", "");
		//pdqQuery.addDomainToReturn("1.3.6.1.4.1.21367.2005.1.1);
		
		//pdqQuery.setInitialQuantity(10);
		
		// Let's check out the message to see what we are sending
		System.out.println(pdqQuery.getQueryParams());
		System.out.println(pdqQuery.toString());
	    
	    // TODO Return an array of pdqResponse structures with demographics and IDs to choose from
	    //PdqConsumerResponse pdqResponse = pdq.sendQuery(pdqQuery, false, "MIR CABIG");
		V3PdqConsumerResponse pdqResponse = null;
		try {
			pdqResponse = pdq.sendQuery(pdqQuery);
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("PDQ Query failed");
		    PDQConsumerAuditor.getAuditor().auditNodeAuthenticationFailure(true, // Mitigated sucessfully?
		    		"PD Consumer", // Reporting actor
		    		"XIP_WS", // Reporting process
		    		"PD Supplier", // Failing actor
		    		pdq.getServerURI().toString(), // Failing URI
		    		"Secure Channel Failed"); // Reason for failure
		    return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (pdqResponse.hasError()) {
			System.out.println("Error in PD Query");
			return null;
		}
		
		System.out.println(pdqResponse.toString());
		
		System.out.println("Number of patients returned = " + pdqResponse.getNumRecordsCurrent());
		patIDRspListOut = new ArrayList<PDQPatientIDResponse> (pdqResponse.getNumRecordsCurrent());
		
		for (int i=0; i < pdqResponse.getNumRecordsCurrent(); i++) {
			String patID[] = pdqResponse.getPatientID(i, 0); // Only need one, only asked for one
			String patName[] = pdqResponse.getPatientName(i, 0); // We'll just look at the first name
			String patAddr[] = pdqResponse.getPatientAddress(i, 0); //We'll just look at the first address
			String patIDString = "ID: " + patID[0];
			patIDString = patIDString + " \nName: " + patName[0] + ", " + patName[1];
			for (int j=2; j < patName.length; j++) {
				if (patName[j] != null) {
					patIDString = patIDString + " " + patName[j];
				}
			}
			patIDString = patIDString + " \nAddress: " + patAddr[0];
			for (int j=1; j < patAddr.length; j++) {
				if (patAddr[j] != null) {
					patIDString = patIDString + ", " + patAddr[j];
				}
			}
			patIDString = patIDString + "\n";
			System.out.println(patIDString);
			
			patIDRspListOut.add(new PDQPatientIDResponse(patID, patIDString));
		}

/*
		//Repeat for continuation
		V3PdqContinuationQuery pdqCont = new V3PdqContinuationQuery(
				HostConfigurator.getHostConfigurator().getpdqSendApplicationOID(),
				HostConfigurator.getHostConfigurator().getPDQSendFacilityOID(),
				pdqSupplier.getReceivingApplication(),
				pdqSupplier.getRecievingFacility(),
				pdqResponse, 
				10);
		
		// Let's check out the message to see what we are sending
		System.out.println("Continuing");
		System.out.println(pdqCont.toString());
	    
	    // TODO Return an array of pdqResponse structures with demographics and IDs to choose from
		V3PdqConsumerResponse pdqResponse2 = null;
		try {
			pdqResponse2 = pdq.sendContinuation(pdqCont);
		} catch (PdqConsumerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    System.out.println("PDQ Query failed");
		    PDQConsumerAuditor.getAuditor().auditNodeAuthenticationFailure(true, // Mitigated sucessfully?
		    		"PD Consumer", // Reporting actor
		    		"XIP_WS", // Reporting process
		    		"PD Supplier", // Failing actor
		    		pdq.getServerURI().toString(), // Failing URI
		    		"Secure Channel Failed"); // Reason for failure
		    return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (pdqResponse2.hasError()) {
			System.out.println("Error in PD Continuation");
			return null;
		}
		
		System.out.println(pdqResponse.toString());
		
		System.out.println("Number of patients returned in continuation = " + pdqResponse.getNumRecordsCurrent());
		patIDRspListOut = new ArrayList<PDQPatientIDResponse> (pdqResponse.getNumRecordsCurrent());
		
		for (int i=0; i < pdqResponse.getNumRecordsCurrent(); i++) {
			String patID[] = pdqResponse2.getPatientID(i, 0); // Only need one, only asked for one
			String patName[] = pdqResponse2.getPatientName(i, 0); // We'll just look at the first name
			String patAddr[] = pdqResponse2.getPatientAddress(i, 0); //We'll just look at the first address
			String patIDString = "ID: " + patID[0];
			patIDString = patIDString + " \nName: " + patName[0] + ", " + patName[1];
			for (int j=2; j < patName.length; j++) {
				if (patName[j] != null) {
					patIDString = patIDString + " " + patName[j];
				}
			}
			patIDString = patIDString + " \nAddress: " + patAddr[0];
			for (int j=1; j < patAddr.length; j++) {
				if (patAddr[j] != null) {
					patIDString = patIDString + ", " + patAddr[j];
				}
			}
			patIDString = patIDString + "\n";
			System.out.println(patIDString);
			
			patIDRspListOut.add(new PDQPatientIDResponse(patID, patIDString));
		}
		
*/		

		// TODO Add return of patient ID and demographic structures
	    return patIDRspListOut;
	    
	}


	File xmlPDQLocFile = new File("./config/pdq_locations.xml");
	public boolean runStartupSequence() {
		if(xmlPDQLocFile == null ){return false;}
		try {
			loadPDQLocations(xmlPDQLocFile);				
		} catch (IOException e) {
			// TODO Auto-generated catch block				
			System.out.println("PDQ module startup sequence error. " + 
			"System could not find: pdq_locations.xml");
			return false;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("PDQ startup sequence error. " + 
			"Error when processing pdq_locations.xml");
			return false;
		}
		return true;
	}
	
	public boolean runShutDownSequence(){
		//closeDicomServer();		
		return true;
	}

}
