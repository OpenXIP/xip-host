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
 * <p>Java class for getDataAsSpecificTypeFileResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataAsSpecificTypeFileResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objectLocators" type="{http://wg23.dicom.nema.org/}ArrayOfObjectLocator"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDataAsSpecificTypeFileResponse", propOrder = {
    "objectLocators"
})
public class GetDataAsSpecificTypeFileResponse {

    @XmlElement(required = true)
    protected ArrayOfObjectLocator objectLocators;

    /**
     * Gets the value of the objectLocators property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfObjectLocator }
     *     
     */
    public ArrayOfObjectLocator getObjectLocators() {
        return objectLocators;
    }

    /**
     * Sets the value of the objectLocators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfObjectLocator }
     *     
     */
    public void setObjectLocators(ArrayOfObjectLocator value) {
        this.objectLocators = value;
    }

}
