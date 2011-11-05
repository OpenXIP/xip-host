/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jdom.JDOMException;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.User;
import com.siemens.scr.avt.ad.io.AnnotationBatchLoader;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class PreloadAIM extends AnnotationBatchLoader{

	public PreloadAIM(){
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		
		fileChooser.showOpenDialog(new JFrame());
		File[] files = fileChooser.getSelectedFiles();
		if(files == null){
			return;
		}						
		ADFacade adService = AVTFactory.getADServiceInstance();
		List<ImageAnnotation> aimObjects = new ArrayList<ImageAnnotation>(); 
		long time1 = System.currentTimeMillis();		
		for(int i = 0; i < files.length; i++){
			ImageAnnotation annot;
			try {
				annot = AnnotationIO.loadAnnotationFromFile(files[i]);
				aimObjects.add(annot);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JDOMException e) {
				e.printStackTrace();
			}			
		}
		User user = new User();
		user.setUserName("Jarek");
		user.setRoleInt(1);
		String comment = "Preloading AD with AIM";
		
		adService.saveAnnotations(aimObjects, user, comment, null);	
		long time2 = System.currentTimeMillis();
		System.out.println("*********** AIM preload SUCCESSFUL *****************");
		System.out.println("Total load time: " + (time2 - time1)/1000+ " s");
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PreloadAIM();		
		System.exit(0);
	}

}
