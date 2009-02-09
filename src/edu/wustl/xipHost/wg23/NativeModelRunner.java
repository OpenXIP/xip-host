/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
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
public class NativeModelRunner implements Runnable 
{	
	ObjectLocator objLoc;
	public NativeModelRunner(ObjectLocator objLocator){
		this.objLoc = objLocator;
	}
	
	NativeModelListener listener;
	public void addNativeModelListener(NativeModelListener l) {
		listener = l;		
	}
	
	void notifyNativeModelAvailable(Document doc, Uuid objUUID){		
		listener.nativeModelAvailable(doc, objUUID);		
	}
		
	String dicomFile;
	/**
	 * 
	 * @param dcmFile
	 * @return
	 * @throws MalformedURLException 
	 * @throws JAXBException
	 */
	public Document makeNativeModel(String strFileURL) {				
		try {
			if(strFileURL == null || strFileURL.trim().isEmpty()){return null;}			
			File dcmFile = new File(new URL(strFileURL).getFile());					
			dicomFile = dcmFile.getCanonicalPath();							
			JAXBContext jaxbContext = JAXBContext.newInstance("com.mycompany.dicom.metadata");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			AttributeList list = new AttributeList();
		    list.read(dicomFile, null,true,true);
			DicomDataSet obj = createDicomDataSetXML(list);
			StringWriter sw = new StringWriter();
			marshaller.marshal(obj, sw);
			String xmlString = sw.toString();
			//SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			SAXBuilder saxBuilder = new SAXBuilder();
			Reader stringReader = new StringReader(xmlString);
			Document jdomDoc = saxBuilder.build(stringReader);
			//marshaller.marshal(obj, new FileOutputStream("./src/com/mycompany/testNM2.xml"));
			/*DocumentResult docResult = new DocumentResult();
			marshaller.marshal(obj, docResult);
			org.dom4j.Document doc = docResult.getDocument();
			Document document = (Document) doc;
			System.out.println(document.toString());*/
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
		String strFileURL = objLoc.getUri();
		Document doc = makeNativeModel(strFileURL);
		notifyNativeModelAvailable(doc, objLoc.getUuid());
	}

	public static void main(String args[]) throws MalformedURLException{
		ObjectLocator objLoc = new ObjectLocator();
		Uuid uuid = new Uuid();
		uuid.setUuid("1");
		objLoc.setUuid(uuid);
		File file = new File("./src/com/mycompany/dcm_with_SQ.dcm");
		String str = file.toURI().toURL().toExternalForm();
		objLoc.setUri(str);
		NativeModelRunner nmRunner = new NativeModelRunner(objLoc);
		Thread t = new Thread(nmRunner);
		t.start();
		
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
				try {
					DicomInputStream dis = new DicomInputStream(new File(getDicomFile()));
					offset = dis.getByteOffsetOfStartOfData();
					
				} catch (IOException e1) {
					return null;
				}							
				bulkData.setOffset(offset);
				bulkData.setLength(new File(getDicomFile()).length() - offset);				
				try {
					bulkData.setPath(new File(getDicomFile()).toURI().toURL().toExternalForm());
				} catch (MalformedURLException e) {
					return null;
				}
				att.setBulkData(bulkData);
			}								
			nativeAtt.add(att);			
		}		    					
		return obj;
	}
	
	public String getDicomFile(){
		return dicomFile;
	}	
}
