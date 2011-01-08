package edu.wustl.xipHost.avt2ext;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.dcm4che2.data.Tag;
import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class SearchResultSetup {
	SearchResult result = new SearchResult("Test");
	Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
	Map<String, Object> aimCriteria = new HashMap<String, Object>();
	
	public SearchResultSetup(){
			dicomCriteria.put(Tag.PatientName, "*");
			//System.out.println("Patient id int: " + Tag.PatientID);
			//System.out.println("Modality int: " + Tag.Modality);
			//dicomCriteria.put(Tag.Modality, "MR");
			Criteria criteria = new Criteria(dicomCriteria, aimCriteria);
			result.setOriginalCriteria(criteria);
			
			Patient patient1 = new Patient("Jarek1", "111", "07/18/1973");
			Timestamp patient1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			patient1.setLastUpdated(patient1LastUpdated);
			Patient patient2 = new Patient("Jarek2", "222", "07/18/1973");
			Timestamp patient2LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			patient2.setLastUpdated(patient2LastUpdated);
			Patient patient3 = new Patient("Jarek3", "333", "07/18/1973");
			Timestamp patient3LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			patient3.setLastUpdated(patient3LastUpdated);
			
			Study study1 = new Study("06/12/2010", "101010", "Test Study", "101.101");
			Timestamp study1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study1.setLastUpdated(study1LastUpdated);
			Study study2 = new Study("06/12/2010", "202020", "Test Study", "202.202");
			Timestamp study2LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study2.setLastUpdated(study2LastUpdated);
			Study study3 = new Study("06/12/2010", "303030", "Test Study", "303.303");
			Timestamp study3LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study3.setLastUpdated(study3LastUpdated);
			Study study4 = new Study("06/12/2010", "404040", "Test Study", "404.404");
			Timestamp study4LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study4.setLastUpdated(study4LastUpdated);
			Study study5 = new Study("06/12/2010", "505050", "Test Study", "505.505");
			Timestamp study5LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study5.setLastUpdated(study5LastUpdated);
			Study study6 = new Study("06/12/2010", "606060", "Test Study", "606.606");
			Timestamp study6LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study6.setLastUpdated(study6LastUpdated);
			
			Series series1 = new Series("1", "CT", "Series Test", "101.101.1");
			Timestamp series1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series1.setLastUpdated(series1LastUpdated);
			Series series2 = new Series("2", "MIR", "Series Test", "202.202.1");
			Timestamp series2LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series2.setLastUpdated(series2LastUpdated);
			Series series3 = new Series("3", "MR", "Series Test", "303.303.1");
			Timestamp series3LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series3.setLastUpdated(series3LastUpdated);
			Series series4 = new Series("4", "CT", "Series Test", "404.404.1");
			Timestamp series4LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series4.setLastUpdated(series4LastUpdated);
			Series series5 = new Series("5", "CT", "Series Test", "505.505.1");
			Timestamp series5LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series5.setLastUpdated(series5LastUpdated);
			Series series6 = new Series("6", "CT", "Series Test", "606.606.1");
			Timestamp series6LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series6.setLastUpdated(series6LastUpdated);
			Series series7 = new Series("7", "CT", "Series Test", "707.707.1");
			Timestamp series7LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series7.setLastUpdated(series7LastUpdated);
			Series series8 = new Series("8", "CT", "Series Test", "808.808.1");
			Timestamp series8LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series8.setLastUpdated(series8LastUpdated);
			Series series9 = new Series("9", "CT", "Series Test", "909.909.1");
			Timestamp series9LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series9.setLastUpdated(series9LastUpdated);
			Series series10 = new Series("10", "CT", "Series Test", "10.10.1");
			Timestamp series10LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series10.setLastUpdated(series10LastUpdated);
			Series series11 = new Series("11", "CT", "Series Test", "11.11.1");
			Timestamp series11LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series11.setLastUpdated(series11LastUpdated);
			Series series12 = new Series("12", "CT", "Series Test", "12.12.1");
			Timestamp series12LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series12.setLastUpdated(series12LastUpdated);
			Series series13 = new Series("13", "CT", "Series Test", "13.13.1");
			Timestamp series13LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series13.setLastUpdated(series13LastUpdated);
			Series series14 = new Series("14", "CT", "Series Test", "14.14.1");
			Timestamp series14LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series14.setLastUpdated(series14LastUpdated);
			
			study1.addSeries(series1);
			study2.addSeries(series2);
			study3.addSeries(series3);
			study3.addSeries(series4);
			study3.addSeries(series5);
			study4.addSeries(series6);
			study4.addSeries(series7);
			study4.addSeries(series8);
			study5.addSeries(series9);
			study5.addSeries(series10);
			study5.addSeries(series11);
			study6.addSeries(series12);
			study6.addSeries(series13);
			study6.addSeries(series14);
			
			patient1.addStudy(study1);
			patient1.addStudy(study2);
			patient2.addStudy(study3);
			patient3.addStudy(study4);
			patient3.addStudy(study5);
			patient3.addStudy(study6);
			result.addPatient(patient1);
			result.addPatient(patient2);
			result.addPatient(patient3);
	}
	
	public SearchResult getSearchResult(){
		return result;
	}
}
