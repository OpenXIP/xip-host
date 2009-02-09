/**
 * 
 */
package edu.wustl.xipHost.caGrid;

import edu.wustl.xipHost.dicom.DicomUtil;
import gme.cacore_cacore._3_2.edu_northwestern_radiology.DICOMImageReference;
import gme.cacore_cacore._3_2.edu_northwestern_radiology.ImageAnnotation;
import gme.cacore_cacore._3_2.edu_northwestern_radiology.ImageReference;
import gme.cacore_cacore._3_2.edu_northwestern_radiology.Series;
import gme.cacore_cacore._3_2.edu_northwestern_radiology.Study;
import gme.cacore_cacore._3_2.edu_northwestern_radiology.ImageAnnotation.ImageReferenceCollection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author jkrych01
 *
 */
public class AimHelper {	
	String studyInstanceUID;
	String seriesInstanceUID;
	
	public void unmarshall(File aimXmlFile){		
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance( "gme.cacore_cacore._3_2.edu_northwestern_radiology" );			
			Unmarshaller u = jc.createUnmarshaller();	        
			JAXBElement obj = (JAXBElement)u.unmarshal(aimXmlFile);							        
	        ImageReferenceCollection imageRefColl = ((ImageAnnotation)obj.getValue()).getImageReferenceCollection();
	        List<ImageReference> imageRef = imageRefColl.getImageReference();
	        for(int i = 0; i < imageRef.size(); i++){
	        	DICOMImageReference ref = (DICOMImageReference) imageRef.get(i);
	        	Study study = ref.getStudy().getStudy();	        		        		        	
	        	Series series = study.getSeries().getSeries();
	        	studyInstanceUID = study.getStudyInstanceUID();
	        	seriesInstanceUID = series.getSeriesInstanceUID();	        	
        	}
			 
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public String getStudyInstanceUID(){
		return studyInstanceUID;
	}
	public String getSeriesInstanceUID(){
		return seriesInstanceUID;
	}
	
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		AimHelper helper = new AimHelper();		
		File file = new File("./src-tests/edu/wustl/xipHost/caGrid/TmpXIP_Test/0e060446-d814-4c5c-a8e9-5c2f0c075373.xml");
		helper.unmarshall(file);
		System.out.println(DicomUtil.mimeType(file));
	}

}
