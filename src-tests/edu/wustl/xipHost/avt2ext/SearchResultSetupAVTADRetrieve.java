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
package edu.wustl.xipHost.avt2ext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dcm4che2.data.Tag;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;

import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class SearchResultSetupAVTADRetrieve {
	SearchResult result = new SearchResult("Test");
	Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
	Map<String, Object> aimCriteria = new HashMap<String, Object>();
	
	public SearchResultSetupAVTADRetrieve(){
			dicomCriteria.put(Tag.PatientName, "YAMAMOTO-00046");
			Criteria criteria = new Criteria(dicomCriteria, aimCriteria);
			result.setOriginalCriteria(criteria);
			
			Patient patient1 = new Patient("100mAs 16x1.5 120KVP 1.2P^.", "YAMAMOTO-00046", "");
			Timestamp patient1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			patient1.setLastUpdated(patient1LastUpdated);
			
			Study study1 = new Study("14/19/2008", "4617", "", "1.2.840.113704.1.111.2272.1226683138.3");
			Timestamp study1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			study1.setLastUpdated(study1LastUpdated);
			
			Series series1 = new Series("", "SEG", "", "1.2.276.0.7230010.3.1.3.2554264370.29928.1264492790.4");
			Timestamp series1LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series1.setLastUpdated(series1LastUpdated);
			Series series2 = new Series("", "SEG", "", "1.2.276.0.7230010.3.1.3.2554264370.29928.1264492801.6");
			Timestamp series2LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series2.setLastUpdated(series2LastUpdated);
			Series series3 = new Series("", "CT", "", "1.2.840.113704.1.111.4280.1226686843.113");
			Timestamp series3LastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			series3.setLastUpdated(series3LastUpdated);
			
			Item item1 = new ImageItem("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492790.3");
			//Start: ObjectDescriptor Item1
			ObjectDescriptor objDescItem1 = new ObjectDescriptor();
			Uuid objDescUUID = new Uuid();
			objDescUUID.setUuid(UUID.randomUUID().toString());
			objDescItem1.setUuid(objDescUUID);
			String mimeType = "application/dicom";
			objDescItem1.setMimeType(mimeType);			
			Uid uid = new Uid();
			String sopClassUID = "";
			uid.setUid(sopClassUID);
			objDescItem1.setClassUID(uid);				
			Modality mod = new Modality();
			mod.setModality("");
			objDescItem1.setModality(mod);
			//End: ObjectDescriptor Item1
			item1.setObjectDescriptor(objDescItem1);
			listUUIDsDicom.add(objDescUUID);
			listUUIDsAIMSEG.add(objDescUUID);
			listUUIDsDICOMAIMSEG.add(objDescUUID);
			series1.addItem(item1);
			series1.setContainsSubsetOfItems(false);
			Item item2 = new ImageItem("1.2.276.0.7230010.3.1.4.2554264370.29928.1264492801.5");
			//Start: ObjectDescriptor Item2
			ObjectDescriptor objDescItem2 = new ObjectDescriptor();
			Uuid objDescUUID2 = new Uuid();
			objDescUUID2.setUuid(UUID.randomUUID().toString());
			objDescItem2.setUuid(objDescUUID2);
			objDescItem2.setMimeType(mimeType);			
			objDescItem2.setClassUID(uid);				
			objDescItem2.setModality(mod);
			//End: ObjectDescriptor Item2
			item2.setObjectDescriptor(objDescItem2);
			listUUIDsDicom.add(objDescUUID2);
			listUUIDsAIMSEG.add(objDescUUID2);
			listUUIDsDICOMAIMSEG.add(objDescUUID2);
			series2.addItem(item2);
			series2.setContainsSubsetOfItems(false);
			Item item3 = new ImageItem("1.2.840.113704.1.111.4044.1226687286.215775");
			//Start: ObjectDescriptor Item3
			ObjectDescriptor objDescItem3 = new ObjectDescriptor();
			Uuid objDescUUID3 = new Uuid();
			objDescUUID3.setUuid(UUID.randomUUID().toString());
			objDescItem3.setUuid(objDescUUID3);
			objDescItem3.setMimeType(mimeType);			
			objDescItem3.setClassUID(uid);				
			objDescItem3.setModality(mod);
			//End: ObjectDescriptor Item3
			item3.setObjectDescriptor(objDescItem3);
			listUUIDsDicom.add(objDescUUID3);
			listUUIDsDICOMAIMSEG.add(objDescUUID3);
			series3.addItem(item3);
			Item item4 = new AIMItem("", "", "", "1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.3.0");
			//Start: ObjectDescriptor Item4
			ObjectDescriptor objDescItem4 = new ObjectDescriptor();
			Uuid objDescUUID4 = new Uuid();
			objDescUUID4.setUuid(UUID.randomUUID().toString());
			objDescItem4.setUuid(objDescUUID4);
			String mimeType4 = "text/xml";
			objDescItem4.setMimeType(mimeType4);	
			objDescItem4.setClassUID(uid);				
			objDescItem4.setModality(mod);
			//End: ObjectDescriptor Item4
			item4.setObjectDescriptor(objDescItem4);
			listUUIDsAIMSEG.add(objDescUUID4);
			listUUIDsDICOMAIMSEG.add(objDescUUID4);
			series3.addItem(item4);
			series3.setContainsSubsetOfItems(false);
			
			study1.addSeries(series1);
			study1.addSeries(series2);
			study1.addSeries(series3);
			patient1.addStudy(study1);
			result.addPatient(patient1);	
	}
	
	public SearchResult getSearchResult(){
		return result;
	}
	
	List<Uuid> listUUIDsDicom = new ArrayList<Uuid>();
	public List<Uuid> getDICOMObjectsUUIDs(){
		return listUUIDsDicom;
	}
	
	List<Uuid> listUUIDsAIMSEG = new ArrayList<Uuid>();
	public List<Uuid> getAIMandSEGObjectsUUIDs(){
		return listUUIDsAIMSEG;
	}
	
	List<Uuid> listUUIDsDICOMAIMSEG = new ArrayList<Uuid>();
	public List<Uuid> getDICOMandAIMandSEGObjectsUUIDs(){
		return listUUIDsDICOMAIMSEG;
	}
}
