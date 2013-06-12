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
 * <p>Java class for getAsModels complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAsModels">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uuids" type="{http://wg23.dicom.nema.org/}ArrayOfUUID"/>
 *         &lt;element name="classUID" type="{http://wg23.dicom.nema.org/}uid"/>
 *         &lt;element name="transferSyntaxUID" type="{http://wg23.dicom.nema.org/}uid"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAsModels", propOrder = {
    "uuids",
    "classUID",
    "transferSyntaxUID"
})
public class GetAsModels {

    @XmlElement(required = true)
    protected ArrayOfUUID uuids;
    @XmlElement(required = true)
    protected Uid classUID;
    @XmlElement(required = true)
    protected Uid transferSyntaxUID;

    /**
     * Gets the value of the uuids property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getUuids() {
        return uuids;
    }

    /**
     * Sets the value of the uuids property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setUuids(ArrayOfUUID value) {
        this.uuids = value;
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

}
