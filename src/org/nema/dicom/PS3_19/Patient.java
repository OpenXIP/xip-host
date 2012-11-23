
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Patient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Patient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AssigningAuthority" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ObjectDescriptors" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfObjectDescriptor" minOccurs="0"/>
 *         &lt;element name="Sex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Studies" type="{http://dicom.nema.org/PS3.19/HostService-20100825}ArrayOfStudy" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Patient", propOrder = {
    "assigningAuthority",
    "dateOfBirth",
    "id",
    "name",
    "objectDescriptors",
    "sex",
    "studies"
})
public class Patient {

    @XmlElement(name = "AssigningAuthority", nillable = true)
    protected String assigningAuthority;
    @XmlElement(name = "DateOfBirth")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(name = "ID", nillable = true)
    protected String id;
    @XmlElement(name = "Name", nillable = true)
    protected String name;
    @XmlElement(name = "ObjectDescriptors", nillable = true)
    protected ArrayOfObjectDescriptor objectDescriptors;
    @XmlElement(name = "Sex", nillable = true)
    protected String sex;
    @XmlElement(name = "Studies", nillable = true)
    protected ArrayOfStudy studies;

    /**
     * Gets the value of the assigningAuthority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssigningAuthority() {
        return assigningAuthority;
    }

    /**
     * Sets the value of the assigningAuthority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssigningAuthority(String value) {
        this.assigningAuthority = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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

    /**
     * Gets the value of the sex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSex() {
        return sex;
    }

    /**
     * Sets the value of the sex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSex(String value) {
        this.sex = value;
    }

    /**
     * Gets the value of the studies property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStudy }
     *     
     */
    public ArrayOfStudy getStudies() {
        return studies;
    }

    /**
     * Sets the value of the studies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStudy }
     *     
     */
    public void setStudies(ArrayOfStudy value) {
        this.studies = value;
    }

}
