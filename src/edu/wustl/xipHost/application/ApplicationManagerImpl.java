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

package edu.wustl.xipHost.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.iterator.IterationTarget;

public class ApplicationManagerImpl implements ApplicationManager{		
	final static Logger logger = Logger.getLogger(ApplicationManagerImpl.class);
	static List<Application> validApplications = new ArrayList<Application>();
	List<Application> notValidApplications = new ArrayList<Application>();
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
				String type = new String();
				boolean requiresGUI;
				String wg23DataModelType = new String();
				int concurrentInstances;
				IterationTarget iterationTarget = null;
		        List<?> appList = root.getChildren("application");		        
		        Iterator<?> iter = appList.iterator();
		        while(iter.hasNext()){                                                               
		        	Element application = (Element)iter.next();
		        	name = application.getChildText("name");
		        	exePath = application.getChildText("exePath");		        	
		        	vendor = application.getChildText("vendor");
		        	version = application.getChildText("version");
		        	iconFile = application.getChildText("iconFile");
		        	type = application.getChildText("type");
		        	requiresGUI = Boolean.getBoolean(application.getChildText("requiresGUI"));
		        	wg23DataModelType = application.getChildText("wg23DataModelType");
		        	concurrentInstances = Integer.parseInt(application.getChildText("concurrentInstances"));
		        	String strTarget = application.getChildText("iterationTarget");
		        	iterationTarget = IterationTarget.valueOf(strTarget);
		        		        				        				        		
	        		Application app;
					app = new Application(name, exePath, vendor, version, iconFile,
							type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
					if(app.isValid()) {
						app.addApplicationTerminationListener(applicationTerminationListener);
						addApplication(app);
					} else {
						addNotValidApplication(app);
					}       			        			        	
		        }   			
			return true;
		}				                                   		       
    }
		
	public boolean storeApplications(List<Application> applications, File xipAppFile){      	
		if(applications == null || xipAppFile == null){return false;}
		Element root = new Element("applications");						
		for(int i = 0; i < applications.size(); i++){						
			if(applications.get(i).getDoSave()){
				Element application = new Element("application");                
				Element name = new Element("name");
				Element exePath = new Element("exePath");
				Element vendor = new Element("vendor");
				Element version = new Element("version");
				Element iconFile = new Element("iconFile");
				Element type = new Element("type");
				Element requiresGUI = new Element("requiresGUI");
				Element wg23DataModelType = new Element("wg23DataModelType");
				Element concurrentInstances = new Element("concurrentInstances");
				Element iterationTarget = new Element("iterationTarget");
				root.addContent(application);        	                	                                        		        
				    application.addContent(name);
				    	name.setText(applications.get(i).getName());
				    application.addContent(exePath);			            
						if(applications.get(i).getExePath() != null) {
							exePath.setText(applications.get(i).getExePath());	
						} else {
							exePath.setText("");
						}								
				    application.addContent(vendor);
				    	vendor.setText(applications.get(i).getVendor());
				    application.addContent(version);
				    	version.setText(applications.get(i).getVersion());	       	
					application.addContent(iconFile);
					String icon = applications.get(i).getIconPath();			       
					if(icon != null){
						iconFile.setText(icon);
					}else {
						iconFile.setText("");
					}
					application.addContent(type);
						type.setText(applications.get(i).getType());
					application.addContent(requiresGUI);
						requiresGUI.setText(new Boolean(applications.get(i).requiresGUI()).toString());
					application.addContent(wg23DataModelType);
						wg23DataModelType.setText(applications.get(i).getWG23DataModelType());
					application.addContent(concurrentInstances);
						concurrentInstances.setText(String.valueOf(applications.get(i).getConcurrentInstances()));
					application.addContent(iterationTarget);
						iterationTarget.setText(applications.get(i).getIterationTarget().toString());
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
			validApplications.add(newApplication);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}		
	}
	
	public boolean addNotValidApplication(Application notValidApplication){
		notValidApplications.add(notValidApplication);
		return true;
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
		} else if(app.getState() != null){
			if(!app.getState().equals(State.EXIT) || !app.getState().equals(null)){
				return false;
			}
		}
		String newName = modifiedApplication.getName();
		String newExePath = modifiedApplication.getExePath();
		String newVendor = modifiedApplication.getVendor();
		String newVersion = modifiedApplication.getVersion();
		String newIconFile = modifiedApplication.getIconPath();
		String newType = modifiedApplication.getType();
		boolean newRequiresGUI = modifiedApplication.requiresGUI();
		String newWG23DataModelType = modifiedApplication.getWG23DataModelType();
		int newConcurrentInstances = modifiedApplication.getConcurrentInstances();
		IterationTarget newIterationTarget = modifiedApplication.getIterationTarget();
		app.setName(newName);
		app.setExePath(newExePath);
		app.setVendor(newVendor);
		app.setVersion(newVersion);
		app.setIconPath(newIconFile);
		app.setType(newType);
		app.setRequiresGUI(newRequiresGUI);
		app.setWG23DataModelType(newWG23DataModelType);
		app.setConcurrentInstances(newConcurrentInstances);
		app.setIterationTarget(newIterationTarget);
		return true;		
	}
	
	/**
	 * Application can be removed when application's State is either null (not set yet) or EXIT
	 * @param applicationUUID
	 * @return
	 */
	public boolean removeApplication(UUID applicationUUID){
		for(int i = 0; i < getNumberOfApplications(); i++){
			if(validApplications.get(i).getID().equals(applicationUUID)){								
				Application app = validApplications.get(i);
				if(app.getState() != null && !app.getState().equals(State.EXIT)){
					return false;
				}else{
					validApplications.remove(i);
					return true;
				}				
			}
		}
		return false;
	}
	
	public void removeNotValidApplication(UUID uuid){
		for(int i = 0; i < getNumberOfNotValidApplications(); i++){
			List<Application> apps = getNotValidApplications();
			Iterator<Application> iter = apps.iterator();
			while(iter.hasNext()){
				Application app = iter.next();
				if(app.getID().equals(uuid)){
					iter.remove();
				}
			}
		}
	}
		
	public Application getApplication(UUID uuid){
		Application app = null;
		for(int i = 0; i < validApplications.size(); i++){
			if(validApplications.get(i).getID().equals(uuid)){
				app = validApplications.get(i);
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
		List<Application> allAppliations = new ArrayList<Application>();
		allAppliations.addAll(validApplications);
		allAppliations.addAll(notValidApplications);
		for(int i = 0; i < allAppliations.size(); i++){
			if(allAppliations.get(i).getName().equalsIgnoreCase(applicationName)){
				app = allAppliations.get(i);
			}
		}
		return app;
	}
		
	public List<Application> getApplications(){
		return validApplications;
	}
	
	public List<Application> getNotValidApplications(){
		return notValidApplications;
	}
	
	public int getNumberOfApplications(){
		return validApplications.size();
	}
	
	public int getNumberOfNotValidApplications(){
		return notValidApplications.size();
	}
		
    
	public URL generateNewApplicationServiceURL(){
		//"http://localhost:8060/ApplicationInterface?wsdl"
		int portNum;
		try {
			ServerSocket socket = new ServerSocket(0);
			portNum = socket.getLocalPort();
		} catch (IOException e1) {
			return null;
		}
		String str1 = "http://localhost:";
		String str2 = "/ApplicationInterface";
		URL url = null;
		try {
			url = new URL(str1 + portNum + str2);
		} catch (MalformedURLException e) {
			return url;
		}
		return url;
	}	
	
	public URL generateNewHostServiceURL(){
		int portNum;
		try {
			ServerSocket socket = new ServerSocket(0);
			portNum = socket.getLocalPort();
		} catch (IOException e1) {
			return null;
		}
		String str1 = "http://localhost:";	
		String str2 = "/HostInterface";
		URL url = null;
		try {
			url = new URL(str1 + portNum + str2);
		} catch (MalformedURLException e) {
			return url;
		}
		return url;
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

	ApplicationTerminationListener applicationTerminationListener;
	@Override
	public void addApplicationTerminationListener(ApplicationTerminationListener listener) {
		applicationTerminationListener = listener;
	}	
}
