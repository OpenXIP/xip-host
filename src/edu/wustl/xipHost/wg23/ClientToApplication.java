/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

import org.nema.dicom.PS3_19.ApplicationService20100825;
import org.nema.dicom.PS3_19.ArrayOfMimeType;
import org.nema.dicom.PS3_19.ArrayOfObjectLocator;
import org.nema.dicom.PS3_19.ArrayOfQueryResult;
import org.nema.dicom.PS3_19.ArrayOfQueryResultInfoSet;
import org.nema.dicom.PS3_19.ArrayOfUID;
import org.nema.dicom.PS3_19.ArrayOfUUID;
import org.nema.dicom.PS3_19.ArrayOfstring;
import org.nema.dicom.PS3_19.AvailableData;
import org.nema.dicom.PS3_19.IApplicationService20100825;
import org.nema.dicom.PS3_19.ModelSetDescriptor;
import org.nema.dicom.PS3_19.Rectangle;
import org.nema.dicom.PS3_19.State;
import org.nema.dicom.PS3_19.UID;

/**
 * <font  face="Tahoma" size="2">
 * Allows host to send requests to wg23 compatibile application via web services technology. <br></br>
 * @version	January 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class ClientToApplication implements IApplicationService20100825 {		
	ApplicationService20100825 service;  
	IApplicationService20100825 appProxy;

	public ClientToApplication() {}
	
	public ClientToApplication(URL appServiceURL) {
		URL wsdlLocation = null;
		try {
			wsdlLocation = new URL(appServiceURL.toExternalForm()+ "?wsdl");			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		service = new ApplicationService20100825(wsdlLocation, new QName("http://dicom.nema.org/PS3.19/ApplicationService-20100825", "ApplicationService-20100825"));
		appProxy = service.getApplicationServiceBinding();
		/*((BindingProvider)appProxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
        appServiceURL.toExternalForm() + "?wsdl");*/		
	}	

	@Override
	public State getState() {
		return appProxy.getState();
	}

	@Override
	public Boolean setState(State newState) {		
		//make sure State is not null
		return appProxy.setState(newState);
	}

	@Override
	public Boolean bringToFront(Rectangle location) {
		return appProxy.bringToFront(location);
	}
	public boolean bringToFront() {		
		return bringToFront(null);
	}	

	@Override
	public Boolean notifyDataAvailable(AvailableData data, Boolean lastData) {
		return appProxy.notifyDataAvailable(data, lastData);		 
	}
	
	@Override
	public ArrayOfObjectLocator getData(ArrayOfUUID objects,
			ArrayOfUID acceptableTransferSyntaxes, Boolean includeBulkData) {
		return appProxy.getData(objects, acceptableTransferSyntaxes, includeBulkData);
	}
	public ArrayOfObjectLocator getDataAsFile(ArrayOfUUID uuids, boolean includeBulkData) {
		// TODO fill in the null with the default transfer syntax
		return getData(uuids, null, includeBulkData);
	}	

	@Override
	public void releaseData(ArrayOfUUID objects) {
		appProxy.releaseData(objects);
	}

	@Override
	public ModelSetDescriptor getAsModels(ArrayOfUUID objects, UID classUID,
			ArrayOfMimeType supportedInfoSetTypes) {
		return appProxy.getAsModels(objects, classUID, supportedInfoSetTypes);
	}

	@Override
	public void releaseModels(ArrayOfUUID models) {
		appProxy.releaseModels(models);	
	}

	@Override
	public ArrayOfQueryResult queryModel(ArrayOfUUID models,
			ArrayOfstring xPaths) {
		return appProxy.queryModel(models, xPaths);
	}

	@Override
	public ArrayOfQueryResultInfoSet queryInfoSet(ArrayOfUUID models,
			ArrayOfstring xPaths) {
		return appProxy.queryInfoSet(models, xPaths);
	}
}
