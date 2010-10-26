/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.util.UUID;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Jaroslaw Krych
 *
 */
public class AppButton extends JButton{		
	public AppButton(String text, Icon icon){
		super(text, icon);
	}
	
	UUID appUUID;
	public void setApplicationUUID(UUID uuid){
		appUUID = uuid;
	}
	public UUID getApplicationUUID(){
		return appUUID;
	}
}