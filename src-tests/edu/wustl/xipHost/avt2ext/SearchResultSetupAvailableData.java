package edu.wustl.xipHost.avt2ext;

import java.awt.BorderLayout;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.dcm4che2.data.Tag;
import edu.wustl.xipHost.avt2ext.iterator.Criteria;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTreeProgressive;

public class SearchResultSetupAvailableData {
	SearchResult result = new SearchResult("Test");
	Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
	Map<String, Object> aimCriteria = new HashMap<String, Object>();
	
	public SearchResultSetupAvailableData(){
			dicomCriteria.put(Tag.PatientName, "*");
			//System.out.println("Patient id int: " + Tag.PatientID);
			//System.out.println("Modality int: " + Tag.Modality);
			//dicomCriteria.put(Tag.Modality, "MR");
			Criteria criteria = new Criteria(dicomCriteria, aimCriteria);
			result.setOriginalCriteria(criteria);
			
			Patient patient3 = new Patient("Jarek3", "333", "07/18/1973");
			Timestamp patient3LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			patient3.setLastUpdated(patient3LastUpdated);
			
			Study study4 = new Study("06/12/2010", "404040", "Test Study", "404.404");
			Timestamp study4LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study4.setLastUpdated(study4LastUpdated);
			Study study5 = new Study("06/12/2010", "505050", "Test Study", "505.505");
			Timestamp study5LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study5.setLastUpdated(study5LastUpdated);
			Study study6 = new Study("06/12/2010", "606060", "Test Study", "606.606");
			Timestamp study6LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study6.setLastUpdated(study6LastUpdated);
			
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
			
			Item item1 = new ImageItem("606.606.1.0.1");
			series6.addItem(item1);
			Item item2 = new AIMItem("", "", "", "606.606.1.0.2");
			series6.addItem(item2);
			
			study4.addSeries(series6);
			study4.addSeries(series7);
			study4.addSeries(series8);
			study5.addSeries(series9);
			study5.addSeries(series10);
			study5.addSeries(series11);
			study6.addSeries(series12);
			study6.addSeries(series13);
			study6.addSeries(series14);
			patient3.addStudy(study4);
			patient3.addStudy(study5);
			patient3.addStudy(study6);
			result.addPatient(patient3);
	}
	
	public SearchResult getSearchResult(){
		return result;
	}
	
	public static void main(String args[] ){
		SearchResultTreeProgressive searchTree = new SearchResultTreeProgressive();
		JFrame frame = new JFrame();
		frame.getContentPane().add(searchTree, BorderLayout.CENTER);		
		frame.setSize(650, 300);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    SearchResultSetupAvailableData searchResult = new SearchResultSetupAvailableData();
	    searchTree.updateNodes(searchResult.getSearchResult());
	}
	
}
