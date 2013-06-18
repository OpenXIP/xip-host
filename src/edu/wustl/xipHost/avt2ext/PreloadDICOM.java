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

package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.dcm4che2.data.DicomObject;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.io.DicomBatchLoader;
import com.siemens.scr.avt.ad.util.DicomParser;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class PreloadDICOM extends DicomBatchLoader {
	
	public PreloadDICOM() throws IOException{
		//Map<String, String> env = System.getenv();  
		//String adStore = env.get("AD_DICOM_STORE");
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		fileChooser.showOpenDialog(new JFrame());	
		File[] files = fileChooser.getSelectedFiles();
		if(files == null){
			return;
		}		
		ADFacade adService = AVTFactory.getADServiceInstance();
		List<DicomObject> dicomObjects = new ArrayList<DicomObject>();
		long time1 = System.currentTimeMillis();
		for(int i = 0; i < files.length; i++){
			try {												 
				File file = files[i];
				System.out.println("File path: " + file.getPath());
				DicomObject dicomObj = DicomParser.read(file);
				//System.out.println(dicomObj.getString(0x00080018));
				dicomObjects.add(dicomObj);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}				
		adService.saveDicoms(dicomObjects);
		long time2 = System.currentTimeMillis();
		System.out.println("*********** DICOM preload SUCCESSFUL *****************");
		System.out.println("Total load time: " + (time2 - time1)/1000+ " s");
	}

	public static void main(String[] args) {
		
		try {
			new PreloadDICOM();
		} catch (IOException e) {
			e.printStackTrace();
		}				
		System.exit(0);
	}
}
