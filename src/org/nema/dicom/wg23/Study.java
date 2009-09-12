
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for study complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="study">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="studyUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectDescriptors" type="{http://wg23.dicom.nema.org/}ArrayOfObjectDescriptor"/>
 *         &lt;element name="series" type="{http://wg23.dicom.nema.org/}ArrayOfSeries"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "study", propOrder = {
    "studyUID",
    "objectDescriptors",
    "series"
})
public class Study {

    @XmlElement(required = true)
    protected String studyUID;
    @XmlElement(required = true)
    protected ArrayOfObjectDescriptor objectDescriptors;
    @XmlElement(required = true)
    protected ArrayOfSeries series;

    /**
     * Gets the value of the studyUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudyUID() {
        return studyUID;
    }

    /**
     * Sets the value of the studyUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudyUID(String value) {
        this.studyUID = value;
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

}
