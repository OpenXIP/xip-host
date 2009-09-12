/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.util.Iterator;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;

/**
 * @author David Maffitt
 *
 */
public class AVTSearchCriteriaVerifier implements SearchCriteriaVerifier{
	private static final long serialVersionUID = 1L;
	
	public boolean verifyCriteria(AttributeList list){															
		if(list.size() > 0){			
			//Make sure values are not null. All of the attributes may be empty.
			DicomDictionary dictionary = AttributeList.getDictionary();
			Iterator iter = dictionary.getTagIterator();			
			while(iter.hasNext()){
				AttributeTag attTag  = (AttributeTag)iter.next();
				String attValue = Attribute.getSingleStringValueOrEmptyString(list, attTag);
				if(attValue.equalsIgnoreCase("null")){
					return false;
				}
			}			
		}
	    return true;
	}
	
}
