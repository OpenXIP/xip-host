
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
 *         &lt;element name="GetStateResult" type="{http://dicom.nema.org/PS3.19/ApplicationService-20100825}State" minOccurs="0"/>
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
    "getStateResult"
})
@XmlRootElement(name = "GetStateResponse")
public class GetStateResponse {

    @XmlElement(name = "GetStateResult")
    protected State getStateResult;

    /**
     * Gets the value of the getStateResult property.
     * 
     * @return
     *     possible object is
     *     {@link State }
     *     
     */
    public State getGetStateResult() {
        return getStateResult;
    }

    /**
     * Sets the value of the getStateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link State }
     *     
     */
    public void setGetStateResult(State value) {
        this.getStateResult = value;
    }

}
