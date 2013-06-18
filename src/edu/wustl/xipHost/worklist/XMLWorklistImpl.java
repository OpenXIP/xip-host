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

package edu.wustl.xipHost.worklist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * <font  face="Tahoma" size="2">
 * Parses worklist XML document.<br></br>
 * @version	August 2007
 * @author Jaroslaw Krych
 * </font>
 */
public class XMLWorklistImpl implements Worklist {	
	SAXBuilder builder = new SAXBuilder();
	Document document;
	Element root;	
	List<WorklistEntry> entries = new ArrayList<WorklistEntry>();
	
	/**
	 * @param args
	 */	
	public XMLWorklistImpl(){
			
	}

	public boolean addWorklistEntry(WorklistEntry entry) {
		return false;
	}

	public boolean deleteWorkListEntry(WorklistEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}	

	public List getWorklistEntries() {		
		return entries;
	}

	public boolean loadWorklist(File xmlWorklistFile) {
		try {
			document = builder.build(xmlWorklistFile);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		root = document.getRootElement();
		int numItems = root.getChildren().size();		
		
		for(int i = 0; i < numItems; i++){
			Element item = ((Element)root.getChildren().get(i));			
			WorklistEntry entry = new WorklistEntry();
			String subjectName = item.getChild("Subject").getChild("Subject_Name").getValue();
			entry.setSubjectName(subjectName);
			String subjectID = item.getChild("Subject").getChild("Subject_ID").getValue();
			entry.setSubjectID(subjectID);
			String studyDate = item.getChild("Study_Date").getValue(); 
			entry.setStudyDate(studyDate);
			String taskDescription = item.getChild("Task").getChild("Description").getValue();
			entry.setTaskDescription(taskDescription);
			String appName = item.getChild("Application").getValue();
			entry.setApplicationName(appName);			
			//Previous data set
			String sopClassUIDPrev = item.getChild("Data").getChild("Previous").getChild("SOPClassUID").getValue(); 
			entry.setSOPClassUIDPrev(sopClassUIDPrev);
			String sopInstanceUIDPrev = item.getChild("Data").getChild("Previous").getChild("SOPInstanceUID").getValue();
			entry.setSOPInstanceUIDPrev(sopInstanceUIDPrev);
			String studyInstanceUIDPrev = item.getChild("Data").getChild("Previous").getChild("StudyInstanceUID").getValue();
			entry.setStudyInstanceUIDPrev(studyInstanceUIDPrev);
			String seriesInstanceUIDPrev = item.getChild("Data").getChild("Previous").getChild("SeriesInstanceUID").getValue();
			entry.setSeriesInstanceUIDPrev(seriesInstanceUIDPrev);
			//Current data set
			String sopClassUIDCurr = item.getChild("Data").getChild("Current").getChild("SOPClassUID").getValue();
			entry.setSOPClassUIDCurr(sopClassUIDCurr);
			String sopInstanceUIDCurr = item.getChild("Data").getChild("Current").getChild("SOPInstanceUID").getValue();
			entry.setSOPInstanceUIDCurr(sopInstanceUIDCurr);
			String studyInstanceUIDCurr = item.getChild("Data").getChild("Current").getChild("StudyInstanceUID").getValue();
			entry.setStudyInstanceUIDCurr(studyInstanceUIDCurr);
			String seriesInstanceUIDCurr = item.getChild("Data").getChild("Current").getChild("SeriesInstanceUID").getValue();
			entry.setSeriesInstanceUIDCurr(seriesInstanceUIDCurr);
			String dicomServiceURI = item.getChild("RetrieveAETitle").getChild("RetrieveDicomURI").getValue();
			entry.setDicomServiceURI(dicomServiceURI);
			String aimServiceURI = item.getChild("RetrieveAETitle").getChild("RetrieveAimURI").getValue();
			entry.setAimServiceURI(aimServiceURI);
			entries.add(entry);
		}	
		return true;		
	}

	public boolean modifyWorklistEntry(WorklistEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	public WorklistEntry getWorklistEntry(int i) {		
		return entries.get(i);
	}

	public int getNumberOfWorklistEntries() {		
		return entries.size();
	}	
}
