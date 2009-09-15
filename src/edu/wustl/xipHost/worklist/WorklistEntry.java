/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.nema.dicom.wg23.ArrayOfObjectDescriptor;
import org.nema.dicom.wg23.ArrayOfPatient;
import org.nema.dicom.wg23.ArrayOfSeries;
import org.nema.dicom.wg23.ArrayOfStudy;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Patient;
import org.nema.dicom.wg23.Study;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.caGrid.AimRetrieve;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.caGrid.GridRetrieve;
import edu.wustl.xipHost.caGrid.GridRetrieveEvent;
import edu.wustl.xipHost.caGrid.GridRetrieveListener;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.dicom.BasicDicomParser2;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.nci.NCIARetrieve;
import edu.wustl.xipHost.nci.NCIARetrieveEvent;
import edu.wustl.xipHost.nci.NCIARetrieveListener;
import edu.wustl.xipHost.wg23.WG23DataModel;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.helper.AIMDataServiceHelper;
import gov.nih.nci.ncia.domain.Series;

/**
 * @author Jaroslaw Krych
 *
 */
public class WorklistEntry implements Runnable, GridRetrieveListener, NCIARetrieveListener {
	String subjectName;
	String subjectID;
	String studyDate;
	String taskDescription;
	String appName;
	String sopClassUIDPrev;
	String sopInstanceUIDPrev;
	String studyInstanceUIDPrev;
	String seriesInstanceUIDPrev;
	String sopClassUIDCurr;
	String sopInstanceUIDCurr;
	String studyInstanceUIDCurr;
	String seriesInstanceUIDCurr;
	String dicomServiceURI;
	String aimServiceURI;
	/**
	 * 
	 */
	public WorklistEntry() {
		// TODO Auto-generated constructor stub
	}
	public String getSubjectName(){
		return subjectName;
	}
	public String getSubjectID(){
		return subjectID;
	}
	public String getStudyDate(){
		return studyDate;
	}
	public String getTaskDescription(){
		return taskDescription;
	}
	public String getApplicationName(){
		return appName;
	}
	public String getSOPClassUIDPrev(){
		return sopClassUIDPrev;
	}
	public String getSOPInstanceUIDPrev(){
		return sopInstanceUIDPrev;
	}
	public String getStudyInstanceUIDPrev(){
		return studyInstanceUIDPrev;
	}
	public String getSeriesInstanceUIDPrev(){
		return seriesInstanceUIDPrev;
	}
	public String getSOPClassUIDCurr(){
		return sopClassUIDCurr;
	}
	public String getSOPInstanceUIDCurr(){
		return sopInstanceUIDCurr;
	}
	public String getStudyInstanceUIDCurr(){
		return studyInstanceUIDCurr;
	}
	public String getSeriesInstanceUIDCurr(){
		return seriesInstanceUIDCurr;
	}
	public String getDicomServiceURI(){
		return dicomServiceURI;
	}
	public String getAimServiceURI(){
		return aimServiceURI;
	}
	
	public void setSubjectName(String subjectName){
		this.subjectName = subjectName;
	}
	public void setSubjectID(String subjectID){
		this.subjectID = subjectID;
	}
	public void setStudyDate(String studyDate){
		this.studyDate = studyDate;
	}
	public void setTaskDescription(String taskDescription){
		this.taskDescription = taskDescription;
	}
	public void setApplicationName(String name){
		this.appName = name;
	}
	public void setSOPClassUIDPrev(String sopClassUIDPrev){
		this.sopClassUIDPrev = sopClassUIDPrev;
	}
	public void setSOPInstanceUIDPrev(String sopInstanceUIDPrev){
		this.sopInstanceUIDPrev = sopInstanceUIDPrev;
	}
	public void setStudyInstanceUIDPrev(String studyInstanceUIDPrev){
		this.studyInstanceUIDPrev = studyInstanceUIDPrev;
	}
	public void setSeriesInstanceUIDPrev(String seriesInstanceUIDPrev){
		this.seriesInstanceUIDPrev = seriesInstanceUIDPrev;
	}
	public void setSOPClassUIDCurr(String sopClassUIDCurr){
		this.sopClassUIDCurr = sopClassUIDCurr;
	}
	public void setSOPInstanceUIDCurr(String sopInstanceUIDCurr){
		this.sopInstanceUIDCurr = sopInstanceUIDCurr;
	}
	public void setStudyInstanceUIDCurr(String studyInstanceUIDCurr){
		this.studyInstanceUIDCurr = studyInstanceUIDCurr;
	}
	public void setSeriesInstanceUIDCurr(String seriesInstanceUIDCurr){
		this.seriesInstanceUIDCurr = seriesInstanceUIDCurr;
	}
	public void setDicomServiceURI(String dicomServiceURI){
		this.dicomServiceURI = dicomServiceURI;
	}
	public void setAimServiceURI(String aimServiceURI){
		this.aimServiceURI = aimServiceURI;
	}
	
