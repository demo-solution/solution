<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:url value="/checkout/multi/payment-method/add" var="paymentFormUrl"/>
<spring:url value="/checkout/multi/delivery-method/choose" var="chooseDeliverySlotUrl"/>
<spring:url value="/checkout/multi/summary/voucher-method/apply" var="voucherCodeApply"/>
<spring:url value="/checkout/multi/summary/voucher-method/confirmation" var="getUserConfirmation"/>
<spring:url value="/checkout/multi/summary/voucher-method/release" var="releaseAppliedVoucher"/>
<spring:url value="/checkout/multi/summary/placeOrder" var="placeOrder"/>
<spring:url value="/checkout/multi/cod/process" var="cashOnDeliveryUrl"/>

<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
<multi-checkout:summaryCartValidation/>
	<%-- <div class="checkout-headline">
		<spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"></spring:theme>
	</div> --%>
	<div class="row mt20">
		<div class="tab-pane" role="tabpanel" id="step3">
			<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<multi-checkout:checkoutOrderSummary cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="true" showTaxEstimate="true" showTax="true" />
			<div class="row mt10 p10 voucher">
				<div class="col-xs-12 col-md-6">
					<div class="row mb10">	
					<!-- Code change start for voucher story	 -->				
						 <form:form id="voucherCodeForm" name="voucherCodeForm" action="${getUserConfirmation}" method="GET"> 
							<div class="voucher-apply js-voucher-apply" data-url="${voucherCodeApply}"></div>
							<%-- <div class="user-confirmation js-user-confirmation" data-url="${getUserConfirmation}"></div> --%> 
							<div class="release-voucher js-voucher-release" data-url="${releaseAppliedVoucher}"></div> 							
							<c:if test="${displayApplyVoucherBtn eq true}">
								<div class="col-md-4 nopadding"><label><spring:theme code="ENTER YOUR VOUCHER" text="ENTER YOUR VOUCHER"/> :</label></div>
								<div class="col-xs-7 col-md-4 nopadding"><input type="text" name="vouchercode" class="form-control" id="vouchercode" placeholder="Please Enter Voucher" style="width:100%"></div>							
								<div class="col-xs-4 col-md-4">
									<button id="voucherbtn" type="submit" class=" btn btnRed">
										<spring:theme code="APPLY VOUCHER" text="APPLY VOUCHER"/>
									</button>
								</div>
							</c:if>
							<c:if test="${releaseApplyVoucherBtn eq true}">
								<div class="pull-left nopadding mr10"><label><spring:theme code="Your Voucher Code is" text="Your Voucher Code is"/> :</label></div>
								<div class="col-xs-7 col-md-4 apply pr0"><input type="text" name="vouchercode" class="form-control" id="vouchercode" value="${cartData.voucherCode}" readonly="readonly" style="width:100%"></div>	
								<div class="col-xs-4 col-md-4 ">
									<button id="releaseBtn" type="button" class="btn btnRed">
										<spring:theme code="RELEASE VOUCHER" text="RELEASE VOUCHER"/>
									</button>
								</div>
							</c:if>
						</form:form> 
						<!-- Changes end here -->
					</div>
				</div>					
				<div class="row summaryPostForm">
				<c:choose>
					<c:when test="${disableMakePayment eq true}">
			           <form:form id="summaryPostForm" name="summaryPostForm" action="${cashOnDeliveryUrl}" method="POST">					
							<div class="col-xs-5 col-md-2 pull-right pr0">
								<button id="payment" type="submit" class=" btn-block  btn btn-green">
									<spring:theme code="checkout.multi.summary.voucher.confirmorder" text="CONFIRM ORDER"/>
								</button>
							</div>	
						</form:form>				
							<div class="col-xs-4 col-md-2 pull-right pr0 continue">
								<button id="payment" type="submit" class=" btn-block checkout-next  btn btnRed" disabled="disabled">
									<spring:theme code="checkout.multi.summary.continue" text="CONTINUE"/>
								</button>
							</div>						
					</c:when>
					<c:otherwise>
						<div class="col-xs-12 col-md-2 pull-right pr0" >
				           <form:form id="summaryPostForm" name="summaryPostForm" action="${paymentFormUrl}" method="GET">
								<button id="payment" type="submit" class=" btn-block submit_silentOrderPostForm checkout-next  btn btnRed" >
									<spring:theme code="checkout.multi.summary.continue" text="CONTINUE"/>
								</button>
							</form:form>
						</div>
					</c:otherwise>				
				</c:choose>			
					<div class="col-xs-12 col-md-1 pull-right nopadding" >
						<a href="${chooseDeliverySlotUrl}" class=" btn-block btn btnRed"><spring:theme code="checkout.multi.summary.previous" text="BACK"/></a>
					</div >	
				</div>
			</div>
		</multi-checkout:checkoutSteps>
		</div>
	</div>
</template:page>