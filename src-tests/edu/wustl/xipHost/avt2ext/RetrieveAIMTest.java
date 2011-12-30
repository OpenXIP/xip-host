/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import static org.junit.Assert.assertEquals;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dcm4che2.data.Tag;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import edu.wustl.xipHost.avt2ext.AVTFactory;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveAIMTest {
	final static Logger logger = Logger.getLogger(RetrieveAIMTest.class);
	static ADFacade adService;	
	static SAXBuilder builder;
	Document document;
	static XMLOutputter outToXMLFile;
	static JAXBContext jc;
	static Unmarshaller u = null;
	
	@BeforeClass
	public static void setUp() throws Exception {
		adService = AVTFactory.getADServiceInstance();
		builder = new SAXBuilder();
		outToXMLFile = new XMLOutputter();
		try {
			jc = JAXBContext.newInstance( "gme.cacore_cacore._3_2.edu_northwestern_radiology" );
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {			
			logger.error(e, e);
		}
		DOMConfigurator.configure("log4j.xml");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		
	}
	
	//INFO: dataset must be preloaded prior to running this JUnit tests from AD_Preload_JUnit_Tests. Use PreloadDICOM and PreloadAIM utility classes in avt2ext to preload database.
	@Test
	public void testRetrieveAIM_1A(){
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		dicomCriteria.put(Tag.SeriesInstanceUID, "1.3.6.1.4.1.9328.50.1.10697");
		List<ImageAnnotation> imageAnnots = adService.retrieveAnnotations(dicomCriteria, null);
		ImageAnnotation annot = (ImageAnnotation)imageAnnots.get(0);
		//Assert image annotation uniqueIdentifier
		String strXML = annot.getAIM();
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(strXML));		
		try {
			JAXBElement<?> obj = (JAXBElement<?>)u.unmarshal(isr);
			gme.cacore_cacore._3_2.edu_northwestern_radiology.ImageAnnotation imageAnnotation = ((gme.cacore_cacore._3_2.edu_northwestern_radiology.ImageAnnotation)obj.getValue());
			String annotID = String.valueOf(imageAnnotation.getUniqueIdentifier());
			//System.out.println(annot.getAIM());
			assertEquals("Wrong annotation retrieved. Expected ID: " + "1.2.288.3.2205383238.1512.1207945935.1" +
					" but actual: " + annotID, annotID, "1.2.288.3.2205383238.1512.1207945935.1");
		} catch (JAXBException e) {
			logger.error(e, e);
		}							        
	}

}
