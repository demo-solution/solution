<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl" />
<spring:url value="/checkout/multi/wallet/confirm" var="confirmWalletPaymentUrl"/> 
<spring:url value="/checkout/multi/wallet/cancel" var="cancelWalletPaymentUrl"/> 
<spring:url value="/checkout/multi/landmarkreward/process" var="doHandleLRPaymentUrl"/>
<spring:url value="/checkout/multi/landmarkreward/confirm" var="confirmLandmarkRewardPaymentUrl"/> 
<spring:url value="/checkout/multi/landmarkreward/cancel" var="cancelLandmarkRewardPaymentUrl"/> 

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<%-- <div class="checkout-headline">
		<spring:theme code="checkout.multi.secure.checkout"/>
	</div>
	 --%>
		<div class="row mt20">
			<div class="tab-pane" role="tabpanel" id="step3">
			<multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
				<jsp:body>
					<div class="check-top-bar1 col-xs-12  col-sm-12  col-md-12  col-lg-12">
						<h4><spring:theme code="checkout.multi.payment.paymentOptions" text="PAYMENT OPTIONS" /></h4>
					</div>
					
                       <div class="paymentBxs">
						<%-- 
						<div class="col-sm-12 col-lg-9">
							<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
								<cms:component component="${feature}"/>
							</cms:pageSlot>
						</div>
						 --%>
					   <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 landmark-blk">
					   <div class="row mb30 wallet">
					   			<c:choose>
									<c:when test="${customerData.isEnrolledToLR}">		
					   					<div class="col-xs-12 col-sm-4 col-md-4 col-lg-4 lr-program">					   		
									   		<label for="landmark-reward" class="payment-mode-icons landmark-reward">Landmark Rewards</label>
									   		<div class="row">
												  <div class="col-lg-12 nopadding">
												    <div class="input-group">
												   	 <span class="payment-label-box disable">Points value</span>
												   	 	<input id="landmarkRewardAmount" name="landmark-reward" type="text" class="form-control">
												   	 	<input id="landmarkAmount" name="landmark-Amount" type="hidden" class="form-control" value="${sparLRUserDetailDataResult.balanceAvailable}">
												   	 	<input id="doHandleLRPaymentUrl" name="doHandleLRPaymentUrl" type="hidden" class="form-control" value="${doHandleLRPaymentUrl}">
												   	 	<input id="cancelLandmarkRewardPaymentUrl" type="hidden" value="${cancelLandmarkRewardPaymentUrl}"/>
												      <span class="input-group-btn">
												        <button class="btn btn-green" type="button">APPLY</button>
												      </span>								      
												    </div>
												  </div>
											</div>
								   		</div>
								   	</c:when>
								   	<c:otherwise>
								   		<div class="col-xs-12 col-sm-4 col-md-4 col-lg-4 lr-program disabled">					   		
									   		<label for="landmark-reward" class="payment-mode-icons landmark-reward">Landmark Rewards</label>
									   		<div class="row">
												  <div class="col-lg-12 nopadding">
												    <div class="input-group">
												   	 <span class="payment-label-box disable">Points value</span>
												   	 	<input id="landmarkRewardAmountDisabled" name="landmark-reward" type="text" class="form-control" disabled>												   	 	
												      <span class="input-group-btn">
												        <button class="btn btn-green" type="button" disabled>APPLY</button>
												      </span>								      
												    </div>
												  </div>
											</div>
								   		</div>
								   	</c:otherwise>
							</c:choose>
								   	<!-- 	 Wallet Commented code - Start -->								   	
								   	 <%--  <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4">	
									   		<label for="customerWalletAmount" class="payment-mode-icons wallet">Spar Wallet</label>							   			
											<multiCheckout:sparWalletForm />
								   		</div> --%> 
								   		
								   		
								   	<%-- 	<div class="col-xs-12 col-sm-4 col-md-4 col-lg-4 gift-card-wrapper">
									   		<label for="spar-giftcard" class="payment-mode-icons giftcard">Spar Gift Card</label>
									   		<multiCheckout:sparGiftCardForm />
									   		 <div class="row">
												  	<div class="col-lg-12 nopadding mb10">
														<input disabled="disabled" type="text" class="form-control card-number" placeholder="Card Number">		
												    </div>
												    <div class="col-lg-12 nopadding">
													    <div class="input-group">
													     	 <input type="text" disabled="disabled" class="form-control" placeholder="Enter Pin">
														      <span class="input-group-btn">
														        <button disabled="disabled" class="btn btn-green" type="button">Apply</button>								      
														      </span>								     
													    </div>
												  </div>
											</div> 
								   		</div> --%>
					   			 <!-- Wallet Commented code - End -->
					   			 <div class="col-xs-12 col-sm-8 col-md-6 col-lg-8">
					   			 	<div class="row pay-summary">
								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 pay-detail pad-top pad-bottom">
		                             	<div class="sub-total"><span class="pay-items"><spring:theme code="checkout.multi.payment.subTotal" text="Sub Total :"/></span> <span><format:price priceData="${cartData.totalPrice}"/></span></div>
		                             <!-- Code commented to don't show the delivery charges on payment page,please don't remove the code from here -->
		                             	<%-- <c:if test="${not empty cartData.deliveryCost}">
		                             				<div class="delivery-charges"><span class="pay-items"><spring:theme code="checkout.multi.summary.page.deliveryCharges" text="Delivery Charges :"/>&nbsp;</span><span class="delivery-charge"><format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE"/></span></div>
																			</c:if>	 --%>
																<!-- end here -->		                             	
		                             	
		                             	<div class="landmark-reward hidden"><span class="pay-items">Points value from Landmark Rewards :</span>
				                            <div>
				                            <span class="amt"></span>
				                      
				                            <span class="remove-entry-button"></span>
				                            </div>
				                          
			                            </div>
		                             	
		                             	 <div class="wallet hidden"><span class="pay-items">Used from SPAR Wallet :</span>
				                            <div>
				                            <span class="amt"></span>
				                      
				                            <span class="remove-entry-button"></span>
				                            </div>
				                          
			                            </div>
			                             <form:form id="cancelWalletPaymentDetailsForm" name="walletPaymentDetailsForm" modelAttribute="walletPaymentDetailsForm" action="${cancelWalletPaymentUrl}" method="POST">
			                            <div class="wallet hidden"><span class="pay-items">Used from SPAR Wallet :</span>
				                            <div>
				                            <span class="amt"></span>
				                      
				                            <span class="remove-entry-button"></span>
				                            </div>
				                          
			                            </div>
			                            </form:form>
			                           <div class="giftcard"><span class="pay-items"><!-- Used from SPAR Giftcard : --></span><span><!-- Rs. 0 --></span></div>			                           
		                             </div>
	                            <%--  <c:choose>
	                             <c:when test="${cartData.balanceDue eq null}">
	                             	<div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 amt-paid"><spring:theme code="checkout.multi.payment.amountToBePaid" text="BALANCE DUE"/>&nbsp; <span><format:price priceData="${cartData.totalPrice}"/></span></div>
								</c:when>
								 	<div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 amt-paid"><spring:theme code="checkout.multi.payment.amountToBePaid" text="BALANCE DUE"/>&nbsp; <span><format:price priceData="${cartData.balanceDue}"/></span></div>
								</c:choose> --%>
								
								 	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 amt-paid">
									 	<spring:theme code="checkout.multi.payment.amountToBePaid" text="BALANCE DUE"/>&nbsp; 
									 	<span><format:price priceData="${cartData.balanceDue}"/></span>
									 	<form:form id="walletPaymentDetailsForm" name="walletPaymentDetailsForm" modelAttribute="walletPaymentDetailsForm" action="${confirmWalletPaymentUrl}" method="POST">
				
											<div class="form-group" style="display:none">	
												<formElement:formSelectBox idKey="sparPaymentMode" labelKey="payment.paymentModeType" path="paymentMode" selectCSSClass="form-control" mandatory="true" skipBlank="true" skipBlankMessageKey="payment.paymentModeType.pleaseSelect" items="${paymentWalletMode}" selectedValue="wallet" tabindex="1"/>
											</div>
										
												<button  id="payment_confirm_button" type="submit" class="btn btn-green submit_CODOrderPostForm checkout-next hidden">
												
												<%-- <img src="${commonResourcePath}/images/wallet-Confirm-delivery.png" style="border:0;"> --%>
												Confirm
											</button>		
  										</form:form>
  										
  										
  										<form:form id="landmarkRewardPaymentDetailsForm" name="landmarkRewardPaymentDetailsForm" modelAttribute="landmarkRewardPaymentDetailsForm" action="${confirmLandmarkRewardPaymentUrl}" method="POST">
				
											<div class="form-group" style="display:none">	
												<formElement:formSelectBox idKey="sparPaymentMode" labelKey="payment.paymentModeType" path="paymentMode" selectCSSClass="form-control" mandatory="true" skipBlank="true" skipBlankMessageKey="payment.paymentModeType.pleaseSelect" items="${paymentLandmarkRewardMode}" selectedValue="landmarkReward" tabindex="1"/>
											</div>
										
												<button  id="landmark_payment_confirm_button" type="submit" class="btn btn-green submit_CODOrderPostForm checkout-next hidden">
												
												<%-- <img src="${commonResourcePath}/images/wallet-Confirm-delivery.png" style="border:0;"> --%>
												Confirm Order
											</button>		
  										</form:form>
								 	</div>
					   			</div>				
					   			 
					   			 </div>
								   			
							</div>
                        </div>
                        
                     <div class="col-xs-12 col-md-12 col-sm-12 col-lg-12 payment-method payment-options">
                    	   <div class="check-top-bar2">
								<span class="heading"><spring:theme code="checkout.multi.payment.paymentOptions.select" text="SELECT YOUR PAYEMNT OPTIONS"/></span>
           					</div>
           					<c:choose>
							<c:when test="${empty paymentFormUrl}">
								<c:if test="${cashondelivery }">
									<multiCheckout:sparCODPaymentForm />
								</c:if>
								<c:if test="${creditcard }">
									<multiCheckout:sparCreditCardPaymentForm  />
								</c:if>
							</c:when>
							<c:otherwise>
										<!-- This block needs no change as this is used for OOTB SOP flow -->
										<form:form id="silentOrderPostForm" name="silentOrderPostForm" modelAttribute="sopPaymentDetailsForm" action="${paymentFormUrl}" method="POST">
											<input type="hidden" name="orderPage_receiptResponseURL" value="${silentOrderPageData.parameters['orderPage_receiptResponseURL']}"/>
											<input type="hidden" name="orderPage_declineResponseURL" value="${silentOrderPageData.parameters['orderPage_declineResponseURL']}"/>
											<input type="hidden" name="orderPage_cancelResponseURL" value="${silentOrderPageData.parameters['orderPage_cancelResponseURL']}"/>
											<c:forEach items="${sopPaymentDetailsForm.signatureParams}" var="entry" varStatus="status">
												<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}"/>
											</c:forEach>
											<c:forEach items="${sopPaymentDetailsForm.subscriptionSignatureParams}" var="entry" varStatus="status">
												<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}"/>
											</c:forEach>
											<input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}" name="billTo_email" id="billTo_email">

											<div class="form-group">
												<c:if test="${not empty paymentInfos}">
													<button type="button" class="btn btn-default btn-block js-saved-payments"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></button>
												</c:if>	
											</div>
											
											<div class="form-group">
												<formElement:formSelectBox idKey="card_cardType" labelKey="payment.cardType" path="card_cardType" selectCSSClass="form-control" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${sopCardTypes}" tabindex="1"/>
											</div>
			
											<div class="form-group">
												<formElement:formInputBox idKey="card_nameOnCard" labelKey="payment.nameOnCard" path="card_nameOnCard" inputCSS="form-control" tabindex="2" mandatory="false" />
		
											</div>
			 
											<div class="form-group">
												<formElement:formInputBox idKey="card_accountNumber" labelKey="payment.cardNumber" path="card_accountNumber" inputCSS="form-control" mandatory="true" tabindex="3" autocomplete="off" />
											</div>
			
											<fieldset id="startDate">
												<label for="" class="control-label"><spring:theme code="payment.startDate"/></label>
												<div class="row">
													<div class="col-xs-6">
														<formElement:formSelectBox idKey="StartMonth" selectCSSClass="form-control" labelKey="payment.month" path="card_startMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="4"/>
													</div>
													<div class="col-xs-6">
														<formElement:formSelectBox idKey="StartYear" selectCSSClass="form-control" labelKey="payment.year" path="card_startYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${startYears}" tabindex="7"/>
													</div>
												</div>
											</fieldset>
		
											<fieldset id="cardDate">
												<label for="" class="control-label"><spring:theme code="payment.expiryDate"/></label>
												<div class="row">
													<div class="col-xs-6">
														<formElement:formSelectBox idKey="ExpiryMonth" selectCSSClass="form-control" labelKey="payment.month" path="card_expirationMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="6"/>
													</div>
													<div class="col-xs-6">
														<formElement:formSelectBox idKey="ExpiryYear" selectCSSClass="form-control" labelKey="payment.year" path="card_expirationYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}" tabindex="7"/>
													</div>
												</div>
											</fieldset>
		
											<div class="row">
												<div class="form-group col-xs-6">
													<formElement:formInputBox idKey="card_cvNumber" labelKey="payment.cvn" path="card_cvNumber" inputCSS="form-control" mandatory="true" tabindex="8" />
												</div>
											</div>
											
											<div class="row">
												<div class="form-group col-xs-6">
													<div id="issueNum">
														<formElement:formInputBox idKey="card_issueNumber" labelKey="payment.issueNumber" path="card_issueNumber" inputCSS="text" mandatory="false" tabindex="9"/>
													</div>
												</div>
											</div>
											
											<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
												<formElement:formCheckbox idKey="savePaymentMethod" labelKey="checkout.multi.sop.savePaymentInfo" path="savePaymentInfo"
							                          inputCSS="" labelCSS="" mandatory="false" tabindex="10"/>
											</sec:authorize> 
		
											<hr/>					
											<div class="headline">
											<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/>
											</div>
		
											<c:if test="${cartData.deliveryItemsQuantity > 0}">
										
												<div id="useDeliveryAddressData" 
													data-titlecode="${deliveryAddress.titleCode}"
													data-firstname="${deliveryAddress.firstName}"
													data-lastname="${deliveryAddress.lastName}"
													data-line1="${deliveryAddress.line1}"
													data-line2="${deliveryAddress.line2}"
													data-town="${deliveryAddress.town}"
													data-postalcode="${deliveryAddress.postalCode}"
													data-countryisocode="${deliveryAddress.country.isocode}"
													data-regionisocode="${deliveryAddress.region.isocodeShort}"
													data-address-id="${deliveryAddress.id}">
													</div>
													<formElement:formCheckbox 
														path="useDeliveryAddress" 
														idKey="useDeliveryAddress"
													    labelKey="checkout.multi.sop.useMyDeliveryAddress"
													    tabindex="11"/>
											</c:if>
											  
											<input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}" class="text" name="billTo_email" id="billTo_email">
											<address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="12"/>
																					
											<p><spring:theme code="checkout.multi.paymentMethod.seeOrderSummaryForMoreInformation"/></p>
															
											<button type="button"
												class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next">
												<spring:theme code="checkout.payment.makePayment" text="MAKE PAYMENT"/>
											</button>
											
											</form:form>
									</c:otherwise>
								</c:choose>
						</div>
					</div>
			   </jsp:body>
			</multiCheckout:checkoutSteps>
				
		<div class="row benrMidle">			
			
			<div class="col-md-12 cards mt30 mb30">
				<ul class="cards-icons">
					<%-- <li><img width="64" height="22" src="${commonResourcePath}/images/ticket-icon.png"></li>
					<li><img width="64" height="22" src="${commonResourcePath}/images/sodexo-icon.png"></li> --%>
					<li><img width="50" height="22" src="${commonResourcePath}/images/visa-icon.png"></li>
					<li><img width="38" height="22" src="${commonResourcePath}/images/mastercard-icon.png"></li>
					<li><img width="36" height="22" src="${commonResourcePath}/images/maestro_card.png"></li>
					<li>
						<span class="iconsBx"><img width="24" height="24" src="${commonResourcePath}/images/Hassle_Free_Return-24x24.png"></span>
						<span class="iconTxt1">HASSLE FREE RETURNS AND NO QUESTIONS ASKED REFUND</span>
					</li>
				</ul>
			</div>
        </div>
				
		
			</div>
			
		</div>
</template:page>
