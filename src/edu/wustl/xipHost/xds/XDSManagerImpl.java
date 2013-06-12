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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import org.openhealthtools.ihe.atna.auditor.PDQConsumerAuditor;
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
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqConsumer;
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqConsumerQuery;
import org.openhealthtools.ihe.pdq.consumer.v3.V3PdqConsumerResponse;
import org.openhealthtools.ihe.xds.consumer.B_Consumer;
import org.openhealthtools.ihe.xds.consumer.query.DateTimeRange;
import org.openhealthtools.ihe.xds.consumer.query.MalformedQueryException;
import org.openhealthtools.ihe.xds.consumer.retrieve.DocumentRequestType;
import org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveDocumentSetRequestType;
import org.openhealthtools.ihe.xds.consumer.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.GetDocumentsQuery;
import org.openhealthtools.ihe.xds.consumer.storedquery.MalformedStoredQueryException;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.metadata.MetadataFactory;
import org.openhealthtools.ihe.xds.metadata.constants.DocumentEntryConstants;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.PersonNameAttribute;
import com.pixelmed.dicom.TagFromName;

import edu.wustl.xipHost.caGrid.GridManager;
import edu.wustl.xipHost.caGrid.GridManagerFactory;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.XDSDocumentItem;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.pdq.PDQLocation;
import edu.wustl.xipHost.xds.XDSRegistryLocation;

/**
 * @author Jaroslaw Krych (stubs, tree display of results), Lawrence Tarbox (OHT implementation)
 *
 */
public class XDSManagerImpl implements XDSManager{

	Document documentPdq;
	Element rootPdq;
	SAXBuilder builder = new SAXBuilder();
	List<PDQLocation> pdqLocations = new ArrayList<PDQLocation>();
	Document documentXds;
	Element rootXds;
	List<XDSRegistryLocation> xdsRegistryLocations = new ArrayList<XDSRegistryLocation>();
	
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

