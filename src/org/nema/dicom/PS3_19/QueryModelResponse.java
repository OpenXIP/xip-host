
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
 *         &lt;element name="QueryModelResult" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfQueryResult" minOccurs="0"/>
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
    "queryModelResult"
})
@XmlRootElement(name = "QueryModelResponse")
public class QueryModelResponse {

    @XmlElement(name = "QueryModelResult", nillable = true)
    protected ArrayOfQueryResult queryModelResult;

    /**
     * Gets the value of the queryModelResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQueryResult }
     *     
     */
    public ArrayOfQueryResult getQueryModelResult() {
        return queryModelResult;
    }

    /**
     * Sets the value of the queryModelResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQueryResult }
     *     
     */
    public void setQueryModelResult(ArrayOfQueryResult value) {
        this.queryModelResult = value;
    }

}
