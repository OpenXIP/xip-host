
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notifyStateChanged complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notifyStateChanged">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="newState" type="{http://wg23.dicom.nema.org/}state"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notifyStateChanged", propOrder = {
    "newState"
})
public class NotifyStateChanged {

    @XmlElement(required = true)
    protected State newState;

    /**
     * Gets the value of the newState property.
     * 
     * @return
     *     possible object is
     *     {@link State }
     *     
     */
    public State getNewState() {
        return newState;
    }

    /**
     * Sets the value of the newState property.
     * 
     * @param value
     *     allowed object is
     *     {@link State }
     *     
     */
    public void setNewState(State value) {
        this.newState = value;
    }

}
