/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
		
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		fileChooser.setVisible(true);
		File[] files = fileChooser.getSelectedItems();
		if(files == null){
			return;
		}		
		ADFacade adService = AVTFactory.getADServiceInstance();
		List<DicomObject> dicomObjects = new ArrayList<DicomObject>();
		long time1 = System.currentTimeMillis();
		for(int i = 0; i < files.length; i++){
			try {												 
				DicomObject dicomObj = DicomParser.read(files[i]);				
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
		
		/*ADFacade adService = AVTFactory.getADServiceInstance();							 
		DicomObject dicomObj = DicomParser.read(new File("1.3.6.1.4.1.9328.50.1.10698.dcm"));									
		adService.saveDicom(dicomObj);*/
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			new PreloadDICOM();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		System.exit(0);
	}
}
