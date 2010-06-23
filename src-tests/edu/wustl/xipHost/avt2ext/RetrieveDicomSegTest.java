/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomOutputStream;
import com.siemens.scr.avt.ad.api.ADFacade;
import edu.wustl.xipHost.avt2ext.AVTFactory;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class RetrieveDicomSegTest extends TestCase {
	ADFacade adService;	
	/**
	 * @param name
	 */
	public RetrieveDicomSegTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		adService = AVTFactory.getADServiceInstance();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testRetrieveDicomSegTest_1A() throws IOException{
		String aimUID1 = "1.3.6.1.4.1.5962.99.1.1772131551.1017304176.1264492549343.1.0";
		String aimUID2 = "1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.3.0";
		List<DicomObject> dicomSegObjs = adService.retrieveSegmentationObjects(aimUID2);
		for(int i = 0; i < dicomSegObjs.size(); i++){
    		DicomObject dicom = dicomSegObjs.get(i);
    		String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
    		DicomObject segDicom = adService.getDicomObject(sopInstanceUID);
			String dirPath = new File("./test-content/AVT2EXT_SEG_Retrieve").getCanonicalPath() + File.separator;
    		String fileName = dirPath + sopInstanceUID + ".dcm";
			File file = new File(fileName);
			DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
			dout.writeDicomFile(segDicom);
			dout.close();
			System.out.println(file.length() / 1024);
    	}	
	}
}
