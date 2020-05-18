<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
	<div class="row nomargin ">
		<div class="col-sm-12 nopadding link_reg " style="display: none;">
			<cms:pageSlot position="LeftContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		<div class="col-md-12 nopadding link_login hide">
			<cms:pageSlot id="idd" position="CenterContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>
	</div>
	</div>
</template:page>