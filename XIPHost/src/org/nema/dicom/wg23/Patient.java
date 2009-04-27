
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for patient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="patient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectDescriptors" type="{http://wg23.dicom.nema.org/}ArrayOfObjectDescriptor"/>
 *         &lt;element name="studies" type="{http://wg23.dicom.nema.org/}ArrayOfStudy"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "patient", propOrder = {
    "name",
    "objectDescriptors",
    "studies"
})
public class Patient {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected ArrayOfObjectDescriptor objectDescriptors;
    @XmlElement(required = true)
    protected ArrayOfStudy studies;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the objectDescriptors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfObjectDescriptor }
     *     
     */
    public ArrayOfObjectDescriptor getObjectDescriptors() {
        return objectDescriptors;
    }

    /**
     * Sets the value of the objectDescriptors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfObjectDescriptor }
     *     
     */
    public void setObjectDescriptors(ArrayOfObjectDescriptor value) {
        this.objectDescriptors = value;
    }

    /**
     * Gets the value of the studies property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStudy }
     *     
     */
    public ArrayOfStudy getStudies() {
        return studies;
    }

    /**
     * Sets the value of the studies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStudy }
     *     
     */
    public void setStudies(ArrayOfStudy value) {
        this.studies = value;
    }

}
