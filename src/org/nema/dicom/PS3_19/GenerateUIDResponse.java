
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
 *         &lt;element name="GenerateUIDResult" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UID" minOccurs="0"/>
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
    "generateUIDResult"
})
@XmlRootElement(name = "GenerateUIDResponse")
public class GenerateUIDResponse {

    @XmlElement(name = "GenerateUIDResult", nillable = true)
    protected UID generateUIDResult;

    /**
     * Gets the value of the generateUIDResult property.
     * 
     * @return
     *     possible object is
     *     {@link UID }
     *     
     */
    public UID getGenerateUIDResult() {
        return generateUIDResult;
    }

    /**
     * Sets the value of the generateUIDResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UID }
     *     
     */
    public void setGenerateUIDResult(UID value) {
        this.generateUIDResult = value;
    }

}
