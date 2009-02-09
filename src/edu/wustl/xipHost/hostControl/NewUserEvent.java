package edu.wustl.xipHost.hostControl;

import java.util.EventObject;

import edu.wustl.xipHost.gui.LoginDialog;

public class NewUserEvent extends EventObject{
	public NewUserEvent(LoginDialog source){	
		super(source);
}
}
