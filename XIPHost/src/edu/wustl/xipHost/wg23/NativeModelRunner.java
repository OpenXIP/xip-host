/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.axis.types.HexBinary;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Uuid;
import com.mycompany.dicom.metadata.BulkData;
import com.mycompany.dicom.metadata.DicomAttribute;
import com.mycompany.dicom.metadata.DicomDataSet;
import com.mycompany.dicom.metadata.ObjectFactory;
import com.mycompany.dicom.metadata.Value;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.SequenceItem;
import com.pixelmed.utils.HexDump;

/**
 * @author Jaroslaw Krych
 *
 */
public class NativeModelRunner implements Runnable {	
	InputStream inputStream;
	DicomInputStream dicomInputStream;	
	ObjectLocator objLoc;
	
	public NativeModelRunner(ObjectLocator objLocator) {
		if(objLocator == null){throw new IllegalArgumentException("Null ObjectLocator.");}
		this.objLoc = objLocator;		
		try {
			File file = new File(new URI(objLoc.getUri()));			
			inputStream = new FileInputStream(file);
			dicomInputStream = new DicomInputStream(inputStream);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Invalid ObjectLocator.");
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid ObjectLocator.");
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid ObjectLocator.");
		}											
	}
		
	public NativeModelRunner(InputStream inputStream) {
		if(inputStream == null){throw new IllegalArgumentException("Null InputStream.");}
		this.inputStream = inputStream;						
		try {
			dicomInputStream = new DicomInputStream(inputStream);
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid InputStream.");
		}										 
	}
	
	NativeModelListener listener;
	public void addNativeModelListener(NativeModelListener l) {
		listener = l;		
	}
	
	void notifyNativeModelAvailable(Document doc, Uuid objUUID){		
		listener.nativeModelAvailable(doc, objUUID);		
	}
	
	void notifyNativeModelAvailable(String xmlNativeModel){
		listener.nativeModelAvailable(xmlNativeModel);
	}
	
	
	/**
	 * @return
	 * @throws MalformedURLException 
	 * @throws JAXBException
	 */
	private Document makeDOMNativeModel(DicomInputStream dicomInputStream) {				
		try {							
			JAXBContext jaxbContext = JAXBContext.newInstance("com.mycompany.dicom.metadata");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			AttributeList list = new AttributeList();
		    list.read(dicomInputStream);
			DicomDataSet obj = createDicomDataSetXML(list);
			StringWriter sw = new StringWriter();
			marshaller.marshal(obj, sw);
			String xmlString = sw.toString();		
			SAXBuilder saxBuilder = new SAXBuilder();
			Reader stringReader = new StringReader(xmlString);
			Document jdomDoc = saxBuilder.build(stringReader);
			return jdomDoc;
		} catch (JAXBException e) {			
			return null;		
		} catch (IOException e) {
			return null;
		} catch (DicomException e) {
			return null;
		} catch (JDOMException e) {
			return null;
		}			
	}
	
	public String makeXMLNativeModel() {
		return makeXMLNativeModel(dicomInputStream);
	}
	
	public Document makeDOMNativeModel() {
		return makeDOMNativeModel(dicomInputStream);
	}
	
	private String makeXMLNativeModel(DicomInputStream dicomInputStream) {
		try {
			if(dicomInputStream == null){return null;}															
			JAXBContext jaxbContext = JAXBContext.newInstance("com.mycompany.dicom.metadata");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			AttributeList list = new AttributeList();		    
			list.read(dicomInputStream);
			DicomDataSet obj = createDicomDataSetXML(list);
			StringWriter sw = new StringWriter();
			marshaller.marshal(obj, sw);
			String xmlString = sw.toString();			
			return xmlString;
		} catch (JAXBException e) {			
			return null;		
		} catch (IOException e) {
			return null;
		} catch (DicomException e) {
			return null;
		}			
	}
	
