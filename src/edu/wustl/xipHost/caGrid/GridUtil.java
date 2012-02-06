package edu.wustl.xipHost.caGrid;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
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
	
	//if value of aaplyQueryModifier > 0 then QueryModifier is used
	//int numOfSeriesTargetsPassed = 0;
	boolean applyQueryModifier = false;
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
			//numOfSeriesTargetsPassed++;
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
				System.out.println("Query was empty");
				query = new HashMap<String, String>();
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
			}
			/*if(applyQueryModifier){
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Image.class.getCanonicalName());
				cqlq = h2cql.makeCQLQuery(query);
				QueryModifier queryModifier = new QueryModifier();
				queryModifier.setCountOnly(true);
				cqlq.setQueryModifier(queryModifier);
			} else {
				cqlq = h2cql.makeCQLQuery(query);
			}*/
			cqlq = h2cql.makeCQLQuery(query);
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
			} else if(selectedObject instanceof Study){
				study = Study.class.cast(selectedObject);
				series = (Series)obj;
				if(study.contains(series.getSeriesInstanceUID()) == false){
					study.addSeries(series);
				}
			} else if(selectedObject instanceof Series){
				series = Series.class.cast(selectedObject);
				Item item = (Item)obj; 			
				if(series.contains(item.getItemID()) == false){
					series.addItem(item);
				}
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
				String patientName = root.getAttribute("patientName").getValue().trim(); if(patientName == null){patientName = "";}
				String patientID = root.getAttribute("patientId").getValue().trim(); if(patientID == null){patientID = "";}			
				String strPatientBirthDate = "";
				Patient patient = new Patient(patientName, patientID, strPatientBirthDate);
				return patient;
			} else if (objName.equalsIgnoreCase("Study")) {
				String studyDate = root.getAttribute("studyDate").getValue().trim(); if(studyDate == null){studyDate = "";}
				String studyID = root.getAttribute("studyId").getValue().trim(); if(studyID == null){studyID = "";}	
				String studyDesc = root.getAttribute("studyDescription").getValue().trim(); if(studyDesc == null){studyDesc = "";}
				String studyInstanceUID = root.getAttribute("studyInstanceUID").getValue().trim(); if(studyInstanceUID == null){studyInstanceUID = "";}				
				Study study = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				return study;
			} else if (objName.equalsIgnoreCase("Series")) {
				String seriesNumber = root.getAttribute("seriesNumber").getValue().trim(); if(seriesNumber == null){seriesNumber = "";}
				String modality = root.getAttribute("modality").getValue().trim(); if(modality == null){modality = "";}
				String seriesDesc = root.getAttribute("seriesDescription").getValue().trim();if(seriesDesc == null){seriesDesc = "";}
				String seriesInstanceUID = root.getAttribute("instanceUID").getValue().trim(); if(seriesInstanceUID == null){seriesInstanceUID = "";}
				Series series = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);
				return series;
			} else if (objName.equalsIgnoreCase("Image")) {
				return null;
			}
		} catch (JDOMException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	    return null;
	}
	
}
