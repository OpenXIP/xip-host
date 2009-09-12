
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDataAsSpecificTypeFileResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataAsSpecificTypeFileResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objectLocators" type="{http://wg23.dicom.nema.org/}ArrayOfObjectLocator"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDataAsSpecificTypeFileResponse", propOrder = {
    "objectLocators"
})
public class GetDataAsSpecificTypeFileResponse {

    @XmlElement(required = true)
    protected ArrayOfObjectLocator objectLocators;

    /**
     * Gets the value of the objectLocators property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfObjectLocator }
     *     
     */
    public ArrayOfObjectLocator getObjectLocators() {
        return objectLocators;
    }

    /**
     * Sets the value of the objectLocators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfObjectLocator }
     *     
     */
    public void setObjectLocators(ArrayOfObjectLocator value) {
        this.objectLocators = value;
    }

}
