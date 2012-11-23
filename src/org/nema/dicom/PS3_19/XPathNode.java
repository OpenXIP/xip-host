
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XPathNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XPathNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NodeType" type="{http://schemas.datacontract.org/2004/07/System.Xml.XPath}XPathNodeType" minOccurs="0"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XPathNode", propOrder = {
    "nodeType",
    "value"
})
public class XPathNode {

    @XmlElement(name = "NodeType")
    protected XPathNodeType nodeType;
    @XmlElement(name = "Value", nillable = true)
    protected String value;

    /**
     * Gets the value of the nodeType property.
     * 
     * @return
     *     possible object is
     *     {@link XPathNodeType }
     *     
     */
    public XPathNodeType getNodeType() {
        return nodeType;
    }

    /**
     * Sets the value of the nodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link XPathNodeType }
     *     
     */
    public void setNodeType(XPathNodeType value) {
        this.nodeType = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
