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

package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class PreloadDICOMtoDICOMDataSource {

	public static void main(String[] args) {
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		JFrame frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fileChooser.showOpenDialog(frame);	
		File[] files = fileChooser.getSelectedFiles();
		if(files == null){
			return;
		}		
		PacsLocation loc = new PacsLocation("127.0.0.1", 3001, "WORKSTATION1", "XIPHost embedded database");
		//PacsLocation loc = new PacsLocation("127.0.0.1", 3002, "WORKSTATION2", "XIPHost test database");
		DicomManager dicomMgr = DicomManagerFactory.getInstance();
		Properties workstation1Prop = new Properties();
		try {
			workstation1Prop.load(new FileInputStream("./pixelmed-server-hsqldb/workstation1.properties"));
			//workstation1Prop.load(new FileInputStream("./src-tests/edu/wustl/xipHost/dicom/server/workstation2.properties"));
			workstation1Prop.setProperty("Application.SavedImagesFolderName", new File("./dicom-dataset-demo").getCanonicalPath());
			//workstation1Prop.setProperty("Application.SavedImagesFolderName", new File("./test-content/WORKSTATION2").getCanonicalPath());	
		} catch (FileNotFoundException e1) {
			System.err.println(e1.getMessage());
			System.exit(0);
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			System.exit(0);
		}
		dicomMgr.runDicomStartupSequence("./pixelmed-server-hsqldb/server", workstation1Prop);
		//dicomMgr.runDicomStartupSequence("./src-tests/edu/wustl/xipHost/dicom/server/serverTest", workstation1Prop);
		dicomMgr.submit(files, loc);
		dicomMgr.runDicomShutDownSequence("jdbc:hsqldb:./pixelmed-server-hsqldb/hsqldb/data/ws1db", "sa", "");
		//dicomMgr.runDicomShutDownSequence("jdbc:hsqldb:./src-tests/edu/wustl/xipHost/dicom/server/hsqldb/data/ws2db", "sa", "");
		System.exit(0);
	}
}
