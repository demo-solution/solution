<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="tabIndex" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:choose>
	<c:when test="${country == 'US'}">
		<formElement:formSelectBox idKey="address.title"
			labelKey="address.title" path="titleCode" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect"
			items="${titles}" selectedValue="${addressForm.titleCode}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.firstName"
			labelKey="address.firstName" path="firstName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.surname"
			labelKey="address.surname" path="lastName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line1"
			labelKey="address.line1" path="line1" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line2"
			labelKey="address.line2" path="line2" inputCSS="form-control"
			mandatory="false" />
		<formElement:formInputBox idKey="address.townCity"
			labelKey="address.townCity" path="townCity" inputCSS="form-control"
			mandatory="true" />
		<formElement:formSelectBox idKey="address.region"
			labelKey="address.state" path="regionIso" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.state"
			items="${regions}"
			itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}"
			selectedValue="${addressForm.regionIso}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.postcode"
			labelKey="address.zipcode" path="postcode" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.phone"
			labelKey="address.phone" path="phone" inputCSS="form-control"
			mandatory="false" />
	</c:when>
	<c:when test="${country == 'CA'}">
		<formElement:formSelectBox idKey="address.title"
			labelKey="address.title" path="titleCode" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect"
			items="${titles}" selectedValue="${addressForm.titleCode}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.firstName"
			labelKey="address.firstName" path="firstName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.surname"
			labelKey="address.surname" path="lastName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line1"
			labelKey="address.line1" path="line1" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line2"
			labelKey="address.line2" path="line2" inputCSS="form-control"
			mandatory="false" />
		<formElement:formInputBox idKey="address.townCity"
			labelKey="address.townCity" path="townCity" inputCSS="form-control"
			mandatory="true" />
		<formElement:formSelectBox idKey="address.region"
			labelKey="address.province" path="regionIso" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.state"
			items="${regions}"
			itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}"
			selectedValue="${addressForm.regionIso}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.postcode"
			labelKey="address.postalcode" path="postcode" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.phone"
			labelKey="address.phone" path="phone" inputCSS="form-control"
			mandatory="false" />
	</c:when>
	<c:when test="${country == 'CN'}">
		<formElement:formInputBox idKey="address.postcode"
			labelKey="address.postalcode" path="postcode" inputCSS="form-control"
			mandatory="true" />
		<formElement:formSelectBox idKey="address.region"
			labelKey="address.province" path="regionIso" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.selectProvince"
			items="${regions}"
			itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}"
			selectedValue="${addressForm.regionIso}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.townCity"
			labelKey="address.townCity" path="townCity" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line1"
			labelKey="address.street" path="line1" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line2"
			labelKey="address.building" path="line2" inputCSS="form-control"
			mandatory="false" />
		<formElement:formInputBox idKey="address.surname"
			labelKey="address.surname" path="lastName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.firstName"
			labelKey="address.firstName" path="firstName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formSelectBox idKey="address.title"
			labelKey="address.title" path="titleCode" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect"
			items="${titles}" selectedValue="${addressForm.titleCode}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.phone"
			labelKey="address.phone" path="phone" inputCSS="form-control"
			mandatory="false" />
	</c:when>
	<c:when test="${country == 'JP'}">
		<formElement:formSelectBox idKey="address.title"
			labelKey="address.title" path="titleCode" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect"
			items="${titles}" selectedValue="${addressForm.titleCode}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.surname"
			labelKey="address.surname" path="lastName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.firstName"
			labelKey="address.firstName" path="firstName" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.line1"
			labelKey="address.furtherSubarea" path="line1"
			inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2"
			labelKey="address.subarea" path="line2" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.townCity"
			labelKey="address.townJP" path="townCity" inputCSS="form-control"
			mandatory="true" />
		<formElement:formSelectBox idKey="address.region"
			labelKey="address.prefecture" path="regionIso" mandatory="true"
			skipBlank="false" skipBlankMessageKey="address.selectPrefecture"
			items="${regions}"
			itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}"
			selectedValue="${addressForm.regionIso}"
			selectCSSClass="form-control" />
		<formElement:formInputBox idKey="address.postalcode"
			labelKey="address.postcode" path="postcode" inputCSS="form-control"
			mandatory="true" />
		<formElement:formInputBox idKey="address.phone"
			labelKey="address.phone" path="phone" inputCSS="form-control"
			mandatory="false" />
	</c:when>
	<c:otherwise>
		<div class="col-sm-6 pad-top">
			<div class="col-xs-12 col-sm-12">
				<div class="col-xs-12 col-sm-4 text-right pr0">
					<label class="control-label" for="address.firstName"><spring:theme
							code="address.firstName" /></label>
				</div>
				<div class="col-xs-3 col-sm-3 pr0">
					<formElement:formSelectBox idKey="address.title"
						labelKey="address.title" path="titleCode" mandatory="true"
						skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect"
						items="${titles}" selectedValue="${addressForm.titleCode}"
						selectCSSClass="form-control" />
				</div>
				<div class="col-xs-9 col-sm-5">
					<formElement:formInputBox idKey="address.firstName"
						labelKey="address.firstName" path="firstName"
						inputCSS="form-control" mandatory="true" />
				</div>
			</div>
			<%-- <div class="col-xs-12 col-sm-12"><formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="true"/> --%>

			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.phone"
					labelKey="address.phone" path="phone" inputCSS="form-control"
					mandatory="true" placeholder="Mobile Number" />
			</div>
			<!-- Change start by sumit -->
			<c:url value="/checkout/multi/delivery-address/otp-generator"
				var="storeFinderFormAction" />
			<div class="otpgenerate-finder js-otpgenerate-otpgenerate"
				data-url="${storeFinderFormAction}"></div>
			<div class="col-xs-12 col-sm-12 text-right" style="font-size: 12px;position: relative;top:-10px;right:15px;">
				<c:choose>
					<c:when test="${showOTPLink eq true}">
			  		To Verify Your Mobile Number <a class="danger-text" href="#myModal" id="generateOTP">Click Here</a>
					</c:when>

					<c:otherwise>
						<%-- Your Primary Mobile number is ${PRIMARYMOBNUM} --%>
					</c:otherwise>
				</c:choose>
				<div id="myModal" class="modal fade" role="dialog" data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">&times;</button>
								<h4 class="modal-title text-left">One time account
									verification</h4>
							</div>
							<div class="modal-body">
								<div class="row">
						    		<div class="col-lg-12 text-left">
										<div class="checkbox">
											 <label for="lrOptStatus"><input id="lrOptStatus" name="lrOptStatus" type="checkbox" value="true" checked />Dear Customer, would you like to get rewarded for all your purchases?
												Enrol for Landmark Rewards & earn valuable points.</label>							
										</div>				
									</div>						    	
						    	</div>	
								<p class="text-center">Enter OTP
								<div class="form-group" style="width: 100px; margin: 0 auto;">
									<input class="form-control" id="userOTP" type="text" />
								</div>
								</p>
							</div>
							<div class="modal-footer">
								<!--  <p class="pull-left green-text"><input type="checkbox"> Sign Up for Landmark Reward Point</p> -->
								<button type="button" id="otpconfirmbtn" class="btn btn-primary">Confirm</button>
								<div id="thisField" class="hidden">
									<c:out value="${sysGenOTPKey}"></c:out>
								</div>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
		<!-- Change end here -->
		<%-- <div class="col-sm-6 pad-top">
			<div class="col-xs-12 col-sm-12">
				<div class="col-sm-4 text-right"><label class="control-label" for="address.firstName"><spring:theme code="address.firstName"/></label></div>
				<div class="col-sm-2"><formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/></div>
				<div class="col-sm-6"><formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" placeholder="First Name"/></div>
			</div>
			<div class="col-xs-12 col-sm-12"><formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="true" placeholder="Mobile Number"/></div>
		</div>	 --%>
		<div class="col-sm-6 pad-top">
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.surname"
					labelKey="address.surname" path="lastName" inputCSS="form-control"
					mandatory="true" placeholder="Last Name" />
			</div>
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.email"
					labelKey="address.email" path="email" inputCSS="form-control"
					mandatory="true" placeholder="abjohn@gmail.com" />
			</div>
			<c:if test="${isdobSet eq false}">
				<div class="col-xs-12 col-sm-12">
					<formElement:formInputBox idKey="address.dateOfBirth"
						labelKey="address.dateOfBirth" path="dateOfBirth"
						inputCSS="form-control" mandatory="true" />
				</div>
			</c:if>
		</div>
		<h5 class="add-det-head mar-bottom">&nbsp;</h5>
		<div class="col-sm-6 pad-top pad-bottom">
			<div class="col-xs-12 col-sm-12 mb10">
				<formElement:formInputBox idKey="storelocator-query1"
					labelKey="address.longAddress" path="longAddress"
					inputCSS="form-control" mandatory="true"
					placeholder="Enter order delivery area" autocomplete="on" />

				<!-- <input type="text" id="storelocator-query1"	placeholder="Enter order delivery area" 
							autocomplete="on"	class="form-control" size="30" /> -->

				<button type="button" id="areaSearch" class="hide" />
			</div>
			<%-- Google location changes --%>



			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.postcode"
					labelKey="address.postcode" path="postcode" inputCSS="form-control"
					mandatory="true" placeholder="Pincode" />
			</div>
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.line1"
					labelKey="address.line1" path="line1" inputCSS="form-control"
					mandatory="true" placeholder="Street, Phase, Details" />
			</div>
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.line2"
					labelKey="address.line2" path="line2" inputCSS="form-control"
					mandatory="true" placeholder="House/Flat/Office #" />
			</div>
		</div>
		<div class="col-sm-6 pad-top pad-bottom">
		<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.townCity"
					labelKey="address.townCity" path="townCity" inputCSS="form-control"
					mandatory="true" placeholder="e.g Bangalore" />
			</div>
			
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.area"
					labelKey="address.area" path="area" inputCSS="form-control"
					mandatory="true" placeholder="e.g Koramangala Block 5" />
			</div>
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.buildingName"
					labelKey="address.buildingName" path="buildingName"
					inputCSS="form-control" mandatory="true"
					placeholder="Building/ Appartment name" />
			</div>
			<div class="col-xs-12 col-sm-12">
				<formElement:formInputBox idKey="address.landmark"
					labelKey="address.landmark" path="landmark" inputCSS="form-control"
					mandatory="false" placeholder="Landmark details" />
			</div>
		</div>
	</c:otherwise>
</c:choose>