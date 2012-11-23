
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INFORMATION"/>
 *     &lt;enumeration value="WARNING"/>
 *     &lt;enumeration value="ERROR"/>
 *     &lt;enumeration value="FATALERROR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StatusType")
@XmlEnum
public enum StatusType {

    INFORMATION,
    WARNING,
    ERROR,
    FATALERROR;

    public String value() {
        return name();
    }

    public static StatusType fromValue(String v) {
        return valueOf(v);
    }

}
