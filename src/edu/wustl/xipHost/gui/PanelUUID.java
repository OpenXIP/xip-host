package edu.wustl.xipHost.gui;

import java.util.UUID;

import javax.swing.JPanel;

public class PanelUUID extends JPanel {
	UUID uuid;
	
	PanelUUID(){
		
	}
	
	PanelUUID(UUID uuid){
		this.uuid = uuid;
	}
	
	public UUID getUUID(){
    	return uuid;
    }
	
	public void setUUID(UUID uuid){
		this.uuid = uuid;
	}
}
