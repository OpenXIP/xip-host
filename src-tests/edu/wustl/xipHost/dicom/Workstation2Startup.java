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
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Workstation2Startup {

	public static void main(String[] args) {
		DicomManager dicomMgr = DicomManagerFactory.getInstance();
		Properties workstation2Prop = new Properties();
		try {
			workstation2Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"));
			workstation2Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION2").getCanonicalPath());	
		} catch (FileNotFoundException e1) {
			System.err.println(e1.getMessage());
			System.exit(0);
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			System.exit(0);
		}
		dicomMgr.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest", workstation2Prop);
	}

}
