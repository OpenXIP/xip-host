package edu.wustl.xipHost.avt2ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;

import edu.wustl.xipHost.avt2ext.iterator.Criteria;
import edu.wustl.xipHost.dataModel.SearchResult;

public class AVTQueryStub extends AVTQuery {

	public AVTQueryStub(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, ADQueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		super(adDicomCriteria, adAimCriteria, target, previousSearchResult, queriedObject);
	}
	
	public void run(){
		switch (target) {
    	case PATIENT:
    		List<Patient> patients;
    		//List of patients would be given at least        		
    		break;
    	case STUDY:
    		List<GeneralStudy> studies;
    		try{
    			studies = adService.findStudiesByCriteria(adDicomCriteria, adAimCriteria);
    			//ignore adAimCriteria
    			//parse and get adDicomCriteria
    			
    			//result = AVTUtil.convertToSearchResult(studies, previousSearchResult, queriedObject);
    		} catch (Exception e){
    			studies = new ArrayList<GeneralStudy>();
    			logger.error(e, e);
    			notifyException(e.getMessage());
    			return;
    		}
    		break;
		case SERIES: 
			List<GeneralSeries> series;
			try{
				series = adService.findSeriesByCriteria(adDicomCriteria, adAimCriteria);
				result = AVTUtil.convertToSearchResult(series, previousSearchResult, queriedObject);
			} catch (Exception e){
				series = new ArrayList<GeneralSeries>();
    			logger.error(e, e);
    			notifyException(e.getMessage());
    			return;
			}
			break;
		case ITEM: 
			List<GeneralImage> images;
			List<String> annotations;
			try{
				images = adService.findImagesByCriteria(adDicomCriteria, adAimCriteria);				
				annotations = adService.findAnnotations(adDicomCriteria, adAimCriteria);
				Set<String> uniqueAnnots = new HashSet<String>(annotations);
				List<Object> listOfObjects = new ArrayList<Object>();
				listOfObjects.addAll(images);
				listOfObjects.addAll(uniqueAnnots);
				result = AVTUtil.convertToSearchResult(listOfObjects, previousSearchResult, queriedObject);
			} catch (Exception e){
				images = new ArrayList<GeneralImage>();
				annotations = new ArrayList<String>();
				logger.error(e, e);
    			notifyException(e.getMessage());
    			return;
			}				
			break;
		default: logger.warn("Unidentified ADQueryTarget");break;
	}		
	//Set original criteria on SearchResult.
	if(previousSearchResult == null){
		Criteria originalCriteria = new Criteria(adDicomCriteria, adAimCriteria);
		result.setOriginalCriteria(originalCriteria);
	}
	fireResultsAvailable(result);
	}

}
