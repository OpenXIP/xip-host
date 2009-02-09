
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAvailableScreen complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAvailableScreen">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="appPreferredScreen" type="{http://wg23.dicom.nema.org/}rectangle"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAvailableScreen", propOrder = {
    "appPreferredScreen"
})
public class GetAvailableScreen {

    @XmlElement(required = true)
    protected Rectangle appPreferredScreen;

    /**
     * Gets the value of the appPreferredScreen property.
     * 
     * @return
     *     possible object is
     *     {@link Rectangle }
     *     
     */
    public Rectangle getAppPreferredScreen() {
        return appPreferredScreen;
    }

    /**
     * Sets the value of the appPreferredScreen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rectangle }
     *     
     */
    public void setAppPreferredScreen(Rectangle value) {
        this.appPreferredScreen = value;
    }

}
