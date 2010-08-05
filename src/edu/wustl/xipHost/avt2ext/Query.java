package edu.wustl.xipHost.avt2ext;

import java.util.Map;
import edu.wustl.xipHost.dataModel.SearchResult;

public interface Query {
	public void run();
	public void addAVTListener(AVTListener l);
	public void setAVTQuery(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, ADQueryTarget target, SearchResult previousSearchResult, Object queriedObject);
}