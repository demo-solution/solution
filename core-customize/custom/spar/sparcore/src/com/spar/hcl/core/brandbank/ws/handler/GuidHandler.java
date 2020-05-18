package com.spar.hcl.core.brandbank.ws.handler;

/*import com.spar.hcl.core.brandbank.ws.ExternalCallerHeader;
import com.spar.hcl.core.brandbank.ws.ObjectFactory;

import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;*/



public class GuidHandler// implements SOAPHandler<SOAPMessageContext>
{

	/*
	 * @Override public boolean handleMessage(final SOAPMessageContext context) { //
	 * TODO Auto-generated method stub
	 * System.out.println("Client : handleMessage()......"); final Boolean isRequest
	 * = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	 * 
	 * //if this is a request, true for outbound messages, false for inbound if
	 * (isRequest.booleanValue()) {
	 * 
	 * try {
	 * 
	 * final SOAPMessage soapMsg = context.getMessage(); final SOAPEnvelope soapEnv
	 * = soapMsg.getSOAPPart().getEnvelope(); SOAPHeader soapHeader =
	 * soapEnv.getHeader();
	 * 
	 * //if no header, add one if (soapHeader == null) { soapHeader =
	 * soapEnv.addHeader(); }
	 * 
	 * final ObjectFactory objectFactory = new ObjectFactory();
	 * 
	 * //creating instance of ExternalCallerHeader final ExternalCallerHeader
	 * externalCallerHeader = objectFactory.createExternalCallerHeader();
	 * externalCallerHeader.setExternalCallerId(
	 * "8EB942BC-198F-4E2D-A258-936F0C072301");
	 * 
	 * //createing JaxBElement out of the ExternalCallerHeader Object final
	 * JAXBElement<ExternalCallerHeader> jaxbExternalCallerHeader = objectFactory
	 * .createExternalCallerHeader(externalCallerHeader);
	 * 
	 * final Marshaller marshaller =
	 * JAXBContext.newInstance(ExternalCallerHeader.class).createMarshaller(); //
	 * marshalling instance (appending) to SOAP header's xml node
	 * marshaller.marshal(jaxbExternalCallerHeader, soapHeader);
	 * 
	 * } catch (final SOAPException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (final JAXBException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 * 
	 * return true; }
	 * 
	 * @Override public void close(final MessageContext context) { // TODO
	 * Auto-generated method stub System.out.println("Client : close()......");
	 * 
	 * }
	 * 
	 * @Override public boolean handleFault(final SOAPMessageContext context) { //
	 * TODO Auto-generated method stub
	 * System.out.println("Client : handleFault()......"); return true; }
	 * 
	 * @Override public Set<QName> getHeaders() { // TODO Auto-generated method stub
	 * System.out.println("Client : getHeaders()......"); return null; }
	 */
}
