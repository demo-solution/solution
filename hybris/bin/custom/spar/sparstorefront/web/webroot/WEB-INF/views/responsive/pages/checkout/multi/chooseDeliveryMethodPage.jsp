<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<c:url value="/checkout/multi/delivery-address/add" var="addressUrl"/>
<c:set var="deliveryAddress" value="${cartData.deliveryAddress}"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div class="row container-fluid" style="margin-top:10px;">
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepTwo">
				<form:form id="selectDeliveryMethodForm" modelAttribute="deliverySlotForm" action="${request.contextPath}/checkout/multi/delivery-method/select" method="post">
				<!-- <div class="check-top-bar col-lg-12 col-xs-12">
	            	<div class="col-lg-6 col-xs-6"><span class="label"><input name="Home Delivery" id="deliveryType1" disabled style="margin:0 10px 0 0" type="radio">Home Delivery</span></div>
	            	<div class="col-lg-6 col-xs-6"><span class="label pull-left"><input name="Home Delivery" id="deliveryType2" disabled style="margin:0 10px 0 0" type="radio">Collection Centre</span></div>
            	</div> -->
				<div class="col-sm-12 col-xs-12 step-content" id="address1" style="display:block;">
				<c:choose>	
					<c:when test="${deliveryMode eq 'pickup'}">	
						<h4 class="add-det-head marg-btm"><spring:theme code="checkout.summary.deliveryMode.collectionCenter" text="Shipping Method"></spring:theme></h4>
						<div class="col-sm-4 nopadding">
						<div class="col-sm-1 nopadding"></div>
         				<div class="col-sm-11 address-block">
								<p><strong>${fn:escapeXml(selectedCollectionCenter.address.firstName)}</strong></p>
								<c:if test="${not empty selectedCollectionCenter.address.line1}">
								<p>${fn:escapeXml(selectedCollectionCenter.address.line1)},</p></c:if>
								<c:if test="${not empty selectedCollectionCenter.address.line2}">
								<p>${fn:escapeXml(selectedCollectionCenter.address.line2)},</p></c:if>
							    <p>${fn:escapeXml(selectedCollectionCenter.address.town)} -&nbsp;
								   ${fn:escapeXml(selectedCollectionCenter.address.postalCode)},</p>
								<p>${fn:escapeXml(selectedCollectionCenter.address.region.name)},&nbsp;
								   ${fn:escapeXml(selectedCollectionCenter.address.country.name)}</p>
								<p>Phone: ${fn:escapeXml(selectedCollectionCenter.address.phone)}</p>
								<p><a href="https://www.google.com/maps/place/Spar Hypermarket,${selectedCollectionCenter.address.town},${selectedCollectionCenter.address.postalCode}/@${selectedCollectionCenter.geoPoint.latitude},${selectedCollectionCenter.geoPoint.longitude}" target="_blank" class="btn btnLocation" ><span><i class="fa fa-map-marker pad-right" style="padding-right:5px;">
								</i>SEE DIRECTION</span></a></p>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<h4 class="add-det-head marg-btm"><spring:theme code="checkout.summary.deliveryMode.selectDeliveryMethodForOrder" text="Shipping Method"></spring:theme></h4>
	   				<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
					</c:otherwise>	
				</c:choose>
				</div>
				
				 <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 step-content" id="address2" style="display:block; margin-top:20px;">
					<%-- <div class="checkout-shipping">
						<multi-checkout:shipmentItems cartData="${cartData}" showDeliveryAddress="true" />
						<div class="checkout-indent">
							<div class="headline"><spring:theme code="checkout.summary.deliveryMode.selectDeliveryMethodForOrder" text="Shipping Method"></spring:theme></div> --%>
								<h4 class="add-det-head marg-btm" style="font-weight: bold;"><spring:theme code="checkout.summary.deliveryMode.nextAvailableSlot" text="Next Available Slot"></spring:theme></h4>
							<div class="slot-table">
							
								<c:forEach items="${map}" var="entry" varStatus="loop">
								<c:choose>
									<c:when test="${(loop.index)%2==0}">
										<c:set var="def" value="even-row" ></c:set>
									</c:when>
									<c:otherwise>
										<c:set var="def" value="odd-row" ></c:set>
									</c:otherwise>
								</c:choose>
								<div class="timeslots">
									<c:choose>
										<c:when test="${whetherEmployee eq true}">										
											<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>									
										</c:when>
										<c:otherwise>										
											<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>										
										</c:otherwise>	
									</c:choose>	
												
								</div>
								</c:forEach>
								<c:forEach items="${map}" var="entry" varStatus="loop">								
								<div class="slots <fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>">
									<c:forEach items="${entry.value}" var="radioValue">
									<c:choose>	
										<c:when test="${whetherEmployee eq true && fn:contains(radioValue.slotDescription, 'LMG') || 
												whetherEmployee eq true && fn:contains(radioValue.slotDescription, 'SPAR')}">
											<c:if test="${cartData.orderWarehouse.code eq '20001' && fn:contains(radioValue.slotDescription, 'SPAR')}">	
												<div class="delivery-slots">
						                        	<div class="form-group">
						                        	
							                        	<c:choose>
								                        	<c:when test="${radioValue.availableSlot eq false}">
								                        		<span class="radio-red"><form:radiobutton path="deliverySlot" label="${radioValue.slotDescription}" 
								                        		value="${entry.key}/${radioValue.slotId}"/></span>
								                        	</c:when>
								                        	<c:otherwise>
								                        		<span class="radio-green"><form:radiobutton path="deliverySlot" label="${radioValue.slotDescription}" 
								                        		value="${entry.key}/${radioValue.slotId}"/></span>
								                        	</c:otherwise>
							                        	</c:choose>
						                        	</div>
						                        </div>
					                        </c:if>
					                        <c:if test="${cartData.orderWarehouse.code eq '20007' && fn:contains(radioValue.slotDescription, 'LMG')}">	
												<div class="delivery-slots">
						                        	<div class="form-group">
							                        	<c:choose>
								                        	<c:when test="${radioValue.availableSlot eq false}">
								                        		<span class="radio-red"><form:radiobutton path="deliverySlot" label="${radioValue.slotDescription}" 
								                        		value="${entry.key}/${radioValue.slotId}"/></span>
								                        	</c:when>
								                        	<c:otherwise>
								                        		<span class="radio-green"><form:radiobutton path="deliverySlot" label="${radioValue.slotDescription}" 
								                        		value="${entry.key}/${radioValue.slotId}"/></span>
								                        	</c:otherwise>
							                        	</c:choose>
						                        	</div>
						                        </div>
					                        </c:if>
				                        </c:when>
				                        <c:when test="${fn:contains(radioValue.slotDescription, 'LMG') || fn:contains(radioValue.slotDescription, 'SPAR')}">
				                        </c:when>
			                        <c:otherwise>
			                        	<div class="delivery-slots">
			                        	<div class="form-group">
				                        	<c:choose>
					                        	<c:when test="${radioValue.availableSlot eq false}">
					                        		<span class="radio-red"><form:radiobutton path="deliverySlot" label="${radioValue.slotDescription}" 
					                        		value="${entry.key}/${radioValue.slotId}"/></span>
					                        	</c:when>
					                        	<c:otherwise>
					                        		<span class="radio-green"><form:radiobutton path="deliverySlot" label="${radioValue.slotDescription}" 
					                        		value="${entry.key}/${radioValue.slotId}"/></span>
					                        	</c:otherwise>
				                        	</c:choose>
			                        	</div>
			                        </div>
			                        </c:otherwise>
								</c:choose>	
								</c:forEach>	
								</div>						
								</c:forEach>
							</div>
					
								<%-- <div class="form-group">
									<multi-checkout:deliveryMethodSelector deliveryMethods="${deliveryMethods}" selectedDeliveryMethodId="${cartData.deliveryMode.code}"/>
								</div> --%>
							<%-- <p><spring:theme code="checkout.multi.deliveryMethod.message" text="Items will ship as soon as they are available. <br> See Order Summary for more information." /></p> --%>
						<!-- </div>
					</div> -->
					</div>
					<div class="col-sm-12"><ul class="list-inline pull-left"><li>
               	<span class="box-green"></span>AVAILABLE</li><li><span class="box-red"></span>SLOTS ALREADY BOOKED</li></ul>
						<ul class="list-inline pull-right">
							<li><button id="deliveryMethodSubmit" type="button" class="btn btn-primary btn-block checkout-next"><spring:theme code="checkout.multi.deliveryMethod.continue" text="Next"/></button></li>
						</ul>
						<ul class="list-inline pull-right">
							<li><a href="${addressUrl}" class="btn btn-primary btn-block"><spring:theme code="checkout.multi.deliveryMethod.previous" text="Previous"/></a></li>
						</ul>
					</div>
				</form:form>
				</ycommerce:testId>
			</jsp:body>
		</multi-checkout:checkoutSteps>
		<%-- <multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" /> --%>

		<%-- <div class="col-sm-12 col-lg-9">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div> --%>
	</div>

</template:page>
