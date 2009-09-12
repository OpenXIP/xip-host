
package org.nema.dicom.wg23;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfObjectLocator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfObjectLocator">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objectLocator" type="{http://wg23.dicom.nema.org/}objectLocator" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfObjectLocator", propOrder = {
    "objectLocator"
})
public class ArrayOfObjectLocator {

    protected List<ObjectLocator> objectLocator;

    /**
     * Gets the value of the objectLocator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectLocator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectLocator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectLocator }
     * 
     * 
     */
    public List<ObjectLocator> getObjectLocator() {
        if (objectLocator == null) {
            objectLocator = new ArrayList<ObjectLocator>();
        }
        return this.objectLocator;
    }

}
