
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
 *         &lt;element name="models" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfUUID" minOccurs="0"/>
 *         &lt;element name="xPaths" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
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
    "models",
    "xPaths"
})
@XmlRootElement(name = "QueryInfoSet")
public class QueryInfoSet {

    @XmlElement(nillable = true)
    protected ArrayOfUUID models;
    @XmlElement(nillable = true)
    protected ArrayOfstring xPaths;

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

    /**
     * Gets the value of the xPaths property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfstring }
     *     
     */
    public ArrayOfstring getXPaths() {
        return xPaths;
    }

    /**
     * Sets the value of the xPaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfstring }
     *     
     */
    public void setXPaths(ArrayOfstring value) {
        this.xPaths = value;
    }

}
