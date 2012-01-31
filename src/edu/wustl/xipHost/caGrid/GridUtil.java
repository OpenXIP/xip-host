package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
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
	
	//if value of aaplyQueryModifier > 0 then QueryModifier is used
	int numOfSeriesTargetsPassed = 0;
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
			numOfSeriesTargetsPassed++;
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
			if(applyQueryModifier){
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Image.class.getCanonicalName());
				cqlq = h2cql.makeCQLQuery(query);
				QueryModifier queryModifier = new QueryModifier();
				queryModifier.setCountOnly(true);
				cqlq.setQueryModifier(queryModifier);
			} else {
				cqlq = h2cql.makeCQLQuery(query);
			}
				
			/*System.err.println(ObjectSerializer.toString(cqlq, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));*/
			if(numOfSeriesTargetsPassed > 0){
				applyQueryModifier = true;
			}
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
		Patient patientFromGrid = null;
		Study studyFromGrid = null;
		Series seriesFromGrid = null;
		while (iter.hasNext()) {			
			java.lang.Object obj = iter.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
			//selectedObject == null means it is a first query in a progressive query process 
			if(selectedObject == null){
				if(obj instanceof gov.nih.nci.ncia.domain.Patient){
					gov.nih.nci.ncia.domain.Patient patientGrid = gov.nih.nci.ncia.domain.Patient.class.cast(obj);			
					String patientName = patientGrid.getPatientName(); if(patientName == null){patientName = "";}
					String patientID = patientGrid.getPatientId(); if(patientID == null){patientID = "";}
					Calendar patientBirthDate = patientGrid.getPatientBirthDate();				
					String strPatientBirthDate = null;
					if(patientBirthDate != null){
						strPatientBirthDate = sdf.format(patientBirthDate.getTime());
						if(strPatientBirthDate == null){strPatientBirthDate = "";}
			        }else{
			        	strPatientBirthDate = "";
			        }
					if(resultGrid.contains(patientID) == false){
						patientFromGrid = new Patient(patientName, patientID, strPatientBirthDate);
						resultGrid.addPatient(patientFromGrid);
					}
				}else{
					
				}
			} else if(selectedObject instanceof Patient){
				patientFromGrid = Patient.class.cast(selectedObject);
				gov.nih.nci.ncia.domain.Study studyGrid = gov.nih.nci.ncia.domain.Study.class.cast(obj);				
				Calendar calendar = studyGrid.getStudyDate(); 			
				String studyDate = null;
				if(calendar != null){
		        	studyDate = sdf.format(calendar.getTime());if(studyDate == null){studyDate = "";}
		        }else{
		        	studyDate = "";
		        }
				String studyID = studyGrid.getStudyId();if(studyID == null){studyID = "";}	
				String studyDesc = studyGrid.getStudyDescription();if(studyDesc == null){studyDesc = "";}
				String studyInstanceUID = studyGrid.getStudyInstanceUID();if(studyInstanceUID == null){studyInstanceUID = "";}				
				studyFromGrid = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				if(patientFromGrid.contains(studyInstanceUID) == false){
					studyFromGrid = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
					patientFromGrid.addStudy(studyFromGrid);
				}
			} else if(selectedObject instanceof Study){
				studyFromGrid = Study.class.cast(selectedObject);
				gov.nih.nci.ncia.domain.Series seriesGrid = gov.nih.nci.ncia.domain.Series.class.cast(obj);
				String seriesNumber = seriesGrid.getSeriesNumber().toString();if(seriesNumber == null){seriesNumber = "";}
				String modality = seriesGrid.getModality();if(modality == null){modality = "";}
				String seriesDesc = seriesGrid.getSeriesDescription();if(seriesDesc == null){seriesDesc = "";}						
				String seriesInstanceUID = seriesGrid.getSeriesInstanceUID();if(seriesInstanceUID == null){seriesInstanceUID = "";}				
				if(studyFromGrid.contains(seriesInstanceUID) == false){
					seriesFromGrid = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);	
					studyFromGrid.addSeries(seriesFromGrid);
				}
			} else if(selectedObject instanceof Series){
				seriesFromGrid = Series.class.cast(selectedObject);
				
			}
		}
		return resultGrid;
	}	
}
