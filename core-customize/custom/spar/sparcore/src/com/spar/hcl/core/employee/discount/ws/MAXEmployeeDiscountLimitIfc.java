
package com.spar.hcl.core.employee.discount.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "MAXEmployeeDiscountLimitIfc", targetNamespace = "http://employee.ws.stores.retail.max/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface MAXEmployeeDiscountLimitIfc {


    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getEmployeeDetails", targetNamespace = "http://employee.ws.stores.retail.max/", className = "com.spar.hcl.core.employee.discount.ws.GetEmployeeDetails")
    @ResponseWrapper(localName = "getEmployeeDetailsResponse", targetNamespace = "http://employee.ws.stores.retail.max/", className = "com.spar.hcl.core.employee.discount.ws.GetEmployeeDetailsResponse")
    public String getEmployeeDetails(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "updateEmployee", targetNamespace = "http://employee.ws.stores.retail.max/", className = "com.spar.hcl.core.employee.discount.ws.UpdateEmployee")
    @ResponseWrapper(localName = "updateEmployeeResponse", targetNamespace = "http://employee.ws.stores.retail.max/", className = "com.spar.hcl.core.employee.discount.ws.UpdateEmployeeResponse")
    public boolean updateEmployee(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        Double arg1);

}