	String makeElementNameFromHexadecimalGroupElementValues(AttributeTag tag) {
		StringBuffer str = new StringBuffer();
		str.append("HEX");		// XML element names not allowed to start with a number
		String groupString = Integer.toHexString(tag.getGroup());
		for (int i=groupString.length(); i<4; ++i) str.append("0");
		str.append(groupString);
		String elementString = Integer.toHexString(tag.getElement());
		for (int i=elementString.length(); i<4; ++i) str.append("0");
		str.append(elementString);
		return str.toString();
	}
	
	public void run() {		
		if(objLoc != null){			
			Document doc = makeDOMNativeModel(dicomInputStream);
			notifyNativeModelAvailable(doc, objLoc.getUuid());
		}else if (objLoc == null){
			String xml = makeXMLNativeModel(dicomInputStream);
			notifyNativeModelAvailable(xml);
		}		
	}	
	
	ObjectFactory factory = new ObjectFactory();
	DicomDictionary dictionary = AttributeList.getDictionary();
	/**
	 * 
	 * @param list
	 * @return
	 */
	DicomDataSet createDicomDataSetXML(AttributeList list){
		DicomDataSet obj = factory.createDicomDataSet();												    
	    Iterator i = list.values().iterator();			
		List<DicomAttribute> nativeAtt = obj.getDicomAttribute();			
		BulkData bulkData = factory.createBulkData();
		
		while (i.hasNext()) {				
			DicomAttribute att = factory.createDicomAttribute();				
			Attribute attribute = (Attribute)i.next();
			AttributeTag tag = attribute.getTag();				
			String strGroup = HexDump.shortToPaddedHexString(tag.getGroup());
			String strElement = HexDump.shortToPaddedHexString(tag.getElement());
			String strTag = strGroup + strElement;				
			String attName = dictionary.getNameFromTag(tag);
			if (attName == null) {
				attName = makeElementNameFromHexadecimalGroupElementValues(tag);		
			}
			att.setKeyword(attName);
			att.setTag(HexBinary.decode(strTag));
			String vr = attribute.getVRAsString();			
			att.setVr(vr);
			att.setPrivateCreator("");					
			List<Value> values = att.getValue();
			//List<DicomDataSet> sqDicomDataSetList = new ArrayList<DicomDataSet>();
			if(vr.equalsIgnoreCase("SQ")){			
				SequenceAttribute sq = (SequenceAttribute)attribute;										
				int size = sq.getNumberOfItems();
				AttributeList attList = null;				
				int index = 1;
				for(int j = 0; j < size; j++){
					SequenceItem item = sq.getItem(j);
					attList = item.getAttributeList();					
					DicomDataSet sqDicomDataSet = createDicomDataSetXML(attList);
					//sqDicomDataSetList.add(sqDicomDataSet);
					Value value = factory.createValue();				
					value.setNumber(BigInteger.valueOf(index));
					List<Object> content = value.getContent();
					content.add(sqDicomDataSet);
					values.add(value);	
					index++;
				}				
			}else{
				String str = attribute.getDelimitedStringValuesOrEmptyString();
				StringTokenizer st = new StringTokenizer(str, "\\");						
				int index = 1;				
				while (st.hasMoreTokens()) {					
					Value value = factory.createValue();				
					value.setNumber(BigInteger.valueOf(index));
					List<Object> content = value.getContent();				
					content.add(st.nextToken());				
					values.add(value);						
					index++;						
				}
			}																
			if(strTag.equalsIgnoreCase("7fe00010")){					
				Long offset = null;
				offset = dicomInputStream.getByteOffsetOfStartOfData();							
				bulkData.setOffset(offset);
				Long length = attribute.getVL();							
				bulkData.setLength(length);
				if(objLoc != null){
					bulkData.setPath(objLoc.getUri());
				}else{
					bulkData.setPath("");
				}
				att.setBulkData(bulkData);
			}								
			nativeAtt.add(att);			
		}		    					
		return obj;
	}
}
