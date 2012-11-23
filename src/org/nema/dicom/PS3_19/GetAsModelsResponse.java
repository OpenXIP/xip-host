
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
 *         &lt;element name="GetAsModelsResult" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ModelSetDescriptor" minOccurs="0"/>
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
    "getAsModelsResult"
})
@XmlRootElement(name = "GetAsModelsResponse")
public class GetAsModelsResponse {

    @XmlElement(name = "GetAsModelsResult", nillable = true)
    protected ModelSetDescriptor getAsModelsResult;

    /**
     * Gets the value of the getAsModelsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ModelSetDescriptor }
     *     
     */
    public ModelSetDescriptor getGetAsModelsResult() {
        return getAsModelsResult;
    }

    /**
     * Sets the value of the getAsModelsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelSetDescriptor }
     *     
     */
    public void setGetAsModelsResult(ModelSetDescriptor value) {
        this.getAsModelsResult = value;
    }

}
