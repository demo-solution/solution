<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>


<template:page pageTitle="${pageTitle}">
	<div class="row">
		<cms:pageSlot position="ProductLeftRefinements" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		<div class="col-sm-8 col-md-9 col-lg-9 col-xs-12 product-listing-wrapper">
		<cms:pageSlot position="SearchResultsGridSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		</div>
	</div>

	<storepickup:pickupStorePopup />

</template:page>
