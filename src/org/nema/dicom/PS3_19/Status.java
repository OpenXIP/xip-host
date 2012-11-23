
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Status complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Status">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StatusType" type="{http://dicom.nema.org/PS3.19/HostService-20100825}StatusType" minOccurs="0"/>
 *         &lt;element name="CodeValue" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="CodingSchemeDesignator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CodeMeaning" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContextIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MappingResource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContextGroupVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContextGroupExtensionFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContextGroupLocalVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContextGroupExtensionCreatorUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Status", propOrder = {
    "statusType",
    "codeValue",
    "codingSchemeDesignator",
    "codeMeaning",
    "contextIdentifier",
    "mappingResource",
    "contextGroupVersion",
    "contextGroupExtensionFlag",
    "contextGroupLocalVersion",
    "contextGroupExtensionCreatorUID"
})
public class Status {

    @XmlElement(name = "StatusType")
    protected StatusType statusType;
    @XmlElement(name = "CodeValue")
    protected Integer codeValue;
    @XmlElement(name = "CodingSchemeDesignator", nillable = true)
    protected String codingSchemeDesignator;
    @XmlElement(name = "CodeMeaning", nillable = true)
    protected String codeMeaning;
    @XmlElement(name = "ContextIdentifier", nillable = true)
    protected String contextIdentifier;
    @XmlElement(name = "MappingResource", nillable = true)
    protected String mappingResource;
    @XmlElement(name = "ContextGroupVersion", nillable = true)
    protected String contextGroupVersion;
    @XmlElement(name = "ContextGroupExtensionFlag", nillable = true)
    protected String contextGroupExtensionFlag;
    @XmlElement(name = "ContextGroupLocalVersion", nillable = true)
    protected String contextGroupLocalVersion;
    @XmlElement(name = "ContextGroupExtensionCreatorUID", nillable = true)
    protected String contextGroupExtensionCreatorUID;

    /**
     * Gets the value of the statusType property.
     * 
     * @return
     *     possible object is
     *     {@link StatusType }
     *     
     */
    public StatusType getStatusType() {
        return statusType;
    }

    /**
     * Sets the value of the statusType property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *     
     */
    public void setStatusType(StatusType value) {
        this.statusType = value;
    }

    /**
     * Gets the value of the codeValue property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodeValue() {
        return codeValue;
    }

    /**
     * Sets the value of the codeValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodeValue(Integer value) {
        this.codeValue = value;
    }

    /**
     * Gets the value of the codingSchemeDesignator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodingSchemeDesignator() {
        return codingSchemeDesignator;
    }

    /**
     * Sets the value of the codingSchemeDesignator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodingSchemeDesignator(String value) {
        this.codingSchemeDesignator = value;
    }

    /**
     * Gets the value of the codeMeaning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeMeaning() {
        return codeMeaning;
    }

    /**
     * Sets the value of the codeMeaning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeMeaning(String value) {
        this.codeMeaning = value;
    }

    /**
     * Gets the value of the contextIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextIdentifier() {
        return contextIdentifier;
    }

    /**
     * Sets the value of the contextIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextIdentifier(String value) {
        this.contextIdentifier = value;
    }

    /**
     * Gets the value of the mappingResource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMappingResource() {
        return mappingResource;
    }

    /**
     * Sets the value of the mappingResource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMappingResource(String value) {
        this.mappingResource = value;
    }

    /**
     * Gets the value of the contextGroupVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextGroupVersion() {
        return contextGroupVersion;
    }

    /**
     * Sets the value of the contextGroupVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextGroupVersion(String value) {
        this.contextGroupVersion = value;
    }

    /**
     * Gets the value of the contextGroupExtensionFlag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextGroupExtensionFlag() {
        return contextGroupExtensionFlag;
    }

    /**
     * Sets the value of the contextGroupExtensionFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextGroupExtensionFlag(String value) {
        this.contextGroupExtensionFlag = value;
    }

    /**
     * Gets the value of the contextGroupLocalVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextGroupLocalVersion() {
        return contextGroupLocalVersion;
    }

    /**
     * Sets the value of the contextGroupLocalVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextGroupLocalVersion(String value) {
        this.contextGroupLocalVersion = value;
    }

    /**
     * Gets the value of the contextGroupExtensionCreatorUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextGroupExtensionCreatorUID() {
        return contextGroupExtensionCreatorUID;
    }

    /**
     * Sets the value of the contextGroupExtensionCreatorUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextGroupExtensionCreatorUID(String value) {
        this.contextGroupExtensionCreatorUID = value;
    }

}
