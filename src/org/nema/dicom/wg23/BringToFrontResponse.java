
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bringToFrontResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bringToFrontResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="retval" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bringToFrontResponse", propOrder = {
    "retval"
})
public class BringToFrontResponse {

    protected boolean retval;

    /**
     * Gets the value of the retval property.
     * 
     */
    public boolean isRetval() {
        return retval;
    }

    /**
     * Sets the value of the retval property.
     * 
     */
    public void setRetval(boolean value) {
        this.retval = value;
    }

}
