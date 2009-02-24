/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataModel;

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
	
	public XDSDocumentItem(String id, String availability, String language, String mime, 
			DocumentEntryType docEntryDetails, CX patientId){
		this.id = id;
		this.availability = availability;
		this.language = language;
		this.mime = mime;
		this.docEntryDetails = docEntryDetails; 
		this.patientId = patientId;
	}
	
	public DocumentEntryType getDocumentType(){
		return docEntryDetails;
	}
	
	public CX getPatientId(){
		return patientId;
	}
	
	@Override
	public String getItemID() {
		// TODO Auto-generated method stub
		return id;
	}
	
	public String toString(){
		return id + " / " + availability + " / " + language + " / " + mime;
	}

}
