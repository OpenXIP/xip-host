
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for State.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="State">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IDLE"/>
 *     &lt;enumeration value="INPROGRESS"/>
 *     &lt;enumeration value="SUSPENDED"/>
 *     &lt;enumeration value="COMPLETED"/>
 *     &lt;enumeration value="CANCELED"/>
 *     &lt;enumeration value="EXIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "State")
@XmlEnum
public enum State {

    IDLE,
    INPROGRESS,
    SUSPENDED,
    COMPLETED,
    CANCELED,
    EXIT;

    public String value() {
        return name();
    }

    public static State fromValue(String v) {
        return valueOf(v);
    }

}
