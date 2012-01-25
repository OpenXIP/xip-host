package edu.wustl.xipHost.nbia;

import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.Retrieve;


public interface DataSource {
	public String getShortName();
	public Query getQuery();
	public Retrieve getRetrieve();
	public Util getUtil();
	
}