	/**
	 * i = 1 would be for previous data set
	 * i = 2 would be for current data set
	 */
	public CQLQuery makeCQLforDICOM(int i){
		CQLQuery cqlq = null;
		try {
			HashMap<String, String> query = null;
			query = new HashMap<String, String>();
			HashmapToCQLQuery h2cql = new HashmapToCQLQuery(new ModelMap());;			
			if(i == 1){
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());																								
				query.put("gov.nih.nci.ncia.domain.Image.sopClassUID", this.getSOPClassUIDPrev());
				query.put("gov.nih.nci.ncia.domain.Image.sopInstanceUID", this.getSOPInstanceUIDPrev());
				query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", this.getStudyInstanceUIDPrev());
				query.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", this.getSeriesInstanceUIDPrev());				
			} else if(i == 2){
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());																								
				query.put("gov.nih.nci.ncia.domain.Image.sopClassUID", this.getSOPClassUIDCurr());
				query.put("gov.nih.nci.ncia.domain.Image.sopInstanceUID", this.getSOPInstanceUIDCurr());
				query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", this.getStudyInstanceUIDCurr());
				query.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", this.getSeriesInstanceUIDCurr());
			}
			if (query.isEmpty()) {					
				System.out.println("Query was empty");
				query = new HashMap<String, String>();
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class.getCanonicalName());
			}
			cqlq = h2cql.makeCQLQuery(query);			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return cqlq;
	}
	
	public CQLQuery makeCQLforAIM(int i){		
		CQLQuery aimCQL = null;
		if(i == 1){
			aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(this.getStudyInstanceUIDPrev(), this.getSeriesInstanceUIDPrev(), null);
			
		} else if(i == 2){
			aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(this.getStudyInstanceUIDCurr(), this.getSeriesInstanceUIDCurr(), null);			
		}		
		return aimCQL;
	}
	
	//importLocation is to be set and used when executing WorklistEntry with Thread
	File importLocation;
	public void setImportLocation(File importLocation){
		this.importLocation = importLocation;
	}
	
	
	public void run() {		
		execute(ApplicationManagerFactory.getInstance().getTmpDir());
	}
	
	GridRetrieve gridRetrievePrev;
	GridRetrieve gridRetrieveCurr;
	AimRetrieve aimRetrievePrev;
	AimRetrieve aimRetrieveCurr;
	NCIARetrieve nciaRetrievePrev;
	NCIARetrieve nciaRetrieveCurr;
	public void execute(File importLocation){						
		System.out.println("Executing worklist entry ...");										
		dicomPrevRetrieved = false;
		dicomCurrRetrieved = false;
		aimPrevRetrieved = false;
		aimCurrRetrieved = false;
		nciaPrevRetrieved = false;
		nciaCurrRetrieved = false;
		//1.Make CQL statement for:
		// a. dicomPrev
		// b. aimPrev
		// c. dicomCurr
		// d. aimCurr
		//2. Retrieve data sets
		//3. Arrange data sets
		//4. Launch assigned application and passe appropriate data set
		/*-------------- For DICOM prev ------------*/
		//NCIA - if NCIA requested retrieve and return;
		if(getDicomServiceURI().equalsIgnoreCase("http://imaging-stage.nci.nih.gov/wsrf/services/cagrid/NCIACoreService")){			
			GridLocation gridLoc = new GridLocation(getDicomServiceURI(), Type.DICOM, "NCIA-4.2", "Worklist DICOM Service URI - previous dataset");
			nciaRetrievePrev = new NCIARetrieve(getSeriesInstanceUIDPrev(), gridLoc, importLocation);
			nciaRetrievePrev.addNCIARetrieveListener(this);
			Thread t1 = new Thread(nciaRetrievePrev);
			t1.start();
			nciaRetrieveCurr = new NCIARetrieve(getSeriesInstanceUIDCurr(), gridLoc, importLocation);
			nciaRetrieveCurr.addNCIARetrieveListener(this);
			Thread t2 = new Thread(nciaRetrieveCurr);
			t2.start();			
		}else{
			CQLQuery cqlDicomPrev = makeCQLforDICOM(1);
			GridLocation gridLoc = new GridLocation(getDicomServiceURI(), Type.DICOM, "DICOM", "Worklist DICOM Service URI - previous dataset");		
			try {
				gridRetrievePrev = new GridRetrieve(cqlDicomPrev, gridLoc, importLocation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gridRetrievePrev.addGridRetrieveListener(this);		
			Thread t = new Thread(gridRetrievePrev);
			t.start();
			/*-------------- For AIM prev ------------*/
			CQLQuery cqlAimPrev = makeCQLforAIM(1);
			GridLocation aimLoc = new GridLocation(getAimServiceURI(), Type.AIM, "AIM-TCGA", "Worklist AIM Service URI - previous dataset");
			try {
				aimRetrievePrev = new AimRetrieve(cqlAimPrev, aimLoc, importLocation);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			aimRetrievePrev.addGridRetrieveListener(this);
			Thread t1AIM = new Thread(aimRetrievePrev);
			t1AIM.start();
			/*-------------- For DICOM curr ------------*/
			CQLQuery cqlDicomCurr = makeCQLforDICOM(2);				
			try {
				gridRetrieveCurr = new GridRetrieve(cqlDicomCurr, gridLoc, importLocation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gridRetrieveCurr.addGridRetrieveListener(this);		
			Thread t2 = new Thread(gridRetrieveCurr);
			t2.start();
			/*-------------- For AIM curr ------------*/
			CQLQuery cqlAimCurr = makeCQLforAIM(2);
			try {
				aimRetrieveCurr = new AimRetrieve(cqlAimCurr, aimLoc, importLocation);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			aimRetrieveCurr.addGridRetrieveListener(this);
			Thread t2AIM = new Thread(aimRetrieveCurr);
			t2AIM.start();
		}				
	}

	boolean dicomPrevRetrieved;
	boolean dicomCurrRetrieved;
	boolean aimPrevRetrieved;
	boolean aimCurrRetrieved;
	List<File> prev = new ArrayList<File>();
	List<File> curr = new ArrayList<File>();
	public void importedFilesAvailable(GridRetrieveEvent e) {		
		//prev is used to hold references to DICOM and AIM objects for previous dataset		
		//curr is used to hold references to DICOM and AIM objects for current dataset				
		if(e.getSource() instanceof GridRetrieve){
			GridRetrieve source = (GridRetrieve) e.getSource();			
			if(source == gridRetrievePrev){				
				prev.addAll(gridRetrievePrev.getRetrievedFiles());				
				dicomPrevRetrieved = true;
			}else if(source == gridRetrieveCurr){
				curr.addAll(gridRetrieveCurr.getRetrievedFiles());					
				dicomCurrRetrieved = true;
			}						
		}else if(e.getSource() instanceof AimRetrieve){
			AimRetrieve source = (AimRetrieve) e.getSource();
			if(source == aimRetrievePrev){
				if(aimRetrievePrev.getRetrievedFiles() != null){
					prev.addAll(aimRetrievePrev.getRetrievedFiles());
				}									
				aimPrevRetrieved = true;
			}else if(source == aimRetrieveCurr){
				if(aimRetrieveCurr.getRetrievedFiles() != null){
					curr.addAll(aimRetrieveCurr.getRetrievedFiles());
				}										
				aimCurrRetrieved = true;
			}			
		}
		if(dicomPrevRetrieved == true && dicomCurrRetrieved == true && aimPrevRetrieved == true && aimCurrRetrieved == true){																	
			//long t1 = System.currentTimeMillis();
			dm = makeWG23DataModel(prev, curr);
			//long t2 = System.currentTimeMillis();
			//System.out.println("Total time needed to create 124 native models " + (t2 - t1));
			notifyWorklistDataEntryAvailable();
		}						
	}

	WorklistEntryListener listener;
	public void addWorklistEntryListener(WorklistEntryListener l) {
		listener = l;		
	}
	
	void notifyWorklistDataEntryAvailable(){
		WorklistEntryEvent event = new WorklistEntryEvent(this);
		listener.worklistEntryDataAvailable(event);		
	}
	
	BasicDicomParser2 dicomParser = new BasicDicomParser2();
	//Check for file type (only dicom and aim are allowed for this worklist)
	public WG23DataModel makeWG23DataModel(List<File> filesPrev, List<File> filesCurr){
		if(filesPrev == null || filesCurr == null){return null;}
		AvailableData availableData = new AvailableData();
		Patient patient = new Patient();						
		patient.setName(getSubjectName());		
		ArrayOfPatient patients = new ArrayOfPatient();					
		patients.getPatient().add(patient);					
		availableData.setPatients(patients);
		ArrayOfObjectDescriptor arrayObjectDescriptorsAvailableData = new ArrayOfObjectDescriptor();		
		List<ObjectDescriptor> listObjectDescriptorsAvailableData = arrayObjectDescriptorsAvailableData.getObjectDescriptor();
		//TODO two datasets may belong to two different studies
		ArrayOfStudy arrayOfStudy = new ArrayOfStudy();
		Study study = null;
		Study study1 = null;
		Study study2 = null;
		if(getStudyInstanceUIDPrev() == null  && getStudyInstanceUIDCurr() != null){
			study = new Study();
			study.setStudyUID(getStudyInstanceUIDCurr());
			arrayOfStudy.getStudy().add(study);
		}else if(getStudyInstanceUIDPrev() != null && getStudyInstanceUIDCurr() != null && 
				getStudyInstanceUIDPrev().equalsIgnoreCase(getStudyInstanceUIDCurr())){
			study = new Study();
			study.setStudyUID(getStudyInstanceUIDCurr());
			arrayOfStudy.getStudy().add(study);
		}else if (getStudyInstanceUIDPrev() != null && getStudyInstanceUIDCurr() != null
				&& !getStudyInstanceUIDPrev().equalsIgnoreCase(getStudyInstanceUIDCurr())){
			study1 = new Study();
			study1.setStudyUID(getStudyInstanceUIDPrev());
			arrayOfStudy.getStudy().add(study1);
			study2 = new Study();
			study2.setStudyUID(getStudyInstanceUIDCurr());
			arrayOfStudy.getStudy().add(study2);
		}				
		patient.setStudies(arrayOfStudy);
		org.nema.dicom.wg23.Series seriesPrev = new org.nema.dicom.wg23.Series();		
		seriesPrev.setSeriesUID(getSeriesInstanceUIDPrev());
		org.nema.dicom.wg23.Series seriesCurr = new org.nema.dicom.wg23.Series();				
		seriesCurr.setSeriesUID(getSeriesInstanceUIDCurr());
		
		if(getStudyInstanceUIDPrev() == null  && getStudyInstanceUIDCurr() != null && study != null){
			ArrayOfSeries arraySeries = new ArrayOfSeries();
			List<org.nema.dicom.wg23.Series> listOfSeries = arraySeries.getSeries();
			listOfSeries.add(seriesPrev);
			listOfSeries.add(seriesCurr);
			study.setSeries(arraySeries);
		}else if(getStudyInstanceUIDPrev() != null && getStudyInstanceUIDCurr() != null &&
				getStudyInstanceUIDPrev().equalsIgnoreCase(getStudyInstanceUIDCurr()) && study != null){
			ArrayOfSeries arraySeries = new ArrayOfSeries();
			List<org.nema.dicom.wg23.Series> listOfSeries = arraySeries.getSeries();
			listOfSeries.add(seriesPrev);
			listOfSeries.add(seriesCurr);
			study.setSeries(arraySeries);
		}else if(getStudyInstanceUIDPrev() != null && getStudyInstanceUIDCurr() != null &&
				!getStudyInstanceUIDPrev().equalsIgnoreCase(getStudyInstanceUIDCurr()) && study1 != null && study2 != null){
			ArrayOfSeries arraySeriesPrev = new ArrayOfSeries();
			List<org.nema.dicom.wg23.Series> listOfSeriesPrev = arraySeriesPrev.getSeries();
			listOfSeriesPrev.add(seriesPrev);
			ArrayOfSeries arraySeriesCurr = new ArrayOfSeries();
			List<org.nema.dicom.wg23.Series> listOfSeriesCurr = arraySeriesCurr.getSeries();
			listOfSeriesCurr.add(seriesCurr);
			study1.setSeries(arraySeriesPrev);
			study2.setSeries(arraySeriesCurr);
		}			
		ArrayOfObjectDescriptor arrayObjectDescriptorsPrev = new ArrayOfObjectDescriptor();		
		List<ObjectDescriptor> listObjectDescriptorsPrev = arrayObjectDescriptorsPrev.getObjectDescriptor();		
		//objLocators holds values of both locators for prev data set and current data set
		List<ObjectLocator> objLocators = new ArrayList<ObjectLocator>(); 
		for(int i = 0; i < filesPrev.size(); i++){
			try {
				ObjectDescriptor objDesc = new ObjectDescriptor();
				//set objDesc
				Uuid objDescUUID = new Uuid();
				objDescUUID.setUuid(UUID.randomUUID().toString());
				objDesc.setUuid(objDescUUID);				
				String mimeType;			
				mimeType = DicomUtil.mimeType(filesPrev.get(i));
				objDesc.setMimeType(mimeType);
				if(mimeType.equalsIgnoreCase("application/dicom")){
					dicomParser.parse(filesPrev.get(i));
					String classUID = dicomParser.getSOPClassUID();
					Uid uid = new Uid();
					uid.setUid(classUID);
					objDesc.setClassUID(uid);
					String modCode = dicomParser.getModality();						
					Modality modality = new Modality();
					modality.setModality(modCode);
					objDesc.setModality(modality);
					listObjectDescriptorsPrev.add(objDesc);	
				}else{
					Uid uid = new Uid();
					uid.setUid("");
					objDesc.setClassUID(uid);
					String modCode = "";						
					Modality modality = new Modality();
					modality.setModality(modCode);
					objDesc.setModality(modality);
					listObjectDescriptorsAvailableData.add(objDesc);
				}										
				ObjectLocator objLoc = new ObjectLocator();				
				objLoc.setUuid(objDescUUID);				
				objLoc.setUri(filesPrev.get(i).toURI().toURL().toExternalForm());
				objLocators.add(objLoc);
			} catch (IOException e) {				
				return null;
			}			
		}
		seriesPrev.setObjectDescriptors(arrayObjectDescriptorsPrev);
				
		ArrayOfObjectDescriptor arrayObjectDescriptorsCurr = new ArrayOfObjectDescriptor();
		List<ObjectDescriptor> listObjectDescriptorsCurr = arrayObjectDescriptorsCurr.getObjectDescriptor();		
		for(int i = 0; i < filesCurr.size(); i++){
			try {
				ObjectDescriptor objDesc = new ObjectDescriptor();
				Uuid objDescUUID = new Uuid();
				objDescUUID.setUuid(UUID.randomUUID().toString());
				objDesc.setUuid(objDescUUID);
				String mimeType = DicomUtil.mimeType(filesCurr.get(i));
				objDesc.setMimeType(mimeType);
				if(mimeType.equalsIgnoreCase("application/dicom")){
					dicomParser.parse(filesCurr.get(i));
					String classUID = dicomParser.getSOPClassUID();
					Uid uid = new Uid();
					uid.setUid(classUID);
					objDesc.setClassUID(uid);
					String modCode = dicomParser.getModality();						
					Modality modality = new Modality();
					modality.setModality(modCode);
					objDesc.setModality(modality);
					listObjectDescriptorsCurr.add(objDesc);		
				}else{
					Uid uid = new Uid();
					uid.setUid("");
					objDesc.setClassUID(uid);
					String modCode = "";						
					Modality modality = new Modality();
					modality.setModality(modCode);
					objDesc.setModality(modality);
					listObjectDescriptorsAvailableData.add(objDesc);
				}													
				ObjectLocator objLoc = new ObjectLocator();				
				objLoc.setUuid(objDescUUID);				
				objLoc.setUri(filesCurr.get(i).toURI().toURL().toExternalForm());
				objLocators.add(objLoc);
			} catch (IOException e) {				
				return null;
			}			
		}
		seriesCurr.setObjectDescriptors(arrayObjectDescriptorsCurr);
		
		WG23DataModelWorklistImpl dataModel = new WG23DataModelWorklistImpl();
		availableData.setObjectDescriptors(arrayObjectDescriptorsAvailableData);
		dataModel.setAvailableData(availableData);
		ObjectLocator[] objLocs = new ObjectLocator[objLocators.size()];
		objLocators.toArray(objLocs);
		dataModel.setObjectLocators(objLocs);
		WG23DataModel wg23DataModel = dataModel;		
		return wg23DataModel;
	}	
	
	WG23DataModel dm;
	public WG23DataModel getDataModel(){
		return dm;
	}
	
	boolean nciaPrevRetrieved;
	boolean nciaCurrRetrieved;
	public void importedFilesAvailable(NCIARetrieveEvent e) {
		if(e.getSource() instanceof NCIARetrieve){
			NCIARetrieve source = (NCIARetrieve) e.getSource();			
			if(source == nciaRetrievePrev){				
				prev.addAll(nciaRetrievePrev.getRetrievedFiles());				
				nciaPrevRetrieved = true;
			}else if(source == nciaRetrieveCurr){
				curr.addAll(nciaRetrieveCurr.getRetrievedFiles());					
				nciaCurrRetrieved = true;
			}			
		}
		if(nciaPrevRetrieved == true && nciaCurrRetrieved == true){																	
			//long t1 = System.currentTimeMillis();
			dm = makeWG23DataModel(prev, curr);
			//long t2 = System.currentTimeMillis();			
			notifyWorklistDataEntryAvailable();
		}	
	}
}
