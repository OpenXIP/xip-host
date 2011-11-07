/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import org.jdom.JDOMException;

/**
 * @author Jaroslaw Krych
 *
 */
public interface ApplicationManager {
	public boolean loadApplications (File xipAppFile) throws JDOMException, IOException;
	public boolean storeApplications(List<Application> applications, File xipAppFile);			
	public boolean addApplication(Application newApplication);
	public boolean modifyApplication(UUID applicationUUID, Application modifiedApplication);
	public boolean removeApplication(UUID applicationUUID);	
	public Application getApplication(UUID uuid);
	public Application getApplication(String applicationName);	
	public List<Application> getApplications();	
	public int getNumberOfApplications();
	public URL generateNewApplicationServiceURL();	
	public URL generateNewHostServiceURL();	
	public void setTmpDir(File tmpDir);
	public File getTmpDir();
	public void setOutputDir(File outDir);
	public File getOutputDir();
	public void addApplicationTerminationListener(ApplicationTerminationListener listener);
}
