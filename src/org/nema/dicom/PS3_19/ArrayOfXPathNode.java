
package org.nema.dicom.PS3_19;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfXPathNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfXPathNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XPathNode" type="{http://dicom.nema.org/PS3.19/HostService-20100825}XPathNode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfXPathNode", propOrder = {
    "xPathNode"
})
public class ArrayOfXPathNode {

    @XmlElement(name = "XPathNode", nillable = true)
    protected List<XPathNode> xPathNode;

    /**
     * Gets the value of the xPathNode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xPathNode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXPathNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XPathNode }
     * 
     * 
     */
    public List<XPathNode> getXPathNode() {
        if (xPathNode == null) {
            xPathNode = new ArrayList<XPathNode>();
        }
        return this.xPathNode;
    }

}
