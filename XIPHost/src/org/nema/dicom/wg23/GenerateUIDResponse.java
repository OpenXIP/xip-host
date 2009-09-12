
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for generateUIDResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generateUIDResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="retval" type="{http://wg23.dicom.nema.org/}uid"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generateUIDResponse", propOrder = {
    "retval"
})
public class GenerateUIDResponse {

    @XmlElement(required = true)
    protected Uid retval;

    /**
     * Gets the value of the retval property.
     * 
     * @return
     *     possible object is
     *     {@link Uid }
     *     
     */
    public Uid getRetval() {
        return retval;
    }

    /**
     * Sets the value of the retval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uid }
     *     
     */
    public void setRetval(Uid value) {
        this.retval = value;
    }

}
