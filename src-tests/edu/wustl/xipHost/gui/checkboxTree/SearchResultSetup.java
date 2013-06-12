/*
Copyright (c) 2013, Washington University in St.Louis
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
package edu.wustl.xipHost.gui.checkboxTree;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.dcm4che2.data.Tag;
import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class SearchResultSetup {
	SearchResult result = new SearchResult("Test");
	Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
	Map<String, Object> aimCriteria = new HashMap<String, Object>();
	
	/* Full SearchResult: Patient x 3, Study x 6, Series x 14 
	 * SearchResultSetup is used to assert values */
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
			
			Study study1 = new Study("06/12/2010", "101010", "Test Study", "101.101");
			Timestamp study1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study1.setLastUpdated(study1LastUpdated);
			Study study2 = new Study("06/12/2010", "202020", "Test Study", "202.202");
			Timestamp study2LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study2.setLastUpdated(study2LastUpdated);
			Study study3 = new Study("06/12/2010", "303030", "Test Study", "303.303");
			Timestamp study3LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study3.setLastUpdated(study3LastUpdated);
			
			
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
			
			
			Item item1 = new ImageItem("101.101.1.000.000.1");
			Item item2 = new ImageItem("202.202.1.000.000.1");
			Item item3 = new ImageItem("202.202.1.000.000.2");
			Item item4 = new ImageItem("303.303.1.000.000.1");
			Item item5 = new ImageItem("303.303.1.000.000.2");
			Item item6 = new ImageItem("404.404.1.000.000.1");
			
			series1.addItem(item1);
			series2.addItem(item2);
			series2.addItem(item3);
			series3.addItem(item4);
			series3.addItem(item5);
			series4.addItem(item6);
			
			study1.addSeries(series1);
			study2.addSeries(series2);
			study2.addSeries(series3);
			study3.addSeries(series4);

			patient1.addStudy(study1);
			patient1.addStudy(study2);
			patient2.addStudy(study3);
			
			result.addPatient(patient1);
			result.addPatient(patient2);
	}
	
	public SearchResult getSearchResult(){
		return result;
	}
}
