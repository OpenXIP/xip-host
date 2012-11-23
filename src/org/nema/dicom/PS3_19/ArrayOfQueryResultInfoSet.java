
package org.nema.dicom.PS3_19;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfQueryResultInfoSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQueryResultInfoSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QueryResultInfoSet" type="{http://dicom.nema.org/PS3.19/HostService-20100825}QueryResultInfoSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQueryResultInfoSet", propOrder = {
    "queryResultInfoSet"
})
public class ArrayOfQueryResultInfoSet {

    @XmlElement(name = "QueryResultInfoSet", nillable = true)
    protected List<QueryResultInfoSet> queryResultInfoSet;

    /**
     * Gets the value of the queryResultInfoSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the queryResultInfoSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQueryResultInfoSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QueryResultInfoSet }
     * 
     * 
     */
    public List<QueryResultInfoSet> getQueryResultInfoSet() {
        if (queryResultInfoSet == null) {
            queryResultInfoSet = new ArrayList<QueryResultInfoSet>();
        }
        return this.queryResultInfoSet;
    }

}
