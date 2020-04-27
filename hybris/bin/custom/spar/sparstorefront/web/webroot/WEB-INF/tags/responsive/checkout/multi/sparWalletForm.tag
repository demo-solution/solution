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

<spring:url value="/checkout/multi/wallet/process" var="walletDeliveryUrl"/>
<spring:url value="/checkout/multi/wallet/confirm" var="confirmWalletPaymentUrl"/>  
		<form:form id="walletPaymentProcessForm" name="walletPaymentDetailsForm" modelAttribute="walletPaymentDetailsForm" action="${walletDeliveryUrl}" method="POST">
			<div class="form-group" style="display:none">	
				<formElement:formSelectBox idKey="sparPaymentMode" labelKey="payment.paymentModeType" path="paymentMode" selectCSSClass="form-control" mandatory="true" skipBlank="true" skipBlankMessageKey="payment.paymentModeType.pleaseSelect" items="${paymentWalletMode}" selectedValue="wallet" tabindex="1"/>
			</div>
			
			<%-- <formElement:formInputBox labelKey="walletFormKey" idKey="walletFormKey" path="paidByWallet" selectCSSClass="form-control"  /> --%>
			<%-- <input type="hidden" id="customerWalletAmount" name="customerWalletAmount" value="${customerData.paidByWallet.value}/>" /> --%>
	
			<%--  <input id="customerWalletAmount" name="customerWalletAmount" value="<format:price priceData="${customerData.paidByWallet}"/>" /> --%> 
			  <div class="row">
				<div class="col-lg-12 nopadding">
					  <div class="input-group">
						 <span class="payment-label-box">Available Balance</span><input id="customerWalletAmount"  name="customerWalletAmount" class="form-control" value="${customerData.paidByWallet.value}" />
							  <span class="input-group-btn">
								<button id="payment_wallet_button" type="button" class="checkout-next btn btn-green">
									<%-- <img src="${commonResourcePath}/images/wallet-delivery.png" style="border:0;"> --%>
									Apply
								</button>
							</span>
						</div>
						<div class="paidwallet-amt"></div>
					</div>
				</div>
	</form:form> 		
				 	
  			