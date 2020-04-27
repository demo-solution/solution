<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>

<c:url value="/cart" var="cartUrl"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<%-- <div class="checkout-headline"><spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"></spring:theme></div> --%>
	<div class="row container-fluid" style="margin-top:10px;">
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepOne">						
							<form:form modelAttribute="sparAddressForm" method="GET" action="${request.contextPath}/checkout/multi/delivery-address/clickncollect">
			            	<div class="deliverycntnr" defaultDelivery="${selectedDeliveryType}">
			            			<form:radiobutton path="deliveryType" value="HD" checked="checked"/>Home Delivery</span>
			            	</div>
	            			<%-- <span class="label pull-left deliveryTypebx">
	            				<form:radiobuttons path="deliveryType" style="margin:0 10px 0 0" items="${deliveryTypeEnums}" itemValue="code" itemLabel="name"/>
	            			</span> --%>
			           
							</form:form>
						</div> 
				<div id="step1" class="tab-pane active" role="tabpanel" style="background:#F4F4F4; padding:0 15px 0 15px;">
				<div class="col-sm-12 step-content" id="address2" style="display:block; margin-top:20px;">
					<!-- <div class="checkout-shipping"> -->
							<%-- <multi-checkout:shipmentItems cartData="${cartData}" showDeliveryAddress="false" /> --%>
						<%-- <c:if test="${not empty employeeDeliveryAddressMsg}"> 
							<div class="saved-rs fs14">${employeeDeliveryAddressMsg}</div>
						</c:if> --%>
						<div class="row newAddress"><div class="col-xs-12 col-sm-4 pull-right">
									<a class="btn btnRed pull-right" href="javascript:void(0)" id="nwAddressBtn">
										<i class="fa fa-plus-circle" style="font-size:16px; padding-right:5px;"></i>
										Add new address
									</a>
								</div></div>
						
						<div class="checkout-indent mt10">
									<!-- <div class="col-sm-6 pad-top"> -->
									<address:addressFormSelector supportedCountries="${countries}"
										regions="${regions}" cancelUrl="${currentStepUrl}"
										country="${country}" />
								<%-- <c:if test="${not empty deliveryAddresses}"> --%>								
									<div id="addressbook">
										<div class="row">
											<div class="checkoutFindStore" data-url="${request.contextPath}/store-finder"></div>											
											<input id="lastDeliveryAddress" type="hidden"  value="${lastUsedDelAddress.line2}"/>
											<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
											<div class="col-sm-4 col-xs-12">
											
													<form id = "deliveryFormSelect" action="${request.contextPath}/checkout/multi/delivery-address/select" method="GET">
													<div class="col-xs-1 col-sm-1 nopadding"><input id="deliveryAddressRadio" type="radio" name="address"/></div>
														<div class="col-xs-11 col-sm-11 address-block nopadding">
														<h3 class="address-header">Deliver to this address 
															<a href="${request.contextPath}/checkout/multi/delivery-address/remove/?addressCode=${deliveryAddress.id}" class="pull-right"><i>X</i></a>
															<a href="${request.contextPath}/checkout/multi/delivery-address/edit/?editAddressCode=${deliveryAddress.id}" class="pull-right"><i class="fa fa-pencil"></i></a></h3>
														
														<input id="editaddress" type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}"/>
														<input id="addressFindStore" type="hidden" name="addressFindStore" value=""/>
														<input id="deliveryPostalCode" type="hidden" name="deliveryPostalCode" value="${fn:escapeXml(deliveryAddress.postalCode)}"/>
														<input id="deliveryAddressTown" type="hidden" value="${fn:escapeXml(deliveryAddress.town)}"/>
															
															<p><strong>${fn:escapeXml(deliveryAddress.title)}&nbsp;
																${fn:escapeXml(deliveryAddress.firstName)}&nbsp;
																${fn:escapeXml(deliveryAddress.lastName)}</strong></p>				
																
															<c:if test="${not empty deliveryAddress.longAddress}">
															
															<p class="addressParaOne">
															<span class="addressHouse">${fn:escapeXml(deliveryAddress.line2)},&nbsp;</span>
															<span class="addressBuilding">${fn:escapeXml(deliveryAddress.buildingName)},&nbsp;</span>
															<span class="addressStreet">${fn:escapeXml(deliveryAddress.line1)},&nbsp;</span>
															<span class="addressLandmark">${fn:escapeXml(deliveryAddress.landmark)},&nbsp;</span>
															<span class="addressLongAddress">${fn:escapeXml(deliveryAddress.longAddress)}</span>
															<span class="addressTown">${fn:escapeXml(deliveryAddress.town)}</span>
															<span class="pinCode">
															<c:if test="${not empty deliveryAddress.postalCode && deliveryAddress.postalCode ne 'NA'}">
																	&nbsp;${fn:escapeXml(deliveryAddress.postalCode)}
															</c:if>
															</span>&nbsp;</p>
																
															</c:if>	
																
															<c:if test="${empty deliveryAddress.longAddress}">	
															<p class="addressParaOne">
															  <span class="addressHouse">${fn:escapeXml(deliveryAddress.line2)},&nbsp;</span>
															  <span class="addressBuilding">${fn:escapeXml(deliveryAddress.buildingName)},&nbsp;</span>
															  <span class="addressStreet">${fn:escapeXml(deliveryAddress.line1)},&nbsp;</span>
															  <span class="addressArea">${fn:escapeXml(deliveryAddress.area)},&nbsp;</span>
															  <span class="addressLandmark">${fn:escapeXml(deliveryAddress.landmark)},&nbsp;</span>
															  <span class="addressTown">${fn:escapeXml(deliveryAddress.town)}</span>,
															  <c:if test="${not empty deliveryAddress.region.name}">
																	&nbsp;${fn:escapeXml(deliveryAddress.region.name)}
																</c:if>
															  <span class="pinCode">
															  		${fn:escapeXml(deliveryAddress.postalCode)},&nbsp;
															  </span>															  
															</p>
															</c:if>
																
															<p><i class="fa fa-phone-square" style="font-size:16px; padding-right:10px;"></i>
															<c:if test="${not empty deliveryAddress.phone}">
																${fn:escapeXml(deliveryAddress.phone)}
															</c:if></p>
															<p><i class="fa fa-envelope" style="font-size:16px; padding-right:10px;"></i>
															<c:if test="${not empty deliveryAddress.email}">
							                       		${fn:escapeXml(deliveryAddress.email)}
							                       	</c:if></p>
														</div>
														<%-- <button type="submit" class="btn btn-primary btn-block">
															<spring:theme
																code="checkout.multi.deliveryAddress.useThisAddress"
																text="Use this Address" />
														</button> --%>
													</form>
													</div>
											</c:forEach>
											<input id="cityNameSelected" type="hidden" name="cityNameSelected" value="${cityName}"/>
										</div>
									</div>									
								<%-- </c:if> --%>
										<address:suggestedAddresses selectedAddressUrl="/checkout/multi/delivery-address/select" />
						</div>
						<div id="clickNCollect" style="display:none;">
							<c:forEach items="${clickNCollect}" var="clickCollect" varStatus="status">
							<c:choose>
								<c:when test="${(fn:substringAfter(loggedInUser, '@') eq 'landmarkgroup.in' && clickCollect.name ne 'SPAR Bannerghatta HO')
										|| (fn:substringAfter(loggedInUser, '@') eq 'maxhypermarkets.com' && clickCollect.name ne 'SPAR LMG CC')}">	
									<form name="pickupInStoreForm" action="${request.contextPath}/checkout/multi/delivery-address/clickncollect" method="GET">
										<div class="clickNCollectData store-finder-navigation">
										<input id="clickCollectPos" type="hidden" name="selectedClickNCollect" value="${clickCollect.name}" />
										<input class="clickCollectPhone" type="hidden" name="clickCollectPhone" value="" />
									
										<ul>	
											<li class="col-sm-4">
													<input type="radio" name="clickCollectAddress" <c:if test="${clickCollect.name eq selectedStore}">checked</c:if> />
													<p><strong>${fn:escapeXml(clickCollect.address.firstName)}</strong></p>
													<c:if test="${not empty clickCollect.address.line1}">
													<p>${fn:escapeXml(clickCollect.address.line1)},</p></c:if>
													<c:if test="${not empty clickCollect.address.line2}">
													<p>${fn:escapeXml(clickCollect.address.line2)},</p></c:if>
													<p>${fn:escapeXml(clickCollect.address.town)} -&nbsp;
													   ${fn:escapeXml(clickCollect.address.postalCode)},</p>
													<p>${fn:escapeXml(clickCollect.address.region.name)},&nbsp;
													   ${fn:escapeXml(clickCollect.address.country.name)}</p>
													<p>Phone: ${fn:escapeXml(clickCollect.address.phone)}</p>
													<p><a href="https://www.google.com/maps/place/Spar Hypermarket,${clickCollect.address.town},${clickCollect.address.postalCode}/@${clickCollect.geoPoint.latitude},${clickCollect.geoPoint.longitude}" target="_blank" class="btn btnLocation" ><span><i class="fa fa-map-marker pad-right" style="padding-right:5px;">
													</i>SEE DIRECTION</span></a></p>
											</li>	
										</ul>
										</div>
									</form>
								</c:when>
								<c:when test="${fn:substringAfter(loggedInUser, '@') ne 'landmarkgroup.in' && fn:substringAfter(loggedInUser, '@') ne 'maxhypermarkets.com' && clickCollect.name eq 'SPAR Bhavani Nagar'}">	
									<form name="pickupInStoreForm" action="${request.contextPath}/checkout/multi/delivery-address/clickncollect" method="GET">
										<div class="clickNCollectData store-finder-navigation">
										<input id="clickCollectPos" type="hidden" name="selectedClickNCollect" value="${clickCollect.name}" />
											<input class="clickCollectPhone" type="hidden" name="clickCollectPhone" value="" />
										<ul>	
											<li class="col-sm-4">
													<input type="radio" name="clickCollectAddress" <c:if test="${clickCollect.name eq selectedStore}">checked</c:if>/>
													<p><strong>${fn:escapeXml(clickCollect.address.firstName)}</strong></p>
													<p>${fn:escapeXml(clickCollect.address.line1)},</p>
													<p>${fn:escapeXml(clickCollect.address.line2)},</p>
													<p>${fn:escapeXml(clickCollect.address.town)} -&nbsp;
													   ${fn:escapeXml(clickCollect.address.postalCode)},</p>
													<p>${fn:escapeXml(clickCollect.address.region.name)},&nbsp;
													   ${fn:escapeXml(clickCollect.address.country.name)}</p>
													<p>Phone: ${fn:escapeXml(clickCollect.address.phone)}</p>
													<p><a href="https://www.google.com/maps/place/Spar Hypermarket,${clickCollect.address.town},${clickCollect.address.postalCode}/@${clickCollect.geoPoint.latitude},${clickCollect.geoPoint.longitude}" target="_blank" class="btn btnLocation" ><span><i class="fa fa-map-marker pad-right" style="padding-right:5px;">
													</i>SEE DIRECTION</span></a></p>
											</li>	
										</ul>
										</div>
									</form>
								</c:when>
								<c:otherwise>I Dont Know
									<c:if test="${cityName ne 'Bengaluru'}">
									<form name="pickupInStoreForm" action="${request.contextPath}/checkout/multi/delivery-address/clickncollect" method="GET">
											<div class="clickNCollectData store-finder-navigation">
											<input id="clickCollectPos" type="hidden" name="selectedClickNCollect" value="${clickCollect.name}" />
											<input class="clickCollectPhone" type="hidden" name="clickCollectPhone" value="" />
										
											<ul>	
												<li class="col-sm-4">
														<input type="radio" name="clickCollectAddress" <c:if test="${clickCollect.name eq selectedStore}">checked</c:if> />
														<p><strong>${fn:escapeXml(clickCollect.address.firstName)}</strong></p>
														<c:if test="${not empty clickCollect.address.line1}">
														<p>${fn:escapeXml(clickCollect.address.line1)},</p></c:if>
														<c:if test="${not empty clickCollect.address.line2}">
														<p>${fn:escapeXml(clickCollect.address.line2)},</p></c:if>
														<p>${fn:escapeXml(clickCollect.address.town)} -&nbsp;
														   ${fn:escapeXml(clickCollect.address.postalCode)},</p>
														<p>${fn:escapeXml(clickCollect.address.region.name)},&nbsp;
														   ${fn:escapeXml(clickCollect.address.country.name)}</p>
														<p>Phone: ${fn:escapeXml(clickCollect.address.phone)}</p>
														<p><a href="https://www.google.com/maps/place/Spar Hypermarket,${clickCollect.address.town},${clickCollect.address.postalCode}/@${clickCollect.geoPoint.latitude},${clickCollect.geoPoint.longitude}" target="_blank" class="btn btnLocation" ><span><i class="fa fa-map-marker pad-right" style="padding-right:5px;">
														</i>SEE DIRECTION</span></a></p>
												</li>	
											</ul>
											</div>
										</form>
									</c:if>
								</c:otherwise>
							</c:choose>
							</c:forEach>
						</div>
						<!-- </div> -->
								<%-- <multi-checkout:pickupGroups cartData="${cartData}" /> --%>
					</div>
					
					<div class="col-sm-12 pad-top">
					<div class="collectNcenterPhoneBx nopadding" style="display:none;">
					<div class="col-sm-6 nopadding">
												
						<form:form modelAttribute="sparAddressForm" method="GET">
		            		<formElement:formInputBox idKey="cnc.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="true"  placeholder="Mobile Number"/>
						</form:form>
						<c:url value="/checkout/multi/delivery-address/otp-generator" var="storeFinderFormAction" />
					 	<div class="otpgenerate-finder js-otpgenerate-otpgenerate" data-url="${storeFinderFormAction}"></div>
					</div>	
					 <div class="col-sm-6 text-left  pad-top mt10" style="font-size:13px;">
					 <input id="lrOptStatus" type="hidden" name="lrOptStatus" value='false'/>
					 <input id="cncPhoneVerify" type="hidden" name="selectedAddressCode" value="${showOTPLink}"/>
					 <input id="cityNameSelected" type="hidden" name="cityNameSelected" value="${cityName}"/> None
			  <c:choose>
			  	<c:when test="${showOTPLink eq true}">
			  		To Verify Your Mobile Number<a class="pad-right danger-text btn" href="#cncModal" id="generateOTP" role="button">Click Here</a>
			  	</c:when>

			  	<c:otherwise>
			  		<%-- Your Primary Mobile number is ${PRIMARYMOBNUM} --%>
			  	</c:otherwise>
			  </c:choose>   	     
	          	<div id="cncModal" class="modal fade" role="dialog">
					<div class="modal-dialog">
					   <div class="modal-content">
						     <div class="modal-header">
						       <button type="button" class="close" data-dismiss="modal">&times;</button>
						       <h4 class="modal-title text-left">One time account verification</h4>
						     </div>
						     <div class="modal-body">
						       <p class="text-center">Enter OTP <div class="form-group" style="width:100px; margin:0 auto;"><input class="form-control" id="cncUserOTP" type="text"/></div></p>
						      </div>
						      <div class="modal-footer">
						        <button type="button" id="cncotpconfirmbtn" class="btn btn-primary">Confirm</button>
						         <div id="thisField" class="hidden"><c:out value="${sysGenOTPKey}"></c:out></div>
						      </div>
					    </div>
					  </div>
				</div>              
                </div>
                	<p class=" col-sm-9 Note_text" style="font-size:14px;"><spring:theme code="text.cnc.phone.message" text="Please use same number as landmark rewards, OTP check text and link."/></p>
				</div>	
				<div class="col-sm-10 Note_text mb10">Note: Change of delivery option / delivery address may affect the product availability selected in your cart.</div>
					<ul class="list-inline pull-right">
						<li><button id="addressSubmit" type="button"
						class="btn btn-primary btn-block checkout-next"><spring:theme code="checkout.multi.deliveryAddress.continue" text="Next"/></button></li>
					</ul>
					<ul class="list-inline pull-right">
						<li><button id="addressCancel" type="button"
						class="btn btn-primary btn-block checkout-cancel"><spring:theme code="checkout.multi.deliveryAddress.cancel" text="Cancel"/></button></li>
					</ul>
					<ul class="list-inline pull-right">
						<li><a href="${cartUrl}" id="previousStep" class="btn btn-primary btn-block"><spring:theme code="checkout.multi.deliveryAddress.back" text="Back"/></a></li>
					</ul>
				</div>
				</div>
				</ycommerce:testId>
			</jsp:body>
			
		</multi-checkout:checkoutSteps>
		
		<%-- <multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="false" showPaymentInfo="false" showTaxEstimate="false" showTax="true" /> --%>

		<%-- <div class="col-sm-12 col-lg-9">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div> --%>
	</div>

</template:page>
