
package com.spar.hcl.core.employee.discount.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.spar.hcl.core.employee.discount.ws package. 
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

    private final static QName _GetEmployeeDetails_QNAME = new QName("http://employee.ws.stores.retail.max/", "getEmployeeDetails");
    private final static QName _GetEmployeeDetailsResponse_QNAME = new QName("http://employee.ws.stores.retail.max/", "getEmployeeDetailsResponse");
    private final static QName _UpdateEmployee_QNAME = new QName("http://employee.ws.stores.retail.max/", "updateEmployee");
    private final static QName _UpdateEmployeeResponse_QNAME = new QName("http://employee.ws.stores.retail.max/", "updateEmployeeResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.spar.hcl.core.employee.discount.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetEmployeeDetails }
     * 
     */
    public GetEmployeeDetails createGetEmployeeDetails() {
        return new GetEmployeeDetails();
    }

    /**
     * Create an instance of {@link GetEmployeeDetailsResponse }
     * 
     */
    public GetEmployeeDetailsResponse createGetEmployeeDetailsResponse() {
        return new GetEmployeeDetailsResponse();
    }

    /**
     * Create an instance of {@link UpdateEmployee }
     * 
     */
    public UpdateEmployee createUpdateEmployee() {
        return new UpdateEmployee();
    }

    /**
     * Create an instance of {@link UpdateEmployeeResponse }
     * 
     */
    public UpdateEmployeeResponse createUpdateEmployeeResponse() {
        return new UpdateEmployeeResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEmployeeDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://employee.ws.stores.retail.max/", name = "getEmployeeDetails")
    public JAXBElement<GetEmployeeDetails> createGetEmployeeDetails(GetEmployeeDetails value) {
        return new JAXBElement<GetEmployeeDetails>(_GetEmployeeDetails_QNAME, GetEmployeeDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEmployeeDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://employee.ws.stores.retail.max/", name = "getEmployeeDetailsResponse")
    public JAXBElement<GetEmployeeDetailsResponse> createGetEmployeeDetailsResponse(GetEmployeeDetailsResponse value) {
        return new JAXBElement<GetEmployeeDetailsResponse>(_GetEmployeeDetailsResponse_QNAME, GetEmployeeDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateEmployee }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://employee.ws.stores.retail.max/", name = "updateEmployee")
    public JAXBElement<UpdateEmployee> createUpdateEmployee(UpdateEmployee value) {
        return new JAXBElement<UpdateEmployee>(_UpdateEmployee_QNAME, UpdateEmployee.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateEmployeeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://employee.ws.stores.retail.max/", name = "updateEmployeeResponse")
    public JAXBElement<UpdateEmployeeResponse> createUpdateEmployeeResponse(UpdateEmployeeResponse value) {
        return new JAXBElement<UpdateEmployeeResponse>(_UpdateEmployeeResponse_QNAME, UpdateEmployeeResponse.class, null, value);
    }

}
