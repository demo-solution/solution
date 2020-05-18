<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<template:page pageTitle="${pageTitle}">
	<div class="row nomargin">	
		<div class="col-md-12 nopadding link_reg" style="display:none;">
			<cms:pageSlot position="LeftContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		 <div class="col-md-12 nopadding link_login hide" >
			<cms:pageSlot id = "idd" position="RightContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div> 
	</div>
</template:page>