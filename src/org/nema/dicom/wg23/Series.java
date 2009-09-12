
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for series complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="series">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="seriesUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectDescriptors" type="{http://wg23.dicom.nema.org/}ArrayOfObjectDescriptor"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "series", propOrder = {
    "seriesUID",
    "objectDescriptors"
})
public class Series {

    @XmlElement(required = true)
    protected String seriesUID;
    @XmlElement(required = true)
    protected ArrayOfObjectDescriptor objectDescriptors;

    /**
     * Gets the value of the seriesUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeriesUID() {
        return seriesUID;
    }

    /**
     * Sets the value of the seriesUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeriesUID(String value) {
        this.seriesUID = value;
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

}
