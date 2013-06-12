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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.util.HibernateUtil;

import edu.wustl.xipHost.avt2ext.AVTFactory;
import edu.wustl.xipHost.avt2ext.AVTStore;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreAIMToADTest extends TestCase {
	AVTStore avtStore;
	ADFacade adService = AVTFactory.getADServiceInstance();
	List<Object> toBeRemoved;
	int numThreads = 3;
	ExecutorService exeService = Executors.newFixedThreadPool(numThreads);
	
	protected void setUp() throws Exception {
		super.setUp();	
		toBeRemoved = new LinkedList<Object>();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		for (Object obj : toBeRemoved) {
			if(obj instanceof ImageAnnotation){
				removeAnnotation((ImageAnnotation) obj);
			}
		}
	}

	private static Logger logger = Logger.getLogger(StoreAIMToADTest.class);
	private static void removeAnnotation(ImageAnnotation annotation){
		Session session = HibernateUtil.getSessionFactory().openSession();
		int pk = annotation.getPK_ID();
		logger.debug("Removing annotation with PK_ID = " + pk);
		Object persistentObj = session.get(ImageAnnotation.class, pk);
		HibernateUtil.delete(persistentObj, session);
		session.close();
	}
	
	
	//AVTStore 1A - basic flow. Perfect condistions. AIM objects to store are valid XML strings.
	//Expected result: boolean true
	public void testStoreAimToAD_1A() throws IOException, JDOMException, InterruptedException, SAXException{
		File[] aims = new File[1];		
		File aim1 = new File("./test-content/AIM_2/Vasari-TCGA6330140190470283886.xml");		
		aims[0] = aim1;			
		ImageAnnotation annotation = AnnotationIO.loadAnnotationFromFile(aim1);
		String annotationUID = annotation.getDescriptor().getUID();
		toBeRemoved.add(aim1);
		avtStore = new AVTStore(aims);
		Thread t = new Thread(avtStore);
		t.start();
		t.join();	
		ImageAnnotation loadedAnnotation = adService.getAnnotation(annotationUID);
		assertTrue(assertAnnotationEquals(annotation, loadedAnnotation));		
	}
		
	static boolean assertAnnotationEquals(ImageAnnotation expected, ImageAnnotation actual) throws SAXException, IOException{
		// Ignoring PK, Attachment, and referenced images
		if(expected == actual){
			return true;
		}
		if(expected != null){
			if(actual != null){
				assertEquals(expected.getDescriptor(), actual.getDescriptor());
				System.out.println(expected.getAIM());				
				//assertXMLEqual(expected.getAIM(), actual.getAIM());
				return true;
			} else{
				fail("expected = " + expected + " while actual is null!");
				return false;
			}
		}
		return false;
	}
	
}
