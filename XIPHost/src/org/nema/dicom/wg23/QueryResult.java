
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for queryResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queryResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="model" type="{http://wg23.dicom.nema.org/}uuid"/>
 *         &lt;element name="xpath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="results" type="{http://wg23.dicom.nema.org/}ArrayOfString"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queryResult", propOrder = {
    "model",
    "xpath",
    "results"
})
public class QueryResult {

    @XmlElement(required = true)
    protected Uuid model;
    @XmlElement(required = true)
    protected String xpath;
    @XmlElement(required = true)
    protected ArrayOfString results;

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link Uuid }
     *     
     */
    public Uuid getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uuid }
     *     
     */
    public void setModel(Uuid value) {
        this.model = value;
    }

    /**
     * Gets the value of the xpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXpath() {
        return xpath;
    }

    /**
     * Sets the value of the xpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXpath(String value) {
        this.xpath = value;
    }

    /**
     * Gets the value of the results property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getResults() {
        return results;
    }

    /**
     * Sets the value of the results property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setResults(ArrayOfString value) {
        this.results = value;
    }

}
