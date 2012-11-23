
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AvailableData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvailableData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ObjectDescriptors" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfObjectDescriptor" minOccurs="0"/>
 *         &lt;element name="Patients" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfPatient" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailableData", propOrder = {
    "objectDescriptors",
    "patients"
})
public class AvailableData {

    @XmlElement(name = "ObjectDescriptors", nillable = true)
    protected ArrayOfObjectDescriptor objectDescriptors;
    @XmlElement(name = "Patients", nillable = true)
    protected ArrayOfPatient patients;

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
     * Gets the value of the patients property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPatient }
     *     
     */
    public ArrayOfPatient getPatients() {
        return patients;
    }

    /**
     * Sets the value of the patients property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPatient }
     *     
     */
    public void setPatients(ArrayOfPatient value) {
        this.patients = value;
    }

}
