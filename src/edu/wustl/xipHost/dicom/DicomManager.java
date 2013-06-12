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
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.jdom.JDOMException;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dataModel.SearchResult;

public interface DicomManager {

	/**
	 * loadPacsLocation method is used to retrieve PACS addresses from the local file.
	 * Each PACS address must meet validation criteria.
	 * 
	 * @param file specifes location of XML file containing PACS addresses
	 * @return returns true only when all addresses are loaded
	 */
	public boolean loadPacsLocations(File file) throws IOException, JDOMException;
	public abstract boolean addPacsLocation(PacsLocation pacsLocation);
	public abstract boolean modifyPacsLocation(PacsLocation oldPacsLocation, PacsLocation newPacsLocation);
	public abstract boolean removePacsLocation(PacsLocation pacsLocation);
	public abstract boolean storePacsLocations(List<PacsLocation> locations, File file) throws FileNotFoundException;
	public abstract SearchResult query(AttributeList criteria, PacsLocation location);
	public abstract boolean submit(File[] dicomFiles, PacsLocation location);
	public abstract List<PacsLocation> getPacsLocations();	
	public abstract void runDicomStartupSequence(String hsqldbServerConfigFilePath, Properties pixelmedProp);
	public abstract boolean runDicomShutDownSequence(String connectionPath, String user, String password);
	public String getDBFileName();
	public PacsLocation getDefaultCallingPacsLocation();
	public void setDefaultCallingPacsLocation(PacsLocation callingPacsLocation);
}
