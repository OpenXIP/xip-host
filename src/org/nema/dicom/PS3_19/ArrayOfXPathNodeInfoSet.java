
package org.nema.dicom.PS3_19;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfXPathNodeInfoSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfXPathNodeInfoSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XPathNodeInfoSet" type="{http://dicom.nema.org/PS3.19/HostService-20100825}XPathNodeInfoSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfXPathNodeInfoSet", propOrder = {
    "xPathNodeInfoSet"
})
public class ArrayOfXPathNodeInfoSet {

    @XmlElement(name = "XPathNodeInfoSet", nillable = true)
    protected List<XPathNodeInfoSet> xPathNodeInfoSet;

    /**
     * Gets the value of the xPathNodeInfoSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xPathNodeInfoSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXPathNodeInfoSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XPathNodeInfoSet }
     * 
     * 
     */
    public List<XPathNodeInfoSet> getXPathNodeInfoSet() {
        if (xPathNodeInfoSet == null) {
            xPathNodeInfoSet = new ArrayList<XPathNodeInfoSet>();
        }
        return this.xPathNodeInfoSet;
    }

}
