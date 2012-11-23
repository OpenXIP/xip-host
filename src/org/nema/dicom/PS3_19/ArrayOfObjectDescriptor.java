
package org.nema.dicom.PS3_19;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfObjectDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfObjectDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ObjectDescriptor" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ObjectDescriptor" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfObjectDescriptor", propOrder = {
    "objectDescriptor"
})
public class ArrayOfObjectDescriptor {

    @XmlElement(name = "ObjectDescriptor", nillable = true)
    protected List<ObjectDescriptor> objectDescriptor;

    /**
     * Gets the value of the objectDescriptor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectDescriptor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectDescriptor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectDescriptor }
     * 
     * 
     */
    public List<ObjectDescriptor> getObjectDescriptor() {
        if (objectDescriptor == null) {
            objectDescriptor = new ArrayList<ObjectDescriptor>();
        }
        return this.objectDescriptor;
    }

}
