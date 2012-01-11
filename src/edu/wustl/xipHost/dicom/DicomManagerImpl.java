/**
 * Copyright (c) 2007 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.hsqldb.Server;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import com.pixelmed.database.DatabaseInformationModel;
import com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.VerificationSOPClassSCU;
import com.pixelmed.query.QueryInformationModel;
import com.pixelmed.query.QueryTreeModel;
import com.pixelmed.query.QueryTreeRecord;
import com.pixelmed.query.StudyRootQueryInformationModel;
import com.pixelmed.server.DicomAndWebStorageServer;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.iterator.Criteria;

public class DicomManagerImpl implements DicomManager{
	final static Logger logger = Logger.getLogger(DicomManagerImpl.class);
	Document documentPacs;
	Element rootPacs;
	SAXBuilder builder = new SAXBuilder();
	List<PacsLocation> pacsLocations = new ArrayList<PacsLocation>();
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#loadPacsLocations(java.io.File)
	 */
	public boolean loadPacsLocations(File file) throws IOException, JDOMException {				
		documentPacs = builder.build(file);
		rootPacs = documentPacs.getRootElement();										
		List<?> children = rootPacs.getChildren("pacs_location");				
		for (int i = 0; i < children.size(); i++){
			String address = (((Element)children.get(i)).getChildText("hostAddress"));
			int port = Integer.valueOf((((Element)children.get(i)).getChildText("hostPort")));
			String aeTitle = (((Element)children.get(i)).getChildText("hostAETitle"));
			String shortName = (((Element)children.get(i)).getChildText("hostShortName"));
			/*System.out.println(address);
			System.out.println(port);
			System.out.println(aeTitle);*/						
			try {
				PacsLocation loc = new PacsLocation(address, port, aeTitle, shortName);
				pacsLocations.add(loc);
			} catch (IllegalArgumentException e) {
				//Prints invalid location and proceeds to load next one
				System.out.println("Unable to load: " + address + " " + port + " " + aeTitle + " " + shortName + " - invalid location.");				
			}																									
		}
		return true;							
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#storePacsLocations(java.util.List, java.io.File)
	 */
	public boolean storePacsLocations(List<PacsLocation> locations, File file) throws FileNotFoundException {					
		Element rootSave = new Element("locations");
		Document document = new Document();
		document.setRootElement(rootSave);		
		if(locations == null){return false;}
		for(int i = 0; i < locations.size(); i++){						
			Element pacsElem = new Element("pacs_location");
			Element addressElem = new Element("hostAddress");
			Element portElem = new Element("hostPort");
			Element aeTitleElem = new Element("hostAETitle");					
			Element shortNameElem = new Element("hostShortName");
			pacsElem.addContent(addressElem);
			pacsElem.addContent(portElem);
			pacsElem.addContent(aeTitleElem);
			pacsElem.addContent(shortNameElem);
			rootSave.addContent(pacsElem);
			addressElem.addContent(locations.get(i).getAddress());
			portElem.addContent(String.valueOf(locations.get(i).getPort()));
			aeTitleElem.addContent(locations.get(i).getAETitle());
			shortNameElem.addContent(locations.get(i).getShortName());
		}
		try {
			FileOutputStream outStream = new FileOutputStream(file);
			XMLOutputter outToXMLFile = new XMLOutputter();
			outToXMLFile.setFormat(Format.getPrettyFormat());
	    	outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();                       
		} catch (IOException e) {
			return false;
		} 
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#addPacsLocation(edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public boolean addPacsLocation(PacsLocation pacsLocation){		
		try {
			if(!pacsLocations.contains(pacsLocation)){
				return pacsLocations.add(pacsLocation);
			} else {
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}								
	}	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#modifyPacsLocation(edu.wustl.xipHost.globalSearch.PacsLocation, edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public boolean modifyPacsLocation(PacsLocation oldPacsLocation, PacsLocation newPacsLocation) {
		//validate method is used to check if parameters are valid, are notmissing, 
		//do not contain empty strings or do not start from white spaces		
		try {
			int i = pacsLocations.indexOf(oldPacsLocation);
			if (i != -1){
				pacsLocations.set(i, newPacsLocation);
				return true;
			} else{
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}
	}	
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#removePacsLocation(edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public boolean removePacsLocation(PacsLocation pacsLocation){
		//System.out.println(pacsLocations.indexOf(pacsLocation));
		try {
			return pacsLocations.remove(pacsLocation);
		} catch (IllegalArgumentException e){
			return false;
		}		
	}

	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#getPacsLocations()
	 */
	public List<PacsLocation> getPacsLocations(){
		return pacsLocations;
	}	

	QueryTreeModel mTree = null;
	QueryInformationModel mModel = null;
	String calledAETitle;
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#query(com.pixelmed.dicom.AttributeList, edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public SearchResult query(AttributeList criteria, PacsLocation location) {										 
	    if(criteria == null || location == null){
	    	return null;
	    }		
	    //Connecting to the server
	    logger.debug("Queried source: " + location.toString());
		String hostName = location.getAddress();
		int port = location.getPort();
		calledAETitle = location.getAETitle();
		//ensure callingAETitle is not empty (not all stations allow empty value). 
		String callingAETitle = "";
		if(HostConfigurator.getHostConfigurator() != null){
			callingAETitle = HostConfigurator.getHostConfigurator().getAETitle();
		}
		if (callingAETitle == "") {
			callingAETitle = "WS_XIP";
		}
		mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);		
		try {
		    new VerificationSOPClassSCU(hostName, port, calledAETitle, callingAETitle, false, 0);
		}catch (Exception e) {
		    return null;
		}
		SearchResult returnResult = null;
		try {			
			//mTree hangs when supplied callingAETitle is an empty string
			mTree = mModel.performHierarchicalQuery(criteria);			
			result = new SearchResult(location.hostShortName);
			Map<Integer, Object> dicomCriteria = DicomUtil.convertToADDicomCriteria(criteria);
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			Criteria originalCriteria = new Criteria(dicomCriteria, aimCriteria);
			result.setOriginalCriteria(originalCriteria);
			Object root = mTree.getRoot();			
			returnResult = (SearchResult) resolveToSearchResult(root);
			if(logger.isDebugEnabled()){
				 Iterator<Patient> patients = result.getPatients().iterator();
				 while(patients.hasNext()){
					 Patient patient = patients.next();
					 Timestamp patientLastUpdated = patient.getLastUpdated();
					 String strPatientLastUpdated = null;
					 if(patientLastUpdated != null){
						 strPatientLastUpdated = patientLastUpdated.toString();
					 }
					 logger.debug(patient.toString() + " Last updated: " + strPatientLastUpdated);
					 Iterator<Study> studies = patient.getStudies().iterator();
					 while(studies.hasNext()){
						 Study study = studies.next();
						 Timestamp studyLastUpdated = study.getLastUpdated();
						 String strStudyLastUpdated = null;
						 if(studyLastUpdated != null){
							 strStudyLastUpdated = studyLastUpdated.toString();
						 }
						 logger.debug("   " + study.toString() + " Last updated: " + strStudyLastUpdated);
						 Iterator<Series> series = study.getSeries().iterator();
						 while(series.hasNext()){
							 Series oneSeries = series.next();
							 Timestamp seriesLastUpdated = oneSeries.getLastUpdated();
							 String strSeriesLastUpdated = null;
							 if(seriesLastUpdated != null){
								 strSeriesLastUpdated = seriesLastUpdated.toString();
							 }
							 logger.debug("      " + oneSeries.toString() + " Last updated: " + strSeriesLastUpdated);
							 Iterator<Item> images = oneSeries.getItems().iterator();
							 while(images.hasNext()){
								 logger.debug("         " + images.next().toString());
							 }
						 }
					 }
				 }
			}
		} catch (IOException e) {
			return null;
		} catch (DicomException e) {
			return null;
		} catch (DicomNetworkException e) {
			return null;
		} 
		return returnResult;
	}	

	Patient patient = null;
	Study study = null;
	Series series = null;	
	SearchResult result;
	SearchResult resolveToSearchResult(Object node) {										
		int numChildren = mTree.getChildCount(node);		
		for(int i = 0; i < numChildren; i++){
			Object child = mTree.getChild(node, i);
			//find if child is Study, Series or Image
			//Case approach
			String level = getRetrieveLevel(child);					
			Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
			if(level.equalsIgnoreCase("Study")){
				String patientName = attValues.get("(0x0010,0x0010)");
				if(patientName == null){patientName = "";}
				String patientID = attValues.get("(0x0010,0x0020)");
				if(patientID == null){patientID = "";}
				String patientBirthDate = attValues.get("(0x0010,0x0030)");
				if(patientBirthDate == null){patientBirthDate = "";}
				patient = new Patient(patientName, patientID, patientBirthDate); 
				//patient.setLastUpdated(lastUpdated);
				if(!result.contains(patientID)){
					result.addPatient(patient);
				} else {
					patient = result.getPatient(patientID);
				}
				String studyDate = attValues.get("(0x0008,0x0020)");
				if(studyDate == null){studyDate = "";}
				String studyID = attValues.get("(0x0020,0x0010)");
				if(studyID == null){studyID = "";}
				String studyDesc = attValues.get("(0x0008,0x1030)");
				if(studyDesc == null){studyDesc = "";}				
				String studyInstanceUID = attValues.get("(0x0020,0x000D)");				
				if(studyInstanceUID == null){studyInstanceUID = "";}				
				study = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				
				if(!patient.contains(studyInstanceUID)){
					patient.addStudy(study);
				} else {
					study = patient.getStudy(studyInstanceUID);
				}
				patient.setLastUpdated(lastUpdated);
				resolveToSearchResult(child);
			}else if(level.equalsIgnoreCase("Series")){
				String seriesNumber = attValues.get("(0x0020,0x0011)");
				if(seriesNumber == null){seriesNumber = "";}
				String modality = attValues.get("(0x0008,0x0060)");
				if(modality == null){modality = "";}
				String seriesDesc = attValues.get("(0x0008,0x103E)");
				if(seriesDesc == null){seriesDesc = "";}
				String seriesInstanceUID = attValues.get("(0x0020,0x000E)");
				if(seriesInstanceUID == null){seriesInstanceUID = "";}
				series = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);
				if(!study.contains(seriesInstanceUID)){
					study.addSeries(series);
				} else {
					series = study.getSeries(seriesInstanceUID);
				}
				study.setLastUpdated(lastUpdated);
				resolveToSearchResult(child);
			}else if(level.equalsIgnoreCase("Image")){
				String imageNumber = attValues.get("(0x0008,0x0018)");	//SOPIntanceUID
				String sopClassUID = attValues.get("(0x0008,0x0016)");	//SOPClassUID
				String modCode = attValues.get("(0x0008,0x0060)");		//Modality
				if(imageNumber == null){imageNumber = "";}								
				Item image = new ImageItem(imageNumber);				
				if(!series.contains(imageNumber)){					
					series.addItem(image);
					ObjectDescriptor objDesc = new ObjectDescriptor();					
					Uuid objDescUUID = new Uuid();
					objDescUUID.setUuid(UUID.randomUUID().toString());
					objDesc.setUuid(objDescUUID);													
					objDesc.setMimeType("application/dicom");			
					Uid uid = new Uid();
					String classUID = sopClassUID;
					uid.setUid(classUID);
					objDesc.setClassUID(uid);											
					Modality modality = new Modality();
					modality.setModality(modCode);
					objDesc.setModality(modality);
					image.setObjectDescriptor(objDesc);
				}
				series.setLastUpdated(lastUpdated);
			}else{
				return null;
			}
		}
		return result;
	}			
	
	
	Map<String, String> attValues;
	/**
	 * 
	 * @param node
	 * @return
	 */
	String getRetrieveLevel(Object node){
		attValues = new HashMap<String, String>();
		if (node instanceof QueryTreeRecord) {
			QueryTreeRecord r = (QueryTreeRecord)node;
			AttributeList keys = r.getUniqueKeys();									
			String seriesInstanceUID = Attribute.getSingleStringValueOrNull(keys, TagFromName.SeriesInstanceUID);
			String studyInstanceUID = Attribute.getSingleStringValueOrNull(keys, TagFromName.StudyInstanceUID);									
	        attValues.put("(0x0020,0x000D)", studyInstanceUID);
	        attValues.put("(0x0020,0x000E)", seriesInstanceUID);
			AttributeList identifier = r.getAllAttributesReturnedInIdentifier();
	        DicomDictionary dictionary = AttributeList.getDictionary();
	        Iterator<?> iter = dictionary.getTagIterator();        
	        String strAtt = null;
	        String attValue = null;
	        while(iter.hasNext()){
				AttributeTag attTag  = (AttributeTag)iter.next();
				strAtt = attTag.toString();									
				attValue = Attribute.getSingleStringValueOrEmptyString(identifier, attTag);			
				//put only those attributes non empty and non null		
				if(!attValue.isEmpty()){
					//System.out.println(strAtt + " " + attValue);
					attValues.put(strAtt, attValue);
				}										
	        }	        
		}
		String level = attValues.get("(0x0008,0x0052)");
		return level;				
	}
	
	
	
	BasicDicomParser2 parser = new BasicDicomParser2();
	public boolean submit(File[] dicomFiles, PacsLocation location) {		
		for(int i = 0; i < dicomFiles.length; i++){
			AttributeList attList = parser.parse(dicomFiles[i]);					
			try {
				dbModel.insertObject(attList, dicomFiles[i].getAbsolutePath(), "R");
			} catch (DicomException e) {
				logger.error(e, e);
				return false;
			}
		}		
		return true;
	}

	File xmlPacsLocFile = new File("./config/pacs_locations.xml");
	public boolean runDicomStartupSequence(String hsqldbServerConfigFilePath, Properties pixelmedProp) {
		if(xmlPacsLocFile == null ){return false;}
		startHSQLDB(hsqldbServerConfigFilePath);		
		startPixelmedServer(pixelmedProp);
		dbFileName = prop.getProperty("Application.DatabaseFileName");		
		setDBModel(dbFileName);
		try {
			loadPacsLocations(xmlPacsLocFile);				
		} catch (IOException e) {		
			logger.error("DICOM module startup sequence error. System could not find: pacs_locations.xml", e);
			return false;
		} catch (JDOMException e) {
			logger.error(e, e);
			return false;
		}
		return true;
	}
	
	public boolean runDicomShutDownSequence(String connectionPath, String user, String password){
		logger.debug("Running DICOM shutdown sequence.");
		closeDicomServer(connectionPath, user, password);		
		return true;
	}
	
	Connection conn;
	public void closeDicomServer(String connectionPath, String user, String password){						
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			logger.error(e1,  e1);
		}
		try {
			conn = DriverManager.getConnection(connectionPath, user, password);
			if(conn.isClosed() == false){
				Statement st = conn.createStatement();
				boolean bln = st.execute("SHUTDOWN");
		        if(bln){
		        	conn.close();
		        }
			}
		} catch (SQLException e) {
			logger.error("ERROR detected when shuting down DICOM Pixelmed and HSQLDB servers");
			logger.error(e, e);
		}
	}
	
	
	Server hsqldbServer;
	public void startHSQLDB(String hsqldbServerConfigFilePath ) {
		hsqldbServer = new Server();
		hsqldbServer.putPropertiesFromFile(hsqldbServerConfigFilePath);		
		hsqldbServer.start();
	}
	
	DicomAndWebStorageServer server;
	Properties prop = new Properties();
	public boolean startPixelmedServer(Properties prop){		
		try {
			this.prop = prop;
			server = new DicomAndWebStorageServer(this.prop);
		} catch (FileNotFoundException e) {			
			logger.error(e,  e);
			return false;
		} catch (IOException e) {
			logger.error(e,  e);
			return false;
		} catch (DicomException e) {
			logger.error(e,  e);
			return false;
		} catch (DicomNetworkException e) {
			logger.error(e,  e);
			return false;
		}
		return true;
	}
	
	DatabaseInformationModel dbModel = null;
	public DatabaseInformationModel getDBModel(){
		return dbModel;
	}
	
	public void setDBModel(String dbModel){
		try {			
			this.dbModel = new PatientStudySeriesConcatenationInstanceModel(dbModel);			
		} catch (DicomException e) {
			logger.error(e, e);
		}
	}
	
	String dbFileName;
	public String getDBFileName(){
		return dbFileName;
	}
	
	public void closeHSQLDB(){
		hsqldbServer.shutdown();
	}


	@Override
	public PacsLocation getDefaultCallingPacsLocation() {
		return callingPacsLocation;
	}
	
	PacsLocation callingPacsLocation;
	public void setDefaultCallingPacsLocation(PacsLocation callingPacsLocation){
		this.callingPacsLocation = callingPacsLocation;
	}
}