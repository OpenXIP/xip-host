/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.InputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.gsi.GlobusCredential; 
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.cagrid.ncia.util.SecureClientUtil;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveNBIASecuredTest {
	String gridServiceUrl = "https://imaging-secure-stage.nci.nih.gov:8443/wsrf/services/cagrid/NCIACoreService";
	String gridUsername = "wustl";
	String gridPassword = "erlERL3r()";
	String authUrl = "https://cagrid-auth-stage.nci.nih.gov:8443/wsrf/services/cagrid/AuthenticationService";
	String dorianURL = "https://cagrid-dorian-stage.nci.nih.gov:8443/wsrf/services/cagrid/Dorian";
	
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
		GlobusCredential globusCred = SecureClientUtil.generateGlobusCredential(gridUsername, gridPassword, dorianURL, authUrl);
		NCIACoreServiceClient client = new NCIACoreServiceClient(gridServiceUrl, globusCred);
		TransferServiceContextReference tscr = client.retrieveDicomDataBySeriesUID("1.3.6.1.4.1.9328.50.1.4718");
		TransferServiceContextClient tclient = new TransferServiceContextClient(tscr.getEndpointReference(), globusCred);
		InputStream istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor(), globusCred);
	}

}
