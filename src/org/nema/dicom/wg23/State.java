
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for state.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="state">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EXIT"/>
 *     &lt;enumeration value="CANCELED"/>
 *     &lt;enumeration value="SUSPENDED"/>
 *     &lt;enumeration value="COMPLETED"/>
 *     &lt;enumeration value="INPROGRESS"/>
 *     &lt;enumeration value="IDLE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "state")
@XmlEnum
public enum State {

    EXIT,
    CANCELED,
    SUSPENDED,
    COMPLETED,
    INPROGRESS,
    IDLE;

    public String value() {
        return name();
    }

    public static State fromValue(String v) {
        return valueOf(v);
    }

}
