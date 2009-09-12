/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.State;

public class ApplicationManagerImpl implements ApplicationManager{		
	static List<Application> applications = new ArrayList<Application>();		
	Document document;
	SAXBuilder saxBuilder = new SAXBuilder();
	
	public boolean loadApplications (File xipAppFile) throws JDOMException, IOException {				
		if(xipAppFile == null || !xipAppFile.exists()){
			return false;
		}else{					
				document = saxBuilder.build(xipAppFile);
				Element root = document.getRootElement();
				String name = new String();
				String exePath = new String();
				String vendor = new String();
				String version = new String();
				String iconFile = new String();
		        List appList = root.getChildren("application");		        
		        Iterator iter = appList.iterator();
		        int i= 0;
		        while(iter.hasNext()){                                                               
		        	Element application = (Element)iter.next();
		        	name = application.getChildText("name");
		        	exePath = new File(application.getChildText("exePath")).getCanonicalPath();		        	
		        	vendor = application.getChildText("vendor");
		        	version = application.getChildText("version");
		        	iconFile = application.getChildText("iconFile");		        	
		        	try{		        				        				        		
		        		Application app = new Application(name, new File(exePath), vendor, version, new File(iconFile));		        			    				
			    		addApplication(app);		        	
			        	i++;
		        	}catch (IllegalArgumentException e){
		        		System.out.println("Unable to load: " + name + " " + exePath + " - invalid parameters.");
		        	}		        			        			        	
		        }   			
			return true;
		}				                                   		       
    }
		
	public boolean storeApplications(List<Application> applications, File xipAppFile){      	
		if(applications == null || xipAppFile == null){return false;}
		Element root = new Element("applications");						
		for(int i = 0; i < getNumberOfApplications(); i++){						
			if(applications.get(i).getDoSave()){
				try {
					Element application = new Element("application");                
					Element name = new Element("name");
			        Element exePath = new Element("exePath");
			        Element vendor = new Element("vendor");
			        Element version = new Element("version");
			        Element iconFile = new Element("iconFile");
					root.addContent(application);        	                	                                        		        
				        application.addContent(name);
				        	name.setText(applications.get(i).getName());
				        application.addContent(exePath);			            
							exePath.setText(applications.get(i).getExePath().getCanonicalPath());						
				        application.addContent(vendor);
				        	vendor.setText(applications.get(i).getVendor());
				        application.addContent(version);
				        	version.setText(applications.get(i).getVersion());	       	
			        	application.addContent(iconFile);
			        	File icon = applications.get(i).getIconFile();			       
			        	if(icon != null){
			        		iconFile.setText(applications.get(i).getIconFile().getCanonicalPath());
			        	}else {
			        		iconFile.setText("");
			        	}
				} catch (MalformedURLException e) {				
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}		                	
    	Document document = new Document(root);
    	FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(xipAppFile);
			XMLOutputter outToXMLFile = new XMLOutputter();
			outToXMLFile.setFormat(Format.getPrettyFormat());
	    	outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();
	    	return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}    	                    
	}
				
	public boolean addApplication(Application newApplication){				
		try{
			applications.add(newApplication);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}		
	}
	
	/**
	 * Application can be modified when application's State is either null (not set yet) or EXIT
	 * @param applicationUUID
	 * @param modifiedApplication
	 * @return
	 */
	public boolean modifyApplication(UUID applicationUUID, Application modifiedApplication){				
		Application app = getApplication(applicationUUID);
		if(app == null){
			return false;
		}else if(app.getState() != null){
			if(!app.getState().equals(State.EXIT) || !app.getState().equals(null)){
				return false;
			}
		}
		String newName = modifiedApplication.getName();
		File newExePath = modifiedApplication.getExePath();
		String newVendor = modifiedApplication.getVendor();
		String newVersion = modifiedApplication.getVersion();
		File newIconFile = modifiedApplication.getIconFile();
		app.setName(newName);
		app.setExePath(newExePath);
		app.setVendor(newVendor);
		app.setVersion(newVersion);
		app.setIconFile(newIconFile);
		return true;		
	}
	
	/**
	 * Application can be removed when application's State is either null (not set yet) or EXIT
	 * @param applicationUUID
	 * @return
	 */
	public boolean removeApplication(UUID applicationUUID){
		for(int i = 0; i < getNumberOfApplications(); i++){
			if(applications.get(i).getID().equals(applicationUUID)){								
				Application app = applications.get(i);
				if(app.getState() != null && !app.getState().equals(State.EXIT)){
					return false;
				}else{
					applications.remove(i);
					return true;
				}				
			}
		}
		return false;
	}		
		
	public Application getApplication(UUID uuid){
		Application app = null;
		for(int i = 0; i < applications.size(); i++){
			if(applications.get(i).getID().equals(uuid)){
				app = applications.get(i);
			}
		}		
		return app;
	}		
	/**
	 * Method was intended for use with worklist, which used applications' names.
	 * Worklist enties do not contain applications UUID. UUIDs are asigned dynamically
	 * when applications are loaded or added.
	 * @param applicationName
	 * @return
	 */
	public Application getApplication(String applicationName){
		Application app = null;
		for(int i = 0; i < applications.size(); i++){
			if(applications.get(i).getName().equalsIgnoreCase(applicationName)){
				app = applications.get(i);
			}
		}
		return app;
	}
		
	public List<Application> getApplications(){
		return applications;
	}
	
	public int getNumberOfApplications(){
		return applications.size();
	}	
		
    
	
	public URL generateNewApplicationServiceURL(){
		//"http://localhost:8060/ApplicationInterface?wsdl"
		int portNum = 8060;
		String str1 = "http://localhost:";
		//String str2 = "/ApplicationInterface?wsdl";		
		String str2 = "/ApplicationInterface";
		while(checkPort(portNum) == false){
			portNum++;			 			
		}
		URL url = null;
		try {
			url = new URL(str1 + portNum + str2);
		} catch (MalformedURLException e) {
			return url;
		}					
		return url;
	}	
	
	public URL generateNewHostServiceURL(){
		int portNum = 8090;
		String str1 = "http://localhost:";
		//String str2 = "/HostInterface?wsdl";		
		String str2 = "/HostInterface";
		while(checkPort(portNum) == false){
			portNum++;			 			
		}
		URL url = null;
		try {
			url = new URL(str1 + portNum + str2);
		} catch (MalformedURLException e) {
			return url;
		}					
		return url;
	}	
	
	public boolean checkPort(int port) {
		try {
			ServerSocket sock = new ServerSocket(port);
			sock.close();			
			return true;
		}catch(BindException b) {			
			return false;
		}
		catch (IOException e) {
			return false;
		}		
	} 
	
	File tmpDir;
	public void setTmpDir(File tmpDir){
		this.tmpDir = tmpDir;
	}
	public File getTmpDir(){
		return tmpDir;
	}
	
	File outDir;
	public void setOutputDir(File outDir){
		this.outDir = outDir;
	}
	public File getOutputDir(){
		return outDir;
	}	
}
