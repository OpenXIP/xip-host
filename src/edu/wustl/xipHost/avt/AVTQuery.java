package edu.wustl.xipHost.avt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.hibernate.Session;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.annotation.ImageAnnotationDescriptor;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.util.HibernateUtil;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class AVTQuery implements Runnable{
	ADFacade adService;	
	HashMap<Integer, Object> adDicomCriteria;
	public AVTQuery(HashMap<Integer, Object> adDicomCriteria){
		this.adDicomCriteria = adDicomCriteria;
		adService = AVTFactory.getADServiceInstance();
		if(adService == null){
			System.out.println("Connection problem");
		}
	}
	
	SearchResult result;
	public void run() {
		//1. Search fro DICOM										
		List<DicomObject> foundDICOMs = adService.retrieveDicomObjsWithoutPixel(adDicomCriteria, null);		
		//2. Search for AIM		
		List<ImageAnnotation> foundAnnotations = adService.retrieveAnnotations(adDicomCriteria, null);						
		result = resolveToSearchResult(foundDICOMs, foundAnnotations);
		fireResultsAvailable(result);
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
