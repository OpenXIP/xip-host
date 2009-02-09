package edu.wustl.xipHost.avt;

import java.io.File;

public interface AVTManager {
	public boolean loadADConfigParameters(File xmlDB2_ADConfig);
	public String getServerName();
	public String getServerPort();
	public String getDatabaseName();
	public String getUserName();
	public String getPassword();
}