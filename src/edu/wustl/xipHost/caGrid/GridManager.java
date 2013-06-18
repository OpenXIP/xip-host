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
package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.List;
import org.apache.axis.types.URI.MalformedURIException;
import org.jdom.JDOMException;
import edu.wustl.xipHost.dataModel.SearchResult;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

public interface GridManager {

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	public abstract boolean loadGridLocations(File file) throws IOException,
			JDOMException;
	/**
	 * 
	 * @param locations
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public abstract boolean storeGridLocations(List<GridLocation> locations, File file) throws FileNotFoundException;
	public abstract List<GridLocation> getGridLocations();
	public abstract List<GridLocation> getGridTypeDicomLocations();
	public abstract List<GridLocation> getGridTypeAimLocations();
	public abstract boolean addGridLocation(GridLocation gridLocation);
	public abstract boolean modifyGridLocation(GridLocation oldGridLocation, GridLocation newGridLocation);
	public abstract boolean removeGridLocation(GridLocation gridLocation);
	public abstract int getNumberOfGridDicomLocations();
	public abstract int getNumberOfGridAimLocations();
	/**
	 * 
	 * @return
	 */
	public abstract boolean runGridStartupSequence();
	public abstract boolean runGridShutDownSequence();
	/**
	 * Overwrites default location of xmlGridLocFile
	 * @param xmlGridLocFile
	 */
	public abstract void setGridLocationFile(File xmlGridLocFile);
	public abstract void setNCIAModelMapFile(File nciaModelMapFile);
	public abstract GridUtil getGridUtil();	
	public abstract void setImportDirectory(File hostTmpDir);
	public abstract File getImportDirectory();
	public abstract boolean exists(GridLocation gridLocation);
	public abstract GridLocation selectedGridLocation();
	public abstract void setSelectedGridLocation(GridLocation gridLocation);
}
