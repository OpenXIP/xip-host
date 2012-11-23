
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QueryResultInfoSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryResultInfoSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Model" type="{http://dicom.nema.org/PS3.19/HostService-20100825}UUID" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfXPathNodeInfoSet" minOccurs="0"/>
 *         &lt;element name="XPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryResultInfoSet", propOrder = {
    "model",
    "result",
    "xPath"
})
public class QueryResultInfoSet {

    @XmlElement(name = "Model", nillable = true)
    protected UUID model;
    @XmlElement(name = "Result", nillable = true)
    protected ArrayOfXPathNodeInfoSet result;
    @XmlElement(name = "XPath", nillable = true)
    protected String xPath;

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link UUID }
     *     
     */
    public UUID getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link UUID }
     *     
     */
    public void setModel(UUID value) {
        this.model = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfXPathNodeInfoSet }
     *     
     */
    public ArrayOfXPathNodeInfoSet getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfXPathNodeInfoSet }
     *     
     */
    public void setResult(ArrayOfXPathNodeInfoSet value) {
        this.result = value;
    }

    /**
     * Gets the value of the xPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXPath() {
        return xPath;
    }

    /**
     * Sets the value of the xPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXPath(String value) {
        this.xPath = value;
    }

}
