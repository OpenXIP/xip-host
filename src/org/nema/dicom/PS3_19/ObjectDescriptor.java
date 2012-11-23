
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ObjectDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjectDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClassUID" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UID" minOccurs="0"/>
 *         &lt;element name="MimeType" type="{http://dicom.nema.org/PS3.19/HostService-20100825}MimeType" minOccurs="0"/>
 *         &lt;element name="Modality" type="{http://dicom.nema.org/PS3.19/HostService-20100825}Modality" minOccurs="0"/>
 *         &lt;element name="TransferSyntaxUID" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UID" minOccurs="0"/>
 *         &lt;element name="DescriptorUuid" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UUID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectDescriptor", propOrder = {
    "classUID",
    "mimeType",
    "modality",
    "transferSyntaxUID",
    "descriptorUuid"
})
public class ObjectDescriptor {

    @XmlElement(name = "ClassUID", nillable = true)
    protected UID classUID;
    @XmlElement(name = "MimeType", nillable = true)
    protected MimeType mimeType;
    @XmlElement(name = "Modality", nillable = true)
    protected Modality modality;
    @XmlElement(name = "TransferSyntaxUID", nillable = true)
    protected UID transferSyntaxUID;
    @XmlElement(name = "DescriptorUuid", nillable = true)
    protected UUID descriptorUuid;

    /**
     * Gets the value of the classUID property.
     * 
     * @return
     *     possible object is
     *     {@link UID }
     *     
     */
    public UID getClassUID() {
        return classUID;
    }

    /**
     * Sets the value of the classUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UID }
     *     
     */
    public void setClassUID(UID value) {
        this.classUID = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link MimeType }
     *     
     */
    public MimeType getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MimeType }
     *     
     */
    public void setMimeType(MimeType value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the modality property.
     * 
     * @return
     *     possible object is
     *     {@link Modality }
     *     
     */
    public Modality getModality() {
        return modality;
    }

    /**
     * Sets the value of the modality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Modality }
     *     
     */
    public void setModality(Modality value) {
        this.modality = value;
    }

    /**
     * Gets the value of the transferSyntaxUID property.
     * 
     * @return
     *     possible object is
     *     {@link UID }
     *     
     */
    public UID getTransferSyntaxUID() {
        return transferSyntaxUID;
    }

    /**
     * Sets the value of the transferSyntaxUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UID }
     *     
     */
    public void setTransferSyntaxUID(UID value) {
        this.transferSyntaxUID = value;
    }

    /**
     * Gets the value of the descriptorUuid property.
     * 
     * @return
     *     possible object is
     *     {@link UUID }
     *     
     */
    public UUID getDescriptorUuid() {
        return descriptorUuid;
    }

    /**
     * Sets the value of the descriptorUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link UUID }
     *     
     */
    public void setDescriptorUuid(UUID value) {
        this.descriptorUuid = value;
    }

}
