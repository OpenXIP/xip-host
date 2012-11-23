
package org.nema.dicom.PS3_19;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfMimeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfMimeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MimeType" type="{http://dicom.nema.org/PS3.19/HostService-20100825}MimeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfMimeType", propOrder = {
    "mimeType"
})
public class ArrayOfMimeType {

    @XmlElement(name = "MimeType", nillable = true)
    protected List<MimeType> mimeType;

    /**
     * Gets the value of the mimeType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mimeType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMimeType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MimeType }
     * 
     * 
     */
    public List<MimeType> getMimeType() {
        if (mimeType == null) {
            mimeType = new ArrayList<MimeType>();
        }
        return this.mimeType;
    }

}
