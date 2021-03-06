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

<spring:url value="/checkout/multi/cod/process" var="cashOnDeliveryUrl"/>

<div class="col-md-5 col-xs-12 col-sm-5 col-lg-5 payment-method">
  	<div class="col-md-12 col-xs-12 col-sm-12 col-lg-12" align="center">
		<form:form id="codOrderPostForm" name="codOrderPostForm" modelAttribute="codPaymentDetailsForm" action="${cashOnDeliveryUrl}" method="POST">
			<input id="balanceTotalDue" name="balanceTotalDue" type="hidden" value="${cartData.balanceDue.value}" />	
			<div class="form-group" style="display:none">
			
			<formElement:formSelectBox idKey="sparPaymentMode" labelKey="payment.paymentModeType" path="paymentMode" selectCSSClass="form-control" mandatory="true" skipBlank="true" skipBlankMessageKey="payment.paymentModeType.pleaseSelect" items="${paymentModes}" selectedValue="cashondelivery" tabindex="1"/>
			</div>
			
			
			<div id="codConfirmation" class="modal fade" role="dialog">
				<div class="modal-dialog">
					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">							
							<h3 class="modal-title text-left">Cash on Delivery Payment</h3>
						</div>
						<div class="modal-body">
							<h3>Do you Want to confirm the Order ?</h3>
						</div>
						<div class="modal-footer">
							 <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
							 <button id="payment_button" type="submit" class="submit_CODOrderPostForm btn btn-primary checkout-next">Yes</button>							 
							<div id="thisField" class="hidden"><c:out value="${otpLR}"></c:out></div>
						</div>
					</div>
				</div>
			</div>	
	
	       	<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#codConfirmation" style="border:none; background:none;">
				<img src="${commonResourcePath}/images/ccc-delivery.png">
			</button>
			
  		</form:form>
	</div>
</div>