
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objects" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfUUID" minOccurs="0"/>
 *         &lt;element name="acceptableTransferSyntaxes" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfUID" minOccurs="0"/>
 *         &lt;element name="includeBulkData" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "objects",
    "acceptableTransferSyntaxes",
    "includeBulkData"
})
@XmlRootElement(name = "GetData")
public class GetData {

    @XmlElement(nillable = true)
    protected ArrayOfUUID objects;
    @XmlElement(nillable = true)
    protected ArrayOfUID acceptableTransferSyntaxes;
    protected Boolean includeBulkData;

    /**
     * Gets the value of the objects property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getObjects() {
        return objects;
    }

    /**
     * Sets the value of the objects property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setObjects(ArrayOfUUID value) {
        this.objects = value;
    }

    /**
     * Gets the value of the acceptableTransferSyntaxes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUID }
     *     
     */
    public ArrayOfUID getAcceptableTransferSyntaxes() {
        return acceptableTransferSyntaxes;
    }

    /**
     * Sets the value of the acceptableTransferSyntaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUID }
     *     
     */
    public void setAcceptableTransferSyntaxes(ArrayOfUID value) {
        this.acceptableTransferSyntaxes = value;
    }

    /**
     * Gets the value of the includeBulkData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeBulkData() {
        return includeBulkData;
    }

    /**
     * Sets the value of the includeBulkData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeBulkData(Boolean value) {
        this.includeBulkData = value;
    }

}
