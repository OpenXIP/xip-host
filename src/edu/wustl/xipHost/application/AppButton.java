/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
