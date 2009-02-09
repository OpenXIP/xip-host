package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class AVTManagerImpl implements AVTManager {
	SAXBuilder builder = new SAXBuilder();
	Document document;
	Element root;	
	String serverName;
	String serverPort;
	String databaseName;
	String userName;
	String password;
	File file;
	
	public AVTManagerImpl(){
		file = new File("./config/DB2_ADConfig.xml");
		loadADConfigParameters(file);
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.avt.AVTManager#loadADConfigParameters(java.io.File)
	 */
	public boolean loadADConfigParameters(File xmlDB2_ADConfig) {
		try {
			document = builder.build(xmlDB2_ADConfig);
			root = document.getRootElement();
			serverName = root.getChild("serverName").getValue();
			serverPort = root.getChild("serverPort").getValue();
			databaseName = root.getChild("databaseName").getValue();
			userName = root.getChild("userName").getValue();
			password = root.getChild("password").getValue();
		} catch (JDOMException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.avt.AVTManager#getServerName()
	 */
	public String getServerName(){
		return serverName;
	}
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.avt.AVTManager#getServerPort()
	 */
	public String getServerPort(){
		return serverPort;
	}
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.avt.AVTManager#getDatabaseName()
	 */
	public String getDatabaseName(){
		return databaseName;
	}
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.avt.AVTManager#getUserName()
	 */
	public String getUserName(){
		return userName;
	}
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.avt.AVTManager#getPassword()
	 */
	public String getPassword(){
		return password;
	}	
}
