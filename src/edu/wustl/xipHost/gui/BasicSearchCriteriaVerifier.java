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
 * @author Jaroslaw Krych
 *
 */
public class BasicSearchCriteriaVerifier implements SearchCriteriaVerifier {
	
	public boolean verifyCriteria(AttributeList list){															
		if(list.size() > 0){			
			//Make sure values are not null and at least one is non empty String (except SpecificCharacterSet)
			DicomDictionary dictionary = AttributeList.getDictionary();
			Iterator iter = dictionary.getTagIterator();			
			boolean isEmpty = true;
			while(iter.hasNext()){
				AttributeTag attTag  = (AttributeTag)iter.next();
				String strAtt = attTag.toString();									
				String attValue = Attribute.getSingleStringValueOrEmptyString(list, attTag);
				if(attValue.equalsIgnoreCase("null")){
					return false;
				} else if (isEmpty == true && !attValue.isEmpty() && !strAtt.equalsIgnoreCase("(0x0008,0x0005)")){	//(0x0008,0x0005) is attribute tag SpecificCharacterSet
					//System.out.println("JK: " + strAtt + " " + attValue);
					isEmpty = false;
				}
			}			
			if(isEmpty == false){
				return true;
			}else{
				return false;
			}
		} else {
			return false;
		}		
	}

}
