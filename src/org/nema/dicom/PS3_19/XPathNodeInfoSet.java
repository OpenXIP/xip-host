
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XPathNodeInfoSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XPathNodeInfoSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InfoSetValue" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="NodeType" type="{http://schemas.datacontract.org/2004/07/System.Xml.XPath}XPathNodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XPathNodeInfoSet", propOrder = {
    "infoSetValue",
    "nodeType"
})
public class XPathNodeInfoSet {

    @XmlElement(name = "InfoSetValue", nillable = true)
    protected byte[] infoSetValue;
    @XmlElement(name = "NodeType")
    protected XPathNodeType nodeType;

    /**
     * Gets the value of the infoSetValue property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getInfoSetValue() {
        return infoSetValue;
    }

    /**
     * Sets the value of the infoSetValue property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setInfoSetValue(byte[] value) {
        this.infoSetValue = ((byte[]) value);
    }

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

}
