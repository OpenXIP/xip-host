package edu.wustl.xipHost.avt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
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
		List<DicomObject> foundDICOMs = adService.retrieveDicomObjsWithoutPixel(adDicomCriteria, null);		
		result = resolveToSearchResult(foundDICOMs);			
		fireResultsAvailable(result);
	}
	
	SearchResult resultAD;
	public void run2(){
		resultAD = new SearchResult("DB2 AD AVT");
		Patient patientFromAD = null;
		Study studyFromAD = null;
		Series seriesFromAD = null;
		
		List<com.siemens.scr.avt.ad.dicom.Patient> patients = adService.findPatientByCriteria(adDicomCriteria, null);
		for(com.siemens.scr.avt.ad.dicom.Patient patient : patients){
			String patientName = patient.getPatientName();if(patientName == null){patientName = "";}
			String patientID = patient.getPatientID();if(patientID == null){patientID = "";}
			//String patientBirthDate = patient.getPatientBirthDate().toString();if(patientBirthDate == null){patientBirthDate = "";}
			String patientBirthDate = "";
			if(resultAD.contains(patientID) == false){
				patientFromAD = new Patient(patientName, patientID, patientBirthDate);
				resultAD.addPatient(patientFromAD);
			}
			Set<GeneralStudy> studies = adService.retrieveStudiesOf(patient);
			for(GeneralStudy study : studies){
				String studyDate = study.getStudyDateTime().toString();if(studyDate == null){studyDate = "";}
				String studyID = study.getStudyID();if(studyID == null){studyID = "";}	
				String studyDesc = study.getStudyDescription();if(studyDesc == null){studyDesc = "";}
				String studyInstanceUID = study.getStudyInstanceUID();if(studyInstanceUID == null){studyInstanceUID = "";}
				//Create and add new study to resultAD only when it is not included in resultAD already
				if(patientFromAD.contains(studyInstanceUID) == false){
					studyFromAD = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
					patientFromAD.addStudy(studyFromAD);
				}
				Set<GeneralSeries> series = adService.retrieveSeriesOf(study);
				for(GeneralSeries serie : series){
					String seriesNumber = serie.getSeriesNumber();if(seriesNumber == null){seriesNumber = "";}
					String modality = serie.getModality();if(modality == null){modality = "";}
					String seriesDesc = serie.getSeriesDescription();if(seriesDesc == null){seriesDesc = "";}
					String seriesInstanceUID = serie.getSeriesInstanceUID();if(seriesInstanceUID == null){seriesInstanceUID = "";}
					if(studyFromAD.contains(seriesInstanceUID) == false){
						seriesFromAD = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);	
						studyFromAD.addSeries(seriesFromAD);										
					}else{
						
					};
					Set<GeneralImage> images = adService.retrieveImagesOf(serie);					
					for(GeneralImage image : images){
						String imageNumber = image.getInstanceNumber();
						if(imageNumber == null || imageNumber == ""){imageNumber = image.getSOPInstanceUID();}
						if(imageNumber == null){imageNumber = "";}								
						Item imageResult = new ImageItem(imageNumber);				
						seriesFromAD.addItem(imageResult);
						/*Set<ImageAnnotation> annotations = adService.retrieveAnnotationsOf(image);
						for(ImageAnnotation annotation : annotations){
							String annotType = annotation.getDescriptor().getImageAnnotationType();if(annotType == null){annotType = "";}
							Date annotDateTime = annotation.getDescriptor().getDateTime();
							String strAnnotDateTime = "";
							if(annotDateTime != null){
								strAnnotDateTime = annotDateTime.toString();
							}						
							String annotAuthorName = annotation.getDescriptor().getAuthorName();if(annotAuthorName == null){annotAuthorName = "";}
							String annotID = String.valueOf(annotation.getDescriptor().getID());if(annotID == null){annotID = "";}
							Item aimItem = new AIMItem(annotType, strAnnotDateTime, annotAuthorName, annotID);
							seriesFromAD.addItem(aimItem);
						}*/
					}
					HashMap<Integer, Object> adCriteria = new HashMap<Integer, Object>();
					if(!seriesInstanceUID.isEmpty()){adCriteria.put(Tag.SeriesInstanceUID, seriesInstanceUID);}
					List<ImageAnnotation> foundAnnotations = adService.retrieveAnnotations(adCriteria, null);
					for(ImageAnnotation annotation : foundAnnotations){						
						String annotType = annotation.getDescriptor().getImageAnnotationType();if(annotType == null){annotType = "";}
						Date annotDateTime = annotation.getDescriptor().getDateTime();
						String strAnnotDateTime = "";
						if(annotDateTime != null){
							strAnnotDateTime = annotDateTime.toString();
						}						
						String annotAuthorName = annotation.getDescriptor().getAuthorName();if(annotAuthorName == null){annotAuthorName = "";}
						String annotUID = annotation.getDescriptor().getUID();if(annotUID == null){annotUID = "";}
						Item aimItem = new AIMItem(annotType, strAnnotDateTime, annotAuthorName, annotUID);
						seriesFromAD.addItem(aimItem);
					}
				}
			}
		}
		fireResultsAvailable(resultAD);
	}
		
	SearchResult resolveToSearchResult(List<DicomObject> dicomObjects){
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
		}
		//Sort items in each series before returning search result
		List<Patient> patients = resultAD.getPatients();
		for(Patient patient : patients){
			List<Study> studies = patient.getStudies();
			for(Study study : studies){
				List<Series> series = study.getSeries();
				for(Series serie : series){
					HashMap<Integer, Object> adCriteria = new HashMap<Integer, Object>();
					String seriesInstanceUID = serie.getSeriesInstanceUID();
					if(!seriesInstanceUID.isEmpty()){adCriteria.put(Tag.SeriesInstanceUID, seriesInstanceUID);}
					List<ImageAnnotation> foundAnnotations = adService.retrieveAnnotations(adCriteria, null);
					for(ImageAnnotation annotation : foundAnnotations){						
						String annotType = annotation.getDescriptor().getImageAnnotationType();if(annotType == null){annotType = "";}
						Date annotDateTime = annotation.getDescriptor().getDateTime();
						String strAnnotDateTime = "";
						if(annotDateTime != null){
							strAnnotDateTime = annotDateTime.toString();
						}						
						String annotAuthorName = annotation.getDescriptor().getAuthorName();if(annotAuthorName == null){annotAuthorName = "";}
						String annotUID = annotation.getDescriptor().getUID();if(annotUID == null){annotUID = "";}
						Item aimItem = new AIMItem(annotType, strAnnotDateTime, annotAuthorName, annotUID);
						//TODO
						//add getAttachements() and then render them on a JTree with AIM objects
						serie.addItem(aimItem);
					}
					serie.sort();					
				}
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
