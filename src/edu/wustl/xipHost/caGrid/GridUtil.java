package edu.wustl.xipHost.caGrid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;

import edu.wustl.xipHost.dataModel.ImageItem;
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
		
	public static CQLQuery convertToCQLStatement(AttributeList criteriaList){
		if(criteriaList == null){
			return null;
		}
		HashMap<String, String> query = new HashMap<String, String>();
		query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
		CQLQuery cqlq = null;
		
		DicomDictionary dictionary = AttributeList.getDictionary();
		Iterator iter = dictionary.getTagIterator();		
		while(iter.hasNext()){
			AttributeTag attTag  = (AttributeTag)iter.next();
			String attValue = Attribute.getSingleStringValueOrEmptyString(criteriaList, attTag);
			String nciaAttName = mapDicomTagToNCIATagName(attTag.toString());
			if(nciaAttName != null && !attValue.isEmpty()){
				//System.out.println(nciaAttName + " " + attValue);
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
			cqlq = h2cql.makeCQLQuery(query);
			/*System.err.println(ObjectSerializer.toString(cqlq, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));*/
			return cqlq;
		} catch (FileNotFoundException e) {
			return null;
		} catch (ModelMapException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (MalformedQueryException e) {
			return null;
		}		
	}
	
	
	/**
	 * 
	 * @param iter
	 * @return
	 */
	public static SearchResult convertCQLQueryResultsIteratorToSearchResult(CQLQueryResultsIterator iter, GridLocation location){				
		SearchResult resultGrid = new SearchResult(location.getShortName());
		Patient patientFromGrid = null;
		Study studyFromGrid = null;
		Series seriesFromGrid = null;
		while (iter.hasNext()) {			
			java.lang.Object obj = iter.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}
			gov.nih.nci.ncia.domain.Series seriesGrid = gov.nih.nci.ncia.domain.Series.class.cast(obj);
			gov.nih.nci.ncia.domain.Study studyGrid = seriesGrid.getStudy();
			//gov.nih.nci.ncia.domain.Patient patientGrid = gov.nih.nci.ncia.domain.Patient.class.cast(obj);
			gov.nih.nci.ncia.domain.Patient patientGrid = studyGrid.getPatient();
			gov.nih.nci.ncia.domain.Image[] images = seriesGrid.getImageCollection();			
			
			String patientName = patientGrid.getPatientName(); if(patientName == null){patientName = "";}
			String patientID = patientGrid.getPatientId(); if(patientID == null){patientID = "";}
			Calendar patientBirthDate = patientGrid.getPatientBirthDate();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
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
			//Create and add new study to resultGrid only when it is not included in resultGrid yet
			if(patientFromGrid.contains(studyInstanceUID) == false){
				studyFromGrid = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				patientFromGrid.addStudy(studyFromGrid);
			}
			//if it is included in resultGrid find study based on studyInstanceUID and add to it new series if new series is not included there yet			
			String seriesNumber = seriesGrid.getSeriesNumber().toString();if(seriesNumber == null){seriesNumber = "";}
			String modality = seriesGrid.getModality();if(modality == null){modality = "";}
			String seriesDesc = seriesGrid.getSeriesDescription();if(seriesDesc == null){seriesDesc = "";}						
			String seriesInstanceUID = seriesGrid.getSeriesInstanceUID();if(seriesInstanceUID == null){seriesInstanceUID = "";}
			
			if(studyFromGrid.contains(seriesInstanceUID) == false){
				seriesFromGrid = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);	
				studyFromGrid.addSeries(seriesFromGrid);
				if(images != null){
					for(int i = 0; i < images.length; i++){
						String imageNumber = images[i].getId().toString();if(imageNumber == null){imageNumber = "";}					
						ImageItem image = new ImageItem(imageNumber);				
						seriesFromGrid.addItem(image);
					}
				}else{
					
				};
			}
						
			/*int size = resultGrid.getStudies().size();
	        for(int i = 0; i < size; i++){
	        	Study study = resultGrid.getStudies().get(i);
	        	System.out.println(study.toString());	        	
	        	int size2 = study.getSeries().size();
	        	for (int j = 0; j < size2; j++){
	        		Series series = study.getSeries().get(j);
	        		System.out.println(series.toString());
	        		int size3 = series.getImages().size();
	        		for (int jj = 0; jj < size3; jj++){
		        		Image image = series.getImages().get(jj);
		        		System.out.println(image.toString());
		        	}
	        	}
	        }*/
		}		
		return resultGrid;
	}		
	
	public static void main(String[] args) throws IOException, DicomException{
		GridUtil test = new GridUtil();
		test.loadNCIAModelMap(new FileInputStream("./resources/NCIAModelMap.properties"));		
		AttributeList attList = new AttributeList();
		Attribute a = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);        
		a.addValue("1.3.6.1.4.1.9328.50.1.9772");
        attList.put(TagFromName.SeriesInstanceUID, a);  
		GridUtil.convertToCQLStatement(attList);
	}		
}
