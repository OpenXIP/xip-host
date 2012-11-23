
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Modality complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Modality">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Modality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Modality", propOrder = {
    "modality"
})
public class Modality {

    @XmlElement(name = "Modality", nillable = true)
    protected String modality;

    /**
     * Gets the value of the modality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModality() {
        return modality;
    }

    /**
     * Sets the value of the modality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModality(String value) {
        this.modality = value;
    }

}
