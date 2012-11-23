
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Study complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Study">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ObjectDescriptors" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfObjectDescriptor" minOccurs="0"/>
 *         &lt;element name="Series" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfSeries" minOccurs="0"/>
 *         &lt;element name="StudyUID" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Study", propOrder = {
    "objectDescriptors",
    "series",
    "studyUID"
})
public class Study {

    @XmlElement(name = "ObjectDescriptors", nillable = true)
    protected ArrayOfObjectDescriptor objectDescriptors;
    @XmlElement(name = "Series", nillable = true)
    protected ArrayOfSeries series;
    @XmlElement(name = "StudyUID", nillable = true)
    protected UID studyUID;

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
     * Gets the value of the series property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSeries }
     *     
     */
    public ArrayOfSeries getSeries() {
        return series;
    }

    /**
     * Sets the value of the series property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSeries }
     *     
     */
    public void setSeries(ArrayOfSeries value) {
        this.series = value;
    }

    /**
     * Gets the value of the studyUID property.
     * 
     * @return
     *     possible object is
     *     {@link UID }
     *     
     */
    public UID getStudyUID() {
        return studyUID;
    }

    /**
     * Sets the value of the studyUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UID }
     *     
     */
    public void setStudyUID(UID value) {
        this.studyUID = value;
    }

}
