
package org.nema.dicom.PS3_19;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XPathNodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="XPathNodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Root"/>
 *     &lt;enumeration value="Element"/>
 *     &lt;enumeration value="Attribute"/>
 *     &lt;enumeration value="Namespace"/>
 *     &lt;enumeration value="Text"/>
 *     &lt;enumeration value="SignificantWhitespace"/>
 *     &lt;enumeration value="Whitespace"/>
 *     &lt;enumeration value="ProcessingInstruction"/>
 *     &lt;enumeration value="Comment"/>
 *     &lt;enumeration value="All"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "XPathNodeType", namespace = "http://schemas.datacontract.org/2004/07/System.Xml.XPath")
@XmlEnum
public enum XPathNodeType {

    @XmlEnumValue("Root")
    ROOT("Root"),
    @XmlEnumValue("Element")
    ELEMENT("Element"),
    @XmlEnumValue("Attribute")
    ATTRIBUTE("Attribute"),
    @XmlEnumValue("Namespace")
    NAMESPACE("Namespace"),
    @XmlEnumValue("Text")
    TEXT("Text"),
    @XmlEnumValue("SignificantWhitespace")
    SIGNIFICANT_WHITESPACE("SignificantWhitespace"),
    @XmlEnumValue("Whitespace")
    WHITESPACE("Whitespace"),
    @XmlEnumValue("ProcessingInstruction")
    PROCESSING_INSTRUCTION("ProcessingInstruction"),
    @XmlEnumValue("Comment")
    COMMENT("Comment"),
    @XmlEnumValue("All")
    ALL("All");
    private final String value;

    XPathNodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XPathNodeType fromValue(String v) {
        for (XPathNodeType c: XPathNodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
