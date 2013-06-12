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

package edu.wustl.xipHost.localFileSystem;

import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.ObjectLocator;

import edu.wustl.xipHost.wg23.WG23DataModel;

/**
 * @author Jaroslaw Krych
 *
 */
public class WG23DataModelFileSystemImpl implements WG23DataModel{
	AvailableData availableData;
	ObjectLocator[] objLocators;
	public AvailableData getAvailableData() {		
		return availableData;
	}
	public ObjectLocator[] getObjectLocators() {
		return objLocators;
	}

	public void setAvailableData(AvailableData availableData){
		this.availableData = availableData;
	}
	
	public void setObjectLocators(ObjectLocator[] objLocators){
		this.objLocators = objLocators;
	}	
}
