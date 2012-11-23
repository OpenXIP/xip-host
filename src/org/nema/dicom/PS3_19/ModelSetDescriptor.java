
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ModelSetDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ModelSetDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FailedSourceObjects" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfUUID" minOccurs="0"/>
 *         &lt;element name="InfosetType" type="{http://dicom.nema.org/PS3.19/HostService-20100825}MimeType" minOccurs="0"/>
 *         &lt;element name="Models" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfUUID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModelSetDescriptor", propOrder = {
    "failedSourceObjects",
    "infosetType",
    "models"
})
public class ModelSetDescriptor {

    @XmlElement(name = "FailedSourceObjects", nillable = true)
    protected ArrayOfUUID failedSourceObjects;
    @XmlElement(name = "InfosetType", nillable = true)
    protected MimeType infosetType;
    @XmlElement(name = "Models", nillable = true)
    protected ArrayOfUUID models;

    /**
     * Gets the value of the failedSourceObjects property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getFailedSourceObjects() {
        return failedSourceObjects;
    }

    /**
     * Sets the value of the failedSourceObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setFailedSourceObjects(ArrayOfUUID value) {
        this.failedSourceObjects = value;
    }

    /**
     * Gets the value of the infosetType property.
     * 
     * @return
     *     possible object is
     *     {@link MimeType }
     *     
     */
    public MimeType getInfosetType() {
        return infosetType;
    }

    /**
     * Sets the value of the infosetType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MimeType }
     *     
     */
    public void setInfosetType(MimeType value) {
        this.infosetType = value;
    }

    /**
     * Gets the value of the models property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getModels() {
        return models;
    }

    /**
     * Sets the value of the models property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setModels(ArrayOfUUID value) {
        this.models = value;
    }

}
