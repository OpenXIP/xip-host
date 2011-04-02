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
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;

import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.DataSource;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataAccess.RetrieveEvent;
import edu.wustl.xipHost.dataAccess.RetrieveListener;
import edu.wustl.xipHost.dataAccess.RetrieveTarget;
import edu.wustl.xipHost.iterator.TargetElement;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;

/**
 * @author Jaroslaw Krych
 *
 */

public class GridRetrieveNCIA implements Retrieve {
	String seriesInstanceUID;
	GridLocation gridLocation;
	File importLocation;
	NCIACoreServiceClient client;
	
	public GridRetrieveNCIA(String seriesInstanceUID, GridLocation gridLocation, File importLocation){
		this.seriesInstanceUID = seriesInstanceUID; 
		this.gridLocation = gridLocation;
		
		File inputDir;
		try {
			inputDir = File.createTempFile("DICOM-XIPHOST", null, importLocation);			
			File localLocation = new File(inputDir.getCanonicalPath());
			inputDir.delete();
			if (!localLocation.exists())
				localLocation.mkdirs();
			this.importLocation = localLocation;		
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
		
		//this.importLocation = importLocation;
		try {
			client = new NCIACoreServiceClient(gridLocation.getAddress());
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@Override
	public void setRetrieve(TargetElement targetElement, RetrieveTarget retrieveTarget) {
		// TODO Auto-generated method stub
		
	}
		
	public void run() {
		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		TransferServiceContextReference tscr;
		try {
			tscr = client.retrieveDicomDataBySeriesUID(seriesInstanceUID);
			tclient = new TransferServiceContextClient(tscr.getEndpointReference());
			istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
		if(istream == null){			
			return;
		}
		ZipInputStream zis = new ZipInputStream(istream);
        ZipEntryInputStream zeis = null;
        BufferedInputStream bis = null;
        int ii = 1;
        while(true) {
        	try {
        		zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {				
				System.out.println("IOException " + e);
			}
            String unzzipedFile = null;
			try {
				unzzipedFile = importLocation.getCanonicalPath();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.out.println(ii++ + " filename: " + zeis.getName());
            bis = new BufferedInputStream(zeis);
            byte[] data = new byte[8192];
            int bytesRead = 0;
            try {
            	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile + File.separator + zeis.getName()));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0)  {
					bos.write(data, 0, bytesRead);
				}
				bos.flush();
		        bos.close();
			} catch (IOException e) {
				System.out.println("IOException " + e);
			}
        }
        try {
			zis.close();
		} catch (IOException e) {
			System.out.println("IOException " + e);
		}
        try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();			
		}
		//fireResultsAvailable();
	}
	
	public List<File> getRetrievedFiles(){
		File[] fs = importLocation.listFiles();
		List<File> files = new ArrayList<File>();
		for(int i = 0; i < fs.length; i++){
			files.add(fs[i]);
		}
		return files;
	}
	
	
	void fireResultsAvailable(String targetElementID){
		RetrieveEvent event = new RetrieveEvent(targetElementID);         		        
		listener.retrieveResultsAvailable(event);
	}

	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;
	}	

	@Override
	public Map<String, ObjectLocator> getObjectLocators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRetrieveListener(RetrieveListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Map<Integer, Object> dicomCriteria,
			Map<String, Object> aimCriteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCriteria(Object criteria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImportDir(File importDir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRetrieveTarget(RetrieveTarget retrieveTarget) {
		// TODO Auto-generated method stub
		
	}
}
