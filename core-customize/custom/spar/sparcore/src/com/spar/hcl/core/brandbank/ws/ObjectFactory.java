
package com.spar.hcl.core.brandbank.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hcl.spar.brandbank.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ExternalCallerHeader_QNAME = new QName("http://www.i-label.net/Partners/WebServices/DataFeed/2005/11", "ExternalCallerHeader");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hcl.spar.brandbank.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetProductDataForGTINsResponse }
     * 
     */
    public GetProductDataForGTINsResponse createGetProductDataForGTINsResponse() {
        return new GetProductDataForGTINsResponse();
    }

    /**
     * Create an instance of {@link GetUnsentProductDataResponse }
     * 
     */
    public GetUnsentProductDataResponse createGetUnsentProductDataResponse() {
        return new GetUnsentProductDataResponse();
    }

    /**
     * Create an instance of {@link GetUnsentProductData }
     * 
     */
    public GetUnsentProductData createGetUnsentProductData() {
        return new GetUnsentProductData();
    }

    /**
     * Create an instance of {@link GetProductDataForGTINsResponse.GetProductDataForGTINsResult }
     * 
     */
    public GetProductDataForGTINsResponse.GetProductDataForGTINsResult createGetProductDataForGTINsResponseGetProductDataForGTINsResult() {
        return new GetProductDataForGTINsResponse.GetProductDataForGTINsResult();
    }

    /**
     * Create an instance of {@link AcknowledgeMessage }
     * 
     */
    public AcknowledgeMessage createAcknowledgeMessage() {
        return new AcknowledgeMessage();
    }

    /**
     * Create an instance of {@link GetProductDataForGTINs }
     * 
     */
    public GetProductDataForGTINs createGetProductDataForGTINs() {
        return new GetProductDataForGTINs();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link AcknowledgeMessageResponse }
     * 
     */
    public AcknowledgeMessageResponse createAcknowledgeMessageResponse() {
        return new AcknowledgeMessageResponse();
    }

    /**
     * Create an instance of {@link ExternalCallerHeader }
     * 
     */
    public ExternalCallerHeader createExternalCallerHeader() {
        return new ExternalCallerHeader();
    }

    /**
     * Create an instance of {@link GetUnsentProductDataResponse.GetUnsentProductDataResult }
     * 
     */
    public GetUnsentProductDataResponse.GetUnsentProductDataResult createGetUnsentProductDataResponseGetUnsentProductDataResult() {
        return new GetUnsentProductDataResponse.GetUnsentProductDataResult();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExternalCallerHeader }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.i-label.net/Partners/WebServices/DataFeed/2005/11", name = "ExternalCallerHeader")
    public JAXBElement<ExternalCallerHeader> createExternalCallerHeader(ExternalCallerHeader value) {
        return new JAXBElement<ExternalCallerHeader>(_ExternalCallerHeader_QNAME, ExternalCallerHeader.class, null, value);
    }

}
