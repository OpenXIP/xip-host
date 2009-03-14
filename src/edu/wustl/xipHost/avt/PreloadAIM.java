/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.io.AnnotationBatchLoader;
import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class PreloadAIM extends AnnotationBatchLoader{

	public PreloadAIM(){
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		fileChooser.setVisible(true);
		File[] files = fileChooser.getSelectedItems();
		if(files == null){
			return;
		}						
		long time1 = System.currentTimeMillis();
		for(int i = 0; i < files.length; i++){
			try {												 
				ImageAnnotation t = this.readFromFile(files[i]);
				this.loadSingleObject(t);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}				
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
