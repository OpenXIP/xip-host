/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uuid" type="{http://wg23.dicom.nema.org/}uuid"/>
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classUID" type="{http://wg23.dicom.nema.org/}uid"/>
 *         &lt;element name="transferSyntaxUID" type="{http://wg23.dicom.nema.org/}uid"/>
 *         &lt;element name="modality" type="{http://wg23.dicom.nema.org/}modality"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectDescriptor", propOrder = {
    "uuid",
    "mimeType",
    "classUID",
    "transferSyntaxUID",
    "modality"
})
public class ObjectDescriptor {

    @XmlElement(required = true)
    protected Uuid uuid;
    @XmlElement(required = true)
    protected String mimeType;
    @XmlElement(required = true)
    protected Uid classUID;
    @XmlElement(required = true)
    protected Uid transferSyntaxUID;
    @XmlElement(required = true)
    protected Modality modality;

    /**
     * Gets the value of the uuid property.
     * 
     * @return
     *     possible object is
     *     {@link Uuid }
     *     
     */
    public Uuid getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uuid }
     *     
     */
    public void setUuid(Uuid value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the classUID property.
     * 
     * @return
     *     possible object is
     *     {@link Uid }
     *     
     */
    public Uid getClassUID() {
        return classUID;
    }

    /**
     * Sets the value of the classUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uid }
     *     
     */
    public void setClassUID(Uid value) {
        this.classUID = value;
    }

    /**
     * Gets the value of the transferSyntaxUID property.
     * 
     * @return
     *     possible object is
     *     {@link Uid }
     *     
     */
    public Uid getTransferSyntaxUID() {
        return transferSyntaxUID;
    }

    /**
     * Sets the value of the transferSyntaxUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uid }
     *     
     */
    public void setTransferSyntaxUID(Uid value) {
        this.transferSyntaxUID = value;
    }

    /**
     * Gets the value of the modality property.
     * 
     * @return
     *     possible object is
     *     {@link Modality }
     *     
     */
    public Modality getModality() {
        return modality;
    }

    /**
     * Sets the value of the modality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Modality }
     *     
     */
    public void setModality(Modality value) {
        this.modality = value;
    }

}
