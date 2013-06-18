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
package edu.wustl.xipHost.osu;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;


public class GridUtil {
	final static Logger logger = Logger.getLogger(GridUtil.class);
	Properties prop = new Properties();
	static Map<String, String> map;
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */	
	public Map<String, String> loadNCIAModelMap(FileInputStream fileInputStream) throws IOException{	
		prop.load(fileInputStream);						
		map = new HashMap<String, String>();		
		Enumeration<Object> enumer = prop.keys();
		while(enumer.hasMoreElements()){
			String name = (String)enumer.nextElement();
			String value = prop.getProperty(name);
			if(!value.isEmpty()){
				map.put(value, name);
			}else{
				return null;
			}	
		}		
		return map;
	}
	
	public static String mapDicomTagToNCIATagName(String tag){
		//Mapping is case sensitive therefore if mapping returns null in first attempt
		//system will try to use toUpperCase except "x" and map to NCIA value again
		if(map.get(tag) != null){
			return map.get(tag);
		}else {
			tag = tag.toUpperCase();
			tag = tag.replace("X", "x");
			return map.get(tag);
		}		 
	}
	
	public CQLQuery convertToCQLStatement(AttributeList criteriaList, CQLTargetName value){
		if(criteriaList == null || value == null){
			return null;
		}
		HashMap<String, String> query = new HashMap<String, String>();
		if(value == CQLTargetName.PATIENT){
			query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Patient.class.getCanonicalName());
		}else if( value == CQLTargetName.STUDY){
			query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Study.class.getCanonicalName());
		}else if(value == CQLTargetName.SERIES){
			query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
		}else if(value == CQLTargetName.IMAGE){
			query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Image.class.getCanonicalName());
		}
		CQLQuery cqlq = null;		
		DicomDictionary dictionary = AttributeList.getDictionary();
		Iterator<?> iter = dictionary.getTagIterator();		
		while(iter.hasNext()){
			AttributeTag attTag  = (AttributeTag)iter.next();
			String attValue = Attribute.getSingleStringValueOrEmptyString(criteriaList, attTag);
			String nciaAttName = null;
			if(!attValue.isEmpty()){
				nciaAttName = mapDicomTagToNCIATagName(attTag.toString());
			}
			if(nciaAttName != null){
				//System.out.println(nciaAttName + " " + attValue);
				logger.debug("Attribute name: " + nciaAttName + " Value: " + attValue);
				//wild card is not allowed with grid criteria and should be replaced by empty string 
				if(attValue.equalsIgnoreCase("*")){attValue = "";}
				query.put(nciaAttName, attValue);
			}							
		}								
		try {
			HashmapToCQLQuery h2cql = new HashmapToCQLQuery(new ModelMap());
			if (query.isEmpty()) {					
				logger.warn("Query was empty");
				query = new HashMap<String, String>();
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
			}
			cqlq = h2cql.makeCQLQuery(query);
			if(value == CQLTargetName.IMAGE){
				QueryModifier queryModifier = new QueryModifier();
				queryModifier.setCountOnly(true);
				cqlq.setQueryModifier(queryModifier);
			}
			/*System.err.println(ObjectSerializer.toString(cqlq, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));*/
			/*if(numOfSeriesTargetsPassed > 0){
				applyQueryModifier = true;
			}*/
			return cqlq;
		} catch (FileNotFoundException e) {
			logger.error(e, e);
			return null;
		} catch (ModelMapException e) {
			logger.error(e, e);
			return null;
		} catch (IOException e) {
			logger.error(e, e);
			return null;
		} catch (ClassNotFoundException e) {
			logger.error(e, e);
			return null;
		} catch (MalformedQueryException e) {
			logger.error(e, e);
			return null;
		}		
	}
	
	
	public static SearchResult convertCQLQueryResultsIteratorToSearchResult(CQLQueryResultsIterator iter, GridLocation location, SearchResult initialSearchResult, Object selectedObject){
		SearchResult resultGrid = null;
		if(initialSearchResult == null){
			resultGrid = new SearchResult(location.getShortName());
		}else{
			resultGrid = initialSearchResult;
		}	
		Patient patient = null;
		Study study = null;
		Series series = null;
	
		while (iter.hasNext()) {
		    String xmlObj = (String)iter.next();
		    Object obj = convertToDataModelObject(xmlObj);
			//selectedObject == null means it is a first query in a progressive query process 
			if(selectedObject == null){
				if(obj instanceof Patient){
					patient = (Patient)obj;
					if(resultGrid.contains(patient.getPatientID()) == false){
						resultGrid.addPatient(patient);
					}
				}else{
					
				}
			} else if(selectedObject instanceof Patient){
				patient = Patient.class.cast(selectedObject);
				study = (Study)obj;
				if(patient.contains(study.getStudyInstanceUID()) == false){
					patient.addStudy(study);
				}
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				patient.setLastUpdated(lastUpdated);
			} else if(selectedObject instanceof Study){
				study = Study.class.cast(selectedObject);
				series = (Series)obj;
				if(study.contains(series.getSeriesInstanceUID()) == false){
					study.addSeries(series);
				}
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				study.setLastUpdated(lastUpdated);
			} else if(selectedObject instanceof Series){
				series = Series.class.cast(selectedObject);
				//Item item = (Item)obj; 
				int countResult = (Integer)obj;
				for(int i = 0; i < countResult; i++){
					ObjectDescriptor objDesc = new ObjectDescriptor();
					Uuid objDescUUID = new Uuid();
					objDescUUID.setUuid(UUID.randomUUID().toString());
					objDesc.setUuid(objDescUUID);
					String mimeType = "application/dicom";
					objDesc.setMimeType(mimeType);			
					Uid uid = new Uid();
					String sopClassUID = "";
					uid.setUid(sopClassUID);
					objDesc.setClassUID(uid);				
					Modality mod = new Modality();
					mod.setModality("");
					objDesc.setModality(mod);
					Item item = new ImageItem(String.valueOf(i));
					item.setObjectDescriptor(objDesc);
					series.addItem(item);
				}
				Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
				series.setLastUpdated(lastUpdated);
			}
		}
		return resultGrid;
	}
	
	static SAXBuilder builder = new SAXBuilder();
	static Object convertToDataModelObject(String xml){
	    Document document;
	    Element root;
	    InputStream is = new ByteArrayInputStream(xml.getBytes());
	    try {
			document = builder.build(is);
			root = document.getRootElement();
			String objName = root.getName();
			if(objName.equalsIgnoreCase("Patient")){
				String patientName = root.getAttributeValue("patientName");
				if(patientName != null) {
					patientName.trim(); 
				} else {
					patientName = "";
				}
				String patientID = root.getAttributeValue("patientId");
				if(patientID != null) {
					patientID.trim(); 			
				} else {
					patientID = "";
				}
				String strPatientBirthDate = "";
				Patient patient = new Patient(patientName, patientID, strPatientBirthDate);
				return patient;
			} else if (objName.equalsIgnoreCase("Study")) {
				String studyDate = root.getAttributeValue("studyDate");
				if(studyDate != null) {
					studyDate.trim(); 
				} else {
					studyDate = "";
				}
				String studyID = root.getAttributeValue("studyId");
				if(studyID != null) {
					studyID.trim(); 
				} else {
					studyID = "";
				}
				String studyDesc = root.getAttributeValue("studyDescription");
				if(studyDesc != null) {
					studyDesc.trim(); 
				} else {
					studyDesc = "";
				}
				String studyInstanceUID = root.getAttributeValue("studyInstanceUID");
				if(studyInstanceUID != null) {
					studyInstanceUID.trim(); 				
				} else {
					studyInstanceUID = "";
				}
				Study study = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				return study;
			} else if (objName.equalsIgnoreCase("Series")) {
				String seriesNumber = root.getAttributeValue("seriesNumber");
				if(seriesNumber != null) {
					seriesNumber.trim(); 
				} else {
					seriesNumber = "";
				}
				String modality = root.getAttributeValue("modality");
				if(modality != null){
					modality.trim(); 
				} else {
					modality = "";
				}
				String seriesDesc = root.getAttributeValue("seriesDescription");
				if(seriesDesc != null) {
					seriesDesc.trim();
				} else {
					seriesDesc = "";
				}
				String seriesInstanceUID = root.getAttributeValue("instanceUID");
				if(seriesInstanceUID != null) {
					seriesInstanceUID.trim(); 
				} else {
					seriesInstanceUID = "";
				}
				Series series = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);
				return series;
			} else if (objName.equalsIgnoreCase("CQLCountResult")) {
				String countResult = root.getAttributeValue("count").trim(); if(countResult == null){countResult = "0";}
				return Integer.valueOf(countResult);
			}
		} catch (JDOMException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	    return null;
	}
	
}
