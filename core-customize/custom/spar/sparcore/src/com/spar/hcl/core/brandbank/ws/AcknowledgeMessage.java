
package com.spar.hcl.core.brandbank.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messageGuid" type="{http://microsoft.com/wsdl/types/}guid"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "messageGuid"
})
@XmlRootElement(name = "AcknowledgeMessage")
public class AcknowledgeMessage {

    @XmlElement(required = true)
    protected String messageGuid;

    /**
     * Gets the value of the messageGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageGuid() {
        return messageGuid;
    }

    /**
     * Sets the value of the messageGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageGuid(String value) {
        this.messageGuid = value;
    }

}
