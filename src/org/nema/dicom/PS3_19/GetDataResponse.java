
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
 *         &lt;element name="GetDataResult" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfObjectLocator" minOccurs="0"/>
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
    "getDataResult"
})
@XmlRootElement(name = "GetDataResponse")
public class GetDataResponse {

    @XmlElement(name = "GetDataResult", nillable = true)
    protected ArrayOfObjectLocator getDataResult;

    /**
     * Gets the value of the getDataResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfObjectLocator }
     *     
     */
    public ArrayOfObjectLocator getGetDataResult() {
        return getDataResult;
    }

    /**
     * Sets the value of the getDataResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfObjectLocator }
     *     
     */
    public void setGetDataResult(ArrayOfObjectLocator value) {
        this.getDataResult = value;
    }

}
