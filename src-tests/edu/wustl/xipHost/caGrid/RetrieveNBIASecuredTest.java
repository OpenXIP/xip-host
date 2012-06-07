/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.log4j.Logger;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.gsi.GlobusCredential; 
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.hostLogin.GridLogin;
import edu.wustl.xipHost.hostLogin.Login;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import java.io.FileFilter;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveNBIASecuredTest {
	final static Logger logger = Logger.getLogger(RetrieveNBIASecuredTest.class);
	String gridServiceUrl = "http://imaging.nci.nih.gov/wsrf/services/cagrid/NCIACoreService";
	String authUrl = "https://cagrid-auth.nci.nih.gov:8443/wsrf/services/cagrid/AuthenticationService";
	String dorianURL = "https://cagrid-dorian.nci.nih.gov:8443/wsrf/services/cagrid/Dorian";
	File inputDir;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		Login login = new GridLogin();
		String userName = "<JIRAusername>";
		String password = "<JIRApassword>";
		DcmFileFilter dcmFilter = new DcmFileFilter();
		login.login(userName, password);
		GlobusCredential globusCred = login.getGlobusCredential();
		boolean isConnectionSecured = login.isConnectionSecured();
		logger.debug("Acquired NBIA GlobusCredential. Connection secured = " + isConnectionSecured);
		if(!isConnectionSecured){
			fail("Unable to acquire NBIA GlobusCredential. Check username and password.");
			return;
		}
		NCIACoreServiceClient client = new NCIACoreServiceClient(gridServiceUrl, globusCred);
		client.setAnonymousPrefered(false);
		TransferServiceContextReference tscr = client.retrieveDicomDataBySeriesUID("1.3.6.1.4.1.9328.50.1.4718");
		TransferServiceContextClient tclient = new TransferServiceContextClient(tscr.getEndpointReference(), globusCred);
		InputStream istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor(), globusCred);
		ZipInputStream zis = new ZipInputStream(istream);
        ZipEntryInputStream zeis = null;
        BufferedInputStream bis = null;
        File importDir = new File("./test-content/NBIA5");
        if(!importDir.exists()){
			importDir.mkdirs();
		} else {
			File[] files = importDir.listFiles(dcmFilter);
			for(int j = 0; j < files.length; j++){
				File file = files[j];
				file.delete();
			}
		}
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
        }
        try {
			zis.close();
			tclient.destroy();
		} catch (IOException e) {
			logger.error(e, e);
		}
        File[] retrievedFiles = importDir.listFiles(dcmFilter);
        int numbOfRetreivedFiles = retrievedFiles.length;
        assertEquals("Number of retrieved files should be 2 but is. " + numbOfRetreivedFiles, numbOfRetreivedFiles, 2);
        //Assert file names. They should be equal to items' SeriesInstanceUIDs
        Map<String, File> mapRetrievedFiles = new HashMap<String, File>();
        for(int i = 0; i < numbOfRetreivedFiles; i++){
        	File file = retrievedFiles[i];
        	mapRetrievedFiles.put(file.getName(), file);
        }
        boolean retrievedFilesCorrect = false;
        if(mapRetrievedFiles.containsKey("1.3.6.1.4.1.9328.50.1.4716.dcm") && mapRetrievedFiles.containsKey("1.3.6.1.4.1.9328.50.1.4720.dcm")){
        	retrievedFilesCorrect = true;
        }
        assertTrue("Retrieved files are not as expected.", retrievedFilesCorrect);
	}

	class DcmFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			try {
				if(DicomUtil.mimeType(file).equalsIgnoreCase("application/dicom")){
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				return false;
			}
		}
	}
}
