/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.nema.dicom.wg23.ArrayOfObjectDescriptor;
import org.nema.dicom.wg23.ArrayOfPatient;
import org.nema.dicom.wg23.ArrayOfSeries;
import org.nema.dicom.wg23.ArrayOfStudy;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Patient;
import org.nema.dicom.wg23.Series;
import org.nema.dicom.wg23.Study;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.InputDialog;
import edu.wustl.xipHost.wg23.WG23DataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class FileManagerImpl implements FileManager, DicomParseListener {
	
	InputDialog inputDialog = new InputDialog();
	int numThreads = 3;
	ExecutorService exeService = Executors.newFixedThreadPool(numThreads);			
	
	File[] items;	
	File[] getSelectedItems(){
		return items;
	}		
	
	List<String[][]> parsedResults;
	public void parse(File[] items){									 
		parsedResults = new ArrayList<String[][]>();
		this.items = items;
		for(int i = 0; i < items.length; i++){						
			FileRunner runner = new FileRunner(items[i]);
			runner.addDicomParseListener(this);						
			//new Thread(runner).start();
			exeService.execute(runner);			
		}		
	}	
	
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	int j = 0;		
	boolean isParsingCompleted = false;
	public void dicomAvailable(DicomParseEvent e) {					
		inputDialog.setCursor(hourglassCursor);
		FileRunner source = (FileRunner)e.getSource();
		File file = source.getItem();
		String[][] result = source.getParsingResult();
		parsedResults.add(result);
		inputDialog.setParsingResult(file.toURI(), result);
		inputDialog.updateUI();
		j++;
		if(j == items.length){
			j = 0;
			isParsingCompleted = true;
			inputDialog.setCursor(normalCursor);			
		}
	}		
		
	public void nondicomAvailable(DicomParseEvent e) {
		FileRunner source = (FileRunner)e.getSource();
		File file = source.getItem();
		inputDialog.setParsingResult(file.toURI(), null);		
		j++;
		if(j == items.length){
			j = 0;
			isParsingCompleted = true;
			inputDialog.setCursor(normalCursor);			
		}
		inputDialog.updateUI();
	}
	
	public List<String[][]> getParsedResults(){
		if(parsedResults == null){
			parse(items);
			while(!isParsingCompleted){
				//TODO change cursor icon for the duration of parsing
			}
		}
		return parsedResults;			
	}
	
	public void clearParsedData(){
		parsedResults = null;
		isParsingCompleted = false;
	}
	
	public boolean isParsingCompleted(){
		return isParsingCompleted;
	}
	
	public void run(File[] items){				
		List<Application> allApps = ApplicationManagerFactory.getInstance().getApplications();
		Map<String, Application> appMap = new LinkedHashMap<String, Application>();
		for(int i = 0; i < allApps.size(); i++){
			Application app = allApps.get(i);
			String name = app.getName();
			appMap.put(name, app);
		}
		Collection<Application> values = appMap.values();			
		inputDialog.setApplications(new ArrayList<Application>(values));
		//inputDialog.display();				
		//parse(items);
		displayItems(items);
		inputDialog.display();
	}	
		
	//TODO ID#919 and ID#887
	void displayItems(File[] items){
		this.items = items;		
		inputDialog.setItems(items);
		inputDialog.updateUI();
	}
	
	public WG23DataModel makeWG23DataModel(List<File> files){		
		if(files == null){return null;}										
		if(getParsedResults() == null){
			AvailableData availableData = new AvailableData();
			WG23DataModelFileSystemImpl dataModel = new WG23DataModelFileSystemImpl();
			dataModel.setAvailableData(availableData);			
			WG23DataModel wg23DataModel = dataModel;		
			return wg23DataModel;
		}
		AvailableData availableData = groupDicomItems(getParsedResults());
		List<ObjectDescriptor> listDescs;
		if(availableData.getObjectDescriptors() != null){
			listDescs = availableData.getObjectDescriptors().getObjectDescriptor();
		}else{
			ArrayOfObjectDescriptor arrayOfObjectDesc = new ArrayOfObjectDescriptor();
			listDescs = arrayOfObjectDesc.getObjectDescriptor(); 
			//listDescs = new ArrayList<ObjectDescriptor>();
			availableData.setObjectDescriptors(arrayOfObjectDesc);
		}		
		//check and add any non-dicom files to the AvailableData and list of ObjectLocators
		for(int i = 0; i < files.size(); i++){
			String mimeType;
			try {
				mimeType = DicomUtil.mimeType(files.get(i));
				if(!mimeType.equalsIgnoreCase("application/dicom")){
					ObjectDescriptor objDesc = new ObjectDescriptor();
					Uuid objDescUUID = new Uuid();
					objDescUUID.setUuid(UUID.randomUUID().toString());
					objDesc.setUuid(objDescUUID);					
					objDesc.setMimeType(mimeType);			
					listDescs.add(objDesc);															
					ObjectLocator objLoc = new ObjectLocator();				
					objLoc.setUuid(objDescUUID);				
					objLoc.setUri(files.get(i).toURI().toURL().toExternalForm());					
					objLocators.add(objLoc);
				}
			} catch (IOException e) {
				return null;
			}									
		}		
		WG23DataModelFileSystemImpl dataModel = new WG23DataModelFileSystemImpl();
		dataModel.setAvailableData(availableData);
		ObjectLocator[] objLocs = new ObjectLocator[objLocators.size()];
		objLocators.toArray(objLocs);
		dataModel.setObjectLocators(objLocs);
		WG23DataModel wg23DataModel = dataModel;		
		return wg23DataModel;
	}
	
	public WG23DataModel getWG23DataModel(){
		List<File> listFiles = new ArrayList<File>();
		for(int i = 0; i < items.length; i++){
			listFiles.add(items[i]);
		}
		WG23DataModel data = makeWG23DataModel(listFiles);
		return data;
	}	
	
	public static void main(String args[]){
		HostFileChooser fileChooser = new HostFileChooser(true, new File("C:/WUSTL/Tmp/RECIST"));
		fileChooser.setVisible(true);
		File[] files = fileChooser.getSelectedItems();
		if(files == null){
			return;
		}
		FileManager fileMgr = FileManagerFactory.getInstance();
		fileMgr.run(files);		
	}
	
	List<ObjectLocator> objLocators;
	AvailableData groupDicomItems(List<String[][]> parsedResults){		
		//TODO check if item is dicom outside this methods		
		Map<String, String> mapPatients = new Hashtable<String, String>();			//PatientID to patient name
		Map<String, String> mapStudiesToPatients = new Hashtable<String, String>();	//StudyInstanceUID to Patient ID
		Set<String> setStudies = new HashSet<String>();
		Set<String> setSeries = new HashSet<String>();
		Map<String, String> mapSeriesToStudies = new Hashtable<String, String>();
		//mapItemToSeries key=SOPInstanceUID, value=seriesInstanceUID
		Map<String, String> mapItemToSeries = new Hashtable<String, String>();
		//mapItemToSeries key=SOPInstanceUID, value=attributes(SOPClassUID, TransferSyntaxUID, Modality)
		Map<String, String[][]> mapItemToAtts = new Hashtable<String, String[][]>();
		for(int i = 0; i < parsedResults.size(); i++){
			String[][] result = parsedResults.get(i);
			mapPatients.put(result[1][1], result[0][1]);
			mapStudiesToPatients.put(result[10][1], result[1][1]);
			setStudies.add(result[10][1]);
			setSeries.add(result[11][1]);
			mapSeriesToStudies.put(result[11][1], result[10][1]);					//SeriesInstanceUID to StudyInstanceUID
			mapItemToSeries.put(result[13][1], result[11][1]);						//SOPInstanceUID to SeriesInstanceUID
			mapItemToAtts.put(result[13][1], result);
		}
		AvailableData availableData = new AvailableData();				
		ArrayOfPatient arrayOfPatient = new ArrayOfPatient();
		List<Patient> listPatients = arrayOfPatient.getPatient();
		Iterator<String> iterPatients = mapPatients.keySet().iterator();
		objLocators = new ArrayList<ObjectLocator>();
		while(iterPatients.hasNext()){
			ArrayOfStudy arrayOfStudy = new ArrayOfStudy();
			List<Study> listOfStudies = arrayOfStudy.getStudy();
			String patientID = iterPatients.next();
			Patient patient = new Patient();			
			patient.setName(mapPatients.get(patientID));
			ArrayOfObjectDescriptor arrayOfObjectDescPatient = new ArrayOfObjectDescriptor();
			patient.setObjectDescriptors(arrayOfObjectDescPatient);
			//add studies to patinet			
			Iterator<String> iterStudies = setStudies.iterator();
			while(iterStudies.hasNext()){
				ArrayOfSeries arrayOfSeries = new ArrayOfSeries();
				List<Series> listOfSeries = arrayOfSeries.getSeries();
				String studyInstanceUID = iterStudies.next();
				if(mapStudiesToPatients.get(studyInstanceUID) != null && mapStudiesToPatients.get(studyInstanceUID).equalsIgnoreCase(patientID)){
					Study study = new Study();
					study.setStudyUID(studyInstanceUID);
					ArrayOfObjectDescriptor arrayOfObjectDescStudy = new ArrayOfObjectDescriptor();					 					
					study.setObjectDescriptors(arrayOfObjectDescStudy);
					//add series to study				
					Iterator<String> iterSeries = setSeries.iterator();
					while(iterSeries.hasNext()){
						ArrayOfObjectDescriptor arrayOfObjectDesc = new ArrayOfObjectDescriptor();
						List<ObjectDescriptor> listObjectDescs = arrayOfObjectDesc.getObjectDescriptor();						
						String seriesInstanceUID = iterSeries.next(); 
						if(mapSeriesToStudies.get(seriesInstanceUID) != null && mapSeriesToStudies.get(seriesInstanceUID).equalsIgnoreCase(studyInstanceUID)){
							Series series = new Series();
							series.setSeriesUID(seriesInstanceUID);
							//add ObjectDescriptor
							Iterator<String> iterItems = mapItemToSeries.keySet().iterator();
							while(iterItems.hasNext()){
								String itemSOPInstanceUID = iterItems.next();
								if(mapItemToSeries.get(itemSOPInstanceUID) != null && mapItemToSeries.get(itemSOPInstanceUID).equalsIgnoreCase(seriesInstanceUID)){
									ObjectDescriptor objDesc = new ObjectDescriptor();					
									Uuid objDescUUID = new Uuid();
									objDescUUID.setUuid(UUID.randomUUID().toString());
									objDesc.setUuid(objDescUUID);													
									objDesc.setMimeType("application/dicom");																						
									String[][] atts = mapItemToAtts.get(itemSOPInstanceUID);
									String classUID = atts[14][1];
									Uid uid = new Uid();
									uid.setUid(classUID);
									objDesc.setClassUID(uid);
									String modCode = atts[7][1];						
									Modality modality = new Modality();
									modality.setModality(modCode);
									objDesc.setModality(modality);	
									listObjectDescs.add(objDesc);
															 
									ObjectLocator objLoc = new ObjectLocator();				
									objLoc.setUuid(objDescUUID);				
									objLoc.setUri(atts[9][1]);
									objLocators.add(objLoc);
								}								
							}					
							series.setObjectDescriptors(arrayOfObjectDesc);
							listOfSeries.add(series);
						}						
					}
					study.setSeries(arrayOfSeries);
					listOfStudies.add(study);
				}				
			}
			patient.setStudies(arrayOfStudy);
			listPatients.add(patient);
		}
		availableData.setPatients(arrayOfPatient);
		return availableData;
	}
	
}
