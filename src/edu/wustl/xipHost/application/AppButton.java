/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.UUID;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Jaroslaw Krych
 *
 */
public class AppButton extends JButton{	
	Color xipColor = new Color(51, 51, 102);
	
	public AppButton(String text, Icon icon){
		super(text, icon);
		setBackground(xipColor);
		setPreferredSize();
	}
	
	UUID appUUID;
	public void setApplicationUUID(UUID uuid){
		appUUID = uuid;
	}
	public UUID getApplicationUUID(){
		return appUUID;
	}
	
	public void setAppButtonTextAndIcon(String text, Icon icon){
		setText(text);
		setIcon(icon);
		setPreferredSize();
	}
	
	
	void setPreferredSize(){
		FontMetrics metrics = getFontMetrics(getFont()); 
		int width = metrics.stringWidth( getText() );
		int height = metrics.getHeight();
		Dimension newDimension;
		if(this.getIcon() != null){
			newDimension =  new Dimension(width + 60, height + 10);
		} else {
			newDimension =  new Dimension(width + 40, height + 10);
		}
		setPreferredSize(newDimension);
		setBounds(new Rectangle(getLocation(), getPreferredSize()));
	}
}