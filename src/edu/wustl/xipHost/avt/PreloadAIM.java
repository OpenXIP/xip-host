/**
 * Copyright (c) 2009 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.hibernate.Session;

import com.siemens.scr.avt.ad.annotation.AnnotationFactory;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.io.AnnotationBatchLoader;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.HibernateUtil;

import edu.wustl.xipHost.localFileSystem.HostFileChooser;

/**
 * @author Jaroslaw Krych
 *
 */
public class PreloadAIM extends AnnotationBatchLoader{

	public PreloadAIM(){
		HostFileChooser fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
		Session session = HibernateUtil.getSessionFactory().openSession();
		fileChooser.setVisible(true);
		File[] files = fileChooser.getSelectedItems();
		if(files == null){
			return;
		}						
		long time1 = System.currentTimeMillis();
		for(int i = 0; i < files.length; i++){
			try {												 
				ImageAnnotation aim = this.readFromFile(files[i]);
				AnnotationFactory factory = AnnotationIO.getFactory();
				List<String> refs = factory.parseReferencedSOPInstanceUIDs(factory.parseDoc(files[i]));
				for(String imageUID : refs){
					GeneralImage image = Queries.findImage(imageUID, session);
					aim.getReferencedImages().add(image);
					image.getAnnotations().add(aim);
				}
				//this.loadSingleObject(aim);
				HibernateUtil.saveOrUpdate(aim, session);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		session.close();
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
