/**
 * Copyright (c) 2012 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import org.openhealthtools.ihe.atna.auditor.IHEAuditor;
import edu.wustl.xipHost.dicom.DicomManager;
import edu.wustl.xipHost.dicom.DicomManagerFactory;
import edu.wustl.xipHost.hostControl.HostConfigurator;

/**
 * @author Jarek Krych
 *
 */
public class StartupRunner implements Runnable {
	DicomManager dicomMgr;
	Properties workstation1Prop = new Properties();
	/**
	 * 
	 */
	public StartupRunner() {
		dicomMgr = DicomManagerFactory.getInstance();
		
		try {
			workstation1Prop.load(new FileInputStream("./pixelmed-server-hsqldb/workstation1.properties"));
		} catch (FileNotFoundException e1) {
			//logger.error(e1, e1);
			
		} catch (IOException e1) {
			//logger.error(e1, e1);
			
		}
	}

	@Override
	public void run() {
		dicomMgr.runDicomStartupSequence("./pixelmed-server-hsqldb/server", workstation1Prop);
		System.setProperty("javax.net.ssl.keyStore","/MESA/certificates/XIPkeystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword","caBIG2011");
		System.setProperty("javax.net.ssl.trustStore","/MESA/runtime/certs-ca-signed/2011_CA_Cert.jks");
		System.setProperty("javax.net.ssl.trustStorePassword","connectathon");
		System.setProperty("https.ciphersuites","TLS_RSA_WITH_AES_128_CBC_SHA");
		
		IHEAuditor.getAuditor().getConfig().setAuditorEnabled(false);
		HostConfigurator hostConfigurator = HostConfigurator.getHostConfigurator();
		String auditRepositoryURL = hostConfigurator.getAuditRepositoryURL();
		String aeTitle = hostConfigurator.getAETitle();
		String userName = hostConfigurator.getUser();
		if (auditRepositoryURL != ""){
			try {
				IHEAuditor.getAuditor().getConfig().setAuditRepositoryUri(new URI(auditRepositoryURL));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    System.out.println("URI to auditor improperly formed");
			}
			IHEAuditor.getAuditor().getConfig().setAuditSourceId(aeTitle);
			IHEAuditor.getAuditor().getConfig().setAuditorEnabled(true);
			// TODO figure out what should go here, or get from configuration
			IHEAuditor.getAuditor().getConfig().setAuditEnterpriseSiteId("IHE ERL");
			IHEAuditor.getAuditor().getConfig().setHumanRequestor("ltarbox");
			IHEAuditor.getAuditor().getConfig().setSystemUserId(userName);
			IHEAuditor.getAuditor().getConfig().setSystemUserName("Wash. Univ.");
		}
		
		StartupEvent event = new StartupEvent(this);
		listener.startupTasksCompleted(event);
		
	}
	
	StartupListener listener;
	public void addDicomServerStartupListener(StartupListener listener) {
		this.listener = listener;
	}
	
	

}
