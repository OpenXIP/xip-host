package edu.wustl.xipHost.hostControl;

public class XindiceManagerFactory {
	private static XindiceManager xmgr = new XindiceManagerImpl();
	
	private XindiceManagerFactory(){}
	
	public static XindiceManager getInstance(){
		return xmgr;
	}	
}
