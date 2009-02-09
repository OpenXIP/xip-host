/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import org.nema.dicom.wg23.Application;
import org.nema.dicom.wg23.ApplicationService;
import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfQueryResult;
import org.nema.dicom.wg23.ArrayOfString;
import org.nema.dicom.wg23.ArrayOfUUID;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ModelSetDescriptor;
import org.nema.dicom.wg23.State;
import org.nema.dicom.wg23.Uid;

/**
 * <font  face="Tahoma" size="2">
 * Allows host to send requests to wg23 compatibile application via web services technology. <br></br>
 * @version	January 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class ClientToApplication implements Application {		
	ApplicationService service;  
	Application appProxy;

	public ClientToApplication() {}
	
	public ClientToApplication(URL appServiceURL) {
		URL wsdlLocation = null;
		try {
			wsdlLocation = new URL(appServiceURL.toExternalForm()+ "?wsdl");			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		service = new ApplicationService(wsdlLocation, new QName("http://wg23.dicom.nema.org/", "ApplicationService"));
		appProxy = service.getApplicationPort();
		/*((BindingProvider)appProxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
        appServiceURL.toExternalForm() + "?wsdl");*/		
	}	

	public boolean bringToFront() {		
		return appProxy.bringToFront();
	}	

	public ModelSetDescriptor getAsModels(ArrayOfUUID uuids, Uid classUID, Uid transferSyntaxUID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayOfObjectLocator getDataAsFile(ArrayOfUUID uuids, boolean includeBulkData) {		
		return appProxy.getDataAsFile(uuids, includeBulkData);
	}	

	public ArrayOfObjectLocator getDataAsSpecificTypeFile(ArrayOfUUID objectUUIDs, String mimeType, Uid transferSyntaxUID, boolean includeBulkData) {
		// TODO Auto-generated method stub
		return null;
	}	

	public boolean notifyDataAvailable(AvailableData availableData, boolean lastData) {
		return appProxy.notifyDataAvailable(availableData, lastData);		 
	}
	
	public ArrayOfQueryResult queryModel(ArrayOfUUID objUUIDs, ArrayOfString modelXpaths, boolean includeBulkDataPointers) {
		// TODO Auto-generated method stub
		return null;
	}	

	public boolean setState(State newState) {		
		//make sure State is not null
		return appProxy.setState(newState);
	}

	public State getState() {
		return appProxy.getState();
	}
}
