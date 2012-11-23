
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ObjectLocator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjectLocator">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Length" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="Offset" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="TransferSyntax" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UID" minOccurs="0"/>
 *         &lt;element name="URI" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="Locator" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UUID" minOccurs="0"/>
 *         &lt;element name="Source" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UUID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectLocator", propOrder = {
    "length",
    "offset",
    "transferSyntax",
    "uri",
    "locator",
    "source"
})
public class ObjectLocator {

    @XmlElement(name = "Length")
    protected Long length;
    @XmlElement(name = "Offset")
    protected Long offset;
    @XmlElement(name = "TransferSyntax", nillable = true)
    protected UID transferSyntax;
    @XmlElement(name = "URI", nillable = true)
    @XmlSchemaType(name = "anyURI")
    protected String uri;
    @XmlElement(name = "Locator", nillable = true)
    protected UUID locator;
    @XmlElement(name = "Source", nillable = true)
    protected UUID source;

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLength(Long value) {
        this.length = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOffset(Long value) {
        this.offset = value;
    }

    /**
     * Gets the value of the transferSyntax property.
     * 
     * @return
     *     possible object is
     *     {@link UID }
     *     
     */
    public UID getTransferSyntax() {
        return transferSyntax;
    }

    /**
     * Sets the value of the transferSyntax property.
     * 
     * @param value
     *     allowed object is
     *     {@link UID }
     *     
     */
    public void setTransferSyntax(UID value) {
        this.transferSyntax = value;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURI(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the locator property.
     * 
     * @return
     *     possible object is
     *     {@link UUID }
     *     
     */
    public UUID getLocator() {
        return locator;
    }

    /**
     * Sets the value of the locator property.
     * 
     * @param value
     *     allowed object is
     *     {@link UUID }
     *     
     */
    public void setLocator(UUID value) {
        this.locator = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link UUID }
     *     
     */
    public UUID getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link UUID }
     *     
     */
    public void setSource(UUID value) {
        this.source = value;
    }

}