	public List<XDSPatientIDResponse> queryPatientIDs(AttributeList queryKeys, PDQLocation pdqSupplier) {	
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
			List<XDSPatientIDResponse> testIDs = new ArrayList<XDSPatientIDResponse>();

			String patientID = queryKeys.get(TagFromName.PatientID).getSingleStringValueOrNull();
			if (patientID != null){
				String assigningAuthority = queryKeys.get(TagFromName.IssuerOfPatientID).getSingleStringValueOrEmptyString();
				String patID[] = {patientID, "", assigningAuthority, "ISO"}; // NIST test ID
				String patIDString = "ID: " + patID[0] + " \nName: User Input\n";
				testIDs.add(new XDSPatientIDResponse(patID, patIDString));
			}

			String patIDa[] = {"5541138b47a445a", "", "1.3.6.1.4.1.21367.2005.3.7", "ISO"}; // NIST test ID
			String patIDStringa = "ID: " + patIDa[0] + " \nName: NIST Test Subject\n";
			testIDs.add(new XDSPatientIDResponse(patIDa, patIDStringa));

			String patIDb[] = {"b89182560b284fd", "", "1.3.6.1.4.1.21367.2005.3.7", "ISO"}; // NIST test ID
			String patIDStringb = "ID: " + patIDb[0] + " \nName: NIST Test Subject - one docs\n";
			testIDs.add(new XDSPatientIDResponse(patIDb, patIDStringb));

			String patIDc[] = {"5a6d285d57bf408", "", "1.3.6.1.4.1.21367.2005.3.7", "ISO"}; // NIST test ID
			String patIDStringc = "ID: " + patIDc[0] + " \nName: NIST Test Subject - two docs\n";
			testIDs.add(new XDSPatientIDResponse(patIDc, patIDStringc));
			
			//String patIDd[] = {"123", "", "1.3.6.1.4.1.21367.2010.1.2.300", "ISO"};
			String patIDd[] = {"101", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDStringd = "ID: " + patIDd[0] + " \nName: IBM\n";
			testIDs.add(new XDSPatientIDResponse(patIDd, patIDStringd));
			
			//String patIDe[] = {"4111", "", "1.3.6.1.4.1.21367.2010.1.2.300", "ISO"}; 
			//String patIDe[] = {"779911", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDe[] = {"20101210161154", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDStringe = "ID: " + patIDe[0] + " \nName: Oracle\n";
			testIDs.add(new XDSPatientIDResponse(patIDe, patIDStringe));
			
			String patIDf[] = {"TestPatient1", "", "1.3.6.1.4.1.21367.13.20.1000", "ISO"};
			String patIDStringf = "ID: " + patIDf[0] + " \nName: CareEvolution\n";
			testIDs.add(new XDSPatientIDResponse(patIDf, patIDStringf));

			String patIDg[] = {"1", "", "1.3.6.1.4.1.21367.13.20.2000", "ISO"};
			String patIDStringg = "ID: " + patIDg[0] + " \nName: GE\n";
			testIDs.add(new XDSPatientIDResponse(patIDg, patIDStringg));

			String patIDh[] = {"161111", "", "1.3.6.1.4.1.21367.13.20.3000", "ISO"};
			String patIDStringh = "ID: " + patIDg[0] + " \nName: MOSS\n";
			testIDs.add(new XDSPatientIDResponse(patIDh, patIDStringh));

			String patIDi[] = {"20101215162537", "", "1.3.6.1.4.1.21367.13.20.3000", "ISO"}; 
			String patIDStringi = "ID: " + patIDi[0] + " \nName: eCW\n";
			testIDs.add(new XDSPatientIDResponse(patIDi, patIDStringi));

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

	public List<XDSPatientIDResponse> queryPatientIDsV2(AttributeList queryKeys, PDQLocation pdqSupplier) {		
		System.out.println("Finding Patient IDs V2.");
		List<XDSPatientIDResponse> patIDRspListOut = null;
		
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

	public List<XDSPatientIDResponse> queryPatientIDsV3(AttributeList queryKeys, PDQLocation pdqSupplier) {		
		System.out.println("Finding Patient IDs V3.");
		List<XDSPatientIDResponse> patIDRspListOut = null;

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
		patIDRspListOut = new ArrayList<XDSPatientIDResponse> (pdqResponse.getNumRecordsCurrent());
		
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
			
			patIDRspListOut.add(new XDSPatientIDResponse(patID, patIDString));
		}

		// TODO Add return of patient ID and demographic structures
	    return patIDRspListOut;
	    
	}

	B_Consumer c = null;
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#loadPDQLocations(java.io.File)
	 */
	public boolean loadXDSRegistryLocations(File file) throws IOException, JDOMException {				
		documentXds = builder.build(file);
		rootXds = documentXds.getRootElement();										
		List<?> children = rootXds.getChildren("xds_registry_location");				
		for (int i = 0; i < children.size(); i++){
			String address = (((Element)children.get(i)).getChildText("XDSRegistryURL"));
			String shortName = (((Element)children.get(i)).getChildText("ShortName"));
			try {
				XDSRegistryLocation loc = new XDSRegistryLocation(address, shortName);
				xdsRegistryLocations.add(loc);
			} catch (URISyntaxException e) {
				System.out.println("Unable to load: " + address + " " + shortName + " - invalid location.");				
			}																									
		}
		return true;							
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#storeXDSLocations(java.util.List, java.io.File)
	 */
	public boolean storeXDSRegistryLocations(List<XDSRegistryLocation> locations, File file) throws FileNotFoundException {					
		Element rootSave = new Element("locations");
		Document document = new Document();
		document.setRootElement(rootSave);		
		if(locations == null){return false;}
		for(int i = 0; i < locations.size(); i++){						
			Element xdsRegistryElem = new Element("xds_registry_location");
			Element registryURL = new Element("XDSRegistryURL");
			Element shortNameElem = new Element("ShortName");
			xdsRegistryElem.addContent(registryURL);
			xdsRegistryElem.addContent(shortNameElem);
			rootSave.addContent(xdsRegistryElem);
			registryURL.addContent(locations.get(i).getXDSRegistryURL());
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
	 * @see edu.wustl.xipHost.xds.XDSManager#addXDSRegistryLocation(edu.wustl.xipHost.globalSearch.XDSRegistryLocation)
	 */
	public boolean addXDSRegistryLocation(XDSRegistryLocation xdsRegistryLocation){		
		try {
			if(!xdsRegistryLocations.contains(xdsRegistryLocation)){
				return xdsRegistryLocations.add(xdsRegistryLocation);
			} else {
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}								
	}	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#modifyXDSRegistryLocation(edu.wustl.xipHost.globalSearch.PDQLocation, edu.wustl.xipHost.globalSearch.XDSRegistryLocation)
	 */
	public boolean modifyXDSRegistryLocation(XDSRegistryLocation oldXDSRegistryLocation, XDSRegistryLocation newXDSRegistryLocation) {
		//validate method is used to check if parameters are valid, are notmissing, 
		//do not contain empty strings or do not start from white spaces		
		try {
			int i = xdsRegistryLocations.indexOf(oldXDSRegistryLocation);
			if (i != -1){
				xdsRegistryLocations.set(i, newXDSRegistryLocation);
				return true;
			} else{
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}
	}	
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#removeXDSRegistryLocation(edu.wustl.xipHost.globalSearch.XDSRegistryLocation)
	 */
	public boolean removeXDSRegistryLocation(XDSRegistryLocation xdsRegistryLocation){
		//System.out.println(PDQLocations.indexOf(PDQLocation));
		try {
			return xdsRegistryLocations.remove(xdsRegistryLocation);
		} catch (IllegalArgumentException e){
			return false;
		}		
	}

	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.xds.XDSManager#getXDSRegistryLocations()
	 */
	public List<XDSRegistryLocation> getXDSRegistryLocations(){
		return xdsRegistryLocations;
	}	

	//public XDSQueryResponseType queryDocuments(String [] patientIDin) {		
	public	SearchResult queryDocuments(String [] patientIDin, XDSRegistryLocation xdsRegistry) {
		if (patientIDin == null){
			return null;
		}
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
		String registryURL = xdsRegistry.getXDSRegistryURL();
		//String registryURL = "http://hxti1:8080/ihe/registry";
		//String registryURL = "http://hcxw2k1.nist.gov:8080/xdsServices2/registry/soap/portals/yr3a/storedquery";
		//String registryURL = "http://129.6.24.109:9080/axis2/services/xdsregistrya";
		//String registryURL = "http://ihexds.nist.gov:9080/tf5/services/xdsregistrya"; // 9085 for tls, swap a for b for XDS.b
		//String registryURL = "https://ihexds.nist.gov:9085/tf5/services/xdsregistrya";

		//String registryURL = "http://ihexds.nist.gov:9080/tf6/services/xdsregistryb"; // NIST on net; 2010, 2011
		//String registryURL = "https://ihexds.nist.gov:9085/tf6/services/xdsregistryb";
		//String registryURL = "http://ihexds.nist.gov:9080/tf6/services/xcaregistry";
		//String registryURL = "https://ihexds.nist.gov:9085/tf6/services/xcaregistry";

		//String registryURL = "https://nist1.ihe.net:9085/tf5/services/xdsregistryb"; // NIST at connectathon
		//String registryURL = "https://127.0.0.1:4100/test";
		//String registryURL = "https://spirit1:8443/XDS/registry";
		//String registryURL = "https://ith-icoserve1:8243/Registry/services/RegistryService";
		//String registryURL = "http://82.15.200.163:8080/Registry/services/RegistryService";
		//String registryURL = "https://ibm3:9448/IBMXDSRegistry/XDSb/SOAP12/Registry";
		//String registryURL = "https://xds-ibm.lgs.com:9443/IBMXDSRegistry/XDSb/SOAP12/Registry";
		// No TLS 2010 Internet Testing
		//String registryURL = "http://198.160.211.53:8010/axis2/services/xdsregistryb"; //MISYS
		//String registryURL = "http://xds-ibm.lgs.com:9080/IBMXDSRegistry/XDSb/SOAP12/Registry";
		//String registryURL = "http://208.115.106.221:1025/xdsservice/xdsregistry";
		//String registryURL = "http://62.182.99.61:8080/pxs-vmr-assembly/webservices/rev6/xdsb-storedquery";
		//String registryURL = "http://208.81.185.143:8080/axis2/services/xdsregistryb";
		//String registryURL = "http://208.37.137.112/xds/DocumentRegistry_Service";
		// TLS 2010 Internet Testing
		//String registryURL = "https://198.160.211.53:8011/axis2/services/xdsregistryb";
		//String registryURL = "https://xds-ibm.lgs.com:9443/IBMXDSRegistry/XDSb/SOAP12/Registry";
		//String registryURL = "https://208.115.106.221:9080/xdsservice/xdsregistry";
		//String registryURL = "https://62.182.99.61:8443/ehealth/webservices/rev6/xdsb-storedquery";
		//String registryURL = "https://208.81.185.143:8181/axis2/services/xdsregistryb";
		//String registryURL = "https://208.37.137.112/xds/DocumentRegistry_Service";
		// No TLS 2011 Internet Testing
		//String registryURL = "http://184.73.10.59:8080/pxs-vmr-assembly/unsecured_webservices/rev6/xdsb-storedquery"; //GE
		//String registryURL = "http://99.6.95.22:8080/axis2/services/xdsregistryb"; //Oracle
		//String registryURL = "http://198.160.211.53:8010/openxds/services/DocumentRegistry"; //MOSS
		//String registryURL = "http://174.129.27.111/InteropSandbox/IheAdapter/XdsRegistryService/XdsRegistryService"; //CareEvolution
		//String registryURL = "http://xds-ibm.lgs.com:9080/IBMXDSRegistry/XDSb/SOAP12/Registry"; //IBM
		//String registryURL = "http://72.248.114.66:8010/axis2/services/xdsregistryb"; //eCW
		
		// TLS 2011 Internet Testing
		//String registryURL = ""; //GE
		//String registryURL = "https://99.6.95.22:8181/axis2/services/xdsregistryb"; //Oracle
		//String registryURL = "https://198.160.211.53:8011/openxds/services/DocumentRegistry"; //MOSS
		//String registryURL = "https://174.129.27.111:8080/InteropSandbox/IheAdapter/XdsRegistryService/XdsRegistryService"; //CareEvolution
		//String registryURL = "https://xds-ibm.lgs.com:9443/IBMXDSRegistry/XDSb/SOAP12/Registry"; //IBM
		//String registryURL = "https://72.248.114.66:8011/axis2/services/xdsregistryb"; //eCW
		
		// 2011 NA Connectathon
		//String registryURL = "https://tiani-cisco6:8443/SpiritProxy/registry"; // Tiani-XUA
			
		// TODO Get URI from a config file
		URI registryURI = null;
		try {
			registryURI = new URI(registryURL);
		} catch (URISyntaxException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		System.out.println("URI of the XDS Registry - " + registryURI.toString());
		
		c = new B_Consumer(registryURI);

		//((B_Consumer)c).setPrimaryRepositoryURI(primaryRepositoryURI); //only if repos supports consolidation
		//String NIST_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2008.1.2.701";
		String XDS_B_REPOSITORY_UNIQUE_ID = "1.19.6.24.109.42.1.5"; // NIST per web site
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.19.6.24.109.42.1"; // NIST per stored doc
		//String XDS_B_REPOSITORY_UNIQUE_ID = "2.16.840.1.113662.2.1.53"; // Spirit
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2009.1.2.1030"; // IBM
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2008.2.5.102"; //ITH
		
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1125";//MISYS
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1155";//IBM
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1140";//Microsoft
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1130";//AXSYS
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1055"; //ICW
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1145";//Oracle
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1095";//AXOLOTL
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.2010.1.2.1040";//Vangent
			// 2011 Internet Testing
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1055";//EMC
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1185";//Vangent
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1045";//eCW
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1050";//EMC_IIG
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1030";//CareFx
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1150";//MOSS
		
		// 2011 NA Connectationg
		//String XDS_B_REPOSITORY_UNIQUE_ID = "1.3.6.1.4.1.21367.13.1180"; // Tiani

		URI XDS_B_INITIATING_GATEWAY = null;
		URI XDS_B_REPOSITORY_URI = null;
		try {
			String NIST_INITIATING_GATEWAY = "http://ihexds.nist.gov:9080/tf6/services/ig";
			String NIST_INITIATING_GATEWAY_SECURE = "https://ihexds.nist.gov:9085/tf6/services/ig";
			String IBM_INITIATING_GATEWAY = "https://ibm3:9448/XGatewayWS/InitiatingGatewayQuery";
			String ITH_INITIATING_GATEWAY = "https://ith-icoserve1:8143/XCommunityBridge/services/InitiatingGatewayService";
			//String IBM_INITIATING_GATEWAY = "http://ibm3:9085/XGatewayWS/InitiatingGatewayRetrieve";
			XDS_B_INITIATING_GATEWAY = new URI(NIST_INITIATING_GATEWAY);

			//String NIST_B_STORED_QUERY_SECURED = "https://nist1.ihe.net:9085/tf5/services/xdsrepositoryb";
			
			// 2011 NA Connectathon
			//XDS_B_REPOSITORY_URI = new URI("https://tiani-cisco6:8443/SpiritProxy/repository"); // Spirit XUA

			XDS_B_REPOSITORY_URI = new URI("http://ihexds.nist.gov:9080/tf6/services/xdsrepositoryb"); // NIST net; 2010, 2011
			//XDS_B_REPOSITORY_URI = new URI("http://ihexds.nist.gov:9080/tf6/services/xcarepository");
			//XDS_B_REPOSITORY_URI = new URI("https://ihexds.nist.gov:9085/tf6/services/xdsrepositoryb");
			//XDS_B_REPOSITORY_URI = new URI("https://ihexds.nist.gov:9085/tf6/services/xcarepository");

			//XDS_B_REPOSITORY_URI = new URI(NIST_B_STORED_QUERY_SECURED);
			//XDS_B_REPOSITORY_URI = new URI("https://spirit1:8443/XDS/repository");
			//XDS_B_REPOSITORY_URI = new URI("https://ith-icoserve1:8243/Repository/services/RepositoryService");
			//XDS_B_REPOSITORY_URI = new URI("http://82.150.200.163:8080/Repository/services/RepositoryService");
			//XDS_B_REPOSITORY_URI = new URI("https://ibm3:9448/IBMXDSRepository/XDSb/SOAP12/Repository");
			//XDS_B_REPOSITORY_URI = new URI("xds-ibm.lgs.com:9443/IBMXDSRepository/XDSb/SOAP12/Repository");
			
			//XDS_B_REPOSITORY_URI = new URI("http://198.160.211.53:8020/axis2/services/xdsrepositoryb"); //MISYS
			//String XDS_B_REPOSITORY_URI = new URI("http://xds-ibm.lgs.com:9080/IBMXDSRepository/XDSb/SOAP12/Repository"); //IBM
			//XDS_B_REPOSITORY_URI = new URI("http://208.115.106.221:1025/xdsservice/xdsrepository"); //Microsoft
			//XDS_B_REPOSITORY_URI = new URI("http://203.196.189.158:6666/services/xdsrepositoryb"); //AXSYS
			//XDS_B_REPOSITORY_URI = new URI("http://62.182.99.61:8080/pxs-vmr-assembly/webservices/xdsb-retrievedocuments"); //ICW
			//String XDS_B_REPOSITORY_URI = new URI("http://oracle2:7777/XDS/xdsrepositoryb_Soap12"); //Oracle
			//String XDS_B_REPOSITORY_URI = new URI("http://208.37.137.112/xds/DocumentRepository_Service");//AXOLOTL
			//XDS_B_REPOSITORY_URI = new URI("http://208.81.185.143:8080/axis2/services/xdsrepositoryb"); //Vangent
			
			//String XDS_B_REPOSITORY_URI = new URI("https://198.160.211.53:8021/axis2/services/xdsrepositoryb"); //MISYS
			//XDS_B_REPOSITORY_URI = new URI("https://xds-ibm.lgs.com:9443/IBMXDSRepository/XDSb/SOAP12/Repository"); //IBM
			//XDS_B_REPOSITORY_URI = new URI("https://208.115.106.221:9081/xdsservice/xdsrepository"); //Microsoft
			//String XDS_B_REPOSITORY_URI = new URI("https://203.196.189.158:7777/services/xdsrepositoryb"); //AXSYS
			//String XDS_B_REPOSITORY_URI = new URI("http://62.182.99.61:8443/pxs-vmr-assembly/webservices/xdsb-retrievedocuments"); //ICW
			//String XDS_B_REPOSITORY_URI = new URI("https://oracle2:4444/XDS/xdsrepositoryb_Soap12"); //Oracle
			//String XDS_B_REPOSITORY_URI = new URI("https://208.37.137.112/xds/DocumentRepository_Service"); //AXOLOTL
			//XDS_B_REPOSITORY_URI = new URI("https://208.81.185.143:8181/axis2/services/xdsrepositoryb"); //Vangent
			
			//String XDS_B_REPOSITORY_URI = new URI("http://demo.karoshealth.com:8080/ids/rids)";

				// No TLS 2011 Internet Testing
			//XDS_B_REPOSITORY_URI = new URI("http://68.179.255.83:9091/mosaix-iti43"); // EMC
			//XDS_B_REPOSITORY_URI = new URI("http://208.81.185.143:8080/axis2/services/xdsrepositoryb"); // Vangent
			//XDS_B_REPOSITORY_URI = new URI("http://72.248.114.66:8020/axis2/services/xdsrepositoryb"); // eCW
			//XDS_B_REPOSITORY_URI = new URI("http://204.236.129.242:9190/xds-iti43"); // EMC_IG
			//XDS_B_REPOSITORY_URI = new URI("http://xdstest.carefx.com:8080/axis2/services/DocumentRepositoryService"); // carefx
			//XDS_B_REPOSITORY_URI = new URI("http://198.160.211.53:8010/openxds/services/DocumentRepository"); // MOSS
				// TLS 2011 Internet Testing
			//XDS_B_REPOSITORY_URI = new URI("https://68.179.255.83:9092/mosaix-iti43"); // EMC secure
			//XDS_B_REPOSITORY_URI = new URI("https://208.81.185.143:8181/axis2/services/xdsrepositoryb"); // Vangent
			//XDS_B_REPOSITORY_URI = new URI("https://72.248.114.66:8021/axis2/services/xdsrepositoryb"); // eCW
			//XDS_B_REPOSITORY_URI = new URI("https://204.236.129.242:9191/xds-iti43"); // EMC_IG
			//XDS_B_REPOSITORY_URI = new URI("https://xdstest.carefx.com:8443/axis2/services/DocumentRepositoryService"); // carefx
			//XDS_B_REPOSITORY_URI = new URI("https://198.160.211.53:8011/openxds/services/DocumentRepository"); // MOSS

		} catch (URISyntaxException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
		//c.setInitiatingGatewayURI(XDS_B_INITIATING_GATEWAY); //needed to resolve home commmunity IDs
		c.getRepositoryMap().put(XDS_B_REPOSITORY_UNIQUE_ID, XDS_B_REPOSITORY_URI);
		
		//////////////////////////////////////////  //////////////////////////////////////
		//Construct the parameters to our FindDocumentsQuery
		////////////////////////////////////////////////////////////////////////////////
		
		CX patientId = Hl7v2Factory.eINSTANCE.createCX();
		// TODO PIX lookup if assigning authority is not what we expect.  Also, read assigning authority from config file

		//our IDs would be incorporated here
		//TODO: configure in the defaults
		String defaultAssigningAuthority = "1.3.6.1.4.1.21367.13.20.2000";
		patientId.setIdNumber(patientIDin[0]);
		if (patientIDin.length > 2) {
			patientId.setAssigningAuthorityUniversalId(patientIDin[2]);
		} else {
			patientId.setAssigningAuthorityUniversalId("defaultAssigningAuthority");
		}
		if (patientIDin.length > 3){
			patientId.setAssigningAuthorityUniversalIdType(patientIDin[3]);
		} else {
			patientId.setAssigningAuthorityUniversalIdType("ISO");
		}

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
		hcfc1[0].setCode("OF");
		hcfc1[0].setSchemeName("2.16.840.1.113883.5.11");
		
		//Create a list of document status types we want to search on. In this example,
		//we only want �Approved� documents.
		AvailabilityStatusType[] status = {AvailabilityStatusType.APPROVED_LITERAL};
		
		////////////////////////////////////////////////////////////////////////////////
		//Construct our FindDocumentsQuery for patient
		////////////////////////////////////////////////////////////////////////////////
		FindDocumentsQuery query = null;
		//FindDocumentsForMultiplePatientsQuery query = null;
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
			/*new FindDocumentsForMultiplePatientsQuery(
					null, // no classCodes
					null, // creationTimeRange, //creationTimeRange,
					null, // no practiceSettingCodes
					hcfc1, //null, //new CodedMetadataType[]{hcfc1},
					null, // no eventCodes
					null, // no confidentialityCodes
					null, // no formatCodes
					status);*/
			//new FindDocumentsForMultiplePatientsQuery(status);
			
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
			GetDocumentsQuery docsQuery = new GetDocumentsQuery(docReferences, true /*, "urn:oid:1.19.6.24.109.42.1.3"*/);				
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
		Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		patient.setLastUpdated(lastUpdated);
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
			//Set itemFromAD WG23 object descriptor			
			ObjectDescriptor objDesc = new ObjectDescriptor();
			Uuid objDescUUID = new Uuid();
			objDescUUID.setUuid(UUID.randomUUID().toString());
			objDesc.setUuid(objDescUUID);
			objDesc.setMimeType(mime);			
			Uid uid = new Uid();
			String sopClassUID = "";
			uid.setUid(sopClassUID);
			objDesc.setClassUID(uid);				
			Modality mod = new Modality();
			mod.setModality("");
			objDesc.setModality(mod);
			item.setObjectDescriptor(objDesc);
			patient.addItem(item);
		}			
		return searchResult;
			
		// TODO Track the paging - we have two responses - one with all UUIDs, and one with just the info on the first 10.		
		//return responseDetails;				
	}

	public File retrieveDocument(DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId){
		File destFile = null;
		// build the document request
	    RetrieveDocumentSetRequestType retrieveRequest = org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory.eINSTANCE.createRetrieveDocumentSetRequestType();
	    DocumentRequestType documentRequest = org.openhealthtools.ihe.xds.consumer.retrieve.RetrieveFactory.eINSTANCE.createDocumentRequestType(); 
	    documentRequest.setRepositoryUniqueId(docEntryDetails.getRepositoryUniqueId());
	    documentRequest.setHomeCommunityId(homeCommunityId);
	    documentRequest.setDocumentUniqueId(docEntryDetails.getUniqueId());
	    
	    retrieveRequest.getDocumentRequest().add(documentRequest);

	    //c.getRepositoryMap().put(XDS_B_REPOSITORY_UNIQUE_ID, new URI("http://194.121.72.76:8080/connect/services/wado-b")); //Philips
	    	
		// execute retrieve
	    XDSRetrieveResponseType response = null;
		try {
			response = c.retrieveDocumentSet(false, retrieveRequest, docEntryDetails.getPatientId());
		} catch (Exception e) {
			System.out.println(e.toString());
			//throw e;
			return null;
		}
		System.out.println("Response status: " + response.getStatus().getName());

		XDSDocument document = null;
        List<XDSDocument> documents = response.getAttachments();
		if(null != documents){
			System.out.println("Returned " + documents.size() + " documents.");
			document = response.getAttachments().get(0);
			System.out.println("First document returned: " + document.toString());

			// TODO generate file name from the document UID and mime type.
			System.out.println("Mime type:  " + docEntryDetails.getMimeType());
			String extension = ".txt"; // default setting
			if (docEntryDetails.getMimeType().contains("application/pdf")) {
				extension = ".pdf";
			} else if (docEntryDetails.getMimeType().contains("text/xml")) {
				extension = ".xml";
			} else if (docEntryDetails.getMimeType().contains("application/dicom")) {
				extension = ".dcm";
			} // TODO just take whatever follows the "/" as the extension?
			
			//GridManagaer is used only to get Tmp directory
			GridManager gridMgr = GridManagerFactory.getInstance();
			File importDir = gridMgr.getImportDirectory();
			File inputDir = null;
			try {
				inputDir = File.createTempFile("XDS-XIPHOST", extension, importDir);
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

			Iterator<XDSDocument> docIterator = documents.iterator();
	        while (docIterator.hasNext()) {
	        	XDSDocument doc = docIterator.next();
	        	System.out.println("Received Document: " + doc);
	        	copyStream(doc.getStream(), out);
	        }
		} else {
			System.out.println("RECEIVED NO DOCUMENTS BACK");
        	return null;
		}

		return destFile;
	}

	private String copyStream(InputStream inputStream, OutputStream out)
	{
        // Transfer bytes from inputStream to out

		byte[] buf = new byte[1024];
        int len;
        try {
			while ((len = inputStream.read(buf)) > 0) {
			    out.write(buf, 0, len);
			}
	        inputStream.close();
	        out.close();	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return "done";

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

	File xmlPDQLocFile = new File("./config/pdq_locations.xml");
	File xmlXDSRegistryLocFile = new File("./config/xds_registry_locations.xml");
	public boolean runStartupSequence() {
		if(xmlPDQLocFile == null ){return false;}
		try {
			loadPDQLocations(xmlPDQLocFile);				
		} catch (IOException e) {
			// TODO Auto-generated catch block				
			System.out.println("XDS module startup sequence error. " + 
			"System could not find: pdq_locations.xml");
			return false;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("XDS startup sequence error. " + 
			"Error when processing pdq_locations.xml");
			return false;
		}

		if(xmlXDSRegistryLocFile == null ){return false;}
		try {
			loadXDSRegistryLocations(xmlXDSRegistryLocFile);				
		} catch (IOException e) {
			// TODO Auto-generated catch block				
			System.out.println("XDS module startup sequence error. " + 
			"System could not find: xds_registry_locations.xml");
			return false;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("XDS startup sequence error. " + 
			"Error when processing xds_registry_locations.xml");
			return false;
		}
		return true;
	}
	
	public boolean runShutDownSequence(){
		//closeDicomServer();		
		return true;
	}

}
