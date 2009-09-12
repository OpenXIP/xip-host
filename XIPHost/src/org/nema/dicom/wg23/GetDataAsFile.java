
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDataAsFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataAsFile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uuids" type="{http://wg23.dicom.nema.org/}ArrayOfUUID"/>
 *         &lt;element name="includeBulkData" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDataAsFile", propOrder = {
    "uuids",
    "includeBulkData"
})
public class GetDataAsFile {

    @XmlElement(required = true)
    protected ArrayOfUUID uuids;
    protected boolean includeBulkData;

    /**
     * Gets the value of the uuids property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getUuids() {
        return uuids;
    }

    /**
     * Sets the value of the uuids property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setUuids(ArrayOfUUID value) {
        this.uuids = value;
    }

    /**
     * Gets the value of the includeBulkData property.
     * 
     */
    public boolean isIncludeBulkData() {
        return includeBulkData;
    }

    /**
     * Sets the value of the includeBulkData property.
     * 
     */
    public void setIncludeBulkData(boolean value) {
        this.includeBulkData = value;
    }

}
