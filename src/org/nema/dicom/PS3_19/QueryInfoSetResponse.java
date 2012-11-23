
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
 *         &lt;element name="QueryInfoSetResult" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfQueryResultInfoSet" minOccurs="0"/>
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
    "queryInfoSetResult"
})
@XmlRootElement(name = "QueryInfoSetResponse")
public class QueryInfoSetResponse {

    @XmlElement(name = "QueryInfoSetResult", nillable = true)
    protected ArrayOfQueryResultInfoSet queryInfoSetResult;

    /**
     * Gets the value of the queryInfoSetResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQueryResultInfoSet }
     *     
     */
    public ArrayOfQueryResultInfoSet getQueryInfoSetResult() {
        return queryInfoSetResult;
    }

    /**
     * Sets the value of the queryInfoSetResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQueryResultInfoSet }
     *     
     */
    public void setQueryInfoSetResult(ArrayOfQueryResultInfoSet value) {
        this.queryInfoSetResult = value;
    }

}
