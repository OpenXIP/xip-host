
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modelSetDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modelSetDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="models" type="{http://wg23.dicom.nema.org/}ArrayOfUUID"/>
 *         &lt;element name="failedSourceObjects" type="{http://wg23.dicom.nema.org/}ArrayOfUUID"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modelSetDescriptor", propOrder = {
    "models",
    "failedSourceObjects"
})
public class ModelSetDescriptor {

    @XmlElement(required = true)
    protected ArrayOfUUID models;
    @XmlElement(required = true)
    protected ArrayOfUUID failedSourceObjects;

    /**
     * Gets the value of the models property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getModels() {
        return models;
    }

    /**
     * Sets the value of the models property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setModels(ArrayOfUUID value) {
        this.models = value;
    }

    /**
     * Gets the value of the failedSourceObjects property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getFailedSourceObjects() {
        return failedSourceObjects;
    }

    /**
     * Sets the value of the failedSourceObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setFailedSourceObjects(ArrayOfUUID value) {
        this.failedSourceObjects = value;
    }

}
