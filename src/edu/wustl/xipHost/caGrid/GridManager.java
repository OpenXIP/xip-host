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
}