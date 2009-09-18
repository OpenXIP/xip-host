/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import org.apache.axis.types.URI.MalformedURIException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import edu.wustl.xipHost.caGrid.GridLocation.Type;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.caGrid.GridUtil;
import edu.wustl.xipHost.dataModel.SearchResult;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.ivi.dicomdataservice.client.DICOMDataServiceClient;


/**
 * @author Jaroslaw Krych
 *
 */
public class GridManagerImpl implements GridManager {

	SAXBuilder builder = new SAXBuilder();
	Document documentGrid;
	Element rootGrid;
	List<GridLocation> gridLocations = new ArrayList<GridLocation>();
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#loadGridLocations(java.io.File)
	 */
	public boolean loadGridLocations(File file) throws IOException, JDOMException {				
		documentGrid = builder.build(file);
		rootGrid = documentGrid.getRootElement();															
		List children = rootGrid.getChildren("grid_location");				
		for (int i = 0; i < children.size(); i++){
			String address = (((Element)children.get(i)).getChildText("gridAddress"));
			String shortName = (((Element)children.get(i)).getChildText("shortName"));
			String type = (((Element)children.get(i)).getChildText("type"));
			String protocolVersion = (((Element)children.get(i)).getChildText("protocolVersion"));
			try{
				if(type != null && type.equalsIgnoreCase("DICOM")){
					gridLocations.add(new GridLocation(address, Type.DICOM, protocolVersion, shortName));
				}else if (type != null && type.equalsIgnoreCase("AIM")){
					gridLocations.add(new GridLocation(address, Type.AIM, protocolVersion, shortName));
				} else{
					System.out.println("Invalid " + address + " " + type + " " + shortName + " - check location's 'type' attribute.");
				}
			}catch(IllegalArgumentException e){
				//Prints invalid location and proceeds to load next one
				System.out.println("Invalid " + address + " " + type + " " + shortName);				
			}
		}												
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#storeGridLocations(java.util.List, java.io.File)
	 */
	public boolean storeGridLocations(List<GridLocation> locations, File file) throws FileNotFoundException {		
		Element rootSave = new Element("locations");
		Document document = new Document();
		document.setRootElement(rootSave);	
		if(locations == null || file == null){return false;}
		for(int i = 0; i < locations.size(); i++){											
			Element gridLocationElem = new Element("grid_location");
			Element gridAddressElem = new Element("gridAddress");
			Element shortNameElem = new Element("shortName");
			Element typeElem = new Element("type");
			Element protocolVersionElem = new Element("protocolVersion");
			rootSave.addContent(gridLocationElem);
			gridLocationElem.addContent(gridAddressElem);
			gridLocationElem.addContent(shortNameElem);
			gridLocationElem.addContent(typeElem);
			gridLocationElem.addContent(protocolVersionElem);
			gridAddressElem.addContent(locations.get(i).getAddress());
			shortNameElem.addContent(locations.get(i).getShortName());			
			typeElem.addContent(locations.get(i).getType().toString());
			protocolVersionElem.addContent(locations.get(i).getProtocolVersion().toString());
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
	 * @see edu.wustl.xipHost.caGrid.GridManager#query(gov.nih.nci.cagrid.cqlquery.CQLQuery, edu.wustl.xipHost.caGrid.GridLocation)
	 */
	public SearchResult query(CQLQuery query, GridLocation location) throws MalformedURIException, RemoteException, ConnectException{		
		DataServiceClient dicomClient = null;
		NCIACoreServiceClient nciaClient = null;
		CQLQueryResultsIterator iter;		
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			dicomClient = new DataServiceClient(location.getAddress());			
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			nciaClient = new NCIACoreServiceClient(location.getAddress());
		}else{
			return null;
		}
		final CQLQuery fcqlq = query;		
		CQLQueryResults results = null;
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			results = dicomClient.query(fcqlq);
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			results = nciaClient.query(fcqlq);
		}						
        iter = new CQLQueryResultsIterator(results);        
        
        /*int ii = 0;
		while (iter.hasNext()) {
			System.out.println(ii++);
			java.lang.Object obj = iter.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}
			Patient patient = Patient.class.cast(obj);						
			System.out.println("Patient Id: " + patient.getPatientId());							
			System.out.println("Patient name: " + patient.getPatientName());
		}*/
        
        
        
        SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location);	        
        return result;			
	}	
		
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#getGridLocations()
	 */
	public List<GridLocation> getGridLocations(){
		return gridLocations;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#getGridTypeDicomLocations()
	 */
	public List<GridLocation> getGridTypeDicomLocations(){
		List<GridLocation> locs = getGridLocations();
		List<GridLocation> gridTypeDicomLocs = new ArrayList<GridLocation>();
		for(int i = 0; i < locs.size(); i++){
			GridLocation loc = (GridLocation)locs.get(i);
			if(loc.getType().equals(Type.DICOM)){
				gridTypeDicomLocs.add(loc);
			}
		}
		return gridTypeDicomLocs;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#getGridTypeAimLocations()
	 */
	public List<GridLocation> getGridTypeAimLocations(){
		List<GridLocation> locs = getGridLocations();
		List<GridLocation> gridTypeAimLocs = new ArrayList<GridLocation>();
		for(int i = 0; i < locs.size(); i++){
			GridLocation loc = (GridLocation)locs.get(i);
			if(loc.getType().equals(Type.AIM)){
				gridTypeAimLocs.add(loc);
			}
		}
		return gridTypeAimLocs;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#addGridLocation(edu.wustl.xipHost.caGrid.GridLocation)
	 */
	public boolean addGridLocation(GridLocation gridLocation){				
		try {
			if (!gridLocations.contains(gridLocation)){
				return gridLocations.add(gridLocation);
			}else {
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}
	}	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#modifyGridLocation(edu.wustl.xipHost.caGrid.GridLocation, edu.wustl.xipHost.caGrid.GridLocation)
	 */
	public boolean modifyGridLocation(GridLocation oldGridLocation, GridLocation newGridLocation){				
		try {	
			int i = gridLocations.indexOf(oldGridLocation);
			if (i != -1){
				gridLocations.set(i, newGridLocation);
				return true;
			} else {
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#removeGridLocation(edu.wustl.xipHost.caGrid.GridLocation)
	 */
	public boolean removeGridLocation(GridLocation gridLocation){
		try {
			return gridLocations.remove(gridLocation);
		} catch (IllegalArgumentException e) {
			return false;
		}		
	}	
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#getNumberOfGridDicomLocations()
	 */
	public int getNumberOfGridDicomLocations(){
		int num = 0;
		for (int i = 0; i < gridLocations.size(); i++){
			if(gridLocations.get(i).getType().equals(Type.DICOM)){
				num++;
			}
		}
		return num;		
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#getNumberOfGridAimLocations()
	 */
	public int getNumberOfGridAimLocations(){
		int num = 0;
		for (int i = 0; i < gridLocations.size(); i++){
			if(gridLocations.get(i).getType().equals(Type.AIM)){
				num++;
			}
		}
		return num;
	}
	
	GridUtil util = new GridUtil();
	//If startup sequence files are not set explicitly the following default values are used 	
	File xmlGridLocFile = new File("./config/grid_locations.xml");
	File nciaModelMapFile = new File("./resources/NCIAModelMap.properties");
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#runGridStartupSequence()
	 */
	public boolean runGridStartupSequence() {					
		///Make sure startup sequence file are not null. If null return false;
		if(xmlGridLocFile == null || nciaModelMapFile == null){return false;}			
			try {
				loadGridLocations(xmlGridLocFile);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("GlobalSearch startup sequence error. " + 
				"System could not find: grid_locations.xml");
				return false;
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				System.out.println("GlobalSearch startup sequence error. " + 
				"Error when processing grid_locations.xml");
				return false;
			}			
			try {
				util.loadNCIAModelMap(new FileInputStream(nciaModelMapFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//System.exit				
				//System.out.println("Regular message: " + e.getMessage());
				System.out.println("GlobalSearch startup sequence error. " + 
				"System could not find: NCIAModelMap.properties.");
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//System.exit
				System.out.println("GlobalSearch startup sequence error. " + 
				"System could not find: NCIAModelMap.properties.");
				return false;
			}
			return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#runGridShutDownSequence()
	 */
	public boolean runGridShutDownSequence(){
		List<GridLocation> gridLocs = getGridLocations();
		try {
			return storeGridLocations(gridLocs, xmlGridLocFile);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#setGridLocationFile(java.io.File)
	 */
	public void setGridLocationFile(File xmlGridLocFile){
		this.xmlGridLocFile = xmlGridLocFile;
	}
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#setNCIAModelMapFile(java.io.File)
	 */
	public void setNCIAModelMapFile(File nciaModelMapFile){
		this.nciaModelMapFile = nciaModelMapFile;
	}	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.caGrid.GridManager#getGridUtil()
	 */
	public GridUtil getGridUtil(){
		return util;
	}

	File hostTmpDir;
	public void setImportDirectory(File hostTmpDir) {
		this.hostTmpDir = hostTmpDir;		
	}

	public File getImportDirectory() {		
		return hostTmpDir;
	}

	public boolean exists(GridLocation gridLocation) {
		List<GridLocation> locs = getGridLocations();
		for(int i = 0; i < locs.size(); i++){
			GridLocation gridLoc = locs.get(i);
			if(gridLoc.getShortName().equalsIgnoreCase(gridLocation.getShortName())){
				return true;
			}
		}
		return false;
	}	
}
