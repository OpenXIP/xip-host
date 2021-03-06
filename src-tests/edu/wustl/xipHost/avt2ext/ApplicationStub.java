/*
Copyright (c) 2013, Washington University in St.Louis
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

package edu.wustl.xipHost.avt2ext;

import org.nema.dicom.wg23.ArrayOfObjectLocator;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.iterator.IterationTarget;

/**
 * @author Jaroslaw Krych
 *
 */
public class ApplicationStub extends Application {

	/**
	 * 
	 */
	public ApplicationStub(String name, String exePath, String vendor, String version, String iconFile,
			String type, boolean requiresGUI, String wg23DataModelType, int concurrentInstances, IterationTarget iterationTarget) {
		super(name, exePath, vendor, version, iconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
	}

	ClientToApplicationStub clientToApplication = new ClientToApplicationStub();
	public ClientToApplicationStub getClientToApplication(){
		return clientToApplication;
	}
	
	public void setObjectLocators(ArrayOfObjectLocator arrayObjLocs){
		clientToApplication.setObjectLocators(arrayObjLocs);
	}
	
}
