<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<spring:url value="/checkout/multi/payment-method/request" var="actionCreditCardURL"/>

<div class="col-md-5 col-xs-12 col-lg-5 col-sm-5 payment-method pull-right">
       	<div class="col-md-12 col-xs-12 col-sm-12 col-lg-12" align="center">
			<form:form id="ccOrderPostForm" name="ccOrderPostForm" modelAttribute="ccPaymentDetailsForm" action="${actionCreditCardURL}" method="POST">
				 
				<div class="form-group" style="display:none">
					<formElement:formSelectBox idKey="sparPaymentMode" labelKey="payment.paymentModeType" path="paymentMode" selectCSSClass="form-control" mandatory="true" skipBlank="true" skipBlankMessageKey="payment.paymentModeType.pleaseSelect" items="${paymentModeTypes}" selectedValue="creditcard" tabindex="1"/>
				</div>
				
              	<button id="payment_button1" type="submit" class="submit_CCOrderPostForm checkout-next">
					<img src="${commonResourcePath}/images/cc-card.jpg" style="border:0;">
				</button>
				</form:form>
		</div>
 </div>