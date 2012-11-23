
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
 *         &lt;element name="data" type="{http://dicom.nema.org/PS3.19/HostService-20100825}AvailableData" minOccurs="0"/>
 *         &lt;element name="lastData" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "data",
    "lastData"
})
@XmlRootElement(name = "NotifyDataAvailable")
public class NotifyDataAvailable {

    @XmlElement(nillable = true)
    protected AvailableData data;
    protected Boolean lastData;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableData }
     *     
     */
    public AvailableData getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableData }
     *     
     */
    public void setData(AvailableData value) {
        this.data = value;
    }

    /**
     * Gets the value of the lastData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLastData() {
        return lastData;
    }

    /**
     * Sets the value of the lastData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLastData(Boolean value) {
        this.lastData = value;
    }

}
