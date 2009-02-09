
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notifyDataAvailable complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notifyDataAvailable">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="availableData" type="{http://wg23.dicom.nema.org/}availableData"/>
 *         &lt;element name="lastData" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notifyDataAvailable", propOrder = {
    "availableData",
    "lastData"
})
public class NotifyDataAvailable {

    @XmlElement(required = true)
    protected AvailableData availableData;
    protected boolean lastData;

    /**
     * Gets the value of the availableData property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableData }
     *     
     */
    public AvailableData getAvailableData() {
        return availableData;
    }

    /**
     * Sets the value of the availableData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableData }
     *     
     */
    public void setAvailableData(AvailableData value) {
        this.availableData = value;
    }

    /**
     * Gets the value of the lastData property.
     * 
     */
    public boolean isLastData() {
        return lastData;
    }

    /**
     * Sets the value of the lastData property.
     * 
     */
    public void setLastData(boolean value) {
        this.lastData = value;
    }

}
