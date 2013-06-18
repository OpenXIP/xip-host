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
 * <p>Java class for queryModelResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queryModelResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="queryResults" type="{http://wg23.dicom.nema.org/}ArrayOfQueryResult"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queryModelResponse", propOrder = {
    "queryResults"
})
public class QueryModelResponse {

    @XmlElement(required = true)
    protected ArrayOfQueryResult queryResults;

    /**
     * Gets the value of the queryResults property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQueryResult }
     *     
     */
    public ArrayOfQueryResult getQueryResults() {
        return queryResults;
    }

    /**
     * Sets the value of the queryResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQueryResult }
     *     
     */
    public void setQueryResults(ArrayOfQueryResult value) {
        this.queryResults = value;
    }

}
