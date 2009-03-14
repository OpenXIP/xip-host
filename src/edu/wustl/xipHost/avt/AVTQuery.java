package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.hibernate.Session;
import org.jdom.JDOMException;

import com.siemens.scr.avt.ad.annotation.AnnotationFactory;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.annotation.ImageAnnotationDescriptor;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.io.DicomIO;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.HibernateUtil;
import com.siemens.scr.avt.ad.util.ResourceLocator;

import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class AVTQuery implements Runnable{
	ADFacade adService;
	String studyInstanceUID;
	String seriesInstanceUID; 
	
	public AVTQuery(String studyInstanceUID, String seriesInstanceUID){
		this.studyInstanceUID = studyInstanceUID;
		this.seriesInstanceUID = seriesInstanceUID;
		adService = AVTFactory.getADServiceInstance();
		if(adService == null){
			System.out.println("Connection problem");
		}
	}
	
	SearchResult result;
	public void run() {
		//1. Search fro DICOM		
		
		//System.out.println(studyInstanceUID);
		//System.out.println(seriesInstanceUID);
		HashMap<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		dicomCriteria.put(Tag.PatientID, "1.3.6.1.4.1.9328.50.1.0022");
		
		//dicomCriteria.put(Tag.StudyInstanceUID, studyInstanceUID);
		//dicomCriteria.put(Tag.SeriesInstanceUID, seriesInstanceUID);		
		List<String> dicomUIDs = adService.findDicomObjs(dicomCriteria, null);						
		List<DicomObject> foundDICOMs = new ArrayList<DicomObject>();
		for(int i = 0; i < dicomUIDs.size(); i++){
			DicomObject loadedDcm = adService.getDicomObject(dicomUIDs.get(i));
			foundDICOMs.add(loadedDcm);
			/*try {
				DicomIO.dumpDicom2File(dicomUIDs.get(i), dicomUIDs.get(i) + ".dcm");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/			
		}		
		
		//2. Search for AIM
		List<String> annotationUIDs = adService.findAnnotations(dicomCriteria, null);
		List<ImageAnnotation> foundAnnotations = new ArrayList<ImageAnnotation>();		
		for(int i = 0; i < annotationUIDs.size(); i++){
			ImageAnnotation loadedAnnot = adService.getAnnotation(annotationUIDs.get(i));			
			foundAnnotations.add(loadedAnnot);			
		}		
		//System.out.println("<------------------------------------------------------------->" + foundDICOMs.get(0).getString(Tag.SOPInstanceUID));
		result = resolveToSearchResult(foundDICOMs, foundAnnotations);
		fireResultsAvailable(result);
		
		/*List<ImageAnnotationDescriptor> aimDescs = adService.listAnnotationsInSeries(seriesInstanceUID);		
		if(aimDescs == null){
			aimDescs = new ArrayList<ImageAnnotationDescriptor>();
		}
		fireResultsAvailable(aimDescs);		//returns array with empty string
		*/
	}
	
	SearchResult resolveToSearchResult(List<DicomObject> dicomObjects, List<ImageAnnotation> imageAnnotations){
		SearchResult resultAD = new SearchResult("DB2 AD AVT");
		Patient patientFromAD = null;
		Study studyFromAD = null;
		Series seriesFromAD = null;
		for(int i = 0 ; i < dicomObjects.size(); i++){
			DicomObject dcmObj = dicomObjects.get(i);
			String patientName = dcmObj.getString(Tag.PatientName);if(patientName == null){patientName = "";}
			String patientID = dcmObj.getString(Tag.PatientID);if(patientID == null){patientID = "";}
			String patientBirthDate = dcmObj.getString(Tag.PatientBirthDate);if(patientBirthDate == null){patientBirthDate = "";}
			if(resultAD.contains(patientID) == false){
				patientFromAD = new Patient(patientName, patientID, patientBirthDate);
				resultAD.addPatient(patientFromAD);
			}
			String studyDate = dcmObj.getString(Tag.StudyDate);if(studyDate == null){studyDate = "";}
			String studyID = dcmObj.getString(Tag.StudyID);if(studyID == null){studyID = "";}	
			String studyDesc = dcmObj.getString(Tag.StudyDescription);if(studyDesc == null){studyDesc = "";}
			String studyInstanceUID = dcmObj.getString(Tag.StudyInstanceUID);if(studyInstanceUID == null){studyInstanceUID = "";}
			//Create and add new study to resultAD only when it is not included in resultAD already
			if(patientFromAD.contains(studyInstanceUID) == false){
				studyFromAD = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				patientFromAD.addStudy(studyFromAD);
			}
			String seriesNumber = dcmObj.getString(Tag.SeriesNumber);if(seriesNumber == null){seriesNumber = "";}
			String modality = dcmObj.getString(Tag.Modality);if(modality == null){modality = "";}
			String seriesDesc = dcmObj.getString(Tag.SeriesDescription);if(seriesDesc == null){seriesDesc = "";}
			String seriesInstanceUID = dcmObj.getString(Tag.SeriesInstanceUID);if(seriesInstanceUID == null){seriesInstanceUID = "";}
			if(studyFromAD.contains(seriesInstanceUID) == false){
				seriesFromAD = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);	
				studyFromAD.addSeries(seriesFromAD);										
			}else{
				
			};
			String imageNumber = dcmObj.getString(Tag.ImageID);
			if(imageNumber == null || imageNumber == ""){imageNumber = dcmObj.getString(Tag.SOPInstanceUID);}
			if(imageNumber == null){imageNumber = "";}								
			Item image = new ImageItem(imageNumber);				
			seriesFromAD.addItem(image);
			for(int j = 0 ; j < imageAnnotations.size(); j++){
				ImageAnnotation annot = imageAnnotations.get(j);
				ImageAnnotationDescriptor annotDesc = annot.getDescriptor();				
				Session session = HibernateUtil.getSessionFactory().openSession();
				annot = (ImageAnnotation) session.merge(annot);
				Set<GeneralImage> refImages = annot.getReferencedImages();			   			
				Iterator<GeneralImage> iter = refImages.iterator();
				boolean seriesFound = false;
				while(iter.hasNext() && seriesFound == false){
					GeneralImage generalImage = iter.next();
					String refSeriesInstanceUID = generalImage.getSeries().getSeriesInstanceUID();
					if(refSeriesInstanceUID != null && refSeriesInstanceUID.equalsIgnoreCase(seriesInstanceUID)){
						String annotType = annotDesc.getImageAnnotationType();if(annotType == null){annotType = "";}
						Date annotDateTime = annotDesc.getDateTime();
						String strAnnotDateTime = "";
						if(annotDateTime != null){
							strAnnotDateTime = annotDateTime.toString();
						}						
						String annotAuthorName = annotDesc.getAuthorName();if(annotAuthorName == null){annotAuthorName = "";}
						String annotID = String.valueOf(annotDesc.getID());if(annotID == null){annotID = "";}
						Item aimItem = new AIMItem(annotType, strAnnotDateTime, annotAuthorName, annotID);
						seriesFromAD.addItem(aimItem);
						imageAnnotations.remove(j);
						seriesFound = true;												
					}
				}
				session.close();
				//j = imageAnnotations.size();
				
				break;
			}			
		}				
		return resultAD;
	}
	
	
	AVTListener listener;
    public void addAVTListener(AVTListener l) {        
        listener = l;          
    }
	void fireResultsAvailable(SearchResult searchResult){
		AVTSearchEvent event = new AVTSearchEvent(searchResult);         		
        listener.searchResultsAvailable(event);
	}	
}
