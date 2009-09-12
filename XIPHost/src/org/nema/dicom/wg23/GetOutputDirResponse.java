
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getOutputDirResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getOutputDirResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="retval" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getOutputDirResponse", propOrder = {
    "retval"
})
public class GetOutputDirResponse {

    @XmlElement(required = true)
    protected String retval;

    /**
     * Gets the value of the retval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetval() {
        return retval;
    }

    /**
     * Sets the value of the retval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetval(String value) {
        this.retval = value;
    }

}
