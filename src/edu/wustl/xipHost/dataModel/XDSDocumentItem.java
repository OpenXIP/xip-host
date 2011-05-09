/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;

/**
 * @author Jaroslaw Krych
 *
 */
public class XDSDocumentItem implements Item {
	String id, availability, language, mime;
	DocumentEntryType docEntryDetails;
	CX patientId;
	String homeCommunityId;
	
	public XDSDocumentItem(String id, String availability, String language, String mime, 
			DocumentEntryType docEntryDetails, CX patientId, String homeCommunityId){
		this.id = id;
		this.availability = availability;
		this.language = language;
		this.mime = mime;
		this.docEntryDetails = docEntryDetails; 
		this.patientId = patientId;
		this.homeCommunityId = homeCommunityId;
	}
	
	public String getAvailability() {
		return availability;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getMimeType() {
		return mime;
	}
	
	public String getHomeCommunityId() {
		return homeCommunityId;
	}
	
	public DocumentEntryType getDocumentType(){
		return docEntryDetails;
	}
	
	public CX getPatientId(){
		return patientId;
	}
	
	@Override
	public String getItemID() {
		return id;
	}
	
	public String toString(){
		return id + " / " + availability + " / " + language + " / " + mime;
	}

	ObjectDescriptor objDesc;
	@Override
	public ObjectDescriptor getObjectDescriptor() {
		return objDesc;
	}
	
	public void setObjectDescriptor(ObjectDescriptor objDesc){
		this.objDesc = objDesc;
	}

	ObjectLocator objLoc;
	@Override
	public ObjectLocator getObjectLocator() {
		return objLoc;
	}
	
	public void setObjectLocator(ObjectLocator objLoc){
		this.objLoc = objLoc;
	}
}
