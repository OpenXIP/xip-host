package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
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
	public abstract boolean loadPacsLocations(File file) throws IOException, JDOMException;
	public abstract boolean addPacsLocation(PacsLocation pacsLocation);
	public abstract boolean modifyPacsLocation(PacsLocation oldPacsLocation, PacsLocation newPacsLocation);
	public abstract boolean removePacsLocation(PacsLocation pacsLocation);
	public abstract boolean storePacsLocations(List<PacsLocation> locations, File file) throws FileNotFoundException;
	public abstract SearchResult query(AttributeList criteria, PacsLocation location);
	//public abstract List<URI> retrieve(AttributeList criteria, PacsLocation called, PacsLocation calling);
	public abstract boolean submit(File[] dicomFiles, PacsLocation location);
	public abstract List<PacsLocation> getPacsLocations();	
	public abstract boolean runDicomStartupSequence();
	public String getDBFileName();
	public PacsLocation getDefaultCallingPacsLocation();
	public void setDefaultCallingPacsLOcation(PacsLocation callingPacsLocation);
}