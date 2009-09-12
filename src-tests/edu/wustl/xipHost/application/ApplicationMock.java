/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import edu.wustl.xipHost.wg23.ClientToApplication;

/**
 * It is used to overwright getClientToApplication method that will return ClientToApplicationMock
 * instead of ClientToApplication
 * @author Jaroslaw Krych
 *
 */
public class ApplicationMock extends Application {

	ClientToApplicationMock clientToApplication;
	public ApplicationMock(String name, File exePath, String vendor, String version, File iconFile) {
		super(name, exePath, vendor, version, iconFile);	
		clientToApplication = new ClientToApplicationMock();
		clientToApplication.setApplication(this);
	}
	
	public ClientToApplication getClientToApplication(){
		return clientToApplication;
	}

}
