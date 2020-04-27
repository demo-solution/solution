<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/responsive/nav/breadcrumb"%>

<spring:url value="/my-account/update-profile" var="updateProfileUrl"/>
<spring:url value="/my-account/update-password" var="updatePasswordUrl"/>
<spring:url value="/my-account/update-email" var="updateEmailUrl"/>
<spring:url value="/my-account/address-book" var="addressBookUrl"/>
<spring:url value="/my-account/payment-details" var="paymentDetailsUrl"/>
<spring:url value="/my-account/orders" var="ordersUrl"/>
<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-2 nopadding">
			<cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>	
		<div class="col-md-10 nopadding">
			<div class="row pl10"><!--container--> 
				<c:if test="${fn:length(breadcrumbs) > 0}">
					<div class="breadcrumb breadcrumbText">
							<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
					</div>
				</c:if>
			</div>		
			<cms:pageSlot position="TopContent" var="feature" element="div" class="accountPageTopContent">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<div class="row account-section">
				<cms:pageSlot position="BodyContent" var="feature" element="div" class="accountPageBodyContent">
					<cms:component component="${feature}" />
				</cms:pageSlot>
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div" class="accountPageBottomContent">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>
</template:page>