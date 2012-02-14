/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.dcm4che2.data.Tag;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;


/**
 * @author Jaroslaw Krych
 *
 */

public class GridRetrieveNCIA implements Retrieve {
	final static Logger logger = Logger.getLogger(GridRetrieveNCIA.class);
	String seriesInstanceUID;
	GridLocation gridLocation;
	NCIACoreServiceClient client;
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	List<ObjectDescriptor> objectDescriptors;
	File importDir;
	RetrieveTarget retrieveTarget;
	DataSource dataSource;
	
	public GridRetrieveNCIA(){}
	
	public GridRetrieveNCIA(String seriesInstanceUID, GridLocation gridLocation, File importDir){
		this.seriesInstanceUID = seriesInstanceUID; 
		this.gridLocation = gridLocation;
		
		File inputDir;
		try {
			inputDir = File.createTempFile("DICOM-XIPHOST", null, importDir);			
			File localLocation = new File(inputDir.getCanonicalPath());
			inputDir.delete();
			if (!localLocation.exists())
				localLocation.mkdirs();
			this.importDir = localLocation;		
			
		} catch (IOException e) {
			logger.error(e, e);
		}
		try {
			client = new NCIACoreServiceClient(gridLocation.getAddress());
		} catch (MalformedURIException e) {
			logger.error(e, e);
		} catch (RemoteException e) {
			logger.error(e, e);
		} 
	}
	
	public void run() {
		Map<String, ObjectLocator> objectLocators = new HashMap<String, ObjectLocator>();
		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		TransferServiceContextReference tscr;
		try {
			Iterator<Integer> iter = dicomCriteria.keySet().iterator();
			gridLocation = GridManagerFactory.getInstance().selectedGridLocation();
			while(iter.hasNext()){
				Integer dicomTag = iter.next();
				if(dicomTag.equals(Tag.SeriesInstanceUID)){
					seriesInstanceUID = (String)dicomCriteria.get(dicomTag);
					break;
				}
			}
			if(client == null){
				client = new NCIACoreServiceClient(gridLocation.getAddress());
			}
			tscr = client.retrieveDicomDataBySeriesUID(seriesInstanceUID);
			tclient = new TransferServiceContextClient(tscr.getEndpointReference());
			istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
		} catch (Exception e) {
			logger.error(e, e);
		}					
		if(istream == null){			
			logger.warn("NBIA: InputStream is NULL!");
			return;
		}
		ZipInputStream zis = new ZipInputStream(istream);
        ZipEntryInputStream zeis = null;
        BufferedInputStream bis = null;
        int i = 0;
        logger.debug("Files retrieved from " + gridLocation.toString() + "\r\n");
        while(true) {
        	try {
        		zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {				
				logger.error(e, e);
			}
            String unzzipedFile = null;
			try {
				unzzipedFile = importDir.getCanonicalPath();
			} catch (IOException e) {
				logger.error(e, e);
			}
            logger.debug(" filename: " + zeis.getName());
            bis = new BufferedInputStream(zeis);
            byte[] data = new byte[8192];
            int bytesRead = 0;
            String retrievedFilePath = unzzipedFile + File.separator + zeis.getName();
            try {
            	
            	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(retrievedFilePath));
				while ((bytesRead = (bis.read(data, 0, data.length))) != -1)  {
					bos.write(data, 0, bytesRead);
				}
				bos.flush();
		        bos.close();
			} catch (IOException e) {
				logger.error(e, e);
			}
            ObjectLocator objLoc = new ObjectLocator();
    		ObjectDescriptor objDesc = objDescsDICOM.get(i);
			Uuid itemUUID = objDesc.getUuid();
    		objLoc.setUuid(itemUUID);				
    		objLoc.setUri(retrievedFilePath); 
    		objectLocators.put(itemUUID.getUuid(), objLoc);	
    		i++;
        }
        try {
			zis.close();
			tclient.destroy();
		} catch (IOException e) {
			logger.error(e, e);
		}
        fireResultsAvailable(objectLocators);
	}
	
	void fireResultsAvailable(Map<String, ObjectLocator> objectLocators){
		RetrieveEvent event = new RetrieveEvent(objectLocators);         		        
		listener.retrieveResultsAvailable(event);
	}

	RetrieveListener listener;	
	@Override
	public void addRetrieveListener(RetrieveListener l) {
		listener = l;
	}

	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria) {
		this.dicomCriteria = dicomCriteria;
		this.aimCriteria = aimCriteria;
	}

	@Override
	public void setCriteria(Object criteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void setImportDir(File importDir) {
		File inputDir;
		try {
			inputDir = File.createTempFile("DICOM-XIPHOST", null, importDir);			
			File dir = new File(inputDir.getCanonicalPath());
			inputDir.delete();
			if (!dir.exists())
				dir.mkdirs();
			this.importDir = dir;		
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	List<ObjectDescriptor> objDescsDICOM;
	List<ObjectDescriptor> objDescsAIM;
	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		this.objectDescriptors = objectDescriptors;
		objDescsDICOM = new ArrayList<ObjectDescriptor>();
		objDescsAIM = new ArrayList<ObjectDescriptor>();
		for(ObjectDescriptor objDesc : objectDescriptors){
			if(objDesc.getMimeType().equalsIgnoreCase("application/dicom")){
				objDescsDICOM.add(objDesc);
			} else if (objDesc.getMimeType().equalsIgnoreCase("text/xml")){
				objDescsAIM.add(objDesc);
			}
		}
	}

	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		this.retrieveTarget = retrieveTarget;		
	}
}
