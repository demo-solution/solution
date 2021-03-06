<%@ attribute name="supportedCountries" required="true" type="java.util.List"%>
<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="false" type="java.lang.String"%>
<%@ attribute name="addressBook" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- <c:if test="${not empty deliveryAddresses}">
	<button type="button" class="btn btn-default btn-block js-address-book">
		<spring:theme
			code="checkout.checkout.multi.deliveryAddress.viewAddressBook"
			text="Address Book" />
	</button>
	<div class="col-sm-4">
		<!-- <div class="col-sm-1 nopadding"><input type="radio" name="address"/></div> -->
		<div class="col-sm-11 address-block nopadding">
			<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
			<h3 class="address-header">Deliver to this address <a href="#" class="pull-right"><i class="fa fa-pencil"></i></a></h3>
				<div class="addressEntry">
					<form action="${request.contextPath}/checkout/multi/delivery-address/select" method="GET">
						<div class="col-sm-1 nopadding"><input type="radio" onclick="submit()" name="address"/></div>
						<input type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}" />
						<ul>
							<li>
								<strong>${fn:escapeXml(deliveryAddress.title)}&nbsp;
								${fn:escapeXml(deliveryAddress.firstName)}&nbsp;
								${fn:escapeXml(deliveryAddress.lastName)}</strong>
								<br>
								${fn:escapeXml(deliveryAddress.line1)}&nbsp;
								${fn:escapeXml(deliveryAddress.line2)}
								<br>
								${fn:escapeXml(deliveryAddress.town)}
								<c:if test="${not empty deliveryAddress.region.name}">
									&nbsp;${fn:escapeXml(deliveryAddress.region.name)}
								</c:if>
								<br>
								${fn:escapeXml(deliveryAddress.country.name)}&nbsp;
								${fn:escapeXml(deliveryAddress.postalCode)}<br>
								<i class="fa fa-phone-square" style="font-size:16px; padding-right:10px;"></i>
								<c:if test="${not empty deliveryAddress.phone}">
									${fn:escapeXml(deliveryAddress.phone)}
								</c:if><br>
								<i class="fa fa-envelope" style="font-size:16px; padding-right:10px;"></i>
								<c:if test="${not empty deliveryAddress.email}">
                       		${fn:escapeXml(deliveryAddress.email)}
                       	</c:if><br>
								<p>
								<label class="pull-left" style="font-weight:normal;">Alternate Number</label>
                        <input type="text" class="alt-mob pull-left"/></p>
                        <p class="pull-left danger-text">Information about the active order will be sent on this alternate number</p>
							</li>
						</ul>
						<button type="submit" class="btn btn-primary btn-block">
							<spring:theme
								code="checkout.multi.deliveryAddress.useThisAddress"
								text="Use this Address" />
						</button>
					</form>
				</div>
			</c:forEach>
		</div>
	</div>
	<br>
</c:if> --%>

<%-- <c:if test="${empty deliveryAddresses}"> --%>
	<div id="addressform" style="display:none;">
	<h5 class="add-det-head" style="font-weight: bold;"><spring:theme code="checkout.summary.shippingAddress" 
								text="Shipping Address"></spring:theme></h5>
		<form:form method="post" modelAttribute="sparAddressForm">
			<form:hidden path="addressId" class="add_edit_delivery_address_id"
				status="${not empty suggestedAddresses ? 'hasSuggestedAddresses' : ''}" />
			<input type="hidden" name="bill_state" id="address.billstate" />
			
			<%-- <input type="hidden" name="longAddress" id="addressLongAddress" value="${addressData.longAddress}"/> --%>
			<!-- <input type="hidden" name="defaultStore" id="address.defaultStore" />
			<input type="hidden" name="defaultCncCenter" id="address.defaultCncCenter" />
			<input type="hidden" name="sparServiceAreaId" id="address.sparServiceArea.areaId" /> -->
		
			<div id="countrySelector" data-address-code="${addressData.id}"
				data-country-iso-code="${addressData.country.isocode}"
				class="form-group" style="display:none">
				<formElement:formSelectBox idKey="address.country"
					labelKey="address.country" path="countryIso" mandatory="false"
					skipBlank="false" skipBlankMessageKey="address.country"
					items="${supportedCountries}" itemValue="isocode"
					selectedValue="IN"
					selectCSSClass="form-control" />
			</div>
			<div id="i18nAddressForm" class="i18nAddressForm">
				<%-- <c:if test="${not empty country}"> --%>
					<address:addressFormElements regions="${regions}"
						country="${country}" />
				<%-- </c:if> --%>
			</div>
			<h5 class="add-det-head mar-bottom" style="float:left; width:100%; padding:0;"></h5>
			<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
				<div class="checkbox clearfix" style="display:none;">	
					<c:choose>
						<c:when test="${showSaveToAddressBook}">
							<formElement:formCheckbox idKey="saveAddressInMyAddressBook"
								labelKey="checkout.summary.deliveryAddress.saveAddressInMyAddressBook"
								path="saveInAddressBook" inputCSS="add-address-left-input"
								labelCSS="add-address-left-label" mandatory="true" />
						</c:when>
						<c:when test="${not addressBookEmpty && not isDefaultAddress}">
							<ycommerce:testId code="editAddress_defaultAddress_box">
								<formElement:formCheckbox idKey="defaultAddress"
									labelKey="address.default" path="defaultAddress"
									inputCSS="add-address-left-input"
									labelCSS="add-address-left-label" mandatory="true" />
							</ycommerce:testId>
						</c:when>
					</c:choose>
				</div>
			</sec:authorize>
			<div id="addressform_button_panel" class="form-actions">
				<c:choose>
					<c:when test="${edit eq true && not addressBook}">
						<ycommerce:testId code="multicheckout_saveAddress_button">
							<button
								class="positive right change_address_button show_processing_message"
								type="submit">
								<spring:theme code="checkout.multi.saveAddress"
									text="Save address" />
							</button>
						</ycommerce:testId>
					</c:when>
					<c:when test="${addressBook eq true}">						
							<div class="row">
								<div class="col-xs-12 col-md-2 pull-right">
								<ycommerce:testId code="editAddress_saveAddress_button">
									<button class="btn btn-primary btn-block change_address_button show_processing_message"
											type="submit">
										<spring:theme code="text.button.save"
													  text="Save" />
									</button>
								</ycommerce:testId>
							</div>
							<div class="col-xs-12 col-md-2 pull-right">
								<ycommerce:testId code="editAddress_cancelAddress_button">
									<c:url value="${cancelUrl}" var="cancel"/>
									<a class="btn btn-block btn-default" href="${cancel}">
										<spring:theme code="text.button.cancel"
													  text="Cancel" />
									</a>
								</ycommerce:testId>
							</div>							
						</div>
					</c:when>
				</c:choose>
			</div>
		</form:form>
	</div>
<%-- </c:if> --%>
