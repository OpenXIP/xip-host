/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.pdq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import com.pixelmed.dicom.AttributeList;

import edu.wustl.xipHost.pdq.PDQLocation;

/**
 * @author Lawrence Tarbox, derived from code originally created by Jaroslaw Krych
 *
 */
public interface PDQManager {
	public abstract List<PDQPatientIDResponse> queryPatientIDs(AttributeList queryKeys, PDQLocation pdqSupplier);
	//public abstract XDSQueryResponseType queryDocuments(String [] patientID);
	public abstract boolean loadPDQLocations(File file) throws IOException, JDOMException;
	public abstract boolean addPDQLocation(PDQLocation pdqLocation);
	public abstract boolean modifyPDQLocation(PDQLocation oldPDQLocation, PDQLocation newPDQLocation);
	public abstract boolean removePDQLocation(PDQLocation pdqLocation);
	public abstract boolean storePDQLocations(List<PDQLocation> locations, File file) throws FileNotFoundException;
	public abstract List<PDQLocation> getPDQLocations();	
	public abstract boolean runStartupSequence();
}
