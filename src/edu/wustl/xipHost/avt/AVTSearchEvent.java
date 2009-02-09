/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.util.EventObject;
import com.siemens.scr.avt.ad.connector.api.ImageAnnotationDescriptor;

/**
 * @author Jaroslaw Krych
 *
 */
public class AVTSearchEvent extends EventObject {
	public AVTSearchEvent(ImageAnnotationDescriptor[] source){	
		super(source);
	}
}
