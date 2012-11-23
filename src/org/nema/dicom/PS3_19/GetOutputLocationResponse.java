
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="GetOutputLocationResult" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
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
    "getOutputLocationResult"
})
@XmlRootElement(name = "GetOutputLocationResponse")
public class GetOutputLocationResponse {

    @XmlElement(name = "GetOutputLocationResult", nillable = true)
    @XmlSchemaType(name = "anyURI")
    protected String getOutputLocationResult;

    /**
     * Gets the value of the getOutputLocationResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetOutputLocationResult() {
        return getOutputLocationResult;
    }

    /**
     * Sets the value of the getOutputLocationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetOutputLocationResult(String value) {
        this.getOutputLocationResult = value;
    }

